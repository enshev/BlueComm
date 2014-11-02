package com.enshev;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements OnSeekBarChangeListener {

	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	private BluetoothAdapter btAdapter;
	private ArrayAdapter<String> newDevices;
	private Messenger mServiceBT;
	private boolean mIsBound;

	boolean blActivatingBT = false;

	ProgressDialog progressDialog = null;

	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	private static final int REQUEST_CONNECT_BT = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static final int REQUEST_NONE = 4;

	private Button btConnect;
	private Button btSendText;
	private Button btRead;
	private Button btClear;
	private Button btAutoManual;
	private Button btLam1;
	private Button btLam2;
	private Button btLamSpot;
	private Button btTrainingRSSI;
	private Button btNoRSSI;
	private Button btModel;
	private Button btScan1;
	private EditText et1;
	private EditText et2;
	private EditText et3;
	private EditText et4;
	private SeekBar btDimmer;
	private TextView valuePWM;

	private final int ACTIVITY = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		btConnect = (Button) findViewById(R.id.btConnect);
		btSendText = (Button) findViewById(R.id.btSendText);
		btRead = (Button) findViewById(R.id.btRead);
		btClear = (Button) findViewById(R.id.btClear);
		btLam1 = (Button) findViewById(R.id.btLam1);
		btLam2 = (Button) findViewById(R.id.btLam2);
		btLamSpot = (Button) findViewById(R.id.btLamSpot);
		btAutoManual = (Button) findViewById(R.id.btAutoManual);
		btTrainingRSSI = (Button) findViewById(R.id.btTrainingRSSI);
		btNoRSSI = (Button) findViewById(R.id.btNoRSSI);
		btModel = (Button) findViewById(R.id.btModel);
		btScan1 = (Button) findViewById(R.id.btScan1);
		btDimmer = (SeekBar) findViewById(R.id.btDimmer);
		et1 = (EditText) findViewById(R.id.et1);
		et2 = (EditText) findViewById(R.id.et2);
		et3 = (EditText) findViewById(R.id.et3);
		et4 = (EditText) findViewById(R.id.et4);
		valuePWM = (TextView) findViewById(R.id.valuePWM);

		btDimmer.setOnSeekBarChangeListener(this);
		btDimmer.setEnabled(false);
		btConnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btConnect.getText().equals("Connect")) {
					setupBT();
				} else {
					sendBluetoothService(BluetoothService.MSG_BT_DISCONNECTED,
							0, null);
					btConnect.setText("Connect");
					btSendText.setEnabled(false);
					btRead.setEnabled(false);
					btLam1.setEnabled(false);
					btLam2.setEnabled(false);
					btLamSpot.setEnabled(false);
					btClear.setEnabled(false);
					btAutoManual.setEnabled(false);
					btDimmer.setEnabled(false);
					btNoRSSI.setEnabled(false);
					btModel.setEnabled(false);
					btScan1.setEnabled(false);
				}
			}
		});
		btAutoManual.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btAutoManual.getText().equals("Manual Mode")) {
					et3.setText("*,14");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btAutoManual.setText("Auto Mode");
				} else {
					et3.setText("*,15");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btAutoManual.setText("Manual Mode");
				}
			}
		});
		btSendText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0, et1
						.getText().toString());
			}
		});
		btLam1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// String str = deviceRSSI;
				if (btLam1.getText().equals("Lampu1 OFF")) {
					et3.setText("*,10," + newDevices + ",3");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLam1.setText("Lampu1 ON");
				} else {
					et3.setText("*,10," + newDevices + ",2");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLam1.setText("Lampu1 OFF");
				}
			}
		});
		btLam2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btLam2.getText().equals("Lampu2 OFF")) {
					et3.setText("*,10,5,3");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLam2.setText("Lampu2 ON");
				} else {
					et3.setText("*,10,5,2");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLam2.setText("Lampu2 OFF");
				}
			}
		});
		btLamSpot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btLamSpot.getText().equals("LampSpot OFF")) {
					et3.setText("*,10,3,3");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLamSpot.setText("LampSpot ON");
					btDimmer.setEnabled(true);
					btDimmer.setMax(255);
				} else {
					et3.setText("*,10,3,2");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLamSpot.setText("LampSpot OFF");
					btDimmer.setEnabled(false);
					btDimmer.setMax(0);
				}
			}
		});
		btRead.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		btClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				et2.setText("");
			}
		});
		btTrainingRSSI.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,
						RSSIListActivity.class));
			}
		});
		btNoRSSI.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,
						MainActivityManual.class));
			}
		});
		btScan1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doDiscovery();
				et4.setText("");
				v.setVisibility(View.GONE);
			}
		});

		// mulai rssi//
		newDevices = new ArrayAdapter<String>(this, R.layout.activity_main);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(receiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(receiver, filter);

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		doBindService();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (btConnect.getText().toString().equals("Disconnect")) {
			sendBluetoothService(BluetoothService.MSG_BT_DISCONNECTED, 0, null);
		}
		if (btAdapter != null) {
			btAdapter.cancelDiscovery();
		}
		this.unregisterReceiver(receiver);
		doUnbindService();
	}

	private void doDiscovery() {
		// setProgressBarIndeterminateVisibility(true);
		setTitle("Discovery");
		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
		btAdapter.startDiscovery();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
		if (requestCode == REQUEST_ENABLE_BT) {

			if (resultCode != RESULT_OK) {
				Toast.makeText(MainActivity.this,
						"Enable Bluetooth has cancel", Toast.LENGTH_SHORT)
						.show();
				finish();
			} else if (resultCode == RESULT_OK)
				requestBT(REQUEST_CONNECT_BT);
		} else if (requestCode == REQUEST_CONNECT_BT) {
			if (resultCode == Activity.RESULT_OK) {
				progressDialog = ProgressDialog.show(this, "Please wait",
						"Making Connection", true);
				String btAddress = intent.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				progressDialog.dismiss();
				sendBluetoothService(BluetoothService.MSG_CREATE_STREAM_BT, 0,
						btAddress);
			} else {
				Toast.makeText(MainActivity.this, "Connect has cencel",
						Toast.LENGTH_SHORT).show();
			}
		} else if (requestCode == REQUEST_NONE) {
			checkBTConnectionStatus();
		}
	}

	private void requestBT(int request) {
		if (request == REQUEST_ENABLE_BT) {
			btAdapter = BluetoothAdapter.getDefaultAdapter();
			if (btAdapter == null) {
				Toast.makeText(this, "No Bluetooth Adapter", Toast.LENGTH_SHORT)
						.show();
				finish();
			}
			if (!btAdapter.isEnabled()) {
				Intent intent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(intent, REQUEST_ENABLE_BT);
			} else
				requestBT(REQUEST_CONNECT_BT);
		} else if (request == REQUEST_CONNECT_BT) {
			Intent serverIntent = new Intent(MainActivity.this,
					DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_BT);
		}
	}

	private void setupBT() {
		if (btConnect.getText().equals("Connect")) {
			requestBT(REQUEST_ENABLE_BT);
		} else {
			sendBluetoothService(BluetoothService.MSG_BT_DISCONNECTED, 0, null);
			btConnect.setText("Disconnect");
		}
	}

	private void sendBluetoothService(int what, int arg2, Object obj) {
		try {
			Message msg = Message.obtain(null, what, ACTIVITY, arg2, obj);
			msg.replyTo = mMessengerBT;
			mServiceBT.send(msg);
		} catch (RemoteException e) {
		}
	}

	private void checkBTConnectionStatus() {
		sendBluetoothService(BluetoothService.MSG_CHECK_CONNECTION_STATUS, 0, 0);
	}

	class IncomingHandlerBT extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothService.MSG_CREATE_STREAM_BT:
				blActivatingBT = true;
				checkBTConnectionStatus();
				break;
			case BluetoothService.MSG_CHECK_CONNECTION_STATUS:
				if (msg.arg2 == BluetoothService.CONNECTION_STATUS_IS_CONNECT) {
					if (blActivatingBT) {
						blActivatingBT = false;
					}
					btConnect.setText("Disconnect");
					btSendText.setEnabled(true);
					btRead.setEnabled(true);
					btLam1.setEnabled(true);
					btLam2.setEnabled(true);
					btLamSpot.setEnabled(true);
					btClear.setEnabled(true);
					btAutoManual.setEnabled(true);
					btNoRSSI.setEnabled(true);
					btModel.setEnabled(true);
				} else {
					btConnect.setText("Connect");
					btSendText.setEnabled(false);
					btRead.setEnabled(false);
					btLam1.setEnabled(false);
					btLam2.setEnabled(false);
					btLamSpot.setEnabled(false);
					btClear.setEnabled(false);
					btAutoManual.setEnabled(false);
					btNoRSSI.setEnabled(false);
					btModel.setEnabled(false);
				}
				break;
			case BluetoothService.MSG_READ_TEXT:
				et2.setText(et2.getText().toString() + msg.obj.toString());
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	final Messenger mMessengerBT = new Messenger(new IncomingHandlerBT());

	private ServiceConnection mConnectionBT = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mServiceBT = new Messenger(service);
			sendBluetoothService(BluetoothService.MSG_REGISTER_CLIENT,
					ACTIVITY, null);
		}

		public void onServiceDisconnected(ComponentName className) {
			mServiceBT = null;
		}
	};

	void doBindService() {
		bindService(new Intent(MainActivity.this, BluetoothService.class),
				mConnectionBT, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			if (mServiceBT != null) {
				sendBluetoothService(BluetoothService.MSG_UNREGISTER_CLIENT,
						ACTIVITY, null);
			}
			unbindService(mConnectionBT);
			mIsBound = false;
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int Arg1, boolean arg2) {
		// TODO Auto-generated method stub
		valuePWM.setText("PWM Value : " + Arg1);
		et3.setText("*,11,3," + Arg1);
		sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0, et3
				.getText().toString());
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		arg0.setSecondaryProgress(arg0.getProgress());
	}

	// mulai rssi//
	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg2, Intent arg3) {
			String action = arg3.getAction();
			// findViewById(R.id.ViewDevices).setVisibility(View.VISIBLE);
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = arg3
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String deviceRSSI = (arg3.getExtras()).get(
						BluetoothDevice.EXTRA_RSSI).toString();
				// newDevices.add(device.getName() + "\n" + deviceRSSI +
				// " dBm");
				et4.setText(et4.getText() + device.getName() + " : "
						+ deviceRSSI + " dBm\n");
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					String str = deviceRSSI;
					if (str == "-62") {
						// str = "6";
						newDevices.add("6");
					}
					if (str == "-63") {
						// str = "6";
						newDevices.add("6");
					}
					if (str == "-64") {
						// str = "6";
						newDevices.add("6");
					}
					if (str == "-65") {
						// str = "6";
						newDevices.add("6");
					}
					if (str == "-66") {
						// str = "6";
						newDevices.add("6");
					}
					if (str == "-70") {
						// str = "5";
						newDevices.add("5");
					}
					if (str == "-71") {
						// str = "5";
						newDevices.add("5");
					}
					if (str == "-72") {
						// str = "5";
						newDevices.add("5");
					}
					if (str == "-73") {
						// str = "5";
						newDevices.add("5");
					}
					if (str == "-74") {
						// str = "5";
						newDevices.add("5");
					}
					if (str == "-75") {
						// str = "5";
						newDevices.add("5");
					}
				}
				// newDevices.add(str + "\n" + device.getAddress());
				// }
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				// setProgressBarIndeterminateVisibility(false);
				findViewById(R.id.btScan1).setVisibility(View.VISIBLE);
				if (newDevices.getCount() == 0) {
					String noDevice = "Not Found";
					et4.setText(noDevice);
					// newDevices.add(noDevice);
				}
			}
		}
	};
}
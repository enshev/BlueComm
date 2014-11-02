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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivityManual extends Activity implements
		OnSeekBarChangeListener {

	private Messenger mServiceBT;
	private boolean mIsBound;

	boolean blActivatingBT = false;

	ProgressDialog progressDialog = null;

	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	private static final int REQUEST_CONNECT_BT = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static final int REQUEST_NONE = 4;

	private BluetoothAdapter btAdapter;

	private Button btRead;
	private Button btClear;
	private Button btAutoManual;
	private Button btLampu1A;
	private Button btLampu1B;
	private Button btLampu1C;
	private Button btLampu2A;
	private Button btLampu2B;
	private Button btLampu2C;
	private Button btLampuSpotA;
	private Button btLampuSpotB;
	private Button btLampuSpotC;
	private EditText et1;
	private EditText et2;
	private EditText et3;
	private SeekBar btDimmer1;
	private SeekBar btDimmer2;
	private SeekBar btDimmer3;
	private TextView valuePWM1;
	private TextView valuePWM2;
	private TextView valuePWM3;

	private final int ACTIVITY = 1;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main_manual);
		setResult(Activity.RESULT_CANCELED);

		btRead = (Button) findViewById(R.id.btRead);
		btClear = (Button) findViewById(R.id.btClear);
		btAutoManual = (Button) findViewById(R.id.btAutoManual);
		btLampu1A = (Button) findViewById(R.id.Lampu1A);
		btLampu1B = (Button) findViewById(R.id.Lampu1B);
		btLampu1C = (Button) findViewById(R.id.Lampu1C);
		btLampu2A = (Button) findViewById(R.id.Lampu2A);
		btLampu2B = (Button) findViewById(R.id.Lampu2B);
		btLampu2C = (Button) findViewById(R.id.Lampu2C);
		btLampuSpotA = (Button) findViewById(R.id.LampuSpotA);
		btLampuSpotB = (Button) findViewById(R.id.LampuSpotB);
		btLampuSpotC = (Button) findViewById(R.id.LampuSpotC);
		et1 = (EditText) findViewById(R.id.et1);
		et2 = (EditText) findViewById(R.id.et2);
		et3 = (EditText) findViewById(R.id.et3);
		btDimmer1 = (SeekBar) findViewById(R.id.btDimmer1);
		btDimmer2 = (SeekBar) findViewById(R.id.btDimmer2);
		btDimmer3 = (SeekBar) findViewById(R.id.btDimmer3);
		valuePWM1 = (TextView) findViewById(R.id.valuePWM1);
		valuePWM2 = (TextView) findViewById(R.id.valuePWM2);
		valuePWM3 = (TextView) findViewById(R.id.valuePWM3);
		btDimmer1.setOnSeekBarChangeListener(this);
		btDimmer2.setOnSeekBarChangeListener(this);
		btDimmer3.setOnSeekBarChangeListener(this);
		btDimmer1.setEnabled(false);
		btDimmer2.setEnabled(false);
		btDimmer3.setEnabled(false);
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
		btLampu1A.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btLampu1A.getText().equals("Lampu1 A OFF")) {
					et3.setText("*,10,6,3");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampu1A.setText("Lampu1 A ON");
				} else {
					et3.setText("*,10,6,2");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampu1A.setText("Lampu1 A OFF");
				}
			}
		});
		btLampu1B.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btLampu1B.getText().equals("Lampu1 B OFF")) {
					et3.setText("*,10,41,3");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampu1B.setText("Lampu1 B ON");
				} else {
					et3.setText("*,10,41,2");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampu1B.setText("Lampu1 B OFF");
				}
			}
		});
		btLampu1C.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btLampu1C.getText().equals("Lampu1 C OFF")) {
					et3.setText("*,10,45,3");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampu1C.setText("Lampu1 C ON");
				} else {
					et3.setText("*,10,45,2");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampu1C.setText("Lampu1 C OFF");
				}
			}
		});
		btLampu2A.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btLampu2A.getText().equals("Lampu2 A OFF")) {
					et3.setText("*,10,5,3");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampu2A.setText("Lampu2 A ON");
				} else {
					et3.setText("*,10,5,2");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampu2A.setText("Lampu2 A OFF");
				}
			}
		});
		btLampu2B.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btLampu2B.getText().equals("Lampu2 B OFF")) {
					et3.setText("*,10,43,3");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampu2B.setText("Lampu2 B ON");
				} else {
					et3.setText("*,10,43,2");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampu2B.setText("Lampu2 B OFF");
				}
			}
		});
		btLampu2C.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btLampu2C.getText().equals("Lampu2 C OFF")) {
					et3.setText("*,10,47,3");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampu2C.setText("Lampu2 C ON");
				} else {
					et3.setText("*,10,47,2");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampu2C.setText("Lampu2 C OFF");
				}
			}
		});
		btLampuSpotA.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btLampuSpotA.getText().equals("LampSpotA OFF")) {
					et3.setText("*,11,4,255");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampuSpotA.setText("LampSpotA ON");
					btDimmer1.setEnabled(true);
					btDimmer1.setMax(255);
				} else {
					et3.setText("*,11,4,0");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampuSpotA.setText("LampSpotA OFF");
					btDimmer1.setEnabled(false);
					btDimmer1.setMax(0);
				}
			}
		});
		btLampuSpotB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btLampuSpotB.getText().equals("LampSpotB OFF")) {
					et3.setText("*,11,10,255");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampuSpotB.setText("LampSpotB ON");
					btDimmer2.setEnabled(true);
					btDimmer2.setMax(255);
				} else {
					et3.setText("*,11,10,0");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampuSpotB.setText("LampSpotB OFF");
					btDimmer2.setEnabled(false);
					btDimmer2.setMax(0);
				}
			}
		});
		btLampuSpotC.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btLampuSpotC.getText().equals("LampSpotC OFF")) {
					et3.setText("*,11,9,255");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampuSpotC.setText("LampSpotC ON");
					btDimmer3.setEnabled(true);
					btDimmer3.setMax(255);
				} else {
					et3.setText("*,11,9,0");
					sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0,
							et3.getText().toString());
					btLampuSpotC.setText("LampSpotC OFF");
					btDimmer3.setEnabled(false);
					btDimmer3.setMax(0);
				}
			}
		});
		btRead.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				et2.setText("Suhu : 28 C , Cahaya Terang");
			}
		});
		btClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				et2.setText("");
			}
		});
		doBindService();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
		if (requestCode == REQUEST_ENABLE_BT) {

			if (resultCode != RESULT_OK) {
				Toast.makeText(MainActivityManual.this,
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
				Toast.makeText(MainActivityManual.this, "Connect has cencel",
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
			Intent serverIntent = new Intent(MainActivityManual.this,
					DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_BT);
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
					btRead.setEnabled(true);
					btClear.setEnabled(true);
					btAutoManual.setEnabled(true);
					btLampu1A.setEnabled(true);
					btLampu1B.setEnabled(true);
					btLampu1C.setEnabled(true);
					btLampu2A.setEnabled(true);
					btLampu2B.setEnabled(true);
					btLampu2C.setEnabled(true);
					btLampuSpotA.setEnabled(true);
					btLampuSpotB.setEnabled(true);
					btLampuSpotC.setEnabled(true);
					btDimmer1.setEnabled(true);
					btDimmer2.setEnabled(true);
					btDimmer3.setEnabled(true);
				} else {
					btRead.setEnabled(false);
					btClear.setEnabled(false);
					btAutoManual.setEnabled(false);
					btLampu1A.setEnabled(false);
					btLampu1B.setEnabled(false);
					btLampu1C.setEnabled(false);
					btLampu2A.setEnabled(false);
					btLampu2B.setEnabled(false);
					btLampu2C.setEnabled(false);
					btLampuSpotA.setEnabled(false);
					btLampuSpotB.setEnabled(false);
					btLampuSpotC.setEnabled(false);
					btDimmer1.setEnabled(false);
					btDimmer2.setEnabled(false);
					btDimmer3.setEnabled(false);
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
		bindService(
				new Intent(MainActivityManual.this, BluetoothService.class),
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
		if (arg0.equals(btDimmer1)) {
			valuePWM1.setText("PWM Value : " + Arg1);
			et3.setText("*,11,4," + Arg1);
			sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0, et3
					.getText().toString());
		}
		if (arg0.equals(btDimmer2)) {
			valuePWM2.setText("PWM Value : " + Arg1);
			et3.setText("*,11,10," + Arg1);
			sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0, et3
					.getText().toString());
		}
		if (arg0.equals(btDimmer3)) {
			valuePWM3.setText("PWM Value : " + Arg1);
			et3.setText("*,11,9," + Arg1);
			sendBluetoothService(BluetoothService.MSG_WRITE_SERIAL, 0, et3
					.getText().toString());
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		arg0.setSecondaryProgress(arg0.getProgress());
	}
}
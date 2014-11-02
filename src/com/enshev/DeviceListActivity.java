package com.enshev;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceListActivity extends Activity {
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	private BluetoothAdapter btAdapter;
	private ArrayAdapter<String> pairedDevices;
	private ArrayAdapter<String> newDevices;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);
		setResult(Activity.RESULT_CANCELED);
		Button btScan = (Button) findViewById(R.id.btScan);
		btScan.setOnClickListener(new OnClickListener() {

			// @Override
			public void onClick(View arg0) {
				doDiscovery();
				arg0.setVisibility(View.GONE);
			}
		});
		pairedDevices = new ArrayAdapter<String>(this, R.layout.device_name);
		newDevices = new ArrayAdapter<String>(this, R.layout.device_name);

		ListView listPairedDevices = (ListView) findViewById(R.id.listPairedDevices);
		listPairedDevices.setAdapter(pairedDevices);
		listPairedDevices.setOnItemClickListener(deviceClickListener);

		ListView listNewDevices = (ListView) findViewById(R.id.listNewDevices);
		listNewDevices.setAdapter(newDevices);
		listNewDevices.setOnItemClickListener(deviceClickListener);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(receiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(receiver, filter);

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		Set<BluetoothDevice> pairedDevice = btAdapter.getBondedDevices();

		if (pairedDevice.size() > 0) {
			findViewById(R.id.lbPairedDevices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevice) {
				String str = device.getName().toString();
				if ((str.equals("HC-05"))
						&& (device.getAddress().equals("20:13:10:22:01:14")))
					str = "Bluetooth Arduino";
				pairedDevices.add(str + "\n" + device.getAddress());
			}
		} else {
			findViewById(R.id.lbNewDevices).setVisibility(View.VISIBLE);
			String noDevice = "Not found";
			pairedDevices.add(noDevice);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (btAdapter != null) {
			btAdapter.cancelDiscovery();
		}
		this.unregisterReceiver(receiver);
	}

	private OnItemClickListener deviceClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			btAdapter.cancelDiscovery();
			String info = ((TextView) arg1).getText().toString();
			try {
				String address = info.substring(info.length() - 17);
				Intent intent = new Intent();
				intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

				setResult(Activity.RESULT_OK, intent);
				finish();

			} catch (IndexOutOfBoundsException ioobe) {
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
		}

	};

	private void doDiscovery() {
		setProgressBarIndeterminateVisibility(true);
		setTitle("Discovery");
		findViewById(R.id.lbPairedDevices).setVisibility(View.VISIBLE);
		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
		btAdapter.startDiscovery();
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			findViewById(R.id.lbNewDevices).setVisibility(View.VISIBLE);
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = arg1
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				newDevices.add(device.getName() + "\n" + device.getAddress());

				// if(device.getBondState() != BluetoothDevice.BOND_BONDED) {
				// String str = device.getName();
				// if(str == "HC-05") {
				// str = "Bluetooth Arduino";
				// }
				// newDevices.add(str + "\n" + device.getAddress());
				// }
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
				setTitle("Select Device");
				findViewById(R.id.btScan).setVisibility(View.VISIBLE);
				if (newDevices.getCount() == 0) {
					String noDevice = "Not Found";

					newDevices.add(noDevice);
				}
			}
		}
	};
}

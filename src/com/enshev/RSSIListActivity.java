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

public class RSSIListActivity extends Activity {
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	private BluetoothAdapter btAdapter;
	private ArrayAdapter<String> newDevices;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.training_data_rssi);
		setResult(Activity.RESULT_CANCELED);
		Button btScan = (Button) findViewById(R.id.btScan);
		btScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doDiscovery();
				arg0.setVisibility(View.GONE);
			}
		});
		newDevices = new ArrayAdapter<String>(this, R.layout.device_name);

		ListView listNewDevices = (ListView) findViewById(R.id.listNewDevices);
		listNewDevices.setAdapter(newDevices);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(receiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(receiver, filter);

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		findViewById(R.id.lbNewDevices).setVisibility(View.VISIBLE);
		String noDevice = "Not found";
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (btAdapter != null) {
			btAdapter.cancelDiscovery();
		}
		this.unregisterReceiver(receiver);
	}

	private void doDiscovery() {
		setProgressBarIndeterminateVisibility(true);
		setTitle("Discovery");
		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
		btAdapter.startDiscovery();
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String action = arg1.getAction();
			findViewById(R.id.lbNewDevices).setVisibility(View.VISIBLE);
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = arg1
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String deviceRSSI = (arg1.getExtras()).get(
						BluetoothDevice.EXTRA_RSSI).toString();
				newDevices.add(device.getName() + "\n" + deviceRSSI + " dBm");

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
				findViewById(R.id.btScan).setVisibility(View.VISIBLE);
				if (newDevices.getCount() == 0) {
					String noDevice = "Not Found";
					newDevices.add(noDevice);
				}
			}
		}
	};
}

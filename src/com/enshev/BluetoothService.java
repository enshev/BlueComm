package com.enshev;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

public class BluetoothService extends Service {

	/** Keeps track of all current registered clients. */
	private Messenger mClients[] = new Messenger[10];

	/** Holds last value set by a client. */
	int mValue = 0;

	static final int MSG_REGISTER_CLIENT = 1;
	static final int MSG_UNREGISTER_CLIENT = 2;
	static final int MSG_CHECK_BT = 3;
	static final int MSG_ENABLE_BT = 4;
	static final int MSG_CREATE_STREAM_BT = 5;
	static final int MSG_CONNECT_BT = 6;
	static final int MSG_DISCONNECT_BT = 7;
	static final int MSG_READ_TEXT = 13;
	static final int MSG_BT_DISCONNECTED = 15;
	static final int MSG_BT_CONNECTED = 16;
	static final int MSG_CHECK_BT_SOCKET = 17;
	static final int MSG_CHECK_CONNECTION_STATUS = 18;
	static final int MSG_WRITE_SERIAL = 21;

	static final int CONNECTION_STATUS_IS_CONNECT = 1;
	static final int CONNECTION_STATUS_IS_DISCONNECT = 0;

	static final int BT_IS_NULL = 1;
	static final int BT_IS_DISABLED = 2;
	static final int BT_IS_ENABLED = 3;
	static final int BT_SOCKET_IS_OPEN = 4;
	static final int BT_SOCKET_IS_CLOSE = 5;

	static final int ST_LOGIN_MENU = 1;
	static final int ST_MAIN_MENU = 2;
	static final int ST_SCAN_MENU = 3;

	static int state = 1;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	private static final UUID sppUUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Debugging
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;

	Byte data[];
	String sData;

	ConnectedThread connectedThread;

	public class ConnectedThread extends Thread {
		private BluetoothSocket btSocket;
		private BluetoothAdapter btAdapter;
		private String address;
		private boolean connectionStatus;
		private InputStream input = null;
		private OutputStream output = null;

		public ConnectedThread() {

		}

		private void serialSend(String dataSend) {
			if (output != null) {
				try {
					dataSend = ";" + dataSend;
					dataSend += "!\r\n";
					output.write(dataSend.getBytes());
				} catch (IOException ioe) {
				}
			}
		}

		private void connect(String address) {
			btAdapter = BluetoothAdapter.getDefaultAdapter();
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			this.address = address;

			try {
				BluetoothDevice device = btAdapter.getRemoteDevice(address);
				btSocket = device.createRfcommSocketToServiceRecord(sppUUID);
				tmpIn = btSocket.getInputStream();
				tmpOut = btSocket.getOutputStream();
				connectionStatus = true;
			} catch (IllegalArgumentException iae) {
				connectionStatus = false;
			} catch (IOException ioe) {
				connectionStatus = false;
			}
			input = tmpIn;
			output = tmpOut;
			btAdapter.cancelDiscovery();
			try {
				btSocket.connect();
			} catch (IOException e1) {
				try {
					connectionStatus = false;
					btSocket.close();
				} catch (IOException e2) {
				}
			}
			int connectifity = MSG_BT_DISCONNECTED;
			if (connectionStatus) {
				connectifity = MSG_BT_CONNECTED;
			}
			try {
				mClients[state].send(Message.obtain(null, MSG_CONNECT_BT,
						state, 0, connectifity));
			} catch (RemoteException re) {
			}
		}

		private void disconnect() {
			if (btSocket != null) {
				try {
					btSocket.close();

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				connectionStatus = false;
			}
		}

		private boolean checkBTAdapter() {
			if (btAdapter == null) {
				return false;
			}
			return true;
		}

		private boolean checkBTEnable() {
			if (checkBTAdapter()) {
				if (btAdapter.enable()) {
					return true;
				}
			}
			return false;
		}

		private boolean checkBTSocket() {
			if (btSocket == null)
				return false;
			return true;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			while (true) {
				byte[] buffer = new byte[64];
				try {
					do {

						buffer[0] = (byte) input.read();
					} while (buffer[0] != ';');
					char i = 0;
					for (i = 1; ((buffer[i - 1] != '!') && (i < 32)); i++) {
						buffer[i] = (byte) input.read();
						if (buffer[i] == ';') {
							buffer = new byte[64];
							buffer[0] = ';';
							i = 0;
						}
					}
					if (i >= 32)
						continue;

					// if(size != -1) {
					String str = new String(buffer, 0, buffer.length);
					try {
						mClients[state].send(Message.obtain(null,
								MSG_READ_TEXT, state, 0, str));
					} catch (Exception re) {
					}
					// }
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					// connectionLost();
					connectionStatus = false;
					break;
				}
			}
		}
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			state = msg.arg1;
			int connectionResult = CONNECTION_STATUS_IS_DISCONNECT;

			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				// mClients.add(msg.replyTo);
				state = msg.arg1;
				mClients[msg.arg1] = msg.replyTo;
				break;
			case MSG_UNREGISTER_CLIENT:
				// state = msg.arg1;
				// mClients.remove(msg.replyTo);
				mClients[msg.arg1] = null;
				break;
			case MSG_CHECK_CONNECTION_STATUS:
				if (connectedThread.connectionStatus)
					connectionResult = CONNECTION_STATUS_IS_CONNECT;
				try {
					mClients[state].send(Message.obtain(null,
							MSG_CHECK_CONNECTION_STATUS, state,
							connectionResult));
				} catch (RemoteException re) {
					mClients[state] = null;
				}
				break;
			case MSG_CHECK_BT:
				state = msg.arg1;
				if (connectedThread.checkBTAdapter()) {
					try {
						mClients[state].send(Message.obtain(null, MSG_CHECK_BT,
								state, BT_IS_NULL));
					} catch (RemoteException re) {
						mClients[state] = null;
					}
					break;
				}
				if (!connectedThread.checkBTEnable()) {
					try {
						mClients[state].send(Message.obtain(null, MSG_CHECK_BT,
								state, BT_IS_DISABLED));
					} catch (RemoteException re) {
						mClients[state] = null;
					}
					break;
				}
				try {
					mClients[state].send(Message.obtain(null, MSG_CHECK_BT,
							state, BT_IS_ENABLED));
				} catch (RemoteException re) {
					mClients[state] = null;
				}
				break;
			case MSG_CHECK_BT_SOCKET:
				if (connectedThread.checkBTSocket()) {
					try {
						mClients[state].send(Message.obtain(null,
								MSG_CHECK_BT_SOCKET, state, BT_SOCKET_IS_OPEN));
					} catch (RemoteException re) {
						mClients[state] = null;
					}
				} else {
					try {
						mClients[state]
								.send(Message.obtain(null, MSG_CHECK_BT_SOCKET,
										state, BT_SOCKET_IS_CLOSE));
					} catch (RemoteException re) {
						mClients[state] = null;
					}
				}
				break;
			case MSG_CREATE_STREAM_BT:
				String adr = (String) msg.obj;
				connectedThread = new ConnectedThread();

				connectedThread.connect(adr);
				connectedThread.start();
				if (connectedThread.connectionStatus)
					connectionResult = CONNECTION_STATUS_IS_CONNECT;

				try {
					mClients[state].send(Message.obtain(null,
							MSG_CREATE_STREAM_BT, connectionResult));
				} catch (RemoteException re) {
				}
				break;
			case MSG_BT_DISCONNECTED:

				if (connectedThread.checkBTSocket())
					connectedThread.disconnect();
				break;
			case MSG_WRITE_SERIAL:
				state = msg.arg1;
				connectedThread.serialSend((String) msg.obj);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	@Override
	public void onCreate() {
		super.onCreate();
		connectedThread = new ConnectedThread();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * When binding to the service, we return an interface to our messenger for
	 * sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}
}
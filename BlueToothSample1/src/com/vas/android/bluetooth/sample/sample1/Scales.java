/*
 * Copyright (C) 2010 Volodimir Slobodanuk
 * All rights reserved
 * 
 * 
 * 
 * Copyright (C) 2009 Stefano Sanna
 * 
 * gerdavax@gmail.com - http://www.gerdavax.it
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vas.android.bluetooth.sample.sample1;

import it.gerdavax.android.bluetooth.BluetoothSocket;
import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import it.gerdavax.android.bluetooth.LocalBluetoothDeviceListener;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;
import it.gerdavax.android.bluetooth.RemoteBluetoothDeviceListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Scales extends ListActivity {
	private static final String TAG = "AndroidBluetoothTest";
	
	protected static final String KEY_PAIRED_DEVICES_NUM = "com.vas.android.scales.paired_devices_num";
	protected static final String KEY_PAIRED_DEVICE_NAME = "com.vas.android.scales.paired_device_name";
	protected static final String KEY_PAIRED_DEVICE_ADDRESS = "com.vas.android.scales.paired_device_address";
	
	
	
	private StringBuffer buffer = new StringBuffer();
	private TextView text;
	private MyThread t, t2;
	
	private float weight = (float) 0.00;
	private float weight_max = (float) 0.00;
	private float weight_immediate = (float) 0.00;;
	//private float weight_previous = (float) 0.00;
	
	private int MAX_WINDOW_TIME = 5000;
	private int BUFFER_LENGTH = 255;
	private long CURRENT_WEIGHT_TIME_STAMP;
	private long PREVIOUS_WEIGHT_TIME_STAMP = 0;
	
	protected LocalBluetoothDevice localBluetoothDevice;
	protected static ProgressDialog dialog;
	protected Handler handler = new Handler();
	protected ArrayList<String> devices;
	protected ArrayList<String> names;

	protected class DeviceAdapter extends BaseAdapter implements LocalBluetoothDeviceListener {

		public int getCount() {
			if (devices != null) {
				return devices.size();
			}
			return 0;
		}

		public Object getItem(int position) {
			return devices.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout feedItem = null;

			try {
				if (convertView == null) {
					feedItem = new LinearLayout(Scales.this);
					String inflater = Context.LAYOUT_INFLATER_SERVICE;
					LayoutInflater vi = (LayoutInflater) Scales.this.getSystemService(inflater);
					vi.inflate(R.layout.item, feedItem, true);
				} else {
					feedItem = (LinearLayout) convertView;
				}

				TextView feedTitle = (TextView) feedItem.findViewById(R.id.address);
				TextView deviceNameAndClass = (TextView) feedItem.findViewById(R.id.name);

				String address = devices.get(position);
				String name = "null";
				String deviceClass = "null";
				try {
					deviceClass = "" + localBluetoothDevice.getRemoteClass(address);
					name = localBluetoothDevice.getRemoteBluetoothDevice(address).getName();
				} catch (Exception e) {
					e.printStackTrace();
					name = "ERROR";
				}

				if (name != null) {
					deviceClass = name + " - " + deviceClass;
				}

				feedTitle.setText(address);
				deviceNameAndClass.setText(deviceClass);

			} catch (Exception e) {
				// e.printStackTrace();
			}

			return feedItem;
		}

		public void scanCompleted(ArrayList<String> devs) {
			devices = devs;
			notifyDataSetChanged();
			hideProgressDialog();
		}

		public void scanStarted() {
			showProgressDialog();
		}

		public void disabled() {

		}

		public void enabled() {

		}

		@Override
		public void bluetoothDisabled() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void bluetoothEnabled() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void deviceFound(String deviceAddress) {
			// TODO Auto-generated method stub
			
		}

	}

	private class MyThread extends Thread {
		boolean halt = false;
	}
	
	private class RemoteBluetoothDeviceEventHandler implements RemoteBluetoothDeviceListener {
		RemoteBluetoothDevice device;

		public void paired() {
			// connects to channel 1
			connectTo(device, 1);
		}

		public void pinRequested() {
			Log.d(TAG, "pinRequested()");
			
			// does not work as expected. To be investigated...
			//
			//Intent intent = new Intent("android.bluetooth.intent.action.PAIRING_REQUEST");
			//startActivity(intent);
		}

		public void gotServiceChannel(int serviceID, int channel) {
			// TODO Auto-generated method stub
			
		}

		public void serviceChannelNotAvailable(int serviceID) {
			// TODO Auto-generated method stub
			
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test);

		DeviceAdapter adapter = new DeviceAdapter();
		setListAdapter(adapter);

		getListView().setOnItemClickListener(new OnItemClickListener() {
			SharedPreferences prefsAddress = getSharedPreferences(KEY_PAIRED_DEVICE_ADDRESS, 0);
			Editor editAddress = prefsAddress.edit();
			
			SharedPreferences prefsName = getSharedPreferences(KEY_PAIRED_DEVICE_NAME, 0);
			Editor editName = prefsName.edit();

			public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
				//Here we want to insert the breakdown....from original program flow.
				//Basically, we store the address and whatever info we want in the string, and call programm that will run as service
				final String address = devices.get(position);

				//Once again, we store device address in the string				
				
				
				try {
					AlertDialog.Builder builder = new AlertDialog.Builder(Scales.this);
					builder.setMessage("Do you want to connect to this device?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//Once again, we store device address in the string
							editAddress.putString(KEY_PAIRED_DEVICE_ADDRESS, address);
							editAddress.commit();
							
							//now we call the service to run it
							//pair(address);
							//DeviceSelection();
							//pair(prefs.getString(KEY_PAIRED_DEVICE_ADDRESS, ""));
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		try {
			localBluetoothDevice = LocalBluetoothDevice.initLocalDevice(this);

			if (localBluetoothDevice.isEnabled()) {
				localBluetoothDevice.setListener(adapter);

				//BlueToothSample1.showDialog(this, R.string.open_dialog_gps);
				
			} else {
				BlueToothSample1.showDialog(this, R.string.bluetooth_not_enabled);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			localBluetoothDevice.scan();
		} catch (Exception e) {

		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Connected to BT device");

		text = (TextView) dialog.findViewById(R.id.text);
		text.setText("(no data)");

		return dialog;
	}

	@Override
	protected void onDestroy() {
		super.onPause();
		System.out.println("PAUSE");
		
		localBluetoothDevice.close();
		
		
		if (t != null) {
			t.halt = true;
		}
		
		if (t2 != null) {
			t2.halt = true;
			}
		}

	private void pair(String address) {
		RemoteBluetoothDevice device = localBluetoothDevice.getRemoteBluetoothDevice(address);
		RemoteBluetoothDeviceEventHandler listener = new RemoteBluetoothDeviceEventHandler();
		listener.device = device;
		device.setListener(listener);
		device.pair();
	}

	private void connectTo(final RemoteBluetoothDevice device, final int port) {
		t = new MyThread() {
			@Override
			public void run() {
				
				StopWatch stopwatch = new StopWatch();
				stopwatch.start();
				
				
				try {
					Log.d(TAG, "Connecting...");

					BluetoothSocket socket = device.openSocket(port);

					InputStream input = socket.getInputStream();
					OutputStream output = socket.getOutputStream();
					output.write("TEST".getBytes());

					byte[] buffer1 = new byte[16];
					byte[] buffer = new byte[BUFFER_LENGTH+1];
					int read;
					int buffer_top = 0;
					int position_in_buffer = 0;
					
					while ((read = input.read(buffer1)) != -1 && !halt) {
						//System.out.println("number of bytes in bufer is "+buffer_top);
						
						//we want fill our long buffer
						if (buffer_top + read < BUFFER_LENGTH){
							//we want to add these bytes to buffer
							for (position_in_buffer = buffer_top; position_in_buffer < buffer_top+read; position_in_buffer++ ) {
								buffer[position_in_buffer] = buffer1 [position_in_buffer - buffer_top]; 
							}
							buffer_top = buffer_top+read;
						}else{
							//resetting buffer counters
							buffer_top = 0;
							position_in_buffer = 0;
							
							//now we want to get weight from this buffer
							weight_immediate = ScalesKLX349.klx349getData(buffer, BUFFER_LENGTH);
							
							System.out.println("immediate weight is " + weight_immediate);
							
							//first of all we want to determine if we are still inside time window
							//we need to operate within sliding window of MAX_WINDOW_TIME seconds to make sure we are picking maximum weight in a given time window 
							CURRENT_WEIGHT_TIME_STAMP = stopwatch.getElapsedTime();
							
							//we need to verify if this in the same time window
							if (CURRENT_WEIGHT_TIME_STAMP - PREVIOUS_WEIGHT_TIME_STAMP < MAX_WINDOW_TIME){
								
								//ok, we are potentially looking for a new maximum
								if(weight_immediate > weight_max){
									//ok, we've got new candidate for MAX value
									weight_max = weight_immediate;
									
									//resetting timer
									PREVIOUS_WEIGHT_TIME_STAMP = CURRENT_WEIGHT_TIME_STAMP;
									
								} else{
									//Do not do anything???
									//weight_previous = weight_immediate;
								}
								
							} else {
								//previous time window has expired
								
								//if previous max weight was 0, and current weight is NOT 0, we will start processing new maximum, otherwise we will do nothing
								
								if (weight_immediate == (float) 0){
									
									//do nothing, keep maximum measured weight un-changed
									weight = weight_max;
									
								} else {
									//start new window
									//we are resetting PREVIOUS_WEIGHT_TIME_STAMP
									PREVIOUS_WEIGHT_TIME_STAMP = CURRENT_WEIGHT_TIME_STAMP;
									
									//we've got new candidate for MAX value
									weight_max = weight_immediate;
									
								}
								
							}
								
						}
						
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		};
		t.start();

		// very bad code... just for demo purpose... :-(
		t2 = new MyThread() {
			@Override
			public void run() {
				while (true && !halt) {
					try {
						//sleep(1000);
						//if (text != null && buffer.length() > 0) {
						if (weight > 0.001){
							handler.post(new Runnable() {
								public void run() {
									text.setText(String.valueOf(weight));
									if (buffer.length() > 3000) {
										buffer.delete(0, 1000);
									}
								}
							});
						} else {
							//System.out.println("Text is null!");
						}
					} catch (Exception e) {

					}
				}
			}
		};
		t2.start();

		handler.post(new Runnable() {

			public void run() {
				showDialog(0);
			}
		});
	}

	private void appendString(String string) {
		buffer.append(string);
	}
	
	protected void showProgressDialog() {
		handler.post(new Runnable() {
			public void run() {
				dialog = ProgressDialog.show(Scales.this, "", "Scanning Bluetooth devices. Please wait...", true);
			}
		});
	}

	protected void hideProgressDialog() {
		handler.post(new Runnable() {
			public void run() {
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, "Scan").setIcon(android.R.drawable.ic_menu_search);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			try {
				localBluetoothDevice.scan();
			} catch (Exception e) {

			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
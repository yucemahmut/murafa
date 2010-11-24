/**
 * 
 */
package com.vas.android.bluetooth.sample.sample1;

import it.gerdavax.android.bluetooth.BluetoothSocket;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;
import it.gerdavax.android.bluetooth.RemoteBluetoothDeviceListener;

import java.io.InputStream;
import java.io.OutputStream;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

/**
 * @author Compaq_Owner
 *
 */
public class DeviceSelection extends Scales {
	//Right now this will be a place holder to select type of the device based on the stored type and address
	
	private TextView text;
	private StringBuffer buffer = new StringBuffer();
	
	//private StringBuffer buffer = new StringBuffer();
	//private TextView text;
	private MyThread t, t2;
	
	private float weight = (float) 0.00;
	private float weight_max = (float) 0.00;
	private float weight_immediate = (float) 0.00;;
	
	private final int MAX_WINDOW_TIME = 5000;
	private final int BUFFER_LENGTH = 255;
	private long CURRENT_WEIGHT_TIME_STAMP;
	private long PREVIOUS_WEIGHT_TIME_STAMP = 0;
	
	
	public static final String KEY_PAIRED_DEVICE_NAME = "com.vas.android.scales.paired_device_name";
	public static final String KEY_PAIRED_DEVICE_ADDRESS = "com.vas.android.scales.paired_device_address";
	
	SharedPreferences prefsName = getSharedPreferences(KEY_PAIRED_DEVICE_NAME, 0);

	SharedPreferences prefsAddress = getSharedPreferences(KEY_PAIRED_DEVICE_ADDRESS, 0);
	//protected double weight;
	
	public void DeviceSelectionSwitch () {
		
		//depending on the name of the device we will select the type
		
		if (prefsName.getString(KEY_PAIRED_DEVICE_NAME, "") == "Serial Adaptor"){
		//We've got scales, we can call them
			pair(prefsAddress.getString(KEY_PAIRED_DEVICE_ADDRESS, ""));
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
	
	
	private class MyThread extends Thread {
		boolean halt = false;
	}
	
}

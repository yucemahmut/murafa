/**
 * 
 */
package com.vas.android.MainGWSY;

import it.gerdavax.android.bluetooth.BluetoothException;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.vas.android.http_post.ServerPost;
import com.vas.android.sensors.ScalesKLX349;
import com.vas.android.user.UserProfile;

import edu.mit.media.android.amarino.BTDevice;
import edu.mit.media.android.amarino.BTService;

/**
 * @author Volodimir Slobodanuk
 *
 */
public class MainLoop extends Service {
	public static final String TAG = "Amarino";
	protected static final String KEY_PAIRED_DEVICES_NUM = "edu.mit.media.android.amarino.paired_devices_num";
	protected static final String KEY_PAIRED_DEVICE_NAME = "edu.mit.media.android.amarino.paired_device_name";
	protected static final String KEY_PAIRED_DEVICE_ADDRESS = "edu.mit.media.android.amarino.paired_device_address";
	private static final int MENU_ABOUT = 23;
	protected static final int SHOW_DISCOVERED_DEVICES = 55;
	public static final int CONNECTED = 1;
	protected  final String BT_PREFS_NAME = "BT_Preferences_Paired_Devices";
	private  final int NUMBER_OF_PARALLEL_THREADS_TO_BLUETOOTH_DEVICES = 5;
	
	static BTService btService;
	protected static final String List_Of_Available_Devices = "com.vas.android.list_of_available_devices";
	
	static BT_Device_Connect bt_device_connect;
	

	/**
	 * @return 
	 * 
	 */
	//@Override
	public static boolean Start_GWSY (SharedPreferences prefs, int numPairedDevices) {
		// TODO Auto-generated constructor stub
		//SharedPreferences prefs = getSharedPreferences (BT_PREFS_NAME, MODE_PRIVATE); 
		//getSharedPreferences(BT_PREFS_NAME, MODE_PRIVATE);
		//int numPairedDevices = prefs.getInt(KEY_PAIRED_DEVICES_NUM, 0);
		
		//we want to use no more than 5 parallel threads
		//in the beginning, we have all devices available to try to connect
		int device;
		int current_position;
		int Number_of_Active_Threads = 0;
		
		
		List<Integer> devices_list = new ArrayList<Integer>();
		
		//lets populate list of the devices
		
		for (int i=0; i<numPairedDevices; i++){
		
			devices_list.add(i);
			
			}
		
		//at the beginning of the loop we start from first element in the devices list
		current_position = 0;
		
		//we will start infinite loop here
		
		
		//we want to keep number of active threads equal to 5
		while (Number_of_Active_Threads < 5 && devices_list.size() > 0){
			//run new thread to connect to new BT device
			//get device index from devices_list
			
			device = devices_list.get(current_position);
			
			//now we want to do couple of things: launch thread for the device, remove the device from the devices list
			
			//launching new thread
			
			bt_device_connect.bt_connect(device);
			
			//remove this device from the list
			device = devices_list.remove(current_position);
			
			//now we need to handle the devices list
			//as we removed device from current position, new device is in this position now
			
			//increment number of active threads
			
			Number_of_Active_Threads++;
			
			
		}
		
		

		for (int i=0; i<numPairedDevices; i++){
			//measured_weight_previous = 0;
			BTDevice d = new BTDevice();
			d.name = prefs.getString(KEY_PAIRED_DEVICE_NAME + i, "");
			d.address = prefs.getString(KEY_PAIRED_DEVICE_ADDRESS + i, "");
			d.state = BTDevice.PAIRED;
			Log.d(TAG, "paired device stored: " + d.name);
			try {
				btService.connect(d.address);
				
				
				measured_weight = ScalesKLX349.klx349getData (btService);
						Log.d(TAG, "measured weight is " + measured_weight);
						btService.disconnect(true);
						data_to_send =new Float (measured_weight).toString();
						data_to_send = data_to_send + Separator + d.address + Separator + d.name + Separator + UserProfile.UserName();
						
						ServerPost.sendGWSYdata(data_to_send);
									
			} catch (BluetoothException e) {
				// TODO show error message
				e.printStackTrace();
				//return false;
			} catch (Exception e) {
				// TODO show error message
				e.printStackTrace();
				//return false;
			}
		}
		}	
		//return true;
		 
		
		
		
		
		
		
		
		
		
		
		
		return true;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	

}

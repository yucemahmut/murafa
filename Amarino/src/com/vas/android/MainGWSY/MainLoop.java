/**
 * 
 */
package com.vas.android.MainGWSY;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

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
		
		
		
		
		
		
		
		
		
		
		
		return true;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	

}

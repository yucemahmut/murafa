/*
  Amarino - A prototyping software toolkit for Android and Arduino
  Copyright (c) 2009 Bonifaz Kaufmann.  All right reserved.
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 3 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

  Acknowledgements:
  Amarino uses the Bluetooth API of Stefano Sanna which can be found at
  http://code.google.com/p/android-bluetooth/
*/
package edu.mit.media.android.amarino;

import it.gerdavax.android.bluetooth.BluetoothException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.media.android.amarino.db.Collection;

public class bt_serial extends Activity {
	
	ImageView bluetoothIV;
	ImageView eventIV;
	ImageView monitoringIV;
	ImageView settingsIV;
	TextView statusTV;
	ImageButton connectBtn;
	
	boolean isBound = false;
	
	BTService btService;
	
	
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        findViews();
        setClickListener();

        startService(new Intent(bt_serial.this, BTService.class));
    }
	
	
	
	
    private void findViews(){
    	bluetoothIV = (ImageView)findViewById(R.id.bluetooth);
    	eventIV = (ImageView)findViewById(R.id.event);
    	monitoringIV = (ImageView)findViewById(R.id.monitoring);
    	settingsIV = (ImageView)findViewById(R.id.settings);
    	statusTV = (TextView)findViewById(R.id.status);
    	connectBtn = (ImageButton)findViewById(R.id.connect);
    }
    
  private void setClickListener(){
    	
    	connectBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        if (isBound) {
		        	try {
						if (btService.bluetoothHandler.isBluetoothEnabled()){
							switch(btService.state){
							case BTService.CONNECTED:
							case BTService.RECONNECTING:
								btService.disconnect(true);
								connectBtn.setImageResource(R.drawable.connect_button_off_128);
								statusTV.setText(R.string.disconnected);
								break;
							case BTService.DISCONNECTED:
								new ConnectorTask().execute(Collection.getCurrentDeviceAddress(bt_serial.this));
								break;
							default:
								Toast.makeText(bt_serial.this, R.string.wait_operation_going_on, Toast.LENGTH_SHORT).show();
							}
						}
						else {
							Toast.makeText(bt_serial.this, R.string.bluetooth_disabled_dialog_msg, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						Toast.makeText(bt_serial.this, "Coulnd not connect. Bluetooth unaccessible. Error 27!", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
		        	
		        }
		        else {
		        	// TODO proper error message
					Toast.makeText(bt_serial.this, "Coulnd not connect. Background service not bound. Error 13!", Toast.LENGTH_SHORT).show();
				
		        }
			}
		});
    	
    	bluetoothIV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(bt_serial.this, BluetoothManagement.class));
			}
		});
    	
		eventIV.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				startActivity(new Intent(bt_serial.this, EventManagement.class));
			}
		});
		
		monitoringIV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(bt_serial.this, Monitoring.class));
			}
		});
		
		settingsIV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(bt_serial.this, BTPreferences.class));
			}
		});
    }

  class ConnectorTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... addresses) {
			try {
				btService.connect(addresses[0]);
			} catch (BluetoothException e) {
				// TODO show error message
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				// TODO show error message
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			btService.state = BTService.CONNECTING;
			connectBtn.setImageResource(R.drawable.connect_button_connecting_128);
			statusTV.setText(R.string.connecting);
		}

		@Override
		protected void onPostExecute(Boolean isConnected) {
			super.onPostExecute(isConnected);
			if (isConnected) {
				statusTV.setText(R.string.connected);
				connectBtn.setImageResource(R.drawable.connect_button_on_128);
			}
			else {
				Toast.makeText(bt_serial.this, "Connecting failed.", Toast.LENGTH_SHORT).show();
				statusTV.setText(R.string.disconnected);
				btService.state = BTService.DISCONNECTED;
				connectBtn.setImageResource(R.drawable.connect_button_off_128);
			}
		}
		
		
		
	}
	
    
}
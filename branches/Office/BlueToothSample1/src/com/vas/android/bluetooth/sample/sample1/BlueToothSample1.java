package com.vas.android.bluetooth.sample.sample1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BlueToothSample1 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
 
		setContentView(R.layout.main);

		Button button = (Button) findViewById(R.id.bluetoothButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent("com.vas.android.bluetooth.sample.sample1.SERVICE");
				startActivity(intent);
			}

		});
		
		button = (Button) findViewById(R.id.localDeviceButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent("com.vas.android.bluetooth.sample.LOCAL");
				startActivity(intent);
			}

		});
		
		button = (Button) findViewById(R.id.deviceScanButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent("com.vas.android.bluetooth.sample.DISCOVERY");
				startActivity(intent);
			}

		});
		
		button = (Button) findViewById(R.id.arduinoButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				showDialog(BlueToothSample1.this, R.string.open_dialog_arduino_sample);
			}

		});
		
		button = (Button) findViewById(R.id.gpsButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent("com.vas.android.bluetooth.sample.GPS");
				startActivity(intent);
			}

		});
		
		button = (Button) findViewById(R.id.aboutButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				showDialog(BlueToothSample1.this, R.string.about_dialog);
			}

		});
		
		showDialog(this, R.string.splash);
		
	}

	public static void showDialog(Context context, int text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(text).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}

		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
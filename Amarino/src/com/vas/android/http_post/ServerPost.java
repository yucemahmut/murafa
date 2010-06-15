package com.vas.android.http_post;



import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ServerPost extends Activity{
	
		
		public static void sendGWSYdata(String s) {
			//Looper.prepare();
			Handler handler = new Handler() {
				public void handleMessage(Message message) {
					switch (message.what) {
					case HttpConnection.DID_START: {
						//TextView.setText("Starting connection...");
						break;
					}
					case HttpConnection.DID_SUCCEED: {
						String response = (String) message.obj;
						//text.setText(response);
						Log.d("received response",response);
						break;
					}
					case HttpConnection.DID_ERROR: {
						Exception e = (Exception) message.obj;
						e.printStackTrace();
						//text.setText("Connection failed.");
						break;
					}
					}
				}
			};
			new HttpConnection(handler)
					.post("http://www.generalwirelesssystems.com/cgi/server.pl", s);
		}
		
		

}

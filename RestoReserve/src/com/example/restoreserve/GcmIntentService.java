package com.example.restoreserve;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationServices;

public class GcmIntentService extends IntentService implements ConnectionCallbacks, OnConnectionFailedListener {
	
    GoogleApiClient mGoogleApiClient;
	String mLatitudeText;
	String mLongitudeText;
	
	String title,res_id,message;
	
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    String locmsg;

    
    
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        title=extras.getString("title");
        message=extras.getString("message");
        res_id=extras.getString("res_id");
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification1("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification1("Deleted messages on server: " +
                        extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i("gcmintentservice", "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i("gcmintentservice", "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                if(extras.toString().contains("booked"))
                sendNotification1("Received: " + extras.toString());
                else if(extras.toString().contains("confirm"))
                {
            
                sendNotification2("Received: " + extras.toString());
                }
                Log.i("gcmintentservice", "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification1(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, StartScreen.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("RestoReserve-Table booked")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    protected synchronized void buildGoogleApiClient() {
	     mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	}
    private void sendNotification2(String msg) {
        
		buildGoogleApiClient();
		mGoogleApiClient.connect();
    }

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
    	while(mGoogleApiClient.isConnected()==false)
    		//do nothing
    		;
		 Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
	        if (mLastLocation != null) {
	        	mLatitudeText=(String.valueOf((mLastLocation).getLatitude()));
	            mLongitudeText=(String.valueOf((mLastLocation).getLongitude()));
	        }
	     
	        mNotificationManager = (NotificationManager)
	                 this.getSystemService(Context.NOTIFICATION_SERVICE);
	         PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	                 new Intent(this, StartScreen.class), 0);
	         NotificationCompat.Builder mBuilder =
	                 new NotificationCompat.Builder(this)
	         .setSmallIcon(R.drawable.ic_launcher)
	         .setContentTitle(title)
	         .setStyle(new NotificationCompat.BigTextStyle()
	         .bigText(message))
	         .setContentText(message);

	         mBuilder.setContentIntent(contentIntent);
	         mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
   
	         new task().execute();
	   		//mGoogleApiClient.disconnect();

	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
	}
	
	
	class task extends AsyncTask<String, String, Void>
	{

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			HttpParams httpParameters = new BasicHttpParams(); //When no internet conn, we need this
	   		HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
	   		HttpConnectionParams.setSoTimeout(httpParameters, 20000);
	   		 
	   		HttpClient httpclient = new DefaultHttpClient(httpParameters); //When no internet conn, need to timeout request
	   		HttpPost httppost = new HttpPost("http://restoreserve.3eeweb.com/insert_location.php");
	   		
	   	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("lat", mLatitudeText));
			nameValuePairs.add(new BasicNameValuePair("long", mLongitudeText));
			nameValuePairs.add(new BasicNameValuePair("res_id", res_id));
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				httpclient.execute(httppost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mGoogleApiClient.disconnect();
			return null;
		}
		
	}
	
}

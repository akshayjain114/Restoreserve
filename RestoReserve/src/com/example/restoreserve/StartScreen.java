package com.example.restoreserve;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class StartScreen extends ActionBarActivity {
	
	String userid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_screen);
		Thread timer=new Thread()
		{
			public void run()
			{
				try
				{
				Thread.sleep(2000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				finally
				{
					SharedPreferences sharedpreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
				    boolean logged_in = sharedpreferences.getBoolean("loggedin", false);
				    int user_id=sharedpreferences.getInt("user_id", 100);
				    if(logged_in) {
				    	Log.i("First","first hai");
				    	userid=user_id+"";
				    	new task1().execute();
				    }
				    else
				    {
				    	Intent intent=new Intent(StartScreen.this,SignIn.class);
				    	startActivity(intent);
				    	finish();
				    }
				}
			}
		};
		timer.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	//This is new task1().execute from finally ka if block
	class task1 extends AsyncTask<String, String, Void>
	{
	
	HttpResponse response;
	String responseString;
	//private ProgressDialog progressDialog = new ProgressDialog(StartScreen.this); //Edit
	
    protected void onPreExecute() {
       /*progressDialog.setMessage("Trying to connect..."); //Edit
       progressDialog.show();
       progressDialog.setOnCancelListener(new OnCancelListener(){
	public void onCancel(DialogInterface arg0) {
		// TODO Auto-generated method stub
		task1.this.cancel(true);
	}});*/
    }
	       @Override
		protected Void doInBackground(String... params)
	       {
		   		
		   		if(true)
			   	{	   		
		   		HttpParams httpParameters = new BasicHttpParams(); //When no internet conn, we need this
		   		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		   		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		   		 
		   		HttpClient httpclient = new DefaultHttpClient(httpParameters); //When no internet conn, need to timeout request
		   		HttpPost httppost = new HttpPost("http://restoreserve.3eeweb.com/loggedin.php");
		   		 
		   		try {
		   			// Add your data
		   		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		   			nameValuePairs.add(new BasicNameValuePair("user_id", userid));

		   			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		   			response=httpclient.execute(httppost);
		   			responseString =  EntityUtils.toString(response.getEntity());
		   			//This is the response from a php application
		   		
		   			}
		   			catch(ConnectTimeoutException e)
		   			{
		   				Log.e("log_tag1", "Error parsing data "+e.toString());
		   			}
		   			catch(SocketTimeoutException e)
		   			{
		   				Log.e("log_tag1", "Error parsing data "+e.toString());
		   			}
		   			catch (ClientProtocolException e) {
		   			Log.e("log_tag1", "Error parsing data "+e.toString());
		   			}
		   			catch (IOException e) {
		   			Log.e("log_tag2", "Error parsing data "+e.toString());
		   			}
		   			catch (Exception e) {
		   			Log.e("log_tag3", "Error parsing data "+e.toString());
		   			}
		   		}
				return null;

	       }

		protected void onPostExecute(Void v){
			if(responseString.contains("Successful"))
			{
			Toast.makeText(getApplicationContext(), "Successfully connected",Toast.LENGTH_LONG).show();
			responseString=responseString.substring(responseString.indexOf("Successful"),responseString.length());
			responseString=responseString.replace("Successful","");
			
			Intent i=new Intent(StartScreen.this,HomeScreen.class);
			i.putExtra("cuisines", responseString);
			i.putExtra("home",true);
			startActivity(i);
			finish();	
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Something went wrong!"+responseString,Toast.LENGTH_LONG).show();
			}
		}
	}
}

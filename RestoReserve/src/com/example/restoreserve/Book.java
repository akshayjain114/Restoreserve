package com.example.restoreserve;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Book extends Activity {
	TextView resname;
	NumberPicker np;
	Button confirm;
	int no_of_persons;
	TimePicker timepicker;
	int hour,minute;
	int user_id,res_id;
	String time;
	String responseString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book);
		resname=(TextView)findViewById(R.id.tvname);
		resname.setText(getIntent().getStringExtra("res_name"));
		res_id=+getIntent().getIntExtra("res_id",-1);
		np=(NumberPicker)findViewById(R.id.numberPicker1);
		timepicker=(TimePicker)findViewById(R.id.timePicker1);
		String[] nums = new String[20];
		for(int i=0; i<nums.length; i++)
		       nums[i] = Integer.toString(i+1);

		np.setMinValue(1);
		np.setMaxValue(20);
		np.setWrapSelectorWheel(false);
		np.setDisplayedValues(nums);
		np.setValue(2);
		
		confirm=(Button)findViewById(R.id.bconfirm);
		confirm.setBackgroundResource(R.drawable.button_shape);
		confirm.setTextColor(Color.WHITE);
		confirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				no_of_persons=np.getValue();
				hour=timepicker.getCurrentHour();
				minute=timepicker.getCurrentMinute();
				time=hour+":"+minute;
				SharedPreferences sharedpreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
			    user_id=sharedpreferences.getInt("user_id", 100);
				new task().execute();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	//AsyncTask again!
	class task extends AsyncTask<String, String, Void>
	{
	HttpResponse response;
	private ProgressDialog progressDialog = new ProgressDialog(Book.this); //Edit
	
	    protected void onPreExecute() {
	       progressDialog.setMessage("Informing restarateur..."); //Edit
	       progressDialog.show();
	       progressDialog.setOnCancelListener(new OnCancelListener(){
		public void onCancel(DialogInterface arg0) {
			// TODO Auto-generated method stub
			task.this.cancel(true);
		}});
	    }
	       @Override
		protected Void doInBackground(String... params)
	       {	
		   		HttpParams httpParameters = new BasicHttpParams(); //When no internet conn, we need this, but it does not work
		   		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		   		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		   		 
		   		HttpClient httpclient = new DefaultHttpClient(httpParameters); //When no internet conn, need to timeout request, but does not work
		   		HttpPost httppost = new HttpPost("http://restoreserve.3eeweb.com/user_reserve.php"); //Edit link
		   		 
		   		try {
		   			//the way to submit data
		   		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		   			nameValuePairs.add(new BasicNameValuePair("user_id", user_id+""));
		   			nameValuePairs.add(new BasicNameValuePair("res_id", res_id+""));
		   			nameValuePairs.add(new BasicNameValuePair("no_of_persons", no_of_persons+""));
		   			nameValuePairs.add(new BasicNameValuePair("time", time));
		   			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		   			// Execute HTTP Post Request

		   			//ResponseHandler<String> responseHandler = new BasicResponseHandler();
		   			response=httpclient.execute(httppost);
		   			responseString =  EntityUtils.toString(response.getEntity());
		   			}
		   			catch(ConnectTimeoutException e)
		   			{
		   				Log.e("log_tag1", "Error parsing data "+e.toString());
		   				//Toast.makeText(getApplicationContext(), "Internet too slow!? Try again.",Toast.LENGTH_LONG).show();
		   			}
		   			catch(SocketTimeoutException e)
					{
		   				Log.e("log_tag1", "Error parsing data "+e.toString());
		   				//Toast.makeText(getApplicationContext(), "Internet too slow!? Try again.",Toast.LENGTH_LONG).show();
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
				return null;

	       }

		protected void onPostExecute(Void v){
			progressDialog.dismiss();
			//Edit the below as required
			if(responseString.contains("Successful")) //Means the username is unique
			{
			Toast.makeText(getApplicationContext(), "You will shortly be informed about your booking :)",Toast.LENGTH_LONG).show();
			Intent i=new Intent(Book.this,StartScreen.class);
			startActivity(i);
			finish();	
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Something went wrong!"+responseString,Toast.LENGTH_LONG).show();
			}
		}
	}
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
	
}

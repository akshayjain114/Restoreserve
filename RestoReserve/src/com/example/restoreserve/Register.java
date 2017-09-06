package com.example.restoreserve;

//import static android.support.v4.app.FragmentActivity.TAG;

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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Register extends ActionBarActivity implements OnClickListener{
	EditText username,password,name,age,lang,occ;
	Button register;
	GoogleCloudMessaging gcm;
	String regId;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		username=(EditText)findViewById(R.id.etusername);
		password=(EditText)findViewById(R.id.etpassword);
		name=(EditText)findViewById(R.id.etname);
		age=(EditText)findViewById(R.id.etage);
		lang=(EditText)findViewById(R.id.etlanguage);
		occ=(EditText)findViewById(R.id.etoccupation);
		register=(Button)findViewById(R.id.bregistered);
		register.setOnClickListener(this);
		register.setBackgroundResource(R.drawable.button_shape);
		register.setTextColor(Color.WHITE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
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
	
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i("Check play services", "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	class task extends AsyncTask<String, String, Void>
	{
	HttpResponse response;
	String responseString;
	boolean background_successful=true;
	private ProgressDialog progressDialog = new ProgressDialog(Register.this);
	    String result = "";
	    protected void onPreExecute() {
	       progressDialog.setMessage("Submitting data...");
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
	    	   String un,pw,n,l,o;
		   		int a;
		   		un=username.getText().toString();
		   		pw=password.getText().toString();
		   		n=name.getText().toString();
		   		a=Integer.parseInt(age.getText().toString());
		   		l=lang.getText().toString();
		   		o=occ.getText().toString();
		   		
		   		if(true)
			   	{	   		
		   		HttpParams httpParameters = new BasicHttpParams(); //When no internet conn, we need this
		   		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		   		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		   		 
		   		HttpClient httpclient = new DefaultHttpClient(httpParameters); //When no internet conn, need to timeout request
		   		HttpPost httppost = new HttpPost("http://restoreserve.3eeweb.com/register.php");
		   		
		   /*		
		   $mysql_host = "mysql3.000webhost.com";
		   $mysql_database = "a6249389_resto";
		   $mysql_user = "a6249389_resto";
		   $mysql_password = "11laksowietgb";
		   
		   SELECT distinct cuisine, user_id FROM `visited`
where user_id IN (select user_id from visited group by user_id having count(user_id)>1)
order by user_id,cuisine;
		   */
		   		 
		   		try {
		   			// Add your data
		   			if (gcm == null) {
		   		     gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
		   		    }
		   			regId = gcm.register("461734931480");
		   			//regId = gcm.register("123");
		   		    Log.d("in async task", regId);
		   		    
		   		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		   			nameValuePairs.add(new BasicNameValuePair("username", un));
		   			nameValuePairs.add(new BasicNameValuePair("password", pw));
		   			nameValuePairs.add(new BasicNameValuePair("name", n));
		   			nameValuePairs.add(new BasicNameValuePair("age", Integer.toString(a)));
		   			nameValuePairs.add(new BasicNameValuePair("lang", l));
		   			nameValuePairs.add(new BasicNameValuePair("occ", o));
		   			nameValuePairs.add(new BasicNameValuePair("regid", regId));

		   			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		   			// Execute HTTP Post Request

		   			//ResponseHandler<String> responseHandler = new BasicResponseHandler();
		   			response=httpclient.execute(httppost);
		   			responseString =  EntityUtils.toString(response.getEntity());
		   			//This is the response from a php application
		   			//String reverseString = response;
		   			}
		   			catch(ConnectTimeoutException e)
		   			{
		   				//background_successful=false;
		   				Log.e("log_tag1", "Error parsing data "+e.toString());
		   				Toast.makeText(getApplicationContext(), "Internet too slow!? Try again.",Toast.LENGTH_LONG).show();
		   			}
		   			catch(SocketTimeoutException e)
		   			{
		   				//background_successful=false;
		   				Log.e("log_tag1", "Error parsing data "+e.toString());
		   				Toast.makeText(getApplicationContext(), "Internet too slow!? Try again.",Toast.LENGTH_LONG).show();
		   			}
		   			catch (ClientProtocolException e) {
		   			//Toast.makeText(getApplicationContext(), "CPE response " + e.toString(), Toast.LENGTH_LONG).show();
		   			// TODO Auto-generated catch block
		   			Log.e("log_tag1", "Error parsing data "+e.toString());
		   			}
		   			catch (IOException e) {
		   			//Toast.makeText(getApplicationContext(), "IOE response " + e.toString(), Toast.LENGTH_LONG).show();
		   			// TODO Auto-generated catch block
		   				//background_successful=false;
		   			Log.e("log_tag2", "Error parsing data "+e.toString());
		   			}
		   			catch (Exception e) {
		   			//Toast.makeText(getApplicationContext(), "IOE response " + e.toString(), Toast.LENGTH_LONG).show();
		   			// TODO Auto-generated catch block
		   				//background_successful=false;
		   			Log.e("log_tag3", "Error parsing data "+e.toString());
		   			}
		   		}
				return null;

	       }

		protected void onPostExecute(Void v){
		if(background_successful)
		{
			progressDialog.dismiss();
			if(responseString.contains("Successful")) //Means the username is unique
			{
			Toast.makeText(getApplicationContext(), "Successfully registered"+regId,Toast.LENGTH_LONG).show();
			
			int user_id=Integer.parseInt(responseString.substring(0, responseString.indexOf("Successful")));
			
			responseString=responseString.substring(responseString.indexOf("Successful"),responseString.length());
			responseString=responseString.replace("Successful","");
			
			SharedPreferences sharedpreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putBoolean("loggedin", true);
			editor.putInt("user_id",user_id);
			editor.commit();
			
			sharedpreferences = getSharedPreferences("LOGIN", Activity.MODE_PRIVATE);
			int users_id=sharedpreferences.getInt("user_id", 0);
			
			Intent i=new Intent(Register.this,HomeScreen.class);
			i.putExtra("cuisines", responseString);
			
			startActivity(i);
			finish();	
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Username already used. Try another one"+regId,Toast.LENGTH_LONG).show();
				username.selectAll();
				username.requestFocus();
			}
		}
		}
	}
	public void onClick(View v) {
		// TO DO Auto-generated method stub
		switch(v.getId()) {
		case R.id.bregistered : new task().execute();
		break;
		}
	}
}


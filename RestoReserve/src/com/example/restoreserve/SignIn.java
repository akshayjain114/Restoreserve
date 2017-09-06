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

import com.example.restoreserve.Book.task;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignIn extends ActionBarActivity {
	EditText etusername,etpassword;
	String username,password;
	GoogleCloudMessaging gcm;
	String regId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		final Button bregister = (Button) findViewById(R.id.bregister);
		bregister.setBackgroundResource(R.drawable.button_shape);
		bregister.setTextColor(Color.WHITE);
        bregister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent=new Intent(SignIn.this,Register.class);
				startActivity(intent);
            }
        });
        final Button bsignin = (Button) findViewById(R.id.bsignin);
        bsignin.setBackgroundResource(R.drawable.button_shape);
		bsignin.setTextColor(Color.WHITE);
        bsignin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	etusername=(EditText)findViewById(R.id.etusername);
            	etpassword=(EditText)findViewById(R.id.etpassword);
            	username=etusername.getText().toString();
            	password=etpassword.getText().toString();
            	new task().execute();
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_in, menu);
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
	
	
	//Asynctask for signin
	class task extends AsyncTask<String, String, Void>
	{
	//private ProgressDialog progressDialog = new ProgressDialog(StartScreen.this);
	HttpResponse response;
	String responseString;
	boolean background_successful=true;
	private ProgressDialog progressDialog = new ProgressDialog(SignIn.this); //Edit
	
    protected void onPreExecute() {
       progressDialog.setMessage("Signing in..."); //Edit
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
		   		HttpParams httpParameters = new BasicHttpParams(); //When no internet conn, we need this
		   		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		   		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		   		 
		   		HttpClient httpclient = new DefaultHttpClient(httpParameters); //When no internet conn, need to timeout request
		   		HttpPost httppost = new HttpPost("http://restoreserve.3eeweb.com/signin.php");
		   		 
		   		try {
		   			// Add your data
		   			if (gcm == null) {
			   		     gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
			   		    }
			   			regId = gcm.register("461734931480");
		   		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		   			nameValuePairs.add(new BasicNameValuePair("username",username));
		   			nameValuePairs.add(new BasicNameValuePair("password",password));
		   			nameValuePairs.add(new BasicNameValuePair("regid",regId));

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
				return null;
	       }

		protected void onPostExecute(Void v){
			progressDialog.dismiss();
			if(responseString.contains("Successful"))
			{
			Toast.makeText(getApplicationContext(), "Successfully signed in",Toast.LENGTH_LONG).show();
			
			int user_id=Integer.parseInt(responseString.substring(0, responseString.indexOf("Successful")));
			responseString=responseString.substring(responseString.indexOf("Successful"),responseString.length());
			responseString=responseString.replace("Successful","");
			
			SharedPreferences sharedpreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putBoolean("loggedin", true);
			editor.putInt("user_id",user_id);
			editor.commit();
			
			Intent i=new Intent(SignIn.this,HomeScreen.class);
			i.putExtra("cuisines", responseString);
			startActivity(i);
			finish();
			}
			else if(responseString.contains("No match"))
			{
				Toast.makeText(getApplicationContext(), "Username and password do not match!",Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Something went wrong!"+responseString,Toast.LENGTH_LONG).show();
			}
		}
	}

	
}

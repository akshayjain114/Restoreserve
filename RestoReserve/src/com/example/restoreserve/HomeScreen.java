package com.example.restoreserve;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restoreserve.StartScreen.task1;

public class HomeScreen extends ActionBarActivity {
	TextView msg,cuisines;
	ImageView search;
	LinearLayout lin;
	EditText searchword;
	String keyword;
	boolean home;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		Bundle bundle = getIntent().getExtras();
		String pref = bundle.getString("cuisines");
		home=bundle.getBoolean("home");
		msg=(TextView)findViewById(R.id.tvstaticmsg);
		search=(ImageView)findViewById(R.id.search);
		searchword=(EditText)findViewById(R.id.editText1);
		String restaurants[]=pref.split(":");
		
		if(home==false)
		{
			msg.setText("Restaurants based on searched keyword:");
		}
	
		for(String each: restaurants)
		{
			String details[]=each.split(",");
			setAndAdd(details[0],details[1],details[2],details[3]);
		}
		
		search.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				keyword=searchword.getText().toString();
				new task().execute();
			}
		});
		
	}
	
	public void setAndAdd(String id,String name, String cuisine, String cost)
	{
		LinearLayout parent=(LinearLayout)findViewById(R.id.linearLayout);
		lin=new LinearLayout(this);
		lin.setOrientation(LinearLayout.VERTICAL);
		lin.setGravity(Gravity.CENTER);
		lin.setBackgroundColor(Color.WHITE);
		LinearLayout.LayoutParams linllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		linllp.setMargins(10, 10, 10, 10);
		
		ImageView img=new ImageView(this);
		img.setImageDrawable(getResources().getDrawable((R.drawable.loading)));
		LinearLayout.LayoutParams imgllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		imgllp.setMargins(10, 10, 10, 0);
		img.setImageDrawable(getResources().getDrawable((R.drawable.loading)));
		lin.addView(img,imgllp);
		String url="http://restoreserve.3eeweb.com/pics/"+name+".jpg";
		new LoadImage(img,url).execute();
		
		TextView tvname=new TextView(this);
		tvname.setText(name);
		tvname.setGravity(Gravity.CENTER);
		tvname.setAllCaps(true);
		tvname.setTextSize(20);
		tvname.setTextColor(getResources().getColor(R.color.dark_blue));
		lin.addView(tvname);
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		TextView tvcuisine=new TextView(this);
		tvcuisine.setText("Principal Cuisine: "+cuisine);
	    tvcuisine.setLayoutParams(llp);
	    tvcuisine.setGravity(Gravity.CENTER);
		
		TextView tvcost=new TextView(this);
		tvcost.setText("Cost for 2: Rs. "+cost);
		tvcost.setLayoutParams(llp);
		tvcost.setGravity(Gravity.CENTER);
		
		lin.addView(tvcuisine);
		lin.addView(tvcost);
		
		LinearLayout.LayoutParams lpbutton = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		Button bdetails=new Button(this);
		bdetails.setText("View more details");
		bdetails.setLayoutParams(lpbutton);
		bdetails.setGravity(Gravity.CENTER);
		
		Button bbook=new Button(this);
		bbook.setText("Book a table");
		lpbutton.setMargins(10, 10, 10, 10);
		bbook.setLayoutParams(lpbutton);
		bbook.setGravity(Gravity.CENTER);
		bbook.setBackgroundResource(R.drawable.button_shape);
		bbook.setTextColor(Color.WHITE);
		
		final int rid=Integer.parseInt(id);
		final String rname=name;
		bbook.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(HomeScreen.this, Book.class);
				//i.putExtra("res_id", rid);
				i.putExtra("res_name", rname);
				i.putExtra("res_id",rid);
				//Toast.makeText(getApplicationContext(), rid+"", Toast.LENGTH_SHORT).show();
				startActivity(i);
			}
			
		});
		
		//lin.addView(bdetails);
		lin.addView(bbook);
		
		View v = new View(this);
        v.setBackgroundColor(Color.BLACK);
        LayoutParams lp = new LayoutParams(
            LayoutParams.MATCH_PARENT,
            2
        );
        v.setLayoutParams(lp);
		
		//line.setLayoutParams(params);
		
		parent.addView(lin,linllp);
		parent.addView(v);
	}
	
	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		Bitmap bitmap;
		ImageView image;
		String urlink;
		protected LoadImage(ImageView img, String url)
		{
			image=img;
			urlink=url;
		}
	    @Override
	       protected Bitmap doInBackground(String... args) {
	         try {
	               bitmap = BitmapFactory.decodeStream((InputStream)new URL(urlink).getContent());
	        } catch (Exception e) {
	              e.printStackTrace();
	        }
	      return bitmap;
	       }
	       protected void onPostExecute(Bitmap bitmapimage) {
	         if(image != null){
	           image.setImageBitmap(bitmapimage);
	         }else{
	         }
	       }
	   }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen, menu);
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
		if(id==R.id.action_logout)
		{
			MenuOptions.logout(HomeScreen.this);
		}
		return super.onOptionsItemSelected(item);
	}
	
	class task extends AsyncTask<String, String, Void>
	{
	
	HttpResponse response;
	String responseString;
	private ProgressDialog progressDialog = new ProgressDialog(HomeScreen.this); //Edit
	
    protected void onPreExecute() {
       progressDialog.setMessage("Getting restaurants based on search word..."); //Edit
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
		   		if(true)
			   	{	   		
		   		HttpParams httpParameters = new BasicHttpParams(); //When no internet conn, we need this
		   		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		   		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		   		 
		   		HttpClient httpclient = new DefaultHttpClient(httpParameters); //When no internet conn, need to timeout request
		   		HttpPost httppost = new HttpPost("http://restoreserve.3eeweb.com/search.php");
		   		 
		   		try {
		   			// Add your data
		   		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		   			nameValuePairs.add(new BasicNameValuePair("keyword", keyword));

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
			progressDialog.dismiss();
			if(responseString.contains("Successful"))
			{
			Toast.makeText(getApplicationContext(), "Successfully retrieved",Toast.LENGTH_LONG).show();
			responseString=responseString.substring(responseString.indexOf("Successful"),responseString.length());
			responseString=responseString.replace("Successful","");
			
			Intent in=new Intent(HomeScreen.this,HomeScreen.class);
			in.putExtra("cuisines", responseString);			
			in.putExtra("home", false);
			startActivity(in);
			if(home==false)
				finish();
			}
			else if(responseString.contains("Not found"))
			{
				Toast.makeText(getApplicationContext(), "Not found",Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Something went wrong!"+responseString,Toast.LENGTH_LONG).show();
			}
		}
	}

	
}

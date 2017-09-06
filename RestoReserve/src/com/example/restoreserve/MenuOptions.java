package com.example.restoreserve;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;

public class MenuOptions extends ActionBarActivity{
	public static void logout(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences("LOGIN", 0);
		preferences.edit().remove("loggedin").commit();
		preferences.edit().remove("user_id").commit();
		Intent i=new Intent(context,SignIn.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
		((Activity)context).finish();
	}
}

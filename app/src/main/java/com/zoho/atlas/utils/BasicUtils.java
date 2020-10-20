package com.zoho.atlas.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class BasicUtils 
{	
	private Context m_cContext;

   	public BasicUtils(Context pContext){
   		
		m_cContext = pContext;
	}

	public boolean isNetAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) m_cContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			for (NetworkInfo networkInfo : info) {
				if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void showSoftKeyboard(EditText edittext, boolean isForced)
	{
		int flag;
		if(isForced)
			flag = InputMethodManager.SHOW_FORCED;
		else
			flag = InputMethodManager.SHOW_IMPLICIT;

 		InputMethodManager imm = (InputMethodManager)m_cContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		Objects.requireNonNull(imm).showSoftInput(edittext, flag);
	}
	
	public void hideSoftKeyboard(View view)
	{
 		InputMethodManager imm = (InputMethodManager)m_cContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
    
	public long getTimestamp(String format, String formattedDateTime) {
		try {
			@SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(format);
			return Objects.requireNonNull(formatter.parse(formattedDateTime)).getTime();
		} catch (ParseException e) {
			Log.e("ParseException", " : "+e.toString());
			e.printStackTrace();
		}
		return new Date().getTime();
	}

	public String getFormattedDate(String format, long timestamp)
	{
		@SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(timestamp);
	}
}

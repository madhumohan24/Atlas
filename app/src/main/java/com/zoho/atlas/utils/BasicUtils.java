package com.zoho.atlas.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BasicUtils 
{	
	private final Context m_cContext;

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

}

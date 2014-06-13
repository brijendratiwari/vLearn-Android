package com.vlearn.android.viedo.savevideo.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UploadBroadCast extends BroadcastReceiver{

	public static final String ONVIDEOUPLOAD = "com.vlearn.android.videoUpload";

	private ONVideoSaveListener listener;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		if(intent.getAction().equals(ONVIDEOUPLOAD)){
			String dbId = intent.getStringExtra("dbid");
			String id = intent.getStringExtra("id");
			if(listener!=null){
				listener.onVideoUpload(dbId,id);
			}
		}
	}
	
	public void setVideoSaveListener(ONVideoSaveListener listener) {
		// TODO Auto-generated method stub
		this.listener = listener;
	}

}

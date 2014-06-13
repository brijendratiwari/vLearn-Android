package com.vlearn.android.myCommunity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class MyCommunity extends Fragment implements OnNetworkResult {

	HomeActivity activity;
	Context context;
	ListView listView;
	List<MyCommunityCollection> list = new ArrayList<>();
	MyCommunityAdapter adapter;

	ImageLoader loader;
	VUtil util;
	NetworkConnection connection;
	AsyncTask<String, Void, String> task;

	public static MyCommunity Create() {
		MyCommunity community = new MyCommunity();
		return community;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		util = activity.getVUtil();
		connection = activity.getNetworkConnection();
		loader = new ImageLoader(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.mycommunity, null, false);
		listView = (ListView) view.findViewById(R.id.listView);

		settingListView();

		return view;
	}

	List<NameValuePair> al = new ArrayList<>();

	private void settingListView(){
	
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("app_type", "video"));
		al.add(new BasicNameValuePair("app_name", "vlearn"));
		al.add(new BasicNameValuePair("userId", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ID)));
		
		task = connection.getData("user/getAllRatings", "POST", al, MyCommunity.this, MyCommunityCalloing);

		adapter = new MyCommunityAdapter(context, list, loader);
		listView.setAdapter(adapter);
		
	}



	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Thread() {

			public void run() {

				activity.runOnUiThread(new Runnable() {
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						activity.setHeader(true,
								R.drawable.button_selector_round_green, 0,
								context.getString(R.string.back), null,
								context.getString(R.string.mycommunity), null,
								0);
					}
				});
			}
		}.start();

	}

	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case MyCommunityCalloing:
			LoadCommunity(result);
			break;

		default:
			break;
		}
	}

	private void LoadCommunity(String result) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = null;
		int length = 0;
		JSONArray rating_info;
		JSONObject rating_info_Object;
		int ratingLength;
		
		try{
			jsonArray = new JSONArray(result);
			length = jsonArray.length();
			if(length == 0){
				Toast.makeText(context, context.getString(R.string.No_Community), Toast.LENGTH_LONG).show();
				return;
			}else if(length>0){
				list.clear();
				for(int i=0;i<length;i++){
					JSONObject obj = jsonArray.getJSONObject(i);
					String description = "\""+obj.getString("description");
					rating_info = obj.getJSONArray("rating_info");
					ratingLength = rating_info.length();
					if(ratingLength>0){
						for(int j=0;j<ratingLength;j++){
							
							rating_info_Object = rating_info.getJSONObject(j);
							String feedback = rating_info_Object.getString("feedback");
							feedback = description+feedback;
							String profilePic = rating_info_Object.getString("avatar_url");
							String videoPic = rating_info_Object.getString("video_thumbnail");
							String fullname = rating_info_Object.getString("fullname");
							String access = rating_info_Object.getString("access");
							fullname = "By "+fullname+" - "+access;
							int rating = Integer.parseInt(rating_info_Object.getString("rating"));
							list.add(new MyCommunityCollection(fullname, feedback, profilePic, videoPic, rating));
						}
					}
				}
				adapter.notifyDataSetChanged();
				listView.invalidate();
			}
			
		}catch(JSONException e){
			e.printStackTrace();
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case MyCommunityCalloing:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadCommunity), Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
}

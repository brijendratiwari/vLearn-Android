package com.vlearn.android.setting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.setting.mykid.Mykid;
import com.vlearn.android.setting.mysetting.MySetting;
import com.vlearn.android.setting.myvlearn.MyVlearn;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Fragment implements OnNetworkResult{

	HomeActivity activity;
	Context context;
	ListView listView;
	LinearLayout linearlayout;
	SettingsAdapter_1 adapter;
	Button mySetting, myvlear, mykid;
	TextView userName,shortBio;
	ImageView profilePicture;
	ImageLoader loader;
	VUtil util;
	NetworkConnection connection;
	AsyncTask<String, Void, String> task;
	List<SettingsCollection> list = new ArrayList<>();

	String user;
	String pass;
	String userId; 
	boolean isKid;
	boolean ispadrino;
	
	
	String v_bio,v_userpic,v_name,v_id;
	
	public static Settings Create() {
		return Create("","","","");
	}
	
	public static Settings Create(String v_bio,String v_userpic,String v_name,String v_id) {
		Settings settings = new Settings();
		Bundle bundle = new Bundle();
		bundle.putString("v_bio", v_bio);
		bundle.putString("v_userpic", v_userpic);
		bundle.putString("v_name", v_name);
		bundle.putString("v_id", v_id);
		settings.setArguments(bundle);
		return settings;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		loader = new ImageLoader(context);
		util = activity.getVUtil();
		connection = activity.getNetworkConnection();
		
		Bundle bundle = getArguments();
		if(bundle!=null){
			if(bundle.containsKey("v_bio")) v_bio = bundle.getString("v_bio");
			if(bundle.containsKey("v_userpic")) v_userpic = bundle.getString("v_userpic");
			if(bundle.containsKey("v_name")) v_name = bundle.getString("v_name");
			if(bundle.containsKey("v_id")) v_id = bundle.getString("v_id");
		}
		
		user = (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME);
		pass = (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD);
		
		isKid = ((String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ACCESS)).contains("student");
		ispadrino = ((String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ACCESS)).contains("padrino");
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.settings, null, false);

		profilePicture = (ImageView) view.findViewById(R.id.profilePic);
		userName = (TextView) view.findViewById(R.id.userName);
		shortBio = (TextView) view.findViewById(R.id.shortBio);
		listView = (ListView) view.findViewById(R.id.listview);
		linearlayout = (LinearLayout) view.findViewById(R.id.linearlayout);
		
		String userNameString = "";
		String userProfile = "";
		
		if(!v_id.isEmpty()){
			linearlayout.setVisibility(View.GONE);
			shortBio.setVisibility(View.VISIBLE);
			userProfile = v_userpic;
			userNameString = v_name;
			userId = v_id;
			shortBio.setText(v_bio);
		}else{
			mySetting = (Button) view.findViewById(R.id.mysetting);
			myvlear = (Button) view.findViewById(R.id.myVlearn);
			mykid = (Button) view.findViewById(R.id.mykid);
	        shortBio.setVisibility(view.GONE);
	        if(isKid || ispadrino) {
				mykid.setVisibility(View.GONE);
				mySetting.setText("Me");
			}
	        
	        userNameString = (String) util.getFromSharedPreference(
					VariableType.STRING, VUtil.USER_FIRSTNAME)+" "+(String) util.getFromSharedPreference(
							VariableType.STRING, VUtil.USER_LASTNAME);
			userProfile = (String) util.getFromSharedPreference(
					VariableType.STRING, VUtil.USER_AVTAAR);
			registerOnClick();
			userId = (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ID);
		}
		
		userName.setText(userNameString);
		loader.DisplayImage(userProfile, profilePicture);
		settingView();
		return view;
	}

	private void registerOnClick() {
		// TODO Auto-generated method stub

		mySetting.setOnClickListener(listener);
		myvlear.setOnClickListener(listener);
		mykid.setOnClickListener(listener);

	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.mysetting:
				activity.changePage(MySetting.Create());
				break;
			case R.id.myVlearn:
				activity.changePage(MyVlearn.Create());
				break;
			case R.id.mykid:
				activity.changePage(Mykid.Create());
				break;

			default:
				break;
			}
		}
	};

	List<NameValuePair> al = new ArrayList<>();
	private void settingView() {
		// TODO Auto-generated method stub

		list.clear();
		adapter = new SettingsAdapter_1(context, list, loader);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState != SCROLL_STATE_IDLE){
					if(view.getFirstVisiblePosition() > adapter.currentRunningviewId || view.getLastVisiblePosition()< adapter.currentRunningviewId){
//						adapter.stopPreviousvideo(null, null, null);
						adapter.stopPreviousvideo();
					}  
				}
			} 

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
			
		});
		util.showLoading(""+context.getString(R.string.please_wait));
		al.clear();
		al.add(new BasicNameValuePair("user", user));
		al.add(new BasicNameValuePair("pass", pass));
		al.add(new BasicNameValuePair("userId", userId));
		al.add(new BasicNameValuePair("app_type", "video"));
		al.add(new BasicNameValuePair("app_name", "vlearn"));
		if(v_id.isEmpty()){
			al.add(new BasicNameValuePair("approval", "3,4"));
		}
		task = connection.getData("user/searchItems", "POST", al, Settings.this, SettingVideoCalling);
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String title = "";
		if(v_id.isEmpty()){
			title = "ME";
		}else{
			title = "My Community";
		}
		activity.setHeader(true, R.drawable.button_selector_round_blue, 0,
				context.getString(R.string.back), null, title, null, 0);
	}

	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case SettingVideoCalling:
			loadSettingVideos(result);
			break;

		default:
			break;
		}
	}

	@SuppressWarnings("unused")
	private void loadSettingVideos(String result) {
		// TODO Auto-generated method stub
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		JSONObject videoObject = null;
		int length = 0;
		
		String id;
		String title;
		String url;
		String filename;
		String thumbnail;
		String username;
		String fullname;
		String description;
		String bio;
		String userId;
		String timestamp;
		String approval;
		JSONArray kids_assigned;
		String avatar;
		int avg_rating;
		String total_comments;
		JSONArray user_rating;
		int revieCount = 0;
		
		try{
			
			jsonObject = new JSONObject(result);
			jsonArray = jsonObject.getJSONArray("videos");
			length = jsonArray.length();
			
			if(length<=0){
				Toast.makeText(context, ""+context.getString(R.string.noVideoFound), Toast.LENGTH_SHORT).show();
				util.hideLoading();
				return;
			}
			
			if(length>0){
				list.clear();
				for(int i=0;i<length;i++){
					videoObject = jsonArray.getJSONObject(i);
					
					id = videoObject.getString("id");
					title = videoObject.getString("title");
					url = videoObject.getString("url");
					filename = videoObject.getString("filename");
					thumbnail = videoObject.getString("thumbnail");
					username = videoObject.getString("username");
					fullname = videoObject.getString("fullname");
					description = videoObject.getString("description");
					bio = videoObject.getString("bio");
					userId = videoObject.getString("userId");
					timestamp = videoObject.getString("timestamp");
					approval = videoObject.getString("approval");
					kids_assigned = videoObject.getJSONArray("kids_assigned");
					avatar = videoObject.getString("avatar");
					avg_rating = Integer.parseInt(videoObject.getString("avg_rating"));
					total_comments = videoObject.getString("total_comments");
					user_rating = videoObject.getJSONArray("user_rating");
					revieCount = user_rating.length();
					description.replaceAll("null |#null | null| #null", "");
					list.add(new SettingsCollection(title, description, thumbnail, url, revieCount, avg_rating));
				} 
				adapter.notifyDataSetChanged();
				listView.invalidate();
				util.hideLoading();
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			util.hideLoading();
		}
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case SettingVideoCalling:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToYourVideos), Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
}

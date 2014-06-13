package com.vlearn.android.viedolist;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class VideoList extends Fragment{

	HomeActivity activity;
	Context context;
	String tagTitle = null;
	ListView listview;
	VideoListAdapter_1 adapter;
	List<VideoListCollection> list = new ArrayList<>();
	
	public static int HASHTAG = 0;
	public static int GRADELIST = 1;
	public static int ASSIGNLIST = 2;
	
	private int resultType = -1;
	private JSONArray data = null;
	
	int length = 0;
	
	public boolean isFirst = false;
	public static boolean isActiviate = false;
	public static JSONArray kidAssign= null;
	public static int currentPostion;
	
	NetworkConnection connection;
	ImageLoader loader;
	VUtil util;
	private boolean isKid;
	
	public static VideoList Create(String tagTitle,String data,int resultType){
		VideoList videoList = new VideoList();
		Bundle bundle = new Bundle();
		bundle.putString("tagtitle" , tagTitle);
		bundle.putString("data" , data);
		bundle.putInt("resultType" , resultType);
		videoList.setArguments(bundle);
		return videoList;
	}
	          
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		
		isFirst = true;
		currentPostion = 0;
		isActiviate = false;
		
		util = activity.getVUtil();
		loader = activity.getImageLoader();
		connection = activity.getNetworkConnection();
		isKid = ((String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ACCESS)).contains("student");
		Bundle bundle = getArguments();
		if(bundle != null){
			try{
				resultType = bundle.getInt("resultType");
				data = new JSONArray(bundle.getString("data"));
				tagTitle = bundle.getString("tagtitle");
				length = data.length();
				bundle = null;
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}else{
			return;
		}
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		listview = (ListView) inflater.inflate(R.layout.video_list, null, false);
		list.clear();
		adapter = new VideoListAdapter_1(list, context, loader);
		listview.setAdapter(adapter);
		listview.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState != SCROLL_STATE_IDLE){
					if(view.getFirstVisiblePosition() > adapter.currentRunningviewId || view.getLastVisiblePosition()< adapter.currentRunningviewId){
						adapter.stopPreviousvideo();
//						adapter.stopPreviousvideo(null,null,null);
					} 
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
			
		});
		//no need to refresh so call in on create view otherwise we sholud to call in on resume
		loadVideo();
		
		return listview;
	}
	

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(resultType == ASSIGNLIST){
			activity.setHeader(false, 0, 0, null, null, null , null, 0);
		}else{
			activity.setHeader(true, R.drawable.button_selector_round_blue, 0, context.getString(R.string.back), null, tagTitle , null, 0);
		}
		if(isFirst){
			isFirst =  false;
		}else{
			adapter.getItem(currentPostion).isFlagActivate  = isActiviate;
			if(kidAssign!=null){
				adapter.getItem(currentPostion).kids_assigned  = kidAssign;
				kidAssign = null;
			}
			adapter.notifyDataSetChanged();
			listview.invalidate();
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(adapter!=null) adapter.stopPreviousvideo();
	}
	
	private void loadVideo(){
		
		if(data == null) return;
		
		String id = "";
		String title = "";
		String url = "";
		String filename = "";
		String thumbnail = "";
		String username = "";
		String fullname = "";
		String description = "";
		String bio = "";
		String userId = "";
		String timestamp = "";
		String approval = "";
		String avatar = "";
		String avg_rating = "";
		String total_comments = "";
		String hashTag = "";
		JSONArray kids_assigned = null;
		JSONArray user_rating = null;
		JSONObject object;
		
		try{
			if(length>0) list.clear();
			for(int i=0;i<length;i++){
				object = data.getJSONObject(i);
				
				id = object.has("id")?object.getString("id"):"";
				title = object.has("title")?object.getString("title"):"";
				url = object.has("url")?object.getString("url"):"";
				filename = object.has("filename")?object.getString("filename"):"";
				thumbnail = object.has("thumbnail")?object.getString("thumbnail"):"";
				username = object.has("username")?object.getString("username"):"";
				fullname = object.has("fullname")?object.getString("fullname"):"";
				description = object.has("description")?object.getString("description"):"";
				bio = object.has("bio")?object.getString("bio"):"";
				userId = object.has("userId")?object.getString("userId"):"";
				avatar = object.has("avatar")?object.getString("avatar"):"";
				avg_rating = object.has("avg_rating")?object.getString("avg_rating"):"";
				hashTag = object.has("hashTag")?object.getString("hashTag"):"";
				total_comments = object.has("total_comments")?object.getString("total_comments"):"";
				try{
				kids_assigned = object.getJSONArray("kids_assigned");
				}catch (Exception e) {
					// TODO: handle exception 
					e.printStackTrace();
					kids_assigned = new JSONArray();
				}
				user_rating = object.getJSONArray("user_rating");
				list.add(new VideoListCollection(id, title, url, filename, thumbnail, username, fullname, description, bio, userId, timestamp, approval, avatar, avg_rating, total_comments, kids_assigned, user_rating, false, hashTag));
			}
			adapter.notifyDataSetChanged();
			listview.invalidate();
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	
}

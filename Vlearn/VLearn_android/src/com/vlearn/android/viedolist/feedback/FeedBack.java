package com.vlearn.android.viedolist.feedback;

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
import com.vlearn.android.myCommunity.MyCommunityAdapter;
import com.vlearn.android.myCommunity.MyCommunityCollection;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

public class FeedBack extends Fragment implements OnNetworkResult {

	EditText editText;
	Button submitButton;
	RatingBar bar;
	ListView listview;

	HomeActivity activity;
	Context context;
	
	NetworkConnection connection;
	VUtil util;
	ImageLoader loader;
	List<MyCommunityCollection> list = new ArrayList<>();
	MyCommunityAdapter adapter;

	
	AsyncTask<String, Void, String> task;
	String videoId,videoUrl;
	
	public static FeedBack Create(String videoId,String videoUrl) {
		FeedBack feedBack = new FeedBack();
		Bundle bun = new Bundle();
		bun.putString("videoId", videoId);
		bun.putString("videoUrl", videoUrl);
		feedBack.setArguments(bun);
		return feedBack;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		videoId = getArguments().getString("videoId");
		videoUrl = getArguments().getString("videoUrl");
		loader = activity.getImageLoader();
		if(videoId == null) videoId = "";
		util = activity.getVUtil();
		connection = activity.getNetworkConnection();
		 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.feedback, null, false);
		
		editText = (EditText) view.findViewById(R.id.feedbackText);
		submitButton = (Button) view.findViewById(R.id.submitButton);
		bar = (RatingBar) view.findViewById(R.id.ratingbar);
		listview = (ListView) view.findViewById(R.id.listview);

		listview.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		submitButton.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String feedbackText = "";
			String id = "";
			int rating = 0;
			
			feedbackText = editText.getText().toString().trim();
			rating = (int) bar.getRating();
			id = videoId;
			
			SubmitFeedBack(feedbackText,id,rating);
			}
		});
		
		return view;
	}
	
	
	
	List<NameValuePair> al = new ArrayList<>();

	private void SubmitFeedBack(String feedbackText,String id,int rating){
	
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("app_type", "video"));
		al.add(new BasicNameValuePair("app_name", "vlearn"));
		al.add(new BasicNameValuePair("feedback", feedbackText));
		al.add(new BasicNameValuePair("rating", ""+rating));
		al.add(new BasicNameValuePair("categoryId", id));
		
		task = connection.getData("user/addRating", "POST", al, FeedBack.this, submitFeedBack);

	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadFeedBack(videoId);
	}

	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case submitFeedBack:
			onFeedBackSubmit(result);
			break;
		case loadFeedBack:
			OnLoadFeedBack(result);
			break;
		default:
			break;
		}
	}
	
	private void loadFeedBack(String id){
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("app_type", "video"));
		al.add(new BasicNameValuePair("app_name", "vlearn"));
		al.add(new BasicNameValuePair("setId", "v_"+id));
		
		task = connection.getData("user/getRating", "POST", al, FeedBack.this, loadFeedBack);
		
		list.clear();
		adapter = new MyCommunityAdapter(context, list, loader);
		listview.setAdapter(adapter);
	}
	
	private void OnLoadFeedBack(String result){
		JSONArray jsonArray = null;
		JSONObject object = null;
		String uid = "";
		String username = "";
		String access = "";
		String fullname = "";
		String img = "";
		String rating = "";
		String feedback = "";
		String avatar_url = "";
		String rating_url = "";
		String total_comments = "";
		String avg_rating = "";
		String total_ebooks = "";
		String access_name = "";
		String user_url = "";
		int length = 0;

		try{
			jsonArray = new JSONArray(result);
			length = jsonArray.length();
			if(length <= 0) return;
			list.clear();
			for(int i=0;i<length;i++){
				object = jsonArray.getJSONObject(i);
				uid = object.getString("uid");
				username = object.getString("username");
				access = object.getString("access");
				fullname = object.getString("fullname");
				img = object.getString("img");
				rating = object.getString("rating");
				feedback = object.getString("feedback");
				avatar_url = object.getString("avatar_url");
				rating_url = object.getString("rating_url");
				total_comments = object.getString("total_comments");
				avg_rating = object.getString("avg_rating");
				total_ebooks = object.getString("total_ebooks");
				access_name = object.getString("access_name");
				user_url = object.getString("user_url");
				
				list.add(new MyCommunityCollection("By "+fullname+" - "+access, feedback, avatar_url, videoUrl, Integer.parseInt(avg_rating)));
			}
			
			adapter.notifyDataSetChanged();
			listview.invalidate();
		}catch (JSONException e) {
			// TODO: handle exception
		}
	}

	private void onFeedBackSubmit(String result) {
		// TODO Auto-generated method stub
		boolean error = false;
		String errorMsg = "";
		String status = "";
		JSONObject object = null;
		try{
			object = new JSONObject(result);
			
			error = object.getBoolean("errorMsg");
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");
			
			if(!error){
				Toast.makeText(context, context.getString(R.string.feedbackImportant), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
			}
			
		}catch (JSONException e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case submitFeedBack:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToSubmitFeedBack), Toast.LENGTH_SHORT).show();
			break;
		case loadFeedBack:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadFeedBack), Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		
	}
}

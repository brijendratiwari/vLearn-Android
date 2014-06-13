package com.vlearn.android.homepage;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.vlearn.android.R;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.setting.myvlearn.MyVlearn;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;
import com.vlearn.android.viedolist.VideoList;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class HomePage extends Fragment implements OnNetworkResult{

	GridView gridView;
	View treaingView;
	Button all_assignments;
	TextView trending1,trending2,trending3,trending4,carrerVlearning;
	SearchView searchView;
	HomePageAdapter adapter;
	HomeActivity activity;
	Context context;
	
	JSONArray data;
	int listType = -1;
	String title = "";
	
	VUtil util;
	NetworkConnection connection;
	AsyncTask<String, Void, String> task;
	
	List<NameValuePair> al;
	
	String user = "";
	String pass = "";
	
	boolean isKid;
	
	public static HomePage Create() {
		HomePage fragment = new HomePage();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity = (HomeActivity) (context = getActivity());
		util = activity.getVUtil();
		connection = activity.getNetworkConnection();
		al = new ArrayList<>();
		isKid = ((String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ACCESS)).contains("student");
		user = (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME);
		pass = (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.home_page, null, false);
		
		carrerVlearning = (TextView) view.findViewById(R.id.career_vlearn);
		searchView	= (SearchView)view .findViewById(R.id.searchView1);
		gridView 	= (GridView)  view.findViewById(R.id.gridiew_class_section);
		trending1	= (TextView)  view.findViewById(R.id.trending1);
		trending2	= (TextView)  view.findViewById(R.id.trending2);
		trending3	= (TextView)  view.findViewById(R.id.trending3);
		trending4	= (TextView)  view.findViewById(R.id.trending4);
		all_assignments = (Button) view.findViewById(R.id.all_assignments);
		
//		searchView.setBackgroundResource(R.drawable.edit_text_bg);
		
		all_assignments.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//activity.changePage(VideoList.Create("kid",(String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_VIDEOS),listType));
				util.showLoading();
				al.clear();
				al.add(new BasicNameValuePair("user", (String) util
						.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
				al.add(new BasicNameValuePair("pass", (String) util
						.getFromSharedPreference(VariableType.STRING,
								VUtil.USER_PASSWORD)));
				al.add(new BasicNameValuePair("app_type", "video"));
				al.add(new BasicNameValuePair("app_name", "vlearn"));
				task = connection.getData("user/searchAssignments", "POST", al,
						HomePage.this, loadAssignments);
			}
		});
		
		treaingView = view.findViewById(R.id.treaingView);
		//setting gridview 
		initGridView();
		if(isKid){
			all_assignments.setVisibility(View.VISIBLE);
			treaingView.setVisibility(View.GONE);
		}else{
			all_assignments.setVisibility(View.GONE);
			treaingView.setVisibility(View.VISIBLE);
		}
		//init on item click listener on gridview & other
		onRegisterClickListerner();
		
		return view;
	}
	
	
	View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			TextView view = (TextView) v;
//			loadVideoList(view.getText().toString().trim());
			if(view.getText().toString().equalsIgnoreCase("Career vLearns")){
				loadGradeandCareerwithSearch(getGradeId("carrer"),"");
			}else{
				loadHashVideos(view.getText().toString().trim());
			}
		}
	};
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadHashTag();
		activity.setHeader(false, 0, 0, null, null, null, null, 0);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	private void initGridView() {
		// TODO Auto-generated method stub
		List<HomePageCollection> list = new ArrayList<>();
		
		list.add(new HomePageCollection("PreK", false));
		list.add(new HomePageCollection("K", false));
		list.add(new HomePageCollection("1st", true));
		list.add(new HomePageCollection("2nd", true));
		list.add(new HomePageCollection("3rd", true));
		list.add(new HomePageCollection("4th", true));
		list.add(new HomePageCollection("5th", true));
		list.add(new HomePageCollection("6th", true));
		list.add(new HomePageCollection("7th", true));
		list.add(new HomePageCollection("8th", true));
		list.add(new HomePageCollection("9th", true));
		list.add(new HomePageCollection("10th", true));
		list.add(new HomePageCollection("11th", true));
		list.add(new HomePageCollection("12th", true));
		list.add(new HomePageCollection("Parent", false));
		list.add(new HomePageCollection("Teacher", false));
		
		adapter = new HomePageAdapter(context, R.layout.home_grid_row, list);
		
		gridView.setAdapter(adapter);
		
	}

	private void onRegisterClickListerner() {
		// TODO Auto-generated method stub

		//textview clicklitener
		trending1.setOnClickListener(listener);
		trending2.setOnClickListener(listener);
		trending3.setOnClickListener(listener);
		trending4.setOnClickListener(listener);
		carrerVlearning.setOnClickListener(listener);
		
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

		    @Override
		    public boolean onQueryTextSubmit(String query) {
		    	loadGradeandCareerwithSearch("",query);
		        return false;
		    }

		    @Override
		    public boolean onQueryTextChange(String newText) {
		        // TODO Auto-generated method stub
		        return false;
		    }
		});
		
		//gridview item click
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HomePageCollection collection = (HomePageCollection) parent.getAdapter().getItem(position);
				loadGradeandCareerwithSearch(getGradeId(collection.title),"");
			}
		});
		
	}

	private void loadHashTag(){ 
		al.clear();
		al.add(new BasicNameValuePair("user", user));
		al.add(new BasicNameValuePair("pass", pass));
		al.add(new BasicNameValuePair("app_name", "vlearn"));
		al.add(new BasicNameValuePair("app_type", "video"));
		task = connection.getData("user/getTopTrendingHashtags", "POST", al, HomePage.this, loadHashTag);
	}
	
	private void loadGradeandCareerwithSearch(String id,String keywords){
		util.showLoading(context.getString(R.string.loadingvideos));
		al.clear();
		al.add(new BasicNameValuePair("user", user));
		al.add(new BasicNameValuePair("pass", pass));
		al.add(new BasicNameValuePair("app_name", "vlearn"));
		al.add(new BasicNameValuePair("app_type", "video"));
		al.add(new BasicNameValuePair("keyword", keywords));
		if(id.equalsIgnoreCase("carrer"))
			al.add(new BasicNameValuePair("career", "1"));
		else
			al.add(new BasicNameValuePair("grade", id));
		task = connection.getData("user/searchItems", "POST", al, HomePage.this, loadGradeVideo);
	}
	
		private String getGradeId(String name){
		
		if(name.equalsIgnoreCase("PreK")){
			if(VUtil.IS_APP_LANG_ENG) title = "PreK  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" PreK ";  
			return "2";
		}else if(name.equalsIgnoreCase("K")){
			if(VUtil.IS_APP_LANG_ENG) title = "K  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" K ";
			return "4";
		}else if(name.equalsIgnoreCase("1st")){
			if(VUtil.IS_APP_LANG_ENG) title = "1st  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" 1st ";
			return "5";
		}else if(name.equalsIgnoreCase("2nd")){
			if(VUtil.IS_APP_LANG_ENG) title = "2nd  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" 2nd ";
			return "6";
		}else if(name.equalsIgnoreCase("3rd")){
			if(VUtil.IS_APP_LANG_ENG) title = "3rd  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" 3rd ";
			return "7";
		}else if(name.equalsIgnoreCase("4th")){
			if(VUtil.IS_APP_LANG_ENG) title = "4th  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" 4th ";
			return "8";
		}else if(name.equalsIgnoreCase("5th")){
			if(VUtil.IS_APP_LANG_ENG) title = "5th  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" 5th ";
			return "9";
		}else if(name.equalsIgnoreCase("6th")){
			if(VUtil.IS_APP_LANG_ENG) title = "6th  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" 6th ";
			return "10";
		}else if(name.equalsIgnoreCase("7th")){
			if(VUtil.IS_APP_LANG_ENG) title = "7th  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" 7th ";
			return "11";
		}else if(name.equalsIgnoreCase("8th")){
			if(VUtil.IS_APP_LANG_ENG) title = "8th  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" 8th ";
			return "12";
		}else if(name.equalsIgnoreCase("9th")){
			if(VUtil.IS_APP_LANG_ENG) title = "9th  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" 9th ";
			return "13";
		}else if(name.equalsIgnoreCase("10th")){
			if(VUtil.IS_APP_LANG_ENG) title = "10th  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" 10th ";
			return "14";
		}else if(name.equalsIgnoreCase("11th")){
			if(VUtil.IS_APP_LANG_ENG) title = "11th  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" 11th ";
			return "15";
		}else if(name.equalsIgnoreCase("12th")){
			if(VUtil.IS_APP_LANG_ENG) title = "12th  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" 12th ";
			return "16";
		}else if(name.equalsIgnoreCase("Parent")){
			title = "Parent";
			return "24";
		}else if(name.equalsIgnoreCase("Teacher")){
			if(VUtil.IS_APP_LANG_ENG) title = "Teacher  "+context.getString(R.string.gradeString); else title = context.getString(R.string.gradeString)+" Teacher ";
			return "23";
		}else if(name.equalsIgnoreCase("carrer")){
			title = "carrer";
			return "carrer";
		}
		
		return "";
	}
				
	private void loadHashVideos(String HashTag){
		al.clear();
		al.add(new BasicNameValuePair("user", user));
		al.add(new BasicNameValuePair("pass", pass));
		al.add(new BasicNameValuePair("app_name", "vlearn"));
		al.add(new BasicNameValuePair("app_type", "video"));
		al.add(new BasicNameValuePair("hashtag", HashTag.substring(1)));
		util.showLoading(context.getString(R.string.loadingvideos));
		task = connection.getData("user/searchSetByHashtag", "POST", al, HomePage.this, loadHashVideo);
	}

	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case loadHashTag:
			setHashTag(result);
			break;
		case loadHashVideo:
			hashVideoHandler(result);
			break;
		case loadGradeVideo:
			gradVideHandler(result);
			break;
		case loadAssignments:
			onAssignment(result);
			break;
		default:
			break;
		}
	}

	private void onAssignment(String result) {
		// TODO Auto-generated method stub
		listType = VideoList.ASSIGNLIST;
		goToList(result);
	}

	private void hashVideoHandler(String result) {
		// TODO Auto-generated method stub
		listType = VideoList.HASHTAG;
		goToList(result);
	}

	private void gradVideHandler(String result) {
		// TODO Auto-generated method stub
		listType = VideoList.GRADELIST;
		goToList(result);
	}

	private void goToList(String result){
		JSONObject object = null;
		boolean error;
		String errorMsg;
		String status;
		int length = 0;
		try{
			
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");
			data = object.getJSONArray("videos");
			if(data.length() <= 0){
				new AlertDialog.Builder(context).setTitle("Sorry").setMessage("There are no videos for now. Come back soon to check again. Be the first one to share a video for this category. Just click on the camera roll on the top right and unleah your creativity!").setPositiveButton("OK", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
					
				}).create().show();
			}else
			activity.changePage(VideoList.Create(title,data.toString(),listType));
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void setHashTag(String result) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		boolean error = false;
		String errorMsg = "";
		String status = "";
		JSONArray hashtags = null;
		String hashtag = "";
		String count = "";
		int length = 0;
		
		try{
			
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");
			hashtags = object.getJSONArray("hashtags");
			length = hashtags.length();
			if(length <=0){
				return;
			}
			if(length>4) length=4;
			for(int i=0;i<length;i++){
				object = hashtags.getJSONObject(i);
				hashtag = "#"+object.getString("hashtag");
				count = object.getString("count");
				setTrending(i,hashtag);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
	}
	
	public void setTrending(int index,String text) {
		switch (index) {
		case 0:
			trending1.setText(text);
			break;
		case 1:
			trending2.setText(text);
			break;
		case 2:
			trending3.setText(text);
			break;
		case 3:
			trending4.setText(text);
			break;

		default:
			break;
		}
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case loadHashTag:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadHashTag), Toast.LENGTH_SHORT).show();
			break;
		case loadHashVideo:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadHashVideo), Toast.LENGTH_SHORT).show();
			break;
		case loadGradeVideo:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadGradVideo), Toast.LENGTH_SHORT).show();
			break;
		case loadAssignments:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadAssignmentVideo), Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	
}

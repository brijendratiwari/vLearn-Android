package com.vlearn.android.setting.myvlearn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vlearn.android.R;
import com.vlearn.android.database.VlearnDataBase;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;
import com.vlearn.android.viedo.savevideo.services.ONVideoSaveListener;
import com.vlearn.android.viedo.savevideo.services.UploadBroadCast;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class MyVlearn extends Fragment implements OnNetworkResult {

	HomeActivity activity;
	Context context;
	ListView listview;

	MyVlearnAdapter adapter;
	List<MyVlearnCollection> list = new ArrayList<MyVlearnCollection>();

	NetworkConnection connection;
	VUtil util;
	ImageLoader loader;

	UploadBroadCast uploadBoadCast;
	private boolean isKid;
	
	static MyVlearnCollection collectionBackUp;
	public static MyVlearn Create() {
		MyVlearn myVlearn = new MyVlearn();
		return myVlearn;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		connection = activity.getNetworkConnection();
		util = activity.getVUtil();
		util.initLoader(activity, "Loading");
		isKid = ((String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ACCESS)).contains("student");
		loader = activity.getImageLoader();
		initDataBase();
		
	}
	private ONVideoSaveListener listener = new ONVideoSaveListener() {
		
		@Override
		public void onVideoUpload(String dbId,String videoId) {
			// TODO Auto-generated method stub
			for(int i=0;i<list.size();i++){
				if(adapter.getItem(i).id.equals(dbId)){
					//adapter.getItem(i).id = videoId;
					adapter.getItem(i).isLocal = false;
					adapter.getItem(i).isDraft = false;
					adapter.getItem(i).language = "3";
					adapter.getItem(i).approval = "Under Review";
//					updateDB(dbId, videoId);
				}
			}
			adapter.notifyDataSetChanged();
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.myvlearn, null, false);
		listview = (ListView) view.findViewById(R.id.listview);
		list.clear();
		settingListView();
		if(isKid){
			initDialog();
		}
		
		loadLocalVideo();
		return view;
	}

	private void loadTeachers() {
		// TODO Auto-generated method stub
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util
				.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util
				.getFromSharedPreference(VariableType.STRING,
						VUtil.USER_PASSWORD)));
		task = connection.getData("user/getTeachers", "POST", al,
				MyVlearn.this, loadTeachers);
	}

	private void loadParents() {
		// TODO Auto-generated method stub
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util
				.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util
				.getFromSharedPreference(VariableType.STRING,
						VUtil.USER_PASSWORD)));
		task = connection.getData("user/getParents", "POST", al,
				MyVlearn.this, loadParents);
	}

	private void settingListView() {
		// TODO Auto-generated method stub

		adapter = new MyVlearnAdapter(context, list, loader,this);
		listview.setAdapter(adapter);
	}



	public void submitVideo(MyVlearnCollection col){
		if(col.isLocal)
			activity.doBindService(col.id);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		activity.setHeader(true, R.drawable.button_selector_round_blue, 0,
				context.getString(R.string.back), null,
				context.getString(R.string.myvlearn), null, 0);
	}

	List<NameValuePair> al = new ArrayList<>();
	private AsyncTask<String, Void, String> task;

	private void loadMyVideos(){
		util.showLoading("Loading...");
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("userId", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ID)));
		al.add(new BasicNameValuePair("app_type", "video"));
		al.add(new BasicNameValuePair("app_name", "vlearn"));
		al.add(new BasicNameValuePair("approval", "2,3,4"));
		task = connection.getData("user/searchItems", "POST", al, MyVlearn.this, loadMyVideo);
	}
	
	//List videoIdArray = new ArrayList<>();
	private void loadLocalVideo(){
		cursor = database.rawQuery("SELECT * FROM video_table", null);
		int length = cursor.getCount();
		if(length > 0){
		cursor.moveToFirst();
			int index = 0;
			
			String video_type = "";
			String video_language = "";
			String video_title = "";
			String video_thumb = "";
			String video_file = "";
			String career_id = "";
			String tell_us = "";
			String tell_us_id = "";
			String stage = "";
			String description = "";
			String grade = "";
			String grade_id = "";
			String subject = "";
			String subject_id = "";
			String domain = "";
			String domain_id = "";
			String standard = "";
			String standard_id = "";
			String skill = "";
			String skill_id = "";
			String video_status = "";
			String stageid = "";
			String video_user_id = "";
			String id = "";
			String video_server_id;
//			videoIdArray.clear();
			while(index<length){
				id = cursor.getString(cursor.getColumnIndex("id"));
				video_type = cursor.getString(cursor.getColumnIndex("video_type"));
				video_user_id = cursor.getString(cursor.getColumnIndex("video_user_id"));
				video_language = cursor.getString(cursor.getColumnIndex("video_language"));
				video_title = cursor.getString(cursor.getColumnIndex("video_title"));
				video_thumb = cursor.getString(cursor.getColumnIndex("video_thumb"));
				video_file = cursor.getString(cursor.getColumnIndex("video_file"));
				career_id = cursor.getString(cursor.getColumnIndex("career_id"));
				tell_us = cursor.getString(cursor.getColumnIndex("tell_us"));
				tell_us_id = cursor.getString(cursor.getColumnIndex("tell_us_id"));
				stage = cursor.getString(cursor.getColumnIndex("stage"));
				stageid = cursor.getString(cursor.getColumnIndex("stageid"));
				description = cursor.getString(cursor.getColumnIndex("description"));
				grade = cursor.getString(cursor.getColumnIndex("grade"));
				grade_id = cursor.getString(cursor.getColumnIndex("grade_id"));
				subject = cursor.getString(cursor.getColumnIndex("subject"));
				subject_id = cursor.getString(cursor.getColumnIndex("subject_id"));
				domain = cursor.getString(cursor.getColumnIndex("domain"));
				domain_id = cursor.getString(cursor.getColumnIndex("domain_id"));
				standard = cursor.getString(cursor.getColumnIndex("standard"));
				standard_id = cursor.getString(cursor.getColumnIndex("standard_id"));
				skill = cursor.getString(cursor.getColumnIndex("skill"));
				skill_id = cursor.getString(cursor.getColumnIndex("skill_id"));
				video_status = cursor.getString(cursor.getColumnIndex("video_status"));
				video_server_id = cursor.getString(cursor.getColumnIndex("video_server_id"));
				if(!new File(video_thumb).exists()) video_thumb = "";
				//if(new File(video_file).exists()) (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ID)
				if(video_user_id.equalsIgnoreCase((String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ID)) && new File(video_file).exists()){
					if(video_server_id.isEmpty())
						list.add(new MyVlearnCollection(domain,id, video_user_id, video_type.equalsIgnoreCase("curriculum")?"1":"0", career_id, tell_us_id, "", video_title, description, "", video_file, video_thumb, stageid, grade_id, subject_id, standard_id, standard_id, skill_id, "2", "", "-1", video_language.equalsIgnoreCase("english")?"0":"1", "0", "", "0", "0", "0", "0", "0", video_type.equalsIgnoreCase("curriculum")?1:0, true, true,domain_id,video_server_id));
					else
						list.add(new MyVlearnCollection(domain,id, video_user_id, video_type.equalsIgnoreCase("curriculum")?"1":"0", career_id, tell_us_id, "", video_title, description, "", video_file, video_thumb, stageid, grade_id, subject_id, standard_id, standard_id, skill_id, "2", "", "-1", video_language.equalsIgnoreCase("english")?"0":"1", "0", "", "0", "0", "0", "0", "0", video_type.equalsIgnoreCase("curriculum")?1:0, false, true,domain_id,video_server_id));
//					videoIdArray.add(video_server_id);
				}
				cursor.moveToNext();
				index ++;
			}
			adapter.notifyDataSetChanged();
		}
		
		loadMyVideos();
		
		
	}
	
	private void loadPendingVideo() {
		
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util
				.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util
				.getFromSharedPreference(VariableType.STRING,
						VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("item_type", "video"));
		
		task = connection.getData("user/getApprovalItems", "POST", al,
				MyVlearn.this, LoadPenduingVideo);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		uploadBoadCast = new UploadBroadCast();
		uploadBoadCast.setVideoSaveListener(listener);
		IntentFilter filter = new IntentFilter();
		filter.addAction(UploadBroadCast.ONVIDEOUPLOAD);
		activity.registerReceiver(uploadBoadCast, filter);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		try {
			activity.unregisterReceiver(uploadBoadCast);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		
		switch (callingId) {
		case LoadPenduingVideo:
			onLoadPendingVideo(result);
			break;
		case updateStatusApprove:
			util.hideLoading();
			onStatusUpdate(result);
			break;
		case loadTeachers:
			util.hideLoading();
			listTeacher = onTeacherLoad(result,1);
			if(ad != null)
				ad.notifyDataSetChanged(); 
			break;
		case loadParents:
			loadTeachers();
			listParnet= onTeacherLoad(result,0);
			if(ad != null)
				ad.notifyDataSetChanged();
			break;
		case loadMyVideo:
			onLoadMyVideo(result);
			break;
		case deleteVideo:
			util.hideLoading();
			onVideoDelete(result);
			break;
		default:
			break;
		}
	}
	
	private void onVideoDelete(String result) {
		// TODO Auto-generated method stub
		boolean error = false;
		JSONObject object = null;
		String errorMsg = "";
		try{
			
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			if(error){
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
			}else{
				removeVideo();
				Toast.makeText(context, ""+context.getString(R.string.videoDelted), Toast.LENGTH_SHORT).show();
			}
			
			
			
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void onLoadMyVideo(String result) {
		// TODO Auto-generated method stub
		JSONObject object;
		boolean error= false ;
		String errorMsg= "" ;
		String status= "" ;
		JSONArray videos= null ;
		String id= "" ;
		String title= "" ;
		String url= "" ;
		String filename= "" ;
		String thumbnail= "" ;
		String username= "" ;
		String fullname= "" ;
		String description= "" ;
		String bio= "" ;
		String userId= "" ;
		String timestamp= "" ;
		String approval= "" ;
		JSONArray kids_assigned= null ;
		String avatar= "" ;
		String avg_rating= "" ;
		String total_comments= "" ;
		JSONArray user_rating= null ;
		
		int length = 0;
		
		try{
			
			object = new JSONObject(result);
			
			if(object.has("error")) error = object.getBoolean("error");
			if(object.has("errorMsg")) errorMsg = object.getString("errorMsg");
			if(object.has("status")) status = object.getString("status");
			if(!error){
				if(object.has("videos")) videos = object.getJSONArray("videos");
				length = videos.length();
				for(int i=0;i<length;i++){
					object = videos.getJSONObject(i);
					if(object.has("id")) id = object.getString("id");
					if(object.has("title")) title = object.getString("title");
					if(object.has("url")) url = object.getString("url");
					if(object.has("filename")) filename = object.getString("filename");
					if(object.has("thumbnail")) thumbnail = object.getString("thumbnail");
					if(object.has("username")) username = object.getString("username");
					if(object.has("fullname")) fullname = object.getString("fullname");
					if(object.has("description")) description = object.getString("description");
					if(object.has("bio")) bio = object.getString("bio");
					if(object.has("userId")) userId = object.getString("userId");
					if(object.has("timestamp")) timestamp = object.getString("timestamp");
					if(object.has("approval")) approval = object.getString("approval");
					if(object.has("kids_assigned")) kids_assigned = object.getJSONArray("kids_assigned");
					if(object.has("avatar")) avatar = object.getString("avatar");
					if(object.has("avg_rating")) avg_rating = object.getString("avg_rating");
					if(object.has("total_comments")) total_comments = object.getString("total_comments");
					if(object.has("user_rating")) user_rating = object.getJSONArray("user_rating");
					list.add(new MyVlearnCollection("", id, userId, "0", "", "", description, title, description, "", url, thumbnail, "", "", "", "", "", "", approval, timestamp, "", "3", "", "", "", "", "", "", "", 0, false, false, "", ""));
				}
			}else{
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
				return;
			}
			adapter.notifyDataSetChanged();
			
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		loadPendingVideo();
	}
	List<ParentTeacherCollection> listParnet= new ArrayList<>();
	List<ParentTeacherCollection> listTeacher= new ArrayList<>();

	private ArrayList<ParentTeacherCollection> onTeacherLoad(String result,int videotype) {
		// TODO Auto-generated method stub
		ArrayList<ParentTeacherCollection> listData= new ArrayList<>();
		JSONObject object;
		String id;
		String first_name;
		String last_name;
		String username;
		String email;
		String avatar;
		boolean error;
		String errorMsg;
		String status;
		JSONArray list;
		int length = 0;
		try{
			
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			if(error){
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
				return listData;
			}
			status = object.getString("status");
			list = object.getJSONArray("list");
			length = list.length();
			if(length<=0){
				return listData;
			}
			
			for(int i=0;i<length;i++){
				object = list.getJSONObject(i);
				id = object.getString("id");
				first_name = object.getString("first_name");
				last_name = object.getString("last_name");
				username = object.getString("username");
				email = object.getString("email");
				avatar = object.getString("avatar");
				
				String projectType = videotype==0?"family":"class";
				
				
				listData.add(new ParentTeacherCollection(id, first_name, last_name, username, email, avatar ,projectType));
			}

		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return listData;
	}

	private void onStatusUpdate(String result) {
		// TODO Auto-generated method stub
		try{
		JSONObject object = new JSONObject(result);
		boolean error = object.getBoolean("error");
		if(!error){
			list.get(position).isDraft = false;
		}
		adapter.notifyDataSetChanged();
		}catch (JSONException e) {
			// TODO: handle exception
		}
	}
	private VlearnDataBase helper;
	private Cursor cursor;
	private SQLiteDatabase database;

	private void initDataBase() {

		helper = new VlearnDataBase(context, VlearnDataBase.dbName, null,
				VlearnDataBase.version);
		database = helper.getWritableDatabase();
	}
	
	private void onLoadPendingVideo(String result) {
		// TODO Auto-generated method stub
		util.hideLoading();
		JSONObject object = null;
		boolean error = false;
		String errorMsg = "";
		String status = "";
		JSONArray items = null;
		String id = "";
		String uid = "";
		String type = "";
		String career = "";
		String aboutyou = "";
		String message = "";
		String name = "";
		String desc = "";
		String embed_code = "";
		String videofile = "";
		String icon = "";
		String stage = "";
		String grade = "";
		String subject = "";
		String standard = "";
		String substandard = "";
		String skill = "";
		String approval = "";
		String added_datetime = "";
		String page = "";
		String language = "";
		String favorite = "";
		String career_name = "";
		String hits = "";
		String f_convert = "";
		String f_convert_failed = "";
		String na_to_learning_bank = "";
		String sife_learning_object = "";
		int length = 0;
		String domainId="";
		String domainName = "";
		try {
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");

			if (error) {
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
				return;
			}

			items = object.getJSONArray("items");
			length = items.length();
			
			for (int i = 0; i < length; i++) {
				object = items.getJSONObject(i);
				id = object.getString("id");
				uid = object.getString("uid");
				type = object.getString("type");
				career = object.getString("career");
				aboutyou = object.getString("aboutyou");
				message = object.getString("message");
				name = object.getString("name");
				desc = object.getString("desc");
				embed_code = object.getString("embed_code");
				videofile = "http://plazafamiliacom.s3-website-us-west-2.amazonaws.com/video/uploaded/videos/"+object.getString("videofile");
				icon = "http://plazafamiliacom.s3-website-us-west-2.amazonaws.com/video/uploaded/icons/"+object.getString("icon");
				stage = object.getString("stage");
				grade = object.getString("grade");
				subject = object.getString("subject");
				standard = object.getString("standard");
				substandard = object.getString("substandard");
				skill = object.getString("skill");
				approval = object.getString("approval");
				added_datetime = object.getString("added_datetime");
				page = object.getString("page");
				language = object.getString("language");
				favorite = object.getString("favorite");
				career_name = object.getString("career_name");
				hits = object.getString("hits");
				f_convert = object.getString("f_convert");
				f_convert_failed = object.getString("f_convert_failed");
				na_to_learning_bank = object.getString("na_to_learning_bank");
				sife_learning_object = object.getString("sife_learning_object");
			
				list.add(new MyVlearnCollection(domainName,id, uid, type, career, aboutyou, message, name, desc, embed_code, videofile, icon, stage, grade, subject, standard, substandard, skill, approval, added_datetime, page, language, favorite, career_name, hits, f_convert, f_convert_failed, na_to_learning_bank, sife_learning_object ,career_name.isEmpty()?1:0,approval.equalsIgnoreCase("2"),false,standard,id));
				
//				if(videoIdArray.contains(id)){
//					updateList(id,videoIdArray.indexOf(id));
//				}
			}
			
			adapter.notifyDataSetInvalidated();

		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		adapter.notifyDataSetInvalidated();
		listview.invalidate();
	}

	int position = 0;
	public void updateStatus(String id,int position){
		util.showLoading("Loading...");
		al.clear();
		this.position = position;
		al.add(new BasicNameValuePair("user", (String) util
				.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util
				.getFromSharedPreference(VariableType.STRING,
						VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("categoryId", id));
		al.add(new BasicNameValuePair("status", "11"));
		al.add(new BasicNameValuePair("app_type", "video"));
		al.add(new BasicNameValuePair("app_name", "vlearn"));
		task = connection.getData("user/updateCategoryStatus", "POST", al,
				MyVlearn.this, updateStatusApprove);
	}
	
	private void updateList(String id,int position) {
		// TODO Auto-generated method stub
		database.delete("video_table", "video_server_id=\""+id+"\"", null);
		list.remove(position);
		adapter.notifyDataSetChanged();
	}
	
	int deletePos = -1;
	void deleteVideo(int pos){
		MyVlearnCollection col = adapter.getItem(pos);
		deletePos = pos;
		if(col.isLocal){
			database.delete("video_table", "id="+col.id, null);
			removeVideo();
		}
		else{
			al.clear();
			al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
			al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
			al.add(new BasicNameValuePair("app_type", "video"));
			al.add(new BasicNameValuePair("app_name", "vlearn"));
			al.add(new BasicNameValuePair("catId", col.video_server_id));
			task = connection.getData("user/delete_set", "POST", al, MyVlearn.this, deleteVideo);
		}
		
	}
	
	private void removeVideo(){
		if(deletePos!=-1){
			list.remove(deletePos);
			adapter.notifyDataSetChanged();
		}
	}
	
	private void updateDB(String id,String video_server_id) {
		// TODO Auto-generated method stub
		database.delete("video_table", "video_server_id=\""+id+"\"", null);
		database.rawQuery("UPDATE video_table SET video_server_id= \"" + video_server_id +
	             "\" WHERE id = ?",
	             new String[] { id });
	}
	
	
	Dialog dg;
	ListView pList;
	RadioButton parent,teacher;
	RadioGroup grp;
	ParentTeacherAdapter ad;
	Button sumbitForApproval,cancel;
	
	private void initDialog(){
		dg = new Dialog(context);
		dg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dg.setContentView(R.layout.who_will_approve);
		pList = (ListView) dg.findViewById(R.id.parentteacherList);
		parent = (RadioButton) dg.findViewById(R.id.family);
		teacher = (RadioButton) dg.findViewById(R.id.teacher);
		grp = (RadioGroup) dg.findViewById(R.id.radiogroup);
		sumbitForApproval = (Button) dg.findViewById(R.id.register);
		cancel = (Button) dg.findViewById(R.id.cancel);
		ad = new ParentTeacherAdapter(context, listParnet);
		pList.setAdapter(ad);
		grp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(parent.isChecked()){
					ad = new ParentTeacherAdapter(context, listParnet);
					pList.setAdapter(ad);
				}else{
					ad = new ParentTeacherAdapter(context, listTeacher);
					pList.setAdapter(ad);
				}
			}
		});
		cancel.setOnClickListener(submitButton);
		sumbitForApproval.setOnClickListener(submitButton);
		pList.setOnItemClickListener(new OnItemClickListener() {

		    @Override
		    public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
		    	if(parent.getAdapter().getCount() != 0){
			    	if(backup!=null)backup.setBackgroundColor(context.getResources().getColor(R.color.white));
			    	backup = view;
			    	view.setBackgroundColor(context.getResources().getColor(R.color.grey));
			        parentTeacherCollection = (ParentTeacherCollection) pList.getAdapter().getItem(position);
		    	}
		    }
		});
		loadParents();
	}
	
	View backup;
	
	public ParentTeacherCollection parentTeacherCollection;
	String backupId;
	boolean isLocal;
	public void showDialog(MyVlearnCollection col){
		backupId  = col.id;
		isLocal = col.isLocal;
		parent.setChecked(true);
		ad = new ParentTeacherAdapter(context, listParnet);
		pList.setAdapter(ad);
		ad.notifyDataSetChanged();
		dg.show();
		
	}
	
	public void hideDialog(){
		dg.dismiss();
	}
	
	
	View.OnClickListener submitButton = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.register:
				try{
				if(isLocal)
					activity.doBindService(backupId, parentTeacherCollection.id, parentTeacherCollection.projectType, true);
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					if(ad.getCount() == 0){
						if(listParnet.size() ==0 && listTeacher.size() ==0){
							new AlertDialog.Builder(context).setTitle("VLearn").setMessage(activity.getString(R.string.noteacherandparent)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).create().show();
						}else if(listParnet.size()>0){
							
							Toast.makeText(context, context.getString(R.string.selectParent), Toast.LENGTH_SHORT).show();
						}else if(listTeacher.size()>0){
							Toast.makeText(context, context.getString(R.string.selectTeacher), Toast.LENGTH_SHORT).show();
						}
					}else if(parentTeacherCollection == null){
						Toast.makeText(context, context.getString(R.string.youshouldtoselectTeacherOrParent), Toast.LENGTH_SHORT).show();
					}
				}
				dg.dismiss();
				break;
			case R.id.cancel:
				hideDialog();
				break;

			default:
				break;
			}
		}
	};
	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case LoadPenduingVideo:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.UnabletoPendingvideo), Toast.LENGTH_SHORT).show();
			break;
		case updateStatusApprove:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedstautus), Toast.LENGTH_SHORT).show();
			break;
		case loadTeachers:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedtoloadtecherlist), Toast.LENGTH_SHORT).show();
			break;
		case loadParents:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedtoloadparentlist), Toast.LENGTH_SHORT).show();
			break;
		case loadMyVideo:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedtoloadyourvideos), Toast.LENGTH_SHORT).show();
			break;
		case deleteVideo:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.unabletodeltedvideo), Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		
	}
}

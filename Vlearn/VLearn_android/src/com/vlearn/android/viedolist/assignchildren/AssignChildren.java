package com.vlearn.android.viedolist.assignchildren;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.vlearn.android.R;
import com.vlearn.android.database.VlearnDataBase;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.setting.mykid.MyKidCollection;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;
import com.vlearn.android.viedolist.VideoList;

public class AssignChildren extends Fragment implements OnNetworkResult {
	
	HomeActivity activity;
	Context context;
	ListView listView;
	
	NetworkConnection connection;
	VUtil util;
	ImageLoader loader;
	
	Button assignChildren;
	List<MyKidCollection> list = new ArrayList<>();
	
	JSONArray kidAssign;
	JSONArray outputData = null;
	ArrayList<String> kidIds = new ArrayList<>();
	AssignChildrenAdapter adapter;
	String id;
	public static AssignChildren Create(String id){
		AssignChildren assignChildren = new AssignChildren();
		Bundle bun = new Bundle();
		bun.putString("id", id);
		assignChildren.setArguments(bun);
		return assignChildren;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		kidAssign = VideoList.kidAssign;
		id = getArguments().getString("id");
		try {
			for(int i=0;i<kidAssign.length();i++) kidIds.add(""+kidAssign.getJSONObject(i).getString("to_uid"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			kidAssign = null;
			
		}
		
		loader = activity.getImageLoader();
		util = activity.getVUtil();
		connection = activity.getNetworkConnection();
		initDataBase();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.assign_listview, null, false);
		listView = (ListView) view.findViewById(R.id.listView);
		assignChildren = (Button) view.findViewById(R.id.assignChildren);
		assignChildren.setOnClickListener(onClick);
		settingListView();
		loadKids();
		return view;
	}
	
	List<NameValuePair> al = new ArrayList<>();
	
	View.OnClickListener onClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			JSONArray array[] = getData();
			kidAssign = array[0];
			outputData = array[1];
			String[] ar = new String[outputData.length()];
			array = null;
			String data = "{";
			try {
				data += outputData.getString(0);
				ar[0] = outputData.getString(0);
				for(int i=1;i<outputData.length();i++){ data+=","+outputData.getString(i); ar[i] = outputData.getString(i);}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			data+="}";
			
			StringBuilder builder = new StringBuilder();
			for(String s : ar) {
			    builder.append(s);
			}
						
			sendToServer(outputData.toString());
			
		}
	};
	private AsyncTask<String, Void, String> task;
	
	private void sendToServer(String data){
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("kidId",  data));
		al.add(new BasicNameValuePair("categoryId",  id));
		al.add(new BasicNameValuePair("app_name",  "vlearn"));
		al.add(new BasicNameValuePair("app_type",  "video"));
		al.add(new BasicNameValuePair("message",  "greate"));

		task = connection.getData("user/addAssignments", "POST", al, AssignChildren.this, assignKid);
	}
	
	private JSONArray[] getData(){
		JSONArray[] jsonArray = new JSONArray[2];
		jsonArray[0] = new JSONArray();
		jsonArray[1] = new JSONArray();
		MyKidCollection col;
		for(int i=0;i<adapter.getCount();i++){
			col = adapter.getItem(i);
			try{
				if(col.isChecked){
				    JSONObject obj = new JSONObject();
					obj.put("fullname", col.first_name+" "+col.last_name);
					obj.put("to_uid", ""+col.id);
					jsonArray[0].put(obj);
					jsonArray[1].put(Integer.parseInt(col.id));
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		return jsonArray;
	}
	
	private void settingListView(){
		list.clear();
		adapter = new AssignChildrenAdapter(context, list, loader);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
	}
	
	AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			adapter.getItem(position).isChecked = !adapter.getItem(position).isChecked;
			adapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		activity.setHeader(true, R.drawable.button_selector_round_blue, 0, context.getString(R.string.back), null, context.getString(R.string.select_children) , null, 0);
	}
	
	private VlearnDataBase helper;
	private Cursor cursor;
	private SQLiteDatabase database;
	
	private void initDataBase() {

		helper = new VlearnDataBase(context, VlearnDataBase.dbName, null,
				VlearnDataBase.version);
		database = helper.getWritableDatabase();
	}
	
	private void loadKids(){
		
		if(cursor != null && !cursor.isClosed())
			cursor.close();
		cursor = database.rawQuery("SELECT k.*,c.imgUrl FROM mykid AS k INNER JOIN career AS c ON k.career_id = c.careers_id", null);
		cursor.moveToFirst();
		int index=0;
		int length = cursor.getCount();
		list.clear();
		while(index<length){
			index+=1;
			int dbid = cursor.getInt(cursor.getColumnIndex("id"));
			String id = cursor.getString(cursor.getColumnIndex("kid_id"));
			String first_name = cursor.getString(cursor.getColumnIndex("first_name"));
			String username = cursor.getString(cursor.getColumnIndex("username"));
			String zip_code = cursor.getString(cursor.getColumnIndex("zip_code"));
			String email = cursor.getString(cursor.getColumnIndex("email"));
			String career_id = cursor.getString(cursor.getColumnIndex("career_id"));
			String dob = cursor.getString(cursor.getColumnIndex("dob"));
			String last_name = cursor.getString(cursor.getColumnIndex("last_name"));
			String role = cursor.getString(cursor.getColumnIndex("role"));
			String access = cursor.getString(cursor.getColumnIndex("access"));
			String grade_level_id = cursor.getString(cursor.getColumnIndex("grade_level_id"));
			String password = cursor.getString(cursor.getColumnIndex("password"));
			String class_id = cursor.getString(cursor.getColumnIndex("class_id"));
			boolean isCross = false;
			boolean isDelete = false;
			String imgUrl = cursor.getString(cursor.getColumnIndex("imgUrl"));
			
			boolean isChecked = kidIds.contains(id);;
			
			list.add(new MyKidCollection(dbid, id, first_name, username, zip_code, email, career_id, dob, last_name, role, access, grade_level_id, password, imgUrl, isCross, isDelete,class_id,isChecked));
			cursor.moveToNext();
		}
		if(!cursor.isClosed())
			cursor.close();
		adapter.notifyDataSetChanged();
		listView.invalidate();
	}

	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case assignKid:
			onKidAssign(result);
			break;

		default:
			break;
		}
	}

	private void onKidAssign(String result) {
		// TODO Auto-generated method stub
		activity.goBack();
		
		JSONObject obj = null;
		boolean error = false;
		String errorMsg = "";
		try{
			obj = new JSONObject(result);
			error = obj.getBoolean("error");
			if(error){
				errorMsg = obj.getString("errorMSG");
				Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
			}else{
				VideoList.kidAssign = kidAssign;
				activity.goBack();
			}
				
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case assignKid:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToassignkid), Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
	
}

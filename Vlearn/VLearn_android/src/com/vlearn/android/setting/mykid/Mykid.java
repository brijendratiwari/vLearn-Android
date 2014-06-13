package com.vlearn.android.setting.mykid;

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
import com.vlearn.android.setting.mykid.AddNewKid.OnKidCreate;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

public class Mykid extends Fragment implements OnNetworkResult {
	
	HomeActivity activity;
	Context context;
	ListView listView;
	MyKidAdapter adapter;
	Button addNewKid;
	ScrollView fragmentCcontainer;
	List<MyKidCollection> list = new ArrayList<>();

	
	JSONObject object = new JSONObject();
	
	NetworkConnection connection;
	VUtil util;
	ImageLoader loader;
	AsyncTask<String, Void, String> task;
	
	FrameLayout layout;
	FragmentManager manager;
	FragmentTransaction transaction;
	private boolean isTeacher;
	private AddNewKid addNewKid1;
	
	public static MyKidCollection col;
	
	public static Mykid Create(){
		Mykid mykid = new Mykid();
		return mykid;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		manager = getFragmentManager();
		
		loader = activity.getImageLoader();
		util = activity.getVUtil();
		initDataBase();
		connection = activity.getNetworkConnection();
		Log.e("Ads", "asd");
		isTeacher = !((String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ACCESS)).contains("parent");
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.mykid, null, false);
		
		layout = (FrameLayout) view.findViewById(R.id.fragment);
		listView = (ListView) view.findViewById(R.id.listView);
		addNewKid = (Button) view.findViewById(R.id.addNewKidButton);	
		fragmentCcontainer = (ScrollView) view.findViewById(R.id.fragmentCcontainer);
		addNewKid.setOnClickListener(onClickListener);
		registerForContextMenu(addNewKid);
		initDialog();
		initClassDialog();
		transaction.commit();
		hideView();
		
		settingListView();
		//loadSchoolTeacherClasses();
		
		return view;
	}
	
	private void initDialog() {
		// TODO Auto-generated method stub
		addNewKid1 = AddNewKid.Create(Mykid.this);
		//addNewKid.show(getFragmentManager(), "New Kid Add");
		addNewKid1.setOnKidCreate(onKidCreate);
		transaction = manager.beginTransaction();
		transaction.replace(R.id.fragment, addNewKid1);
	}

	boolean isHide = false;
	public void hideView(){
		isHide = true;
		activity.enableUs(); 
		fragmentCcontainer.setVisibility(View.INVISIBLE);
		//resetDialog();
	}
	public void showView(){
		isHide = false;
		activity.disableUs();
		fragmentCcontainer.setVisibility(View.VISIBLE);
	}
	
	public void showContextMenuaddNewKid(){
//		addNewKid.showContextMenu();
		dia.show();
	}
	
	/*public void resetDialog(){
		addNewKid1 = null;
		initDialog();
	}*/
	public void showClassView(){
		//showClass
		addNewKid1.showClass();
	}
	
	public void loadSchoolTeacher(){
		util.showLoading("Loading...");
		if(isTeacher) loadSchoolTeacherClasses();
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(isHide){
				showView();
			}else{
				hideView();
			}
		}
	};
	
	OnKidCreate onKidCreate = new OnKidCreate() {
		
		@Override
		public void onKidCreate(String result, String gender,
				String grade_level_id, String lastName, String name,
				String username, String password, String career_id, String imgUrl,String classId)  {
			// TODO Auto-generated method stub
			JSONObject object = null;
			boolean error = false;
			String errorMsg = "";
			String status = "";
			String message = "";
			String kidId = "";
			
			try{
				object = new JSONObject(result);
				error = object.getBoolean("error");
				errorMsg = object.getString("errorMsg");
				status = object.getString("status");
				if(!error){
					message = object.getString("message");
					kidId = object.getString("kidId");
					MyKidCollection col = new MyKidCollection((list.get(list.size()-1).dbId+1), kidId, name, username, "", "", career_id, "", lastName, "", "", grade_level_id, password, imgUrl, false, false,classId,false);
					list.add(col);
					adapter.notifyDataSetChanged();
					
					insertORUpdateMyKid(col.dbId, col.id, col.first_name, col.username, col.zip_code, col.email, col.career_id, col.dob, col.last_name, col.role, col.access, col.grade_level_id, col.password, col.classId);
				}
				else{
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
				}

			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	};
	
	
	private void insertORUpdateMyKid(int id, String kid_id, String first_name, String username, String zip_code, String email, String career_id, String dob, String last_name, String role, String access, String grade_level_id, String password,String class_id) {
		// TODO Auto-generated method stub
		id += 1;
		database.execSQL("INSERT INTO mykid (id, kid_id, first_name, username, zip_code, email, career_id, dob, last_name, role, access, grade_level_id, password,class_id) VALUES ("
				+ id
				+ ",\""
				+ kid_id
				+ "\",\""
				+ first_name
				+ "\",\""
				+ username
				+ "\",\""
				+ zip_code
				+ "\",\""
				+ email
				+ "\",\""
				+ career_id
				+ "\",\""
				+ dob
				+ "\",\""
				+ last_name
				+ "\",\""
				+ role
				+ "\",\""
				+ access
				+ "\",\""
				+ grade_level_id
				+ "\",\""
				+ password 
				+ "\",\"" 
				+ class_id +"\");");
	}
	
	int currenindex = -1;
	String kidId = "";
	public void deleteKid(String kidId,int position){
		al.clear();
		this.kidId = kidId;
		currenindex = position;
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("kidId",  kidId));

		
		task = connection.getData("user/deleteKid", "POST", al, Mykid.this, delkid);
		
	}
	
	private void settingListView() {
		// TODO Auto-generated method stub
		
		list.clear();
		adapter = new MyKidAdapter(context,Mykid.this, list, loader);
		listView.setAdapter(adapter);
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		activity.setHeader(true, R.drawable.button_selector_round_green, R.drawable.edit_button_selector_bg, context.getString(R.string.back), context.getString(R.string.Edit), context.getString(R.string.Children), forward, 0);
		loadKids();
	
	}
	
	boolean isSelected = false;
	View.OnClickListener forward = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isSelected = !isSelected;
			v.setSelected(isSelected);
			showCrosss(isSelected);
		}
	};
	
	private void showCrosss(boolean isShow){
		for(int i=0;i<adapter.getCount();i++){
			adapter.getItem(i).isCross = isShow;
			adapter.getItem(i).isDelete = false;
			adapter.notifyDataSetChanged();
			listView.invalidate();
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
			list.add(new MyKidCollection(dbid, id, first_name, username, zip_code, email, career_id, dob, last_name, role, access, grade_level_id, password, imgUrl, isCross, isDelete,class_id,false));
			cursor.moveToNext();
		}
		if(!cursor.isClosed())
			cursor.close();
		adapter.notifyDataSetChanged();
		listView.invalidate();
	}
	
	
	List<NameValuePair> al = new ArrayList<>();
	private void loadSchoolTeacherClasses(){
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("teacher_id",  (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ID)));

		
		task = connection.getData("user/getSchoolTeacherClasses", "POST", al, Mykid.this, SchoolTeacherClasses);
	}

	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case SchoolTeacherClasses:
			onSchoolTeacherClasses(result);
			break;
		case delkid:
			onDelKid(result);
		default:
			break;
		}
	}
	
	private void deleteKid(){
		database.delete("mykid", "kid_id="+kidId, null);
		list.remove(currenindex);
		adapter.notifyDataSetChanged();
	}

	private void onDelKid(String result) {
		// TODO Auto-generated method stub
		boolean error;
		String errorMsg;
		String status;
		String message;
			
		JSONObject object = null;
		try{
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");
			if(!error){
				message = object.getString("message");
				deleteKid();
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
			}
//			message = object.getBoolean("error");
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}

	private String classData = "";

	
	List<ClassCollection> classList = new ArrayList<>();
	private ListView list_alert;
	private Dialog dia;
	private ClassCollectionAdapter ad;
	/*@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		if(!isTeacher) return;
		menu.add(0, -1, 1, "+Add new Class");
		onSchoolTeacherClasses(classData, menu);
		//onSchoolTeacherClasses()
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(!isTeacher) return false;
		if(item.getItemId() == -1){
			//add new class
			showClassView();
			return super.onContextItemSelected(item);
		}else{
			ClassCollection col = classList.get(item.getItemId());
			addNewKid1.saveKid(col.class_id);
		}
		
		return super.onContextItemSelected(item);
	}
	*/
	public void initClassDialog(){
	  dia = new Dialog(context);
	  dia.setContentView(R.layout.alert_list);
	  dia.setTitle("Select a class");
	  dia.setCancelable(true);
	
	  list_alert = (ListView) dia.findViewById(R.id.alert_list);
	  ad = new ClassCollectionAdapter(context,
	            classList);
	  list_alert.setAdapter(ad);
	  list_alert.setOnItemClickListener(new OnItemClickListener() {
	      public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
	                long arg3) {
	           // String fname = main_genral_class.file_list.get(pos);
	            dia.dismiss();
	            if(pos == 0){
	    			//add new class
	    			showClassView();
	    		}else{
	    			ClassCollection col = classList.get(pos);
	    			addNewKid1.saveKid(col.class_id);
	    		}
	        }
	  });
	}
//	public void show_alert() {
//        // TODO Auto-generated method stub
//
//        dia.show();
//    }
	
	private void onSchoolTeacherClasses(String result) {
		// TODO Auto-generated method stub
		showContextMenuaddNewKid();
		JSONObject object = null;
		boolean error = false;
		String errorMsg = "";
		String status = "";
		JSONArray classes = null;
		String id = "";
		String class_name = "";
		String class_type_id = "";
		String grade_name = "";
		String type_name = "";
		String rosters = "";
		String student_count = "";
		int length = 0;
		
		try{
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");
			if(error) {
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
				return;
			}
			
			classes = object.getJSONArray("classes");
			length = 0;
			length = classes.length();
			
//			if(length<=0){
//				return;
//			}
			
			classList.clear();
			classList.add(new ClassCollection("", "+Add New Class", "", "", "", "", ""));
			for(int i=0;i<length;i++){
				object = classes.getJSONObject(i);
				id = object.getString("id");
				class_name = object.getString("class_name");
				class_type_id = object.getString("class_type_id");
				grade_name = object.getString("grade_name");
				type_name = object.getString("type_name");
				rosters = object.getString("rosters");
				student_count = object.getString("student_count");
				classList.add(new ClassCollection(id, class_name, class_type_id, grade_name, type_name, rosters, student_count));
				//menu.add(0, i, i+1, class_name);
			}
			ad.notifyDataSetChanged();
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case SchoolTeacherClasses:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadSchoolTeacherClass), Toast.LENGTH_SHORT).show();
			break;
		case delkid:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToRemoveKid), Toast.LENGTH_SHORT).show();
		default:
			break;
		}
	
	}
	
}

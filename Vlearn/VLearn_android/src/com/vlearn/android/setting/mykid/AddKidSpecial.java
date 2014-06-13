package com.vlearn.android.setting.mykid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
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
import com.vlearn.android.ui.picker.CareerPicker;
import com.vlearn.android.ui.picker.Picker;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AddKidSpecial extends Fragment implements OnNetworkResult{

	ListView listView;
	Button editButton;
	Button backButton,titleIcon;
	TextView title;

	HomeActivity activity;
	Context context;
	
	View editKid;
	
	NetworkConnection connection;
	VUtil util;
	ImageLoader loader;
	
	MyKidCollection col = Mykid.col;
	
	AddKidSpecialAdapter adapter;
	ImageView kidIcon;
	List<AddKidSpecialCollection> list = new ArrayList<>();
	
	ImageView profilePic;
	LinearLayout nameContainer;
	EditText firstName,lastName,username,password;
	Button grade;
	RadioGroup gender;
	RadioButton male,female;
	Button cancel,save,logout;
	
	public static AddKidSpecial Create() {
		AddKidSpecial addKidSpecial = new AddKidSpecial();
		return addKidSpecial;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		connection = activity.getNetworkConnection();
		util = activity.getVUtil();
		util.initLoader(activity);
		loader = activity.getImageLoader();
		initDataBase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.add_kid_special, null, false);
		
		listView = (ListView) view.findViewById(R.id.listview);
		backButton = (Button) view.findViewById(R.id.backButton);
		editButton = (Button) view.findViewById(R.id.editButton);
		title = (TextView) view.findViewById(R.id.headerTitle);
		editKid = view.findViewById(R.id.fragment);
		kidIcon = (ImageView)view.findViewById(R.id.kidIcon);
		
		profilePic = (ImageView) view.findViewById(R.id.profilePic);
		nameContainer = (LinearLayout) view.findViewById(R.id.nameContainer);
		firstName = (EditText) view.findViewById(R.id.firstName);
		lastName = (EditText) view.findViewById(R.id.lastName);
		grade = (Button) view.findViewById(R.id.grade);
		username = (EditText) view.findViewById(R.id.username);
		password = (EditText) view.findViewById(R.id.password);
		gender = (RadioGroup) view.findViewById(R.id.gender);
		male = (RadioButton) view.findViewById(R.id.male);
		female = (RadioButton) view.findViewById(R.id.female);
		cancel = (Button) view.findViewById(R.id.cancel);
		save = (Button) view.findViewById(R.id.save);
		logout = (Button) view.findViewById(R.id.logout);
		
		profilePic.setOnClickListener(pickClick);
		grade.setOnClickListener(pickClick);
		
		logout.setVisibility(View.GONE);
		save.setOnClickListener(submitClickListener);
		cancel.setOnClickListener(onClickListener);
		editButton.setOnClickListener(onClickListener);
		backButton.setOnClickListener(new View.OnClickListener(){public void onClick(View v){activity.goBack();}});
		list.clear();
		adapter = new AddKidSpecialAdapter(context, list, loader);
		listView.setAdapter(adapter);
		loadKidLeaderBoard();
		if(!isHide) hideView();
		
		title.setText(col.username+"'s Learning");
		loader.DisplayImage(col.imgUrl, kidIcon);
		career_imgUrl = col.imgUrl;
		career_id = col.career_id;
		fillEditView();
		getGradeList();
		view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
		return view;
	}

	
	private void fillEditView(){
		loader.DisplayImage(col.imgUrl, profilePic);
		firstName.setText(col.first_name);
		lastName.setText(col.last_name);
		grade.setTag(col.grade_level_id);
		username.setText(col.username);
		password.setText(col.password);
	}
	
	boolean isHide = false;
	public void hideView(){
		isHide = true;
		editKid.setVisibility(View.INVISIBLE);
	}
	public void showView(){
		isHide = false;
		editKid.setVisibility(View.VISIBLE);
	}
	
	String imageString2 ="";
	String userString2 ="";
	String passString2 ="";
	String grade_level_idString2 ="";
	String passwordString2 ="";
	String usernameString2 ="";
	String nameString2 ="";
	String lastNameString2 ="";
	String userIdString2 ="";
	String genderString2 ="";
	String career_idString2 ="";
	String kidIdString2 ="";
	
	private void editKidToServer(){
		if(checkForEmptyKid()){
			Toast.makeText(context, ""+context.getString(R.string.allFiledsRequired), Toast.LENGTH_SHORT).show();
			return;
		}
		
		String imageString2 = career_imgUrl;
		String userString2 = (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME);
		String passString2= (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD);
		String grade_level_idString2 = grade.getTag()==null?col.grade_level_id:(String)grade.getTag();
		String passwordString2 = password.getText().toString().trim();
		String usernameString2 = username.getText().toString().trim();
		String nameString2 = firstName.getText().toString().trim();
		String lastNameString2 = lastName.getText().toString().trim();
		String userIdString2 = (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ID);
		String genderString2 = male.isChecked()?"M":"F";
		String career_idString2 = career_id;
		String kidIdString2 = col.id;
		
		if(!imagePathString.isEmpty()){
			
			
		}
		if(!career_id.isEmpty()){
		String fileName = career_imgUrl.substring(career_imgUrl.lastIndexOf('/') + 1);
			al.add(new BasicNameValuePair("career_id", career_id));
			al.add(new BasicNameValuePair("image", fileName));
			col.career_id = career_id;
		}
		
		col.grade_level_id = grade_level_idString2;
		col.first_name = nameString2;
		col.last_name = lastNameString2;
		col.username = usernameString2;
		col.password = passwordString2;
	
		al.add(new BasicNameValuePair("gender", genderString2));
		al.add(new BasicNameValuePair("userId", userIdString2));
		al.add(new BasicNameValuePair("lastName", lastNameString2));
		al.add(new BasicNameValuePair("name", nameString2));
		al.add(new BasicNameValuePair("username", usernameString2));
		al.add(new BasicNameValuePair("password", passwordString2));
		al.add(new BasicNameValuePair("grade_level_id", grade_level_idString2));
		al.add(new BasicNameValuePair("pass", passString2));
		al.add(new BasicNameValuePair("user", userString2));
		al.add(new BasicNameValuePair("kidId", kidIdString2));
		
		task = connection.getData("user/editKidInfo", "POST", al, AddKidSpecial.this, editKidDetails);
		
	}
	
	private void uploadImage(){
		al.clear();
		activity.runOnUiThread(new Runnable() {
			public void run() {
				util.showLoading("Please wait");
			}
		});
		
		String filePath = "";
		try{
	        URL url = new URL(career_imgUrl); //you can write here any link

	        File myDir =  new File("/sdcard"+"/vLearn");
	        //Something like ("/sdcard/file.mp3")


	        if(!myDir.exists()){
	            myDir.mkdir();
	            Log.v("", "inside mkdir");

	        }

	        Random generator = new Random();
	        int n = 10000;
	        n = generator.nextInt(n);
	        String fname = "kid.png";
	        File file = new File (myDir, fname);
	        filePath = file.getAbsolutePath();
	        if (file.exists ()) file.delete (); 

	             /* Open a connection to that URL. */
	            URLConnection ucon = url.openConnection();
	            InputStream inputStream = null;
	           HttpURLConnection httpConn = (HttpURLConnection)ucon;
	          httpConn.setRequestMethod("GET");
	          httpConn.connect();

	          if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
	           inputStream = httpConn.getInputStream();
	          }

	            /*
	             * Define InputStreams to read from the URLConnection.
	             */
	           // InputStream is = ucon.getInputStream();
	            /*
	             * Read bytes to the Buffer until there is nothing more to read(-1).
	             */
	           int bytesDownloaded = 0;
	            FileOutputStream fos = new FileOutputStream(file);
	            int size = 1024*1024;
	            byte[] buf = new byte[size];
	            int byteRead;
	            while (((byteRead = inputStream.read(buf)) != -1)) {
	                fos.write(buf, 0, byteRead);
	                bytesDownloaded += byteRead;
	            }
	            /* Convert the Bytes read to a String. */

	            fos.close();

	    }catch(IOException io)
	    {
	        //networkException = true;
	       // continueRestore = false;
	    }
	    catch(Exception e)
	    {   
	       // continueRestore = false;
	        e.printStackTrace();
	    }
		
		
		try{
		 MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
         File file = new File (filePath);
         entity.addPart("file", new FileBody(file));
         entity.addPart("fileName", new StringBody(file.getName()));
         connection.getData("user/uploadPost", entity, AddKidSpecial.this, submitK9idPhoto);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		 		
	}
	
	public boolean checkForEmptyKid(){
		

		if(firstName.getText().toString().trim().isEmpty()){
			Toast.makeText(context, ""+context.getString(R.string.enterfirstname), Toast.LENGTH_SHORT).show();
			return true;
		}
		
		if(lastName.getText().toString().trim().isEmpty()){
			Toast.makeText(context, ""+context.getString(R.string.enterlastname), Toast.LENGTH_SHORT).show();
			return true;
		}
		
		if(grade.getText().toString().trim().isEmpty() || grade.getText().toString().trim().equalsIgnoreCase("grade")){
			Toast.makeText(context, ""+context.getString(R.string.chooseGrade), Toast.LENGTH_SHORT).show();
			return true;
		}
		
		if(username.getText().toString().trim().isEmpty()){
			Toast.makeText(context, ""+context.getString(R.string.enterusername), Toast.LENGTH_SHORT).show();
			return true;
		}
		
		return false;
	}
	
	
	List<NameValuePair> al = new ArrayList<>();
	private AsyncTask<String, Void, String> task;
	private void loadKidLeaderBoard(){
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("kidId",  col.id));

		task = connection.getData("user/getBadgeInformation", "POST", al, AddKidSpecial.this, loadKidLeaderboard);
	}
	
	View.OnClickListener submitClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			editKidToServer();
		}
	};
	
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
	private String career_id = "";
	private String career_careers_id = "";
	private String career_imgUrl = "";
	private String career_name = "";
	private final int gradeRequest = 110;
	int kidCareerPicker = 109;
	
	public void onloadKidLeaderBoard(String result){
		
		boolean error = false;
		String errorMsg = "";
		String status = "";
		JSONObject object = null;
		String virtual_balance = "";
		String virtual_expenses = "";
		JSONArray all_user_badges = null;
		int length = 0;
		String badge_image_filename = "";
		String title = "";
		String title_spanish = "";
		String level = "";
		String exercise_id = "";
		String exercise_id_spanish = "";
		String score = "";
		String time_taken = "";
		String date = "";
		String timestamp = "";
		String grade = "";
		String standard_spanish = "";
		String standard = "";
		String badge_value = "";
		String item_type = "";
		
		try{
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			
			if(!error){
			
				status = object.getString("status");
				object = object.getJSONObject("list");
				virtual_balance = object.getString("virtual_balance");
				virtual_expenses = object.getString("virtual_expenses");
				try{
					all_user_badges = object.getJSONArray("all_user_badges");
					length = all_user_badges.length();
				}catch (JSONException e) {
					// TODO: handle exception
					e.printStackTrace();
					length = 0;
				}
				
				if(length<=0){
					return;
				}
				list.clear();
				for(int i=0;i<length;i++){
					object = all_user_badges.getJSONObject(i);
					badge_image_filename = object.getString("badge_image_filename");
					title = object.getString("title");
					title_spanish = object.getString("title_spanish");
					level = object.getString("level");
					try{
						exercise_id = object.getString("exercise_id");
						exercise_id_spanish = object.getString("exercise_id_spanish");
					}catch (Exception e) {
						// TODO: handle exception
						exercise_id_spanish = "";
						exercise_id = "";
					}
					score = object.getString("score");
					try{
					time_taken = object.getString("time_taken");
					}catch (Exception e) {
						// TODO: handle exception
						time_taken = "";
					}
					date = object.getString("date");
					timestamp = object.getString("timestamp");
					grade = object.getString("grade");
					standard_spanish = object.getString("standard_spanish");
					standard = object.getString("standard");
					badge_value = object.getString("badge_value");
					item_type = object.getString("item_type");
					list.add(new AddKidSpecialCollection(badge_image_filename, title, title_spanish, level, exercise_id, exercise_id_spanish, score, time_taken, date, timestamp, grade, standard_spanish, standard, badge_value, item_type));
				}
				adapter.notifyDataSetChanged();
			}
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("asd", "ads");
		}
		
	}
	private String[] gradArray;
	View.OnClickListener pickClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			Intent intent;
			Bundle bun;
			
			switch (v.getId()) {
			case R.id.grade:
				intent = new Intent(context, Picker.class);
				bun = new Bundle();
				bun.putString(Picker._PICKERTITLE, 	context.getString(R.string.Select_Grade));
				bun.putString(Picker._OKButton, context.getString(R.string.Select_Grade));
				bun.putString(Picker._CANCELButton, context.getString(R.string.back));
				bun.putStringArray(Picker._ListEntries, gradArray);
				intent.putExtras(bun);
				startActivityForResult(intent, gradeRequest);
				activity.overridePendingTransition(R.anim.slideleft1, R.anim.slideleft2);
				break;
			case R.id.profilePic:
				intent = new Intent(context, CareerPicker.class);
				bun = new Bundle();
				bun.putString(CareerPicker.CHECKTOCARRERR, career_careers_id);
				intent.putExtras(bun);
				startActivityForResult(intent, kidCareerPicker);
				activity.overridePendingTransition(R.anim.slideleft1, R.anim.slideleft2);
				break;

			default:
				break;
			}
		}
	};
	
	private HashMap<String, String> gradeMap = new HashMap<>();
	private void getGradeList(){
		String columnname = "englishName";
		if (!VUtil.IS_APP_LANG_ENG)
			columnname = "spanishName";

		cursor = database.rawQuery("SELECT grade_id," + columnname
				+ " FROM grades", null);
		int length = cursor.getCount();
		cursor.moveToFirst();
		if (length > 0) {
			gradeMap.clear();
			gradArray = new String[length];
			for (int i = 0; i < length; i++) {
				String title = cursor.getString(cursor
						.getColumnIndex(columnname));
				gradeMap.put(title,
						cursor.getString(cursor.getColumnIndex("grade_id")));
				gradArray[i] = title;
				
				if(col.grade_level_id.equalsIgnoreCase(cursor.getString(cursor.getColumnIndex("grade_id")))){
					grade.setText(title);
				}
				
				cursor.moveToNext();
			}
		}
	}
	

	private VlearnDataBase helper;
	private Cursor cursor;
	private SQLiteDatabase database;
	private String imagePathString = "";
	
	private void initDataBase() {

		helper = new VlearnDataBase(context, VlearnDataBase.dbName, null,
				VlearnDataBase.version);
		database = helper.getReadableDatabase();
	}
	
	private void updateKid(){
		
		ContentValues cv = new ContentValues();
		cv.put("password", col.password);
		cv.put("first_name", col.first_name);
		cv.put("username", col.username);
		cv.put("last_name", col.last_name);
		cv.put("grade_level_id", col.grade_level_id);
		cv.put("career_id", col.career_id);
		
		database.update("mykid", cv, "kid_id="+col.id, null);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		activity.setHeader(false, 0, 0, null, null, null, null, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == kidCareerPicker){
			if(resultCode == Activity.RESULT_OK){
				
				Bundle bun = data.getExtras();
				if(bun == null) return;
				career_id = bun.getString("id");
				career_careers_id = bun.getString("careers_id");
				career_imgUrl = bun.getString("imgUrl");
				career_name = bun.getString("name");
				loader.DisplayImage(career_imgUrl, profilePic);
			}else if(resultCode == Activity.RESULT_CANCELED){
				Toast.makeText(context, ""+context.getString(R.string.errorchoosecareer), Toast.LENGTH_SHORT).show();
			}
		}
		
		if(requestCode == gradeRequest){
			try{
				grade.setText(data.getStringExtra("result"));
				grade.setTag(gradeMap.get(data.getStringExtra("result")));
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				}
		}
	}
	
	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		activity.runOnUiThread(new Runnable() {
			public void run() {
				util.hideLoading();
			}
		});
		
		switch (callingId) {
		case loadKidLeaderboard:
			onloadKidLeaderBoard(result);
			break;
		case editKidDetails:
			oneditKidDetails(result);
			break;
		case submitK9idPhoto:
			onUploadProfile(result);
			break;
		default:
			break;
		}
	}

	private void onUploadProfile(String result) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		boolean error;
		String errorMsg;
		String status;
		String fileName;
		String fileUrl;
		try{
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			if(!error){
				imagePathString = object.getString("fileName");
				//imagePathString = object.getString("fileUrl");
				activity.runOnUiThread(new Runnable() {
					public void run() {
						editKidToServer();
					}
				});
				
			}else{
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	private void oneditKidDetails(String result) {
		// TODO Auto-generated method stub
		updateKid();
		hideView();
		loader.DisplayImage(career_imgUrl, AddKidSpecial.this.kidIcon);
		title.setText(col.username+"'s Learning");
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		activity.runOnUiThread(new Runnable() {
			public void run() {
				util.hideLoading();
			}
		});
		
		switch (callingId) {
		case loadKidLeaderboard:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadKidLeader), Toast.LENGTH_SHORT).show();
			break;
		case editKidDetails:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToEditkidDetails), Toast.LENGTH_SHORT).show();
			break;
		case submitK9idPhoto:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedTosubmitkidAvataar), Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
}

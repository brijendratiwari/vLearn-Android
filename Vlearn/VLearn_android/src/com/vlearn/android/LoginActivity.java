package com.vlearn.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.vlearn.android.database.VlearnDataBase;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnNetworkResult {

	Button register, loginButton;
	EditText username, password;
	TextView forgetPassword;

	private final int GET_LOGIN = 101;

	Context context = this;
	NetworkConnection connection;
	VUtil util;
	AsyncTask task;
	List<NameValuePair> al = new ArrayList<>();

	final int registerButtonId = R.id.registerButton;
	InputMethodManager imm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		
		connection = new NetworkConnection(context);
		util = new VUtil(context);
		util.initLoader(LoginActivity.this, getString(R.string.Logging_in));
		register = (Button) findViewById(registerButtonId);
		loginButton = (Button) findViewById(R.id.loginButton);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		forgetPassword = (TextView) findViewById(R.id.forgetPassword);
		loginButton.setOnClickListener(OnclickListener);
		forgetPassword.setOnClickListener(OnclickListener);
		register.setOnClickListener(OnclickListener);
		
		initDataBase();

	}

	View.OnClickListener OnclickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case registerButtonId:
				startActivity(new Intent(context, RegistrationActivity.class));
				overridePendingTransition(R.anim.slideup1, R.anim.ideal);
				break;
			case R.id.forgetPassword:
				startActivity(new Intent(context, ForgetPassword.class));
				overridePendingTransition(R.anim.slidedwn1, R.anim.ideal);
				break;
			case R.id.loginButton:
				
				doLogin();
				break;
			default:
				break;
			}
			imm.hideSoftInputFromWindow(loginButton.getWindowToken(), 0);
		}
	};
	private String user;
	private String pass;

	private void doLogin() {
		// TODO Auto-generated method stub

		user = username.getText().toString().trim();
		pass = password.getText().toString().trim();
		if (user.isEmpty() || pass.isEmpty()) {
//			user = "Ana";
//			pass = "Gipiemno";
			Toast.makeText(context, ""+context.getString(R.string.both_fields), Toast.LENGTH_SHORT).show();
			return;
		}
		if (connection.isNetworkAvailable()) {
			al.clear();
			al.add(new BasicNameValuePair("user", user));
			al.add(new BasicNameValuePair("pass", pass));
			// al.add(new BasicNameValuePair("user", "maritza"));
			// al.add(new BasicNameValuePair("pass", "maritza"));
			// vishal7t1/vishal7t1
			//
			al.add(new BasicNameValuePair("app_type", "video"));
			al.add(new BasicNameValuePair("app_name", "vlearn"));
			util.showLoading();
			task = connection.getData("user/login", "POST", al,
					LoginActivity.this, GET_LOGIN);
		} else {
			connection.NoNetworkAvailable(context);
		}
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtras(new Bundle());
		startActivity(intent);
		overridePendingTransition(R.anim.slidedwn1, R.anim.slidedwn2);
		
	}
	
	
	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case GET_LOGIN:
			util.hideLoading();
			ProcessToLogin(result);
			break;
		
		default:
			break;
		}
	}

	int index = 0;
	
	private void ProcessToLogin(String result) {

		JSONObject object = null;
		boolean error;
		String errorMsg, status, id, first_name, last_name, dob, email, zip_code, avatar, access, role, grade_name, grade_id, bio, videos, kids, career_info, classes;
		
		try {
			object = new JSONObject(result);

			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");
			if (!error) {
				id = object.getString("id");
				first_name = object.getString("first_name");
				last_name = object.getString("last_name");
				dob = object.getString("dob");
				email = object.getString("email");
				zip_code = object.getString("zip_code");
				avatar = object.getString("avatar");
				access = object.getString("access");
				role = object.getString("role");
				grade_name = object.getString("grade_name");
				grade_id = object.getString("grade_id");
				bio = object.getString("bio");
				videos = object.getString("videos");
				kids = object.getString("kids");
				career_info = object.getString("career_info");
				classes = object.getString("classes");
				HashMap<String, Object> map = new HashMap<>();

				map.put(VUtil.USER_ID, id);
				map.put(VUtil.USER_FIRSTNAME, first_name);
				map.put(VUtil.USER_LASTNAME, last_name);
				map.put(VUtil.USER_DOB, dob);
				map.put(VUtil.USER_EMAIL, email);
				map.put(VUtil.USER_ZIPCODE, zip_code);
				map.put(VUtil.USER_AVTAAR, avatar);
				map.put(VUtil.USER_ACCESS, access);
				map.put(VUtil.USER_ROLE, role);
				map.put(VUtil.USER_GRADNAME, grade_name);
				map.put(VUtil.USER_GRADID, grade_id);
				map.put(VUtil.USER_BIO, bio);
				map.put(VUtil.USER_VIDEOS, videos);
				map.put(VUtil.USER_KIDS, kids);
				map.put(VUtil.USER_CAREERINFO, career_info);
				map.put(VUtil.USER_CLASSES, classes);
				map.put(VUtil.USER_AUTOLOGIN, true);
				map.put(VUtil.USER_NAME, user);
				map.put(VUtil.USER_PASSWORD, pass);
				util.saveInPreference(map);
				
				index = 0;
				DelMykid();
				//DelMyClass();
				saveMyKid(kids,"");
				saveClassKid(classes);
//				if(access.equalsIgnoreCase("student") || access.equalsIgnoreCase("teacher")) getAsssigment();
//				else 
				finish();
				startActivity(new Intent(context, HomeActivity.class));
				overridePendingTransition(R.anim.slideleft1, R.anim.slideleft2);
			} else {
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
			}

		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

	}
	

	
	private void saveClassKid(String result) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		JSONArray array = null;
		String classid = "";
		String class_name = "";
		String class_type_id = "";
		String grade_name = "";
		String type_name = "";
		String rosters = "";
		String student_count = "";
		String kids = "[]";
		int length = 0;
		
		try{
			array = new JSONArray(result);
			length = array.length();
			if(length<=0) return;
			for(int i=0;i<length;i++){
				object = array.getJSONObject(i);
				classid = object.getString("id");
				class_name = object.getString("class_name");
				class_type_id = object.getString("class_type_id");
				grade_name = object.getString("grade_name");
				type_name = object.getString("type_name");
				rosters = object.getString("rosters");
				student_count = object.getString("student_count");
				kids = object.getString("kids");
				
				HashMap<String, Object> map = new HashMap<>();
				
				util.saveInPreference(map);
				//insertORUpdateMyClass(i, classid, class_name, class_type_id, grade_name, type_name, rosters, student_count);
				saveMyKid(kids,classid);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void saveMyKid(String kids,String class_id) {
		// TODO Auto-generated method stub
		JSONArray array = null;
		int length = 0;
		
		String id = "";
		String first_name = "";
		String username = "";
		String zip_code = "";
		
		String email = "";
		String career_id = "";
		String dob = "";
		String last_name = "";
		String role = "";
		String access = "";
		String grade_level_id = "";
		String password = "";
		JSONObject object = null;
		
		try{
			array = new JSONArray(kids);
			length = array.length();
			if(length<=0)
				return;
			for(int i=0;i<length;i++){
				object = array.getJSONObject(i);
				id = object.getString("id");
				first_name = object.getString("first_name");
				username = object.getString("username");
				zip_code = object.getString("zip_code");
				email = object.getString("email");
				career_id = object.getString("career_id");
				dob = object.getString("dob");
				last_name = object.getString("last_name");
				role = object.getString("role");
				access = object.getString("access");
				grade_level_id = object.getString("grade_level_id");
				password = object.getString("password");
				
				if (id == null)
					id = "";
				if (first_name == null)
					first_name = "";
				if (username == null)
					username = "";
				if (zip_code == null)
					zip_code = "";
				if (email == null)
					email = "";
				if (career_id == null)
					career_id = "";
				if (dob == null)
					dob = "";
				if (last_name == null)
					last_name = "";
				if (role == null)
					role = "";
				if (access == null)
					access = "";
				if (grade_level_id == null)
					grade_level_id = "";
				if (password == null)
					password = "";
				insertORUpdateMyKid(index, id, first_name, username, zip_code, email, career_id, dob, last_name, role, access, grade_level_id, password,class_id);
				index++;
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

/*	private void DelMyClass() {
		// TODO Auto-generated method stub
		try {
			database.execSQL("DELETE FROM mykid");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void insertORUpdateMyClass(int id, String class_id, String class_name, String class_type_id, String grade_name, String type_name, String rosters, String student_count) {
		// TODO Auto-generated method stub
		id += 1;
		database.execSQL("INSERT INTO myclass (id, class_id, class_name, class_type_id, grade_name, type_name, rosters, student_count) VALUES ("
				+ id
				+ ",\""
				+ class_id
				+ "\",\""
				+ class_name
				+ "\",\""
				+ class_type_id
				+ "\",\""
				+ grade_name
				+ "\",\""
				+ type_name
				+ "\",\""
				+ rosters
				+ "\",\""
				+ student_count + "\");");
	}*/
	
	private void DelMykid() {
		// TODO Auto-generated method stub
		try {
			database.execSQL("DELETE FROM mykid");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

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
	
	private VlearnDataBase helper;
	private Cursor cursor;
	private SQLiteDatabase database;
	
	private void initDataBase() {

		helper = new VlearnDataBase(context, VlearnDataBase.dbName, null,
				VlearnDataBase.version);
		database = helper.getWritableDatabase();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isAutoLogin = (boolean) util.getFromSharedPreference(VariableType.BOOLEAN, VUtil.USER_AUTOLOGIN);
		if(isAutoLogin){
			username.setText((CharSequence) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME));
			password.setText((CharSequence) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD));
			loginButton.performClick();
		}
	}


	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case GET_LOGIN:
			util.hideLoading();
			Toast.makeText(getBaseContext(), getString(R.string.serverError)+" "+getString(R.string.failedToLogin), Toast.LENGTH_SHORT).show();
			break;
		
		default:
			break;
		}
	}

	@Override
	protected void onStart()
	{
	super.onStart();
	FlurryAgent.onStartSession(this, getString(R.string.flurrykey));
	}
	@Override
	protected void onStop()
	{
	super.onStop();	
	FlurryAgent.onEndSession(this);
	}


}

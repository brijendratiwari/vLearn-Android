package com.vlearn.android;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.flurry.android.FlurryAgent;
import com.vlearn.android.database.VlearnDataBase;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.ui.picker.DatePickerFragment;
import com.vlearn.android.ui.picker.Picker;
import com.vlearn.android.util.VUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RegistrationActivity extends Activity implements OnNetworkResult {

	Button role,dob,gender, grad, are_you_representing, how_did_you_hear,schoolLevel;

	
	EditText firstName, LastName, Email, userName, password, zip_code,
			shortBio, reasonForIntrest, schoolZipCode, mobikeNo, schoolName;

	LinearLayout teacherSection, padrinoSection;

	String[] roleArray;
	String[] genderArray;
	String[] gradArray;
	String[] are_you_representingArray;
	String[] how_did_you_heargArray;
	String[] schoolLevelArray;

	Button registerButton, cancelButton;

	String roleTitle;
	String cancelTitle;
	String okTitle;
	String genderTitle;
	String gradTitle;
	String are_you_representingTitle = "Select Padrino";
	String how_did_you_hearTitle = "Select Padrino";
	String schoolLevelTitle = "Select Teacher Level";

	private final int RegisterCall = 101;
	private final int UploadCall = 102;
	private final int roleRequest = 0;
	private final int genderRequest = 1;
	private final int gradeRequest = 2;
	private final int are_you_representRequest = 3;
	private final int how_did_you_hereRequest = 4;
	private final int schoolLevelRequest = 5;

	private final int preferlang = 51;
	private final int preferMethod = 52;
	
	private final int GALLERy_PICK_PHOTO = 45;

	ImageView photoPick;
	NetworkConnection connection;
	VUtil util;
	Context context;
	private AsyncTask<String, Void, String> registerTask;
	InputMethodManager imm;
	private String access[] = new String[] { "parent", "teacher", "padrino" };

	LinearLayout parentSection;
	Button preferMethodOfCommunication,preferLanguageOfCommunication;
	EditText parentMobile;
	
	private String[] methodArray;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// set the view
				setContentView(R.layout.registration);
		imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		context = RegistrationActivity.this;
		util = new VUtil(context);

		methodArray = getResources().getStringArray(R.array.modeofcommunication);
		
		connection = new NetworkConnection(context);
		util.initLoader(RegistrationActivity.this,
				getString(R.string.register_please_wait));
		loadLanguage();
		if (!connection.isNetworkAvailable()) {
			Toast.makeText(context, getString(R.string.NO_NETWORK_),
					Toast.LENGTH_SHORT).show();
		}

		initRegisterResources();

		

		roleTitle = getString(R.string.Select_Role);
		cancelTitle = getString(R.string.Back_to_registration);
		okTitle = getString(R.string.Select);
		genderTitle = getString(R.string.Select_gender);
		gradTitle = getString(R.string.Select_Grade);

		role = (Button) findViewById(R.id.role);
		dob = (Button) findViewById(R.id.dob);
		gender = (Button) findViewById(R.id.gender);
		grad = (Button) findViewById(R.id.grade);
		are_you_representing = (Button) findViewById(R.id.are_you_represent);
		how_did_you_hear = (Button) findViewById(R.id.how_did_you);
		schoolLevel = (Button) findViewById(R.id.schoolLevel);

		registerButton = (Button) findViewById(R.id.register);
		cancelButton = (Button) findViewById(R.id.cancel);

		padrinoSection = (LinearLayout) findViewById(R.id.padrino);
		teacherSection = (LinearLayout) findViewById(R.id.teacher);

		parentSection = (LinearLayout) findViewById(R.id.parent);
		preferMethodOfCommunication 	= (Button) findViewById(R.id.prefermethod);
		preferLanguageOfCommunication 	= (Button) findViewById(R.id.preferlanguage);
		parentMobile 	= (EditText) findViewById(R.id.parentMobile);
		
		firstName = (EditText) findViewById(R.id.firstName);
		LastName = (EditText) findViewById(R.id.lastName);
		Email = (EditText) findViewById(R.id.emailAddress);
		mobikeNo = (EditText) findViewById(R.id.schoolMobileno);
		schoolName = (EditText) findViewById(R.id.schoolName);
		zip_code = (EditText) findViewById(R.id.zipcode);
		schoolZipCode = (EditText) findViewById(R.id.schoolZipCode);
		userName = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		shortBio = (EditText) findViewById(R.id.shorbio);
		reasonForIntrest = (EditText) findViewById(R.id.why_are_you_);

		photoPick = (ImageView) findViewById(R.id.no_photo_image_Button);

		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finishSelf();
			}
		});
		dob.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerFragment newFragment = new DatePickerFragment();
				newFragment.show(getFragmentManager(), "datePicker");
				newFragment
						.setOnDateChangeListener(new DatePickerFragment.DateChangeListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int month, int day) {
								// TODO Auto-generated method stub
								dob.setText(year + "/" + (month + 1) + "/"
										+ day);
							}

						});
			}
		});

		photoPick.setOnClickListener(onClickListenerPicker);
		registerButton.setOnClickListener(onClickListenerPicker);
		role.setOnClickListener(onClickListenerPicker);
		gender.setOnClickListener(onClickListenerPicker);
		grad.setOnClickListener(onClickListenerPicker);
		are_you_representing.setOnClickListener(onClickListenerPicker);
		how_did_you_hear.setOnClickListener(onClickListenerPicker);
		schoolLevel.setOnClickListener(onClickListenerPicker);
		
		preferMethodOfCommunication.setOnClickListener(onClickListenerPicker);
		preferLanguageOfCommunication.setOnClickListener(onClickListenerPicker);
		
		showOtherSection();
		saveToSdCard();
	}

	private void showTeacherSection() {
		padrinoSection.setVisibility(View.GONE);
		teacherSection.setVisibility(View.VISIBLE);
		parentSection.setVisibility(View.GONE);
		schoolLevel.setFocusable(false);
		schoolLevel.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/KRONIKA_.ttf"));
		schoolLevel.setFocusableInTouchMode(false);
	}

	private void loadLanguage(){ 
		util.showLoading(getString(R.string.please_wait));
		connection.getData("user/getParams/languages", "POST", null, this, getLanguageFromServer);
	}
	
	private void showPadrinoSection() {
		padrinoSection.setVisibility(View.VISIBLE);
		teacherSection.setVisibility(View.GONE);
		parentSection.setVisibility(View.GONE);
		are_you_representing.setFocusable(false);
		are_you_representing.setFocusableInTouchMode(false);
		how_did_you_hear.setFocusable(false);
		how_did_you_hear.setFocusableInTouchMode(false);
		are_you_representing.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/KRONIKA_.ttf"));
		how_did_you_hear.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/KRONIKA_.ttf"));
	}

	private void showOtherSection() {
		padrinoSection.setVisibility(View.GONE);
		teacherSection.setVisibility(View.GONE);
		parentSection.setVisibility(View.VISIBLE);
	}

	View.OnClickListener onClickListenerPicker = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getBaseContext(), Picker.class);
			Bundle bun = new Bundle();
			int request = 0;
			switch (v.getId()) {
			case R.id.role:
				request = roleRequest;
				bun.putString(Picker._PICKERTITLE, roleTitle);
				bun.putString(Picker._OKButton, roleTitle);
				bun.putString(Picker._CANCELButton, cancelTitle);
				bun.putStringArray(Picker._ListEntries, roleArray);
				break;
			case R.id.gender:
				request = genderRequest;
				bun.putString(Picker._PICKERTITLE, genderTitle);
				bun.putString(Picker._OKButton, genderTitle);
				bun.putString(Picker._CANCELButton, cancelTitle);
				bun.putStringArray(Picker._ListEntries, genderArray);
				break;
			case R.id.grade:
				request = gradeRequest;
				bun.putString(Picker._PICKERTITLE, gradTitle);
				bun.putString(Picker._OKButton, gradTitle);
				bun.putString(Picker._CANCELButton, cancelTitle);
				bun.putStringArray(Picker._ListEntries, gradArray);
				break;
			case R.id.are_you_represent:
				request = are_you_representRequest;
				bun.putString(Picker._PICKERTITLE, are_you_representingTitle);
				bun.putString(Picker._OKButton, okTitle);
				bun.putString(Picker._CANCELButton, cancelTitle);
				bun.putStringArray(Picker._ListEntries,
						are_you_representingArray);
				break;
			case R.id.how_did_you:
				request = how_did_you_hereRequest;
				bun.putString(Picker._PICKERTITLE, how_did_you_hearTitle);
				bun.putString(Picker._OKButton, okTitle);
				bun.putString(Picker._CANCELButton, cancelTitle);
				bun.putStringArray(Picker._ListEntries, how_did_you_heargArray);
				break;
			case R.id.schoolLevel:
				request = schoolLevelRequest;
				bun.putString(Picker._PICKERTITLE, schoolLevelTitle);
				bun.putString(Picker._OKButton, okTitle);
				bun.putString(Picker._CANCELButton, cancelTitle);
				bun.putStringArray(Picker._ListEntries, schoolLevelArray);
				break;
			case R.id.prefermethod:
				request = preferMethod;
				bun.putString(Picker._PICKERTITLE, getString(R.string.preferedMethodCommunication));
				bun.putString(Picker._OKButton, okTitle);
				bun.putString(Picker._CANCELButton, cancelTitle);
				bun.putStringArray(Picker._ListEntries, methodArray);
				break;
			 case R.id.preferlanguage:
				request = preferlang;
				bun.putString(Picker._PICKERTITLE, getString(R.string.preferedlangCommunication));
				bun.putString(Picker._OKButton, okTitle);
				bun.putString(Picker._CANCELButton, cancelTitle);
				bun.putStringArray(Picker._ListEntries, langArray.toArray(new String[langArray.size()]));
				break;
			case R.id.register:
				if (connection.isNetworkAvailable()) {
					if(!checkForEmptyField()){
						if (ImageFilePath.isEmpty())
							doRegister("");
						else
							uploadPhoto();
					}else{
						Toast.makeText(context, getString(R.string.All_fields_are_required), Toast.LENGTH_SHORT).show();
					}
				} else
					Toast.makeText(context, getString(R.string.NO_NETWORK_),
							Toast.LENGTH_SHORT).show();
				break;
			case R.id.no_photo_image_Button:
				Intent intent1 = new Intent();
				intent1.setType("image/*");
				if (Build.VERSION.SDK_INT < 19) {
					intent1.setAction(Intent.ACTION_GET_CONTENT);
				} else {
					intent1.addCategory(Intent.CATEGORY_OPENABLE);
				}

				startActivityForResult(
						Intent.createChooser(intent1, "Choose Pictures..."),
						GALLERy_PICK_PHOTO);
				
				break;
			default:
				break;
			}

			intent.putExtras(bun);
			startActivityForResult(intent, request);
			imm.hideSoftInputFromWindow(registerButton.getWindowToken(), 0);
			if(v.getId() != R.id.no_photo_image_Button)
				overridePendingTransition(R.anim.slideleft1, R.anim.slideleft2);
		}

	};

	protected void onPause() {
		super.onPause();
		util.releaseDaabase(database);
	};

	private SQLiteDatabase database;
	private VlearnDataBase helper;
	private Cursor cursor;

	private HashMap<String, String> roleMap = new HashMap<>();
	private HashMap<String, String> gradeMap = new HashMap<>();
	private HashMap<String, String> schoolLevelMap = new HashMap<>();

	private void initRegisterResources() {
		// load Databse Object;
		initDataBase();
		int length = 0;

		// load role
		cursor = database.rawQuery("SELECT role_id,name FROM role", null);
		length = cursor.getCount();
		cursor.moveToFirst();
		if (length > 0) {
			roleMap.clear();
			roleArray = new String[length];
			for (int i = 0; i < length; i++) {
				String title = cursor.getString(cursor.getColumnIndex("name"));
				roleMap.put(title,
						cursor.getString(cursor.getColumnIndex("role_id")));
				roleArray[i] = title;
				cursor.moveToNext();
			}
		}

		// load Gender
		genderArray = new String[] {
				RegistrationActivity.this.getString(R.string.Male),
				RegistrationActivity.this.getString(R.string.Female) };

		// load Gender
		String columnname = "englishName";
		if (!VUtil.IS_APP_LANG_ENG)
			columnname = "spanishName";

		cursor = database.rawQuery("SELECT grade_id," + columnname
				+ " FROM grades", null);
		length = cursor.getCount();
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
				cursor.moveToNext();
			}
		}

		// load are you representing padrino section
		cursor = database.rawQuery("SELECT data FROM padreno_indivdual", null);
		length = cursor.getCount();
		cursor.moveToFirst();
		if (length > 0) {
			are_you_representingArray = new String[length];
			for (int i = 0; i < length; i++) {
				String title = cursor.getString(cursor.getColumnIndex("data"));
				are_you_representingArray[i] = title;
				cursor.moveToNext();
			}
		}

		// load how did you hear about us padrino social secion
		cursor = database.rawQuery("SELECT data FROM padreno_social", null);
		length = cursor.getCount();
		cursor.moveToFirst();
		if (length > 0) {
			how_did_you_heargArray = new String[length];
			for (int i = 0; i < length; i++) {
				String title = cursor.getString(cursor.getColumnIndex("data"));
				how_did_you_heargArray[i] = title;
				cursor.moveToNext();
			}
		}

		// load School Level Section
		cursor = database.rawQuery(
				"SELECT school_levelid,level_name FROM school_level", null);
		length = cursor.getCount();
		cursor.moveToFirst();
		if (length > 0) {
			schoolLevelMap.clear();
			schoolLevelArray = new String[length];
			for (int i = 0; i < length; i++) {
				String title = cursor.getString(cursor
						.getColumnIndex("level_name"));
				schoolLevelMap.put(title, cursor.getString(cursor
						.getColumnIndex("school_levelid")));
				schoolLevelArray[i] = title;
				cursor.moveToNext();
			}
		}

	}

	private void initDataBase() {

		helper = new VlearnDataBase(context, VlearnDataBase.dbName, null,
				VlearnDataBase.version);
		database = helper.getReadableDatabase();
	}

	@SuppressLint("NewApi")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {

			switch (requestCode) {
			case roleRequest:
				String text = data.getStringExtra("result");
				if (text.toLowerCase().contains("padrino")) {
					showPadrinoSection();
				} else if (text.toLowerCase().contains("teacher")) {
					showTeacherSection();
				} else {
					showOtherSection();
				}
				role.setText(text);
				break;
			case genderRequest:
				gender.setText(data.getStringExtra("result"));
				break;
			case gradeRequest:
				grad.setText(data.getStringExtra("result"));
				break;
			case are_you_representRequest:
				are_you_representing.setText(data.getStringExtra("result"));
				break;
			case how_did_you_hereRequest:
				how_did_you_hear.setText(data.getStringExtra("result"));
				break;
			case schoolLevelRequest:
				schoolLevel.setText(data.getStringExtra("result"));
				break;
			case preferMethod:
				preferMethodOfCommunication.setText(data.getStringExtra("result"));
				break;
			case preferlang:
				preferLanguageOfCommunication.setText(data.getStringExtra("result"));
				preferLanguageOfCommunication.setTag(langMap.get(data.getStringExtra("result")));
				break;
			case GALLERy_PICK_PHOTO:
				Uri originalUri = null;

				if (Build.VERSION.SDK_INT < 19) {
					originalUri = data.getData();
				} else {
					originalUri = data.getData();
					final int takeFlags = data.getFlags()
							& (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
					// Check for the freshest data.
					getContentResolver().takePersistableUriPermission(
							originalUri, takeFlags);

				}
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(originalUri,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				ImageFilePath = filePath;
				cursor.close();

				photoPick.setImageBitmap(VUtil.decodeFile(new File(filePath),
						70, 70));
				break;

			default:
				break;
			}

		}
	}

	private void saveToSdCard(){
		File vFile = null;
		FileOutputStream out = null;
		try {
			vFile = new File(Environment.getExternalStorageDirectory(),
					"VLearn");
			vFile.mkdirs();
			vFile = new File(vFile, "profile.jpg");
			if (vFile.exists())
				vFile.delete();
			vFile.createNewFile();
			out = new FileOutputStream(vFile);
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
			bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
			ImageFilePath = vFile.getAbsolutePath();
			photoPick.setImageBitmap(bmp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Throwable ignore) {
			}
		}

	}
	
	String ImageFilePath = "";
	String ImageFileName = "";

	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case RegisterCall:
			onRegisterPerform(result);
			break;
		case UploadCall:
			onPhotoUpload(result);
			break;
		case getLanguageFromServer:
			onLoadLanguage(result);
			break;
		default:
			break;
		}
	}
HashMap<String, String> langMap = new HashMap<>();
ArrayList<String> langArray = new ArrayList<>();
	private void onLoadLanguage(String result) {
		util.hideLoading();
		// TODO Auto-generated method stub
		
		boolean error;
		String errorMsg;
		JSONObject object;
		
		try{
			object =  new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("error");
			if(error){
				util.hideLoading();
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
			}else{
				langMap.clear();
				langArray.clear();
				object = object.getJSONObject("data");
				Iterator<String> it = object.keys();
				while(it.hasNext()){
					String key = it.next();
					String value = object.getString(key);
					langMap.put(value,key);
					langArray.add(value);
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			util.hideLoading();
			Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		
	}

	private void onPhotoUpload(String result) {
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
			status = object.getString("status");
			if(!error){
				fileName = object.getString("fileName");
				fileUrl = object.getString("fileUrl");
				doRegister(fileName);
			}else{
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	private void onRegisterPerform(String str) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		boolean error = false;
		String errorMsg = "";
		String message = "";
		String id = "";
		try {

			object = new JSONObject(str);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			if (!error) {
				message = object.getString("message");
				id = object.getString("id");
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
				HashMap<String, Object> map = new HashMap<>();
				map.put(VUtil.USER_NAME, username);
				map.put(VUtil.USER_PASSWORD, pass);
				map.put(VUtil.USER_ID, id);
				map.put(VUtil.USER_AUTOLOGIN, true);
				util.saveInPreference(map);
				finishSelf();
				// util.showLoading();
			} else {
				util.hideLoading();
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
			util.hideLoading();
		}

	};

	private void finishSelf() {
		// TODO Auto-generated method stub
		finish();
		imm.hideSoftInputFromWindow(registerButton.getWindowToken(), 0);
		overridePendingTransition(R.anim.ideal, R.anim.slidedwn2);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finishSelf();
	}
	
	String username = "";
	String pass = "";
	List<NameValuePair> al = new ArrayList<NameValuePair>();

	private void uploadPhoto() {
		al.clear();
		String roletitle = role.getText().toString().trim();

		if (roletitle.toLowerCase().contains("teacher")) {
			roletitle = access[1];
		} else if (roletitle.toLowerCase().contains("padrino")) {
			roletitle = access[2];
		} else {
			roletitle = access[0];
		}
		
		util.showLoading(getString(R.string.register_please_wait));
		
		try{
		 MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
         File file = new File (VUtil.getCompressFile(new File(ImageFilePath), 120, 120));
         entity.addPart("file", new FileBody(file));
         entity.addPart("access", new StringBody(roletitle));
         connection.getData("user/uploadProfilePhoto", entity, RegistrationActivity.this, UploadCall);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		 		
	}

	
	private void doRegister(String fileName) {
		// TODO Auto-generated method stub
		
		String firstName = "";
		String lastName = "";
		String email = "";
		String dob = "";
		String zipcode = "";
		String gender = "";
		String grade_level_id = "";
		String role_id = "";
		String role_title = "";
		String bio = "";
		String representing = "";
		String why_interested = "";
		String hear_about_us = "";
		String image = "";
		String school_name = "";
		String school_level = "";
		String school_zip = "";
		String mobile = "";
		String grade;
		
		String modeofcommunication = "";
		String communicationlanguage = "";
		String othermobileno = "";
		
		// role
		role_title = role.getText().toString();
		// set image 
		
		image = fileName;
		
		modeofcommunication 	= this.preferMethodOfCommunication.getText().toString().trim();
	    communicationlanguage	= this.preferLanguageOfCommunication.getTag() == null?"":(String)this.preferLanguageOfCommunication.getTag();
	    othermobileno 			= this.parentMobile.getText().toString().trim();
	    
		// firstName
		firstName = this.firstName.getText().toString().trim();
		// lastName
		lastName = LastName.getText().toString().trim();
		// email
		email = Email.getText().toString().trim();
		// username
		username = userName.getText().toString().trim();
		// password
		pass = password.getText().toString().trim();
		// dob
		dob = this.dob.getText().toString().trim();
		// set zipcode
		zipcode = zip_code.getText().toString().trim();
		// set gender
		gender = this.gender.getText().toString().trim();
		// set grade
		grade = this.grad.getText().toString().trim();
		// set bio
		bio = this.shortBio.getText().toString().trim();
		// are your representing you oraganitaion
		representing = are_you_representing.getText().toString().trim();
		// why are you interested in becoming a Learning Padrino/Madrino?
		why_interested = reasonForIntrest.getText().toString().trim();
		// How did you hear about us?
		hear_about_us = how_did_you_hear.getText().toString().trim();
		// schoolName
		school_name = schoolName.getText().toString().trim();
		// school Level
		school_level = schoolLevel.getText().toString().trim();
		// school zip code
		school_zip = schoolZipCode.getText().toString().trim();
		// mobile no
		mobile = mobikeNo.getText().toString().trim();

		if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
				|| username.isEmpty() || pass.isEmpty() || dob.isEmpty()
				|| zipcode.isEmpty() || gender.isEmpty()
				|| role_title.isEmpty()
				|| role_title.equalsIgnoreCase(getString(R.string.Role))
				|| bio.isEmpty() || grade.isEmpty()) {
			Toast.makeText(context, getString(R.string.value_missing),
					Toast.LENGTH_SHORT).show();
			util.hideLoading();
			return;
		}

		if (role_title.toLowerCase().contains("teacher")) {
			
			if (school_zip.isEmpty() || school_level.isEmpty() || school_name.isEmpty()) {
				Toast.makeText(context, getString(R.string.value_missing),
						Toast.LENGTH_SHORT).show();
				util.hideLoading();
				return;
			}
			
		} else if (role_title.toLowerCase().contains("padrino")) {

			if (representing.isEmpty() || why_interested.isEmpty() || hear_about_us.isEmpty()) {
				Toast.makeText(context, getString(R.string.value_missing),
						Toast.LENGTH_SHORT).show();
				util.hideLoading();
				return;
			}
		}else{
			//|| email.isEmpty()
			mobile = othermobileno;
			if (modeofcommunication.isEmpty() || communicationlanguage.isEmpty() ) {
				Toast.makeText(context, getString(R.string.value_missing),
						Toast.LENGTH_SHORT).show();
				util.hideLoading();
				return;
			}
			
//			/|| othermobileno.isEmpty()|| email.isEmpty()
			
			if (!modeofcommunication.equalsIgnoreCase(methodArray[0]) && othermobileno.isEmpty()) {
				Toast.makeText(context, getString(R.string.value_missing),
						Toast.LENGTH_SHORT).show();
				util.hideLoading();
				return;
			}
			
			
		}

		if(!VUtil.isEmailValid(email)){
			Toast.makeText(this,getString(R.string.vailedemail), Toast.LENGTH_SHORT).show();
			util.hideLoading();
			return;
		}

		role_id = roleMap.get(role_title);
		grade_level_id = gradeMap.get(grade);

		al.clear();

		// required
		al.add(new BasicNameValuePair("firstName", firstName));
		al.add(new BasicNameValuePair("lastName", lastName));
		al.add(new BasicNameValuePair("email", email));
		al.add(new BasicNameValuePair("username", username));
		al.add(new BasicNameValuePair("pass", pass));
		al.add(new BasicNameValuePair("dob", dob));
		al.add(new BasicNameValuePair("zipcode", zipcode));
		al.add(new BasicNameValuePair("gender", gender));
		al.add(new BasicNameValuePair("grade_level_id", grade_level_id));
		al.add(new BasicNameValuePair("role_id", role_id));
		al.add(new BasicNameValuePair("role_title", role_title));
		al.add(new BasicNameValuePair("bio", bio));

		// not required
		al.add(new BasicNameValuePair("image", image));// filename

		// padrino
		// not required
		al.add(new BasicNameValuePair("representing", representing));
		al.add(new BasicNameValuePair("why_interested", why_interested));
		al.add(new BasicNameValuePair("hear_about_us", hear_about_us));

		// Teacher role
		// not required
		al.add(new BasicNameValuePair("school_name", school_name));
		al.add(new BasicNameValuePair("school_level", school_level));
		al.add(new BasicNameValuePair("school_zip", school_zip));
		al.add(new BasicNameValuePair("mobile", mobile));

		//for parents
		al.add(new BasicNameValuePair("pmoc", modeofcommunication));
		al.add(new BasicNameValuePair("ploc", communicationlanguage));
		
		registerTask = connection.getData("user/register", "POST", al,
				RegistrationActivity.this, RegisterCall);
	}
	
	private boolean checkForEmptyField(){
		if (firstName.getText().toString().trim().isEmpty() || LastName.getText().toString().trim().isEmpty() || Email.getText().toString().trim().isEmpty()
				|| userName.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty() || dob.getText().toString().trim().isEmpty()
				|| zip_code.getText().toString().trim().isEmpty() || gender.getText().toString().trim().isEmpty()
				|| role.getText().toString().trim().isEmpty()
				|| role.getText().toString().trim().equalsIgnoreCase(getString(R.string.Role))
				|| shortBio.getText().toString().trim().isEmpty() || grad.getText().toString().trim().isEmpty()) {
			Toast.makeText(context, getString(R.string.value_missing),
					Toast.LENGTH_SHORT).show();
			util.hideLoading();
			return true;
		}
		return false;
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case RegisterCall:
			Toast.makeText(getBaseContext(), getString(R.string.serverError)+" "+getString(R.string.failedToRegister), Toast.LENGTH_SHORT).show();
			break;
		case getLanguageFromServer:
		case UploadCall:
			Toast.makeText(getBaseContext(), getString(R.string.serverError)+" "+getString(R.string.failedToUploadImage), Toast.LENGTH_SHORT).show();
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

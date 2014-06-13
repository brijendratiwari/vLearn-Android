package com.vlearn.android.setting.mykid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

public class AddNewKid extends Fragment implements OnNetworkResult {

	HomeActivity activity;
	Context context;
	
	EditText firstName, lastName, username, password;
	Button grade;
	RadioButton male,female;
	RadioGroup gender;
	Button cancel,save,logout;
	ImageView profilePic;
	
	NetworkConnection connection;
	VUtil util;
	ImageLoader loader;
	
	int kidCareerPicker = 109;
	int classTypePicker = 501;
	int classGradePicker = 502;
	
	View layout1,layout2;
	
	String career_id = "";
	String career_careers_id = "";
	String career_name = "";
	String career_imgUrl = "";
	
	private boolean isTeacher = false;
	
	private final int gradeRequest = 110;
//	private String classData;
	
	private static Mykid mykid;
	private boolean isFirst = false;
	EditText className,gradeName;
	Button classType,classgrade;
	Button submitButton;
	//cancelButton
	
	public static AddNewKid Create(Mykid mykid){
		AddNewKid addNewKid = new AddNewKid();
		AddNewKid.mykid = mykid;
		return addNewKid;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isFirst = true;
		context = activity = (HomeActivity) getActivity();
		//setStyle(STYLE_NO_FRAME, 0);
		loader = activity.getImageLoader();
		util = activity.getVUtil();
		connection = activity.getNetworkConnection();
		initDataBase();
		getGradeList();
		getclassType();
		isTeacher = !((String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ACCESS)).contains("parent");
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.addnewkid, null, false);
		
		firstName =  (EditText) view.findViewById(R.id.firstName);
		lastName =  (EditText) view.findViewById(R.id.lastName);
		grade =  (Button) view.findViewById(R.id.grade);
		username =  (EditText) view.findViewById(R.id.username);
		password =  (EditText) view.findViewById(R.id.password);
		
		username.setOnFocusChangeListener(onFocusChangeListener);
		
		gender =  (RadioGroup) view.findViewById(R.id.gender);
		
		layout1 = view.findViewById(R.id.layout1);
		layout2 = view.findViewById(R.id.layout2);
		
		male =  (RadioButton) view.findViewById(R.id.male);
		female =  (RadioButton) view.findViewById(R.id.female);
		
		cancel =  (Button) view.findViewById(R.id.cancel);
		save =  (Button) view.findViewById(R.id.save);
		logout =  (Button) view.findViewById(R.id.logout); 
		
		className =  (EditText) view.findViewById(R.id.className);
		gradeName =  (EditText) view.findViewById(R.id.gradeName);
		classType =  (Button) view.findViewById(R.id.classType);
		classgrade =  (Button) view.findViewById(R.id.classgrade);
		
//		cancelButton =  (Button) view.findViewById(R.id.cancelButton);
		submitButton =  (Button) view.findViewById(R.id.submitButton);
		
		classgrade.setOnClickListener(onClickListenerClass);
		classType.setOnClickListener(onClickListenerClass);
		submitButton.setOnClickListener(onClickListenerClass);
		
		profilePic =  (ImageView) view.findViewById(R.id.profilePic);
		registerForContextMenu(save);
		save.setOnClickListener(onClickListener);
		cancel.setOnClickListener(onClickListener);
		logout.setOnClickListener(onClickListener);
		profilePic.setOnClickListener(onClickListener);
		grade.setOnClickListener(onClickListener);
		
		return view;
	}
	
	View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(!hasFocus){
				String un = username.getText().toString().trim();
				checkForAvailble(un);
			}
		}
	};
	
	private String[] classsType;
	View.OnClickListener onClickListenerClass = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent;
			Bundle bun;
			switch (v.getId()) {
			case R.id.classType:
				intent = new Intent(context, Picker.class);
				bun = new Bundle();
				bun.putString(Picker._PICKERTITLE, 	"Select Class Type");
				bun.putString(Picker._OKButton, "OK");
				bun.putString(Picker._CANCELButton, "CANCEL");
				bun.putStringArray(Picker._ListEntries, classsType);
				intent.putExtras(bun);
				startActivityForResult(intent, classTypePicker);
				activity.overridePendingTransition(R.anim.slideleft1, R.anim.slideleft2);
				break;
			case R.id.classgrade:
				intent = new Intent(context, Picker.class);
				bun = new Bundle();
				bun.putString(Picker._PICKERTITLE, 	"Select Grade");
				bun.putString(Picker._OKButton, "OK");
				bun.putString(Picker._CANCELButton, "CANCEL");
				bun.putStringArray(Picker._ListEntries, gradArray);
				intent.putExtras(bun);
				startActivityForResult(intent, classGradePicker);
				activity.overridePendingTransition(R.anim.slideleft1, R.anim.slideleft2);
				break;
			case R.id.submitButton:
				createKidWithClass();
				break;

			default:
				break;
			}
		}
		
	};
	
	private String[] gradArray;
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			Intent intent;
			Bundle bun;
			
			switch(v.getId()){
				case R.id.save:
					createnewKid();
					break;
				case R.id.cancel:
				//	getDialog().dismiss();
					mykid.hideView();
					break;
				case R.id.logout:
					util.saveInPreference(VUtil.USER_AUTOLOGIN, false);
					activity.finish();
					break;
				case R.id.grade:
					intent = new Intent(context, Picker.class);
					bun = new Bundle();
					bun.putString(Picker._PICKERTITLE, 	context.getString(R.string.Select_Grade));
					bun.putString(Picker._OKButton, context.getString(R.string.Select_Grade));
					bun.putString(Picker._CANCELButton, context.getString(R.string.Back_to_registration));
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
				cursor.moveToNext();
			}
		}
	}
	
	

	protected void checkForAvailble(String username) {
		// TODO Auto-generated method stub
		al.clear();
		al.add(new BasicNameValuePair("username", username));
		task = connection.getData("user/check_username", "POST", al, this, getAvailablity);
	}

	private HashMap<String, String> classTypeMap = new HashMap<>();
	private void getclassType(){
		

		cursor = database.rawQuery("SELECT * FROM classType", null);
		int length = cursor.getCount();
		cursor.moveToFirst();
		if (length > 0) {
			classTypeMap.clear();
			classsType = new String[length];
			for (int i = 0; i < length; i++) {
				String title = cursor.getString(cursor
						.getColumnIndex("name"));
				classTypeMap.put(title,
						""+cursor.getInt(cursor.getColumnIndex("id")));
				classsType[i] = title;
				cursor.moveToNext();
			}
		}
	}
	
	List<NameValuePair> al = new ArrayList<>();
	private AsyncTask<String, Void, String> task;
	private OnKidCreate listener;
	private String firstNameString;
	private String lastNameString;
	private String genderString;
	private String grade_level_idString;
	private String usernameString;
	private String passwordString;
	private String classId = "";
	private void createnewKid() {
		// TODO Auto-generated method stub
		if(checkForEmptyKid()) return;		
		if(isTeacher) {
			mykid.loadSchoolTeacher();
		}
		else{
			saveKid();
		}
	}
	
	private String classNameString;
	private String classTypeString;
	private String classGradeString;
	private String classGradeNameString;
	
	public void resetView(){
		className.setText("");
		gradeName.setText("");
		classgrade.setText("");
		classType.setText("");
		firstName.setText("");
		lastName.setText("");
		grade.setText("");
		username.setText("");
		password.setText("");
	}
	
	protected void createKidWithClass() {
		// TODO Auto-generated method stub
		if(checkForEmptyClass()) return;	
		
		classNameString = className.getText().toString().trim();
		classGradeNameString = gradeName.getText().toString().trim();
		classGradeString = ((String)classgrade.getTag()).toString().trim();
		classTypeString = ((String)classType.getTag()).toString().trim();
		saveKid(classGradeNameString, classGradeString, classNameString, classTypeString);
	}
	
	private void saveKid(){
		saveKid("",false,"","","","");
	}
	
	public void saveKid(String class_id){
		saveKid(class_id,false,"","","","");
	}
	
	private void saveKid(String class_grade_name,String class_grade,String class_name,String class_type){
		saveKid("",true,class_grade_name, class_grade, class_name,class_type);
	}
	
	private void saveKid(String class_id,boolean isNEW,String class_grade_name,String class_grade,String class_name,String class_type){
		
		firstNameString = this.firstName.getText().toString().trim();
		lastNameString = this.lastName.getText().toString().trim();
		genderString = male.isChecked()?"M":"F";
		grade_level_idString = (String) this.grade.getTag();
		usernameString = this.username.getText().toString().trim();
		passwordString = this.password.getText().toString().trim();
		
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("userId", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ID)));
		al.add(new BasicNameValuePair("gender", genderString));
		al.add(new BasicNameValuePair("grade_level_id", grade_level_idString));
		al.add(new BasicNameValuePair("lastName", lastNameString));
		al.add(new BasicNameValuePair("name", firstNameString));
		al.add(new BasicNameValuePair("username", usernameString));
		al.add(new BasicNameValuePair("password", passwordString));
		al.add(new BasicNameValuePair("career_id", career_careers_id));
		if(!class_id.isEmpty()) al.add(new BasicNameValuePair("class_id", class_id)); 
		if(isNEW){
			al.add(new BasicNameValuePair("class_id", ""));
			al.add(new BasicNameValuePair("class_grade_name", class_grade_name));
			al.add(new BasicNameValuePair("class_grade", class_grade));
			al.add(new BasicNameValuePair("class_name", class_name));
			al.add(new BasicNameValuePair("class_type", class_type));
			
		}
		showkidAdd();
		resetView();
		mykid.hideView();
		task = connection.getData("user/addNewKid", "POST", al, AddNewKid.this, addnewKidParent);
	}
	
		public boolean checkForEmptyClass(){
		
	
		if(className.getText().toString().trim().isEmpty()){
			Toast.makeText(context, ""+context.getString(R.string.enterclassname), Toast.LENGTH_SHORT).show();
			return true;
		}
		
		if(classgrade.getText().toString().trim().isEmpty()){
			Toast.makeText(context, ""+context.getString(R.string.enterclassgrade), Toast.LENGTH_SHORT).show();
			return true;
		}
		
		if(classType.getText().toString().trim().isEmpty() || grade.getText().toString().trim().equalsIgnoreCase("grade")){
			Toast.makeText(context, ""+context.getString(R.string.sleectclasstype), Toast.LENGTH_SHORT).show();
			return true;
		}
		
		if(gradeName.getText().toString().trim().isEmpty()){
			Toast.makeText(context, ""+context.getString(R.string.entergradename), Toast.LENGTH_SHORT).show();
			return true;
		}
		
		return false;
	}
	
	public boolean checkForEmptyKid(){
		
		if(career_careers_id.trim().isEmpty()){
			Toast.makeText(context, "Select kid Avatar", Toast.LENGTH_SHORT).show();
			return true;
		}
		
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
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isFirst){
			isFirst = false;
			showkidAdd();
		}
	}

	protected void showkidAdd(){
		layout1.setVisibility(View.VISIBLE);
		layout2.setVisibility(View.GONE);
	}
	
	protected void showClass(){
		layout1.setVisibility(View.GONE);
		layout2.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case addnewKidParent:
			if(listener != null) listener.onKidCreate(result, genderString, grade_level_idString, lastNameString, firstNameString, usernameString, passwordString, career_careers_id, career_imgUrl, classId);
			//getDialog().dismiss();
			mykid.hideView();
			break;
		case getAvailablity:
			onCheckForAvailble(result);
			break;
		default:
			break;
		}
	}
	
	private void onCheckForAvailble(String result) {
		// TODO Auto-generated method stub
		 boolean error;
		 String errorMsg;
		 JSONObject object = null;
		 try{
			 object = new JSONObject(result);
			 error = object.getBoolean("error");
			 errorMsg = object.getString("errorMsg");
			 if(error){
				 new AlertDialog.Builder(context).setTitle("Alert!").setMessage(errorMsg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
				 
			 }
		 }catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
		}
	}

/*	
	String classId2 = "";
	public void setClassId(String classId){
		this.classId2 = classId;
	}*/

	public void setOnKidCreate(OnKidCreate listener){
		this.listener = listener;
	}
	
	public interface OnKidCreate{
		void onKidCreate(String result,String gender,String grade_level_id,String lastName,String name,String username,String password,String career_id,String imgUrl,String class_id);
	}

	private VlearnDataBase helper;
	private Cursor cursor;
	private SQLiteDatabase database;
	
	private void initDataBase() {

		helper = new VlearnDataBase(context, VlearnDataBase.dbName, null,
				VlearnDataBase.version);
		database = helper.getReadableDatabase();
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
		
		if(requestCode == classGradePicker){
			try{
				classgrade.setText(data.getStringExtra("result"));
				classgrade.setTag(gradeMap.get(data.getStringExtra("result")));
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				}
		}
		
		if(requestCode == classTypePicker){
			try{
				classType.setText(data.getStringExtra("result"));
				classType.setTag(classTypeMap.get(data.getStringExtra("result")));
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				}
		}
	}

	int dbid = 0;

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
	
		switch (callingId) {
		case addnewKidParent:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedTocreatekid), Toast.LENGTH_SHORT).show();
			break;
		case getAvailablity:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedTocheckusername), Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	
}

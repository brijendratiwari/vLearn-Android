package com.vlearn.android;

import java.util.Iterator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.vlearn.android.database.VlearnDataBase;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.ui.CustomAdapter;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnNetworkResult {

	Spinner spinner;
	Button start;
	TextView textView;
	Context context = MainActivity.this;
	View container, mainView;
	ProgressBar progressBar;

	private final int ENGLISH = 0;
	private final int SPANISH = 1;

	String[] entries;

	NetworkConnection connection;
	private VlearnDataBase helper;
	private Cursor cursor;
	private SQLiteDatabase database;

	private static final int GET_SUBJECT = 101;
	private static final int GET_ROLE = 102;
	private static final int GET_CAREER = 103;
	private static final int GET_PADRINO = 104;
	private static final int GET_PADRIN_SOCIAL = 105;
	private static final int GET_SCHOOL_LEVEL = 106;
	private static final int GET_CLASSTYPE = 107;

	private VUtil util;
	AsyncTask<String, Void, String> taskSubject, taskRole, taskCareer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		util = new VUtil(context);
		VUtil.IS_APP_LANG_ENG = true;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		connection = new NetworkConnection(context);

		if (!connection.isNetworkAvailable()
				&& !(boolean) util.getFromSharedPreference(
						VariableType.BOOLEAN, VUtil.ISDATASAVED)) {
			showExitErrorDialog();
		}

		mainView = findViewById(R.id.mainView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		container = findViewById(R.id.container);
		spinner = (Spinner) findViewById(R.id.spinner);
		start = (Button) findViewById(R.id.startButton);
		textView = (TextView) findViewById(R.id.poweredby);

		entries = getResources().getStringArray(R.array.languageOption);
		spinner.setAdapter(new CustomAdapter(context,
				R.layout.spinner_text_view, entries, "fonts/KRONIKA_.ttf"));
		spinner.setOnItemSelectedListener(itemSelectedListener);

		
		if (getIntent().getExtras() == null) {
			new Handler().post(new Runnable() {
				public void run() {
					loadData();
				}
			});
		} else {
			new Handler().postDelayed(new Runnable() {        
			    public void run() {
			    	mainView.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
					if(VUtil.IS_APP_LANG_ENG){
						spinner.setSelection(0);
					}else{
						spinner.setSelection(1);
					}
			    }
			  }, 100);
			
		}

	}
	
	
	/**
	 * loadData() first time load role then it will be load grade and stages
	 * grade & subject
	 */
	private void loadData() {
		taskRole = connection.getData("user/getRoles", "GET", null, this,
				GET_ROLE);

	}

	String localCode = "en";

	AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			String localCode = "";
			if (position == ENGLISH) {
				localCode = "en";
				VUtil.IS_APP_LANG_ENG = true;
			} else if (position == SPANISH) {
				localCode = "es";
				VUtil.IS_APP_LANG_ENG = false;
			}
			if (!MainActivity.this.localCode.equals(localCode)) {
				MainActivity.this.localCode = localCode;
				setLocale(localCode);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

	public void startButtonClick(View v) {
		// start Button Click
		finish();
		startActivity(new Intent(getBaseContext(), LoginActivity.class));
		overridePendingTransition(R.anim.slideup1, R.anim.slideup2);
		
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initDataBase();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		util.releaseDaabase(database);
	}

	/**
	 * This method is used to change application Locale.
	 * 
	 * @param localeCode
	 *            en-us,es
	 */
	private void setLocale(String localeCode) {
		Locale locale = new Locale(localeCode);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
		getApplicationContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
		start.setText(getString(R.string.start));
		textView.setText(getString(R.string.poweredby));
	}

	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub

		switch (callingId) {
		case GET_SUBJECT:
			loadSubjectGradeStage(result);
			taskSubject = connection.getData(
					"user/getParams/padrino/hear_about_us", "GET", null, this,
					GET_PADRIN_SOCIAL);

			break;
		case GET_ROLE:
			loadRole(result);
			taskCareer = connection.getData("user/getCareers", "GET", null,
					this, GET_CAREER);
			break;
		case GET_CAREER:
			loadCareer(result);
			taskSubject = connection.getData(
					"user/getStagesAndGradesAndSubjects", "GET", null, this,
					GET_SUBJECT);
			break;
		case GET_PADRIN_SOCIAL:
			loadPadrinoSocial(result);
			taskSubject = connection.getData(
					"user/getParams/padrino/representing", "GET", null, this,
					GET_PADRINO);
			break;
		case GET_PADRINO:
			loadPadrino(result);
			taskSubject = connection.getData("user/getParams/classtypes",
					"GET", null, this, GET_CLASSTYPE);
			break;
		case GET_SCHOOL_LEVEL:
			loadSchoolLevel(result);
			mainView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			break;
		case GET_CLASSTYPE:
			loadClassType(result);
			taskSubject = connection.getData("user/getParams/school_levels",
					"GET", null, this, GET_SCHOOL_LEVEL);
		default:
			break;
		}

	}

	private void loadSchoolLevel(String result) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		JSONArray array = null;

		boolean error;
		String errorMsg;
		int length = 0;

		try {

			object = new JSONObject(result);
			error = Boolean.parseBoolean(object.getString("error"));
			errorMsg = object.getString("errorMsg");
			array = object.getJSONArray("data");
			length = array.length();

			if (error) {
				Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

		try {

			if (length <= 0) {
				return;
			}
			object = null;
			String id;
			String title;
			DelSchoolLevel();
			for (int i = 0; i < length; i++) {
				object = array.getJSONObject(i);

				id = object.getString("id");
				title = object.getString("level_name");
				insertORUpdateroleSchoolLevel(i, id, title);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}
		
		if((boolean) util.getFromSharedPreference(VariableType.BOOLEAN, VUtil.USER_AUTOLOGIN)){
			startButtonClick(null);
		}
		
	}

	private void loadClassType(String result) {
		JSONObject object = null;
		JSONArray array = null;

		boolean error;
		String errorMsg;
		String status;
		int length = 0;

		try {

			object = new JSONObject(result);
			error = Boolean.parseBoolean(object.getString("error"));
			errorMsg = object.getString("errorMsg");
			array = object.getJSONArray("data");
			length = array.length();

			if (error) {
				Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

		try {

			if (length <= 0) {
				return;
			}
			object = null;
			String id;
			String title;
			DelclassType();
			for (int i = 0; i < length; i++) {
				object = array.getJSONObject(i);

				id = object.getString("id");
				title = object.getString("name");
				insertORUpdateroleClassType(Integer.parseInt(id), title);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}
	}

	private void loadPadrinoSocial(String result) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		JSONObject array = null;

		boolean error;
		String errorMsg;
		int length = 0;
		Iterator<String> it;

		try {

			object = new JSONObject(result);
			error = Boolean.parseBoolean(object.getString("error"));
			errorMsg = object.getString("errorMsg");
			array = object.getJSONObject("data");
			it = array.keys();
			length = array.length();

			if (error) {
				Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

		try {

			if (length <= 0) {
				return;
			}
			object = null;
			String title;
			DelPadrinoSocial();
			int i = 0;
			while (it.hasNext()) {
				i++;
				String string = (String) it.next();
				title = array.getString(string);
				insertORUpdaterolePadrinoSocial(i, title);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}
	}

	private void loadPadrino(String result) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		JSONObject array = null;

		boolean error;
		String errorMsg;
		int length = 0;
		Iterator<String> it;

		try {

			object = new JSONObject(result);
			error = Boolean.parseBoolean(object.getString("error"));
			errorMsg = object.getString("errorMsg");
			array = object.getJSONObject("data");
			it = array.keys();
			length = array.length();

			if (error) {
				Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

		try {

			if (length <= 0) {
				return;
			}
			object = null;
			String title;
			DelPadrino();
			int i = 0;
			while (it.hasNext()) {
				i++;
				String string = (String) it.next();
				title = array.getString(string);
				insertORUpdaterolePadrino(i, title);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}
	}

	// insert or replace into Book (Name, TypeID, Level, Seen) values ( ... )
	public void loadRole(String result) {
		JSONObject object = null;
		JSONArray array = null;

		boolean error;
		String errorMsg;
		String status;
		int length = 0;

		try {

			object = new JSONObject(result);
			error = Boolean.parseBoolean(object.getString("error"));
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");
			array = object.getJSONArray("roles");
			length = array.length();

			if (error) {
				Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

		try {

			if (length <= 0) {
				return;
			}
			object = null;
			String id;
			String title;
			Delrole();
			for (int i = 0; i < length; i++) {
				object = array.getJSONObject(i);

				id = object.getString("role_id");
				title = object.getString("role_title");
				insertORUpdateroleRow(i, id, title);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}
	}

	private void Delrole() {
		try {
			database.execSQL("DELETE FROM role");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void insertORUpdateroleRow(int index, String role_id,
			String rolde_title) {
		index += 1;
		database.execSQL("INSERT INTO role (id,role_id,name) VALUES ( " + index
				+ ",\"" + role_id + "\", \"" + rolde_title + "\");");
	}

	public void loadCareer(String result) {

		JSONObject object = null;
		JSONArray array = null;

		boolean error;
		String errorMsg;
		String status;
		int length = 0;

		try {

			object = new JSONObject(result);
			error = Boolean.parseBoolean(object.getString("error"));
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");
			array = object.getJSONArray("careers");
			length = array.length();

			if (error) {
				Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

		try {

			if (length <= 0) {
				return;
			}
			object = null;
			String id;
			String title;
			String imgUrl;
			DelCareers();
			for (int i = 0; i < length; i++) {
				object = array.getJSONObject(i);

				id = object.getString("id");
				title = object.getString("career_name");
				imgUrl = object.getString("career_img");
				insertORUpdateroleCareers(i, id, title, imgUrl);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

	}

	private void DelCareers() {
		// TODO Auto-generated method stub
		try {
			database.execSQL("DELETE FROM career");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void insertORUpdateroleCareers(int index, String id, String title,
			String imgUrl) {
		// TODO Auto-generated method stub
		index += 1;
		database.execSQL("INSERT INTO career (id,careers_id,name,imgUrl) VALUES ( "
				+ index
				+ ",\""
				+ id
				+ "\", \""
				+ title
				+ "\", \""
				+ imgUrl
				+ "\");");
	}

	public void loadSubjectGradeStage(String result) {

		JSONObject object = null;
		JSONArray arraystages, arraygrades, arraysubjects = null;

		boolean error;
		String errorMsg;
		String status;
		int length1 = 0, length2 = 0, length3 = 0;

		try {

			object = new JSONObject(result);
			error = Boolean.parseBoolean(object.getString("error"));
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");
			arraystages = object.getJSONArray("stages");
			arraygrades = object.getJSONArray("grades");
			arraysubjects = object.getJSONArray("subjects");

			length1 = arraystages.length();
			length2 = arraygrades.length();
			length3 = arraysubjects.length();

			if (error) {
				Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
				if (!(boolean) util.getFromSharedPreference(
						VariableType.BOOLEAN, VUtil.ISDATASAVED)) {
					showExitErrorDialog();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

		try {
			if (length1 <= 0) {
				return;
			}
			object = null;
			String id;
			String englishName;
			String spanishName;
			DelStage();
			for (int i = 0; i < length1; i++) {
				object = arraystages.getJSONObject(i);

				id = object.getString("id");
				englishName = object.getString("name");
				spanishName = object.getString("name_spanish");
				insertORUpdateroleStage(i, id, englishName, spanishName);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

		try {
			if (length2 <= 0) {
				return;
			}
			object = null;
			String id;
			String englishName;
			String spanishName;
			DelGrade();
			for (int i = 0; i < length2; i++) {
				object = arraygrades.getJSONObject(i);

				id = object.getString("id");
				englishName = object.getString("name");
				spanishName = object.getString("name_spanish");
				insertORUpdateroleGrade(i, id, englishName, spanishName);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

		try {
			if (length3 <= 0) {
				return;
			}
			object = null;
			String id;
			String englishName;
			String spanishName;
			DelSubject();
			for (int i = 0; i < length3; i++) {
				object = arraysubjects.getJSONObject(i);

				id = object.getString("id");
				englishName = object.getString("name");
				spanishName = object.getString("name_spanish");
				insertORUpdateroleSubject(i, id, englishName, spanishName);
			}
			util.saveInPreference(VUtil.ISDATASAVED, true);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

	}

	private void showExitErrorDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(context).setTitle("Error")
				.setMessage("Error in retrive data, please check your network")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						MainActivity.this.finish();
						System.exit(0);
					}
				}).create().show();
	}

	private void DelStage() {
		// TODO Auto-generated method stub
		try {
			database.execSQL("DELETE FROM stages");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void insertORUpdateroleStage(int index, String stage_id,
			String englishName, String spanishName) {
		// TODO Auto-generated method stub
		index += 1;
		database.execSQL("INSERT INTO stages (id,stage_id,englishName,spanishName) VALUES ( "
				+ index
				+ ",\""
				+ stage_id
				+ "\", \""
				+ englishName
				+ "\", \""
				+ spanishName + "\");");
	}

	private void DelGrade() {
		// TODO Auto-generated method stub
		try {
			database.execSQL("DELETE FROM grades");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void insertORUpdateroleGrade(int index, String grade_id,
			String englishName, String spanishName) {
		// TODO Auto-generated method stub
		index += 1;
		database.execSQL("INSERT INTO grades (id,grade_id,englishName,spanishName) VALUES ( "
				+ index
				+ ",\""
				+ grade_id
				+ "\", \""
				+ englishName
				+ "\", \""
				+ spanishName + "\");");
	}

	private void DelSubject() {
		// TODO Auto-generated method stub
		try {
			database.execSQL("DELETE FROM subject");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void insertORUpdateroleSubject(int index, String subject_id,
			String englishName, String spanishName) {
		// TODO Auto-generated method stub
		index += 1;
		database.execSQL("INSERT INTO subject (id,subject_id,englishName,spanishName) VALUES ( "
				+ index
				+ ",\""
				+ subject_id
				+ "\", \""
				+ englishName
				+ "\", \"" + spanishName + "\");");
	}

	private void DelPadrino() {
		// TODO Auto-generated method stub
		try {
			database.execSQL("DELETE FROM padreno_indivdual");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void insertORUpdaterolePadrino(int index, String title) {
		// TODO Auto-generated method stub
		index += 1;
		database.execSQL("INSERT INTO padreno_indivdual (id,data) VALUES ( "
				+ index + ",\"" + title + "\");");
	}

	private void DelPadrinoSocial() {
		// TODO Auto-generated method stub
		try {
			database.execSQL("DELETE FROM padreno_social");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void insertORUpdaterolePadrinoSocial(int index, String title) {
		// TODO Auto-generated method stub
		index += 1;
		database.execSQL("INSERT INTO padreno_social (id,data) VALUES ( "
				+ index + ",\"" + title + "\");");
	}

	/*
	 * 
	 * 
	 * 
	 * private void createclassType(SQLiteDatabase db){ db.execSQL(
	 * "CREATE TABLE IF NOT EXISTS classType(id INTEGER PRIMARY KEY , name TEXT);"
	 * ); }
	 */

	private void DelclassType() {
		// TODO Auto-generated method stub
		try {
			database.execSQL("DELETE FROM classType");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void insertORUpdateroleClassType(int id, String name) {
		// TODO Auto-generated method stub
		id += 1;
		database.execSQL("INSERT INTO classType (id,name) VALUES ( " + id
				+ ",\"" + name + "\");");
	}

	private void DelSchoolLevel() {
		// TODO Auto-generated method stub
		try {
			database.execSQL("DELETE FROM school_level");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void insertORUpdateroleSchoolLevel(int index, String schoolLevelId,
			String schoolLevel) {
		// TODO Auto-generated method stub
		index += 1;
		database.execSQL("INSERT INTO school_level (id,school_levelid,level_name) VALUES ( "
				+ index
				+ ",\""
				+ schoolLevelId
				+ "\", \""
				+ schoolLevel
				+ "\");");
	}

	private void initDataBase() {

		helper = new VlearnDataBase(context, VlearnDataBase.dbName, null,
				VlearnDataBase.version);
		database = helper.getWritableDatabase();
	}



	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub

		switch (callingId) {
		case GET_SUBJECT:
		case GET_ROLE:
		case GET_CAREER:
		case GET_PADRIN_SOCIAL:
		case GET_PADRINO:
		case GET_SCHOOL_LEVEL:
		case GET_CLASSTYPE:
			Toast.makeText(getBaseContext(), getString(R.string.serverError), Toast.LENGTH_SHORT).show();
			break;
		}

	}

}

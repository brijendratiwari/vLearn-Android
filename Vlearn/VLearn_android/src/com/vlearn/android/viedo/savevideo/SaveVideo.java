package com.vlearn.android.viedo.savevideo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Shader.TileMode;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vlearn.android.MainActivity;
import com.vlearn.android.R;
import com.vlearn.android.database.VlearnDataBase;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.setting.mykid.Mykid;
import com.vlearn.android.setting.myvlearn.MyVlearn;
import com.vlearn.android.ui.CustomAdapter;
import com.vlearn.android.ui.picker.CareerPicker;
import com.vlearn.android.ui.picker.Picker;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;

public class SaveVideo extends Fragment implements OnNetworkResult {

	HomeActivity activity;
	Context context;

	NetworkConnection connection;
	VUtil util;

	int VideoType = CareerVlearn;
	public static final int CurriculumVlearn = 0;
	public static final int CareerVlearn = 1;
	String columnname;
	String VideoUrl;
	private Bitmap thumb;
	private boolean isKid;

	TextView tv;
	public static SaveVideo Create(int VideoType, String VideoUrl) {
		SaveVideo saveVideo = new SaveVideo();
		Bundle bundle = new Bundle();
		bundle.putInt("VideoType", VideoType);
		bundle.putString("VideoUrl", VideoUrl);
		saveVideo.setArguments(bundle);
		return saveVideo;
	}

	public static SaveVideo Create() {
		return Create(CareerVlearn, "");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		VideoType = getArguments().getInt("VideoType");
		VideoUrl = getArguments().getString("VideoUrl");
		videoFile = VideoUrl;
		util = activity.getVUtil();
		util.initLoader(activity, "Loading...");
		isKid = ((String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ACCESS)).contains("student");
	
		if(isKid){
			VideoType = CurriculumVlearn;
		}
		if(!VideoUrl.isEmpty()){
			thumb = ThumbnailUtils.createVideoThumbnail(VideoUrl,
				    MediaStore.Images.Thumbnails.MINI_KIND);
		}else{
			activity.goBack();
		}
		
		if(thumb != null)
		{
	
			File file = new File(Environment.getExternalStorageDirectory(), "Vlearn");
			
			file = new File("img_"+new Date().getTime()+".png");
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace(); 
			}
			FileOutputStream out = null;
			try {
			       out = new FileOutputStream(file.getAbsoluteFile());
			       thumb.compress(Bitmap.CompressFormat.PNG, 90, out);
			       videoThumb = file.getAbsolutePath();
			} catch (Exception e) {
			    e.printStackTrace();
			} finally {
			       try{
			           out.close();
			       } catch(Throwable ignore) {}
			}
			
		}
		
		initDataBase();
		connection = activity.getNetworkConnection();

		columnname = "englishName";
		if (!VUtil.IS_APP_LANG_ENG)
			columnname = "spanishName";

		if(!isKid)
			loadAboutYou();
		loadState();
		loadGrade();
		loadSubject();
	}

	private void loadSubject() {
		// TODO Auto-generated method stub cursor =
		// database.rawQuery("SELECT * FROM subject", null);

		cursor = database.rawQuery("SELECT subject_id," + columnname
				+ " FROM subject", null);
		int length = cursor.getCount();
		cursor.moveToFirst();

		if (length > 0) {
			subList.clear();
			int i = 0;
			while (i < length) {
				i++;

				String stage_id = cursor.getString(cursor
						.getColumnIndex("subject_id"));
				String name = cursor.getString(cursor
						.getColumnIndex(columnname));
				subList.add(new CommonCollection(stage_id, name, ""));
				cursor.moveToNext();
			}
		}

	}

	private void loadGrade() {
		// TODO Auto-generated method stub

		cursor = database.rawQuery("SELECT grade_id," + columnname
				+ " FROM grades", null);
		int length = cursor.getCount();
		cursor.moveToFirst();

		if (length > 0) {
			gradeList.clear();
			int i = 0;
			while (i < length) {
				i++;

				String stage_id = cursor.getString(cursor
						.getColumnIndex("grade_id"));
				String name = cursor.getString(cursor
						.getColumnIndex(columnname));
				gradeList.add(new CommonCollection(stage_id, name, ""));
				cursor.moveToNext();
			}
		}

	}

	private void loadState() {
		// TODO Auto-generated method stub

		cursor = database.rawQuery("SELECT stage_id," + columnname
				+ " FROM stages", null);
		int length = cursor.getCount();
		cursor.moveToFirst();

		if (length > 0) {
			stageList.clear();
			int i = 0;
			while (i < length) {
				i++;

				String stage_id = cursor.getString(cursor
						.getColumnIndex("stage_id"));
				String name = cursor.getString(cursor
						.getColumnIndex(columnname));
				stageList.add(new CommonCollection(stage_id, name, ""));
				cursor.moveToNext();
			}
		}

	}

	List<NameValuePair> al = new ArrayList<NameValuePair>();
	private AsyncTask<String, Void, String> task;

	private void loadAboutYou() {
		// TODO Auto-generated method stub
		util.showLoading();
		task = connection.getData("user/getParams/aboutyou", "GET", null,
				SaveVideo.this, loadAboutYou);
	}
	
	private void loadDomain(String subjectId){
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("subject_id", subjectId));
		
		util.showLoading();
		task = connection.getData("user/getDomains", "POST", al,SaveVideo.this, loadDomain);
	}
	
	private void loadStandardSkill(String domainId){
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("domain_id", domainId));
		al.add(new BasicNameValuePair("language", VUtil.IS_APP_LANG_ENG?"0":"1"));
		
		util.showLoading();
		task = connection.getData("user/getSkills", "POST", al,SaveVideo.this, loadStandSkill);
	}

	View layout1, layout2;
	Button backButton;
	Spinner videoType;
	ScrollView fragmentContainer;
	// -----------------------------------layout1-----------------------
	Button nextButton1;
	ImageView videoThumbnail1;
	EditText videoTitle1, videoDescription1;
	Button selectLanguage1, selectCareers1, tellUsAboutYou,
			selectState1;
	TextView label;
	ScrollView scrollview1;

	// -------------------------------laytout2-----------------------------------
	Button nextButton2;
	ImageView videoThumbnail2;
	EditText videoTitle2, videoDescription2;
	Button selectLanguage2, selectState2, grade2, selectSubject,
			selectDomain, selecStandard, selectSkill;
	private String[] entries;
	TextView label2,label1;
	View header;
	ScrollView scrollview2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.save_video, null, false);

		backButton = (Button) view.findViewById(R.id.backButton);
		layout1 = view.findViewById(R.id.layout1);
		layout2 = view.findViewById(R.id.layout2);
		tv = (TextView) view.findViewById(R.id.title);
		header = view.findViewById(R.id.header);
		
		videoType = (Spinner) view.findViewById(R.id.spinner);

		fragmentContainer = (ScrollView) view.findViewById(R.id.fragmentContainer);
		
		nextButton1 = (Button) view.findViewById(R.id.nextButton_1);
		videoThumbnail1 = (ImageView) view.findViewById(R.id.videoThumbnail_1);
		selectLanguage1 = (Button) view.findViewById(R.id.seleceLanguage_1);
		videoTitle1 = (EditText) view.findViewById(R.id.videoTitle_1);
		selectCareers1 = (Button) view.findViewById(R.id.selectCareers_1);
		tellUsAboutYou = (Button) view.findViewById(R.id.tellAboutUs_1);
		selectState1 = (Button) view.findViewById(R.id.selectState_1);
		videoDescription1 = (EditText) view
				.findViewById(R.id.videoDescription_1);
		label = (TextView) view.findViewById(R.id.label);
		scrollview1 = (ScrollView) view.findViewById(R.id.scrollview1);
		
		nextButton2 = (Button) view.findViewById(R.id.nextButton_2);
		videoThumbnail2 = (ImageView) view.findViewById(R.id.videoThumbnail_2);
		selectLanguage2 = (Button) view.findViewById(R.id.seleceLanguage_2);
		videoTitle2 = (EditText) view.findViewById(R.id.videoTitle_2);
		selectState2 = (Button) view.findViewById(R.id.stage_2);
		grade2 = (Button) view.findViewById(R.id.grade_2);
		selectSubject = (Button) view.findViewById(R.id.selectSubject_2);
		selectDomain = (Button) view.findViewById(R.id.selectDomain_2);
		selecStandard = (Button) view.findViewById(R.id.selectStandard_2);
		selectSkill = (Button) view.findViewById(R.id.selectSkill_2);
		scrollview2 = (ScrollView) view.findViewById(R.id.scrollview2);
		videoDescription2 = (EditText) view
				.findViewById(R.id.videoDescription_2);
		label1 = (TextView) view.findViewById(R.id.label_1);
		label2 = (TextView) view.findViewById(R.id.label_2);

//		scrollview2.setEnabled(false);
//		scrollview1.setEnabled(false);
		
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//new AlertDialog.Builder(context).setTitle("Alert!").setMessage("Are sure want to go back, This changes will not be save")
				activity.goBack();
			}
		});
		selectLanguage1.setOnClickListener(pickerClick);
		selectCareers1.setOnClickListener(pickerClick);
		tellUsAboutYou.setOnClickListener(pickerClick);
		selectState1.setOnClickListener(pickerClick);
		selectLanguage2.setOnClickListener(pickerClick);
		selectState2.setOnClickListener(pickerClick);
		grade2.setOnClickListener(pickerClick); 
		selectSubject.setOnClickListener(pickerClick);
		selectDomain.setOnClickListener(pickerClick);
		selecStandard.setOnClickListener(pickerClick);
		selectSkill.setOnClickListener(pickerClick);
		
		nextButton1.setOnClickListener(onVideoSubmit);
		nextButton2.setOnClickListener(onVideoSubmit);
		
		entries = getResources().getStringArray(R.array.videoType);
		videoType.setAdapter(new CustomAdapter(context,
				R.layout.spinner_text_view, entries, "fonts/KRONIKA_.ttf"));
		videoType.setOnItemSelectedListener(itemSelectedListener);
		
		if(isKid){
			videoType.setSelection(1);
			videoType.setVisibility(View.GONE);
			tv.setVisibility(View.GONE);
			showView(CurriculumVlearn);
			currentSelection = CurriculumVlearn; 
		}
		else{
			showView(CareerVlearn);
		}
		
		setVideoThumbnail(thumb);
		return view;
	}

	
	View.OnClickListener onVideoSubmit = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(currentSelection == CareerVlearn){
				videoDescription = label.getText()+"\n"+videoDescription;
				saveInDB("career");
			}else if(currentSelection == CurriculumVlearn){
				videoDescription = label1.getText()+" "+label2.getText()+"\n"+videoDescription;
				saveInDB("curriculum");
			}
		}
	};
	
	View.OnClickListener pickerClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			Intent intent;
			String[] array = null;
			
			if(v.getId() == R.id.selectCareers_1){
				intent = new Intent(context, CareerPicker.class);
			}else{
				intent = new Intent(context, Picker.class);
			}
			Bundle bun = new Bundle();
			bun.putString(Picker._PICKERTITLE, 	"Select...");
			bun.putString(Picker._OKButton, context.getString(R.string.Ok));
			bun.putString(Picker._CANCELButton, context.getString(R.string.back));

			int requestCode = 0;
			
			switch (v.getId()) {
			case R.id.seleceLanguage_1:
			case R.id.seleceLanguage_2:
				requestCode = SELECT_LANGUAGE_REQUEST;
				array = new String[]{"English","Spanish"};
				break;
			case R.id.selectCareers_1:
				requestCode = SELECT_CAREER_REQUEST;
				break;
			case R.id.tellAboutUs_1:
				requestCode = SELECT_TELL_ABOUT_YOU_REQUEST;
				array = getArray(aboutYourCollection);
				break;
			case R.id.selectState_1:
			case R.id.stage_2:
				requestCode = SELECT_STAGE_REQUEST;
				array = getArray(stageList);
				break;
			case R.id.grade_2:
				requestCode = SELECT_GRADE_REQUEST;
				array = getArray(gradeList);
				break;
			case R.id.selectSubject_2:
				requestCode = SELECT_SUBJECT_REQUEST;
				array = getArray(subList);
				break;
			case R.id.selectDomain_2:
				requestCode = SELECT_DOMAIN_REQUEST;
				if(domainList.isEmpty()){
					Toast.makeText(context, ""+context.getString(R.string.pleaseSelectSubjectFirst), Toast.LENGTH_SHORT).show();
					return;
				}
				array = getArray(domainList);
				break;
			case R.id.selectStandard_2:
				if(subList.isEmpty()){
					Toast.makeText(context, ""+context.getString(R.string.pleaseSelectDomainFirst), Toast.LENGTH_SHORT).show();
					return;
				}
				requestCode = SELECT_STANDARD_REQUEST;
				array = getArray(standardList);
				break;
			case R.id.selectSkill_2:
				if(skillList.isEmpty()){
					Toast.makeText(context, ""+context.getString(R.string.pleaseSelectDomainFirst), Toast.LENGTH_SHORT).show();
					return;
				}
				requestCode = SELECT_SKILL_REQUEST;
				array = getArray(skillList);
				break;
			default:
				break;
			}
			try{
				bun.putStringArray(Picker._ListEntries, array);
			}catch (Exception e) {
				// TODO: handle exception
			}
			if(v.getId() != R.id.selectCareers_1) intent.putExtras(bun);
			startActivityForResult(intent, requestCode);
			activity.overridePendingTransition(R.anim.slideleft1, R.anim.slideleft2);
		}
	};
	
	String[] getArray(List<CommonCollection> list){
		String array[] = new String[list.size()];
		for(int i=0;i<list.size();i++){
			array[i] = list.get(i).name;
		}
		return array;
	}
	
	String[] getSpanishArray(List<CommonCollection> list){
		String array[] = new String[list.size()];
		for(int i=0;i<list.size();i++){
			array[i] = list.get(i).extraDetail;
		}
		return array;
	}

	int currentSelection;
	
	AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			currentSelection = position;
			showView(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

	String videoLanguage = "";
	String videoTitle = "";
	String videoThumb = "";
	String videoFile = "";
	String videoCareerId = "";
	String videoCareer = "";
	String videoTellAboutUsId = "";
	String videoTellAboutUs = "";
	String videoStageId = "";
	String videoStage = "";
	String videoDescription = "";
	String videoGraadeId = "";
	String videoGraade = "";
	String videoSubjectId = "";
	String videoSubject = "";
	String videoStandardId = "";
	String videoStandard = "";
	String videoDomainId = "";
	String videoDomain = "";
	String videoSkillId = "";
	String videoSkill = "";
	String videoStatus = "pending";
	String videoServerId = "";
	
	private void setVideoThumbnail(Bitmap thumb) {
		videoThumbnail1.setImageBitmap(thumb);
		videoThumbnail2.setImageBitmap(thumb);
	}

	private void setVideoDescription(String description) {
		videoDescription1.setText(description);
		videoDescription2.setText(description);
		videoDescription = description;
	}

	private void setVideoState(String id, String title) {
		selectState2.setText(title);
		selectState1.setText(title);

		selectState2.setTag(id);
		selectState1.setTag(id);
		
		videoStageId = id;
		videoStage = title;
	}

	private void setVideoLanguage(String title) {
		selectLanguage1.setText(title);
		selectLanguage2.setText(title);
		videoLanguage = title;
	}

	private void setVideoGrade(String id,String title) {
		grade2.setText(title);
		grade2.setTag(id);
		videoGraadeId = id;
		videoGraade = title;
	}
	
	private void setVideoSub(String id,String title) {
		selectSubject.setText(title);
		selectSubject.setTag(id);
		videoSubjectId = id;
		videoSubject = title;
		
	}
	
	private void setVideoDomain(String id,String title) {
		selectDomain.setText(title);
		selectDomain.setTag(id);
		videoDomainId = id;
		videoDomain = title;
	}
	
	private void setVideoStandard(String id,String title) {
		selecStandard.setText(title);
		selecStandard.setTag(id);
		videoStandardId = id;
		videoStandard = title;
	}
	
	private void setVideoSKill(String id,String title) {
		selectSkill.setText(title);
		selectSkill.setTag(id);
		videoSkillId = id;
		videoSkill = title;
	}
	
	private void setVideotitle(String title) {
		videoTitle1.setText(title);
		videoTitle2.setText(title);
		videoTitle = title;
	}
	
	private void setVideoCareer(String id,String title) {
		selectCareers1.setText(title);
		selectCareers1.setTag(id);
		videoCareerId = id;
		videoCareer = title;
	}
	
	private void setVideoTellAboutUs(String id,String title) {
		tellUsAboutYou.setText(title);
		tellUsAboutYou.setTag(id);
		videoTellAboutUsId = id;
		videoTellAboutUs = title;
	}

	private void showView(int id) {

		int careerVlear = 0;
		int curriculumVlearn = 0;
		String title = "";
		switch (id) {
		case CareerVlearn:
			careerVlear = View.VISIBLE;
			curriculumVlearn = View.GONE;
			title = videoTitle1.getText().toString().trim();
			
			break;
		case CurriculumVlearn:
			careerVlear = View.INVISIBLE;
			curriculumVlearn = View.VISIBLE;
			title = videoTitle2.getText().toString().trim();
			break;

		default:
			break;
		}

		layout1.setVisibility(careerVlear);
		layout2.setVisibility(curriculumVlearn);
		if(!title.isEmpty())
			setVideotitle(title);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		activity.setHeader(false, 0, 0, null, null, null, null, 0);

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
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case loadAboutYou:
			onloadAboutYou(result);
			break;
		case loadDomain:
			onDomainLoad(result);
			break;
		case loadStandSkill:
			onLoadStandSkill(result);
			break;
		default:
			break;
		}
	}

	private void onLoadStandSkill(String result) {
		// TODO Auto-generated method stub

		JSONObject object;
		boolean error = false;
		String errorMsg = "";
		JSONArray data1 = null;
		JSONArray data2= null;
		String id = "";
		String name = "";
		String name_spanish = "";
		int length = 0;

		try {
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			if (error) {
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
				return;
			}
			data1 = object.getJSONArray("standards");
			length = data1.length();
			if (length <= 0) {
				return;
			}
			standardList.clear();
			for (int i = 0; i < length; i++) {
				JSONObject object1 = data1.getJSONObject(i);
				id = object1.getString("index");
				name = object1.getString("value");
				//name_spanish = object.getString("name_spanish");
				standardList.add(new CommonCollection(id, name, ""));
			}
			
			data2 = object.getJSONArray("skills");
			length = data2.length();
			if (length <= 0) {
				return;
			}
			skillList.clear();
			for (int i = 0; i < length; i++) {
				JSONObject object2 = data2.getJSONObject(i);
				id = object2.getString("id");
				name = object2.getString("name");
				name_spanish = object2.getString("name_spanish");
				skillList.add(new CommonCollection(id, name, name_spanish));
			}

		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void onDomainLoad(String result) {
		// TODO Auto-generated method stub

		JSONObject object;
		boolean error = false;
		String errorMsg = "";
		JSONArray data = null;
		String id = "";
		String name = "";
		String name_spanish = "";
		int length = 0;

		try {
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			if (error) {
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
				return;
			}
			data = object.getJSONArray("domains");
			length = data.length();
			if (length <= 0) {
				return;
			}
			domainList.clear();
			for (int i = 0; i < length; i++) {
				object = data.getJSONObject(i);
				id = object.getString("id");
				name = object.getString("name");
				name_spanish = object.getString("name_spanish");
				domainList.add(new CommonCollection(id, name, name_spanish));
			}

		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void onloadAboutYou(String result) {
		// TODO Auto-generated method stub

		JSONObject object;
		boolean error = false;
		String errorMsg = "";
		JSONArray data = null;
		String id = "";
		String name = "";
		int length = 0;

		try {
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			if (error) {
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
				return;
			}
			data = object.getJSONArray("data");
			length = data.length();
			if (length <= 0) {
				return;
			}
			aboutYourCollection.clear();
			for (int i = 0; i < length; i++) {
				object = data.getJSONObject(i);
				id = object.getString("id");
				name = object.getString("name");
				aboutYourCollection.add(new CommonCollection(id, name, ""));
			}

		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	String hashtag ;
	public void onActivityResult(int requestCode, int resultCode,
			android.content.Intent data) {
		String title= "";
		int position = 0;
		
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case SELECT_CAREER_REQUEST:
				Bundle bun = data.getExtras();
				if(bun == null) return;
				String career_careers_id = bun.getString("careers_id");
				String career_name = bun.getString("name");
				label.setText("#VCareer #"+VUtil.convertToTag(career_name));
				setVideoCareer(career_careers_id, career_name);
				break;
			case SELECT_TELL_ABOUT_YOU_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				setVideoTellAboutUs(aboutYourCollection.get(position).id, aboutYourCollection.get(position).name);
				break;
			case SELECT_STAGE_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				setVideoGrade("0", "");
				setVideoState(stageList.get(position).id, stageList.get(position).name);
				break;
			case SELECT_GRADE_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				setVideoState("0", "");
				setVideoGrade(gradeList.get(position).id, gradeList.get(position).name);
				break;
			case SELECT_SUBJECT_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				if(!title.equalsIgnoreCase(selectSubject.getText().toString().trim())){
					setVideoSub(subList.get(position).id, subList.get(position).name);
					loadDomain(subList.get(position).id);
					setVideoDomain("", "");
					domainList.clear();
				}
				break;
			case SELECT_DOMAIN_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				if(!title.equalsIgnoreCase(selectDomain.getText().toString().trim())){
					setVideoDomain(domainList.get(position).id, title);
					loadStandardSkill(domainList.get(position).id);
					setVideoStandard("","");
					setVideoSKill("","");
					standardList.clear();
					skillList.clear();
				}
				break;
			case SELECT_STANDARD_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				setVideoStandard(standardList.get(position).id, title);
				if(title.contains("-"))
					hashtag = "#"+VUtil.convertToTag(title.substring(0, title.indexOf("-")));
				else
					hashtag = "#"+VUtil.convertToTag(title);
				if(!hashtag.equalsIgnoreCase("#IDon'tKnow")) label1.setText(hashtag);
				break;
			case SELECT_SKILL_REQUEST: 
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				setVideoSKill(skillList.get(position).id, title);
				hashtag = "#"+VUtil.convertToTag(title); 
				if(!hashtag.equalsIgnoreCase("#IDon'tKnow")) label2.setText(hashtag);
				break;
			case SELECT_LANGUAGE_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				setVideoLanguage(title);
				break;

			default:
				break; 
			}
		}
	};

	ArrayList<CommonCollection> aboutYourCollection = new ArrayList<>();
	ArrayList<CommonCollection> stageList = new ArrayList<>();
	ArrayList<CommonCollection> gradeList = new ArrayList<>();
	ArrayList<CommonCollection> subList = new ArrayList<>();
	ArrayList<CommonCollection> domainList = new ArrayList<>();
	ArrayList<CommonCollection> standardList = new ArrayList<>();
	ArrayList<CommonCollection> skillList = new ArrayList<>();
	private final int SELECT_CAREER_REQUEST = 101;
	private final int SELECT_TELL_ABOUT_YOU_REQUEST = 102;
	private final int SELECT_STAGE_REQUEST = 103;
	private final int SELECT_GRADE_REQUEST = 104;
	private final int SELECT_SUBJECT_REQUEST = 105;
	private final int SELECT_DOMAIN_REQUEST = 106;
	private final int SELECT_STANDARD_REQUEST = 107;
	private final int SELECT_SKILL_REQUEST = 108;
	private final int SELECT_LANGUAGE_REQUEST = 109;

	
	
	private void saveInDB(String videoType){
		
		boolean isEmpty = false;

		if(videoType.equalsIgnoreCase("curriculum")){
			
			videoLanguage = selectLanguage2.getText().toString().trim();
			videoTitle = videoTitle2.getText().toString().trim();
			videoStageId = (String) selectState2.getTag() == null?"0":(String) selectState2.getTag();
			videoStage = selectState2.getText().toString().trim();
//			videoDescription = videoDescription2.getText().toString().trim();
			videoGraadeId = (String) grade2.getTag() == null?"0":(String) grade2.getTag();
			videoGraade = grade2.getText().toString().trim();
			videoSubjectId = (String) selectSubject.getTag() == null?"":(String) selectSubject.getTag();
			videoSubject = selectSubject.getText().toString().trim();
			videoStandardId = (String) selecStandard.getTag() == null?"":(String) selecStandard.getTag();
			videoStandard = selecStandard.getText().toString().trim();
			videoDomainId = (String) selectDomain.getTag() == null?"":(String) selectDomain.getTag();
			videoDomain = selectDomain.getText().toString().trim();
			videoSkillId = (String) selectSkill.getTag() == null?"":(String) selectSkill.getTag();
			videoSkill = selectSkill.getText().toString().trim();
			
			isEmpty = checkForEmptyCurriculum();	
		}else{
			
			videoLanguage = selectLanguage1.getText().toString().trim();
			videoTitle = videoTitle1.getText().toString().trim();
			videoCareerId = (String) selectCareers1.getTag() == null?"":(String) selectCareers1.getTag();
			videoCareer = selectCareers1.getText().toString().trim();
			videoTellAboutUsId = (String) tellUsAboutYou.getTag() == null?"":(String) tellUsAboutYou.getTag();
			videoTellAboutUs = tellUsAboutYou.getText().toString().trim();
			videoStageId = (String) selectState1.getTag() == null?"":(String) selectState1.getTag();
			videoStage = selectState1.getText().toString().trim();
//			videoDescription = videoDescription1.getText().toString().trim();
			
			isEmpty = checkForEmptyCareer();
		}
		if(isEmpty){
			Toast.makeText(context, ""+context.getString(R.string.allFiledsRequired), Toast.LENGTH_SHORT).show();
			return;
		}
		
		database.execSQL("INSERT INTO video_table (video_type ,video_user_id, video_language ,video_title ,video_thumb ,video_file, career_id , tell_us , tell_us_id ,stage ,stageid,description , grade , grade_id , subject ,subject_id, domain ,domain_id, standard ,standard_id, skill ,skill_id, video_status,video_server_id) VALUES ( \""
				+ videoType
				+ "\",\""
				+ (String)util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ID)
				+ "\", \""
				+ videoLanguage
				+ "\", \""
				+ videoTitle
				+ "\", \""
				+videoThumb
				+ "\", \""
				+videoFile
				+ "\", \""
				+videoCareerId
				+ "\", \""
				+videoTellAboutUs
				+ "\", \""
				+videoTellAboutUsId
				+ "\", \""
				+videoStage
				+ "\", \""
				+videoStageId
				+ "\", \""
				+videoDescription
				+ "\", \""
				+videoGraade
				+ "\", \""
				+videoGraadeId
				+ "\", \""
				+videoSubject
				+ "\", \""
				+videoSubjectId
				+ "\", \""
				+videoDomain
				+ "\", \""
				+videoDomainId
				+ "\", \""
				+videoStandard
				+ "\", \""
				+videoStandardId
				+ "\", \""
				+videoSkill
				+ "\", \""
				+videoSkillId
				+ "\", \""
				+videoStatus
				+ "\", \""
				+ videoServerId + "\");");
		
		activity.changePage(MyVlearn.Create());
	}

	private boolean checkForEmptyCareer() {
		// TODO Auto-generated method stub
		if(videoLanguage.isEmpty()){return true;}
		if(videoTitle.isEmpty()){return true;}
		if(videoFile.isEmpty()){return true;}
		if(videoCareerId.isEmpty()){return true;}
		if(videoCareer.isEmpty()){return true;}
		if(videoTellAboutUsId.isEmpty()){return true;}
		if(videoTellAboutUs.isEmpty()){return true;}
		if(videoStageId.isEmpty()){return true;}
		if(videoStage.isEmpty()){return true;}
		if(videoDescription.isEmpty()){return true;}
		
		if(videoStatus.isEmpty()){return true;}
		
		return false;
	}

	private boolean checkForEmptyCurriculum() {
		// TODO Auto-generated method stub
		
		if(videoLanguage.isEmpty()){return true;}
		if(videoTitle.isEmpty()){return true;}
		if(videoFile.isEmpty()){return true;}
		if(videoStageId.isEmpty() && videoGraadeId.isEmpty()){return true;}
		if(videoStage.isEmpty() && videoGraade.isEmpty()){return true;}
		if(videoDescription.isEmpty()){return true;}

		if(videoSubjectId.isEmpty()){return true;}
		if(videoSubject.isEmpty()){return true;}
		if(videoStandard.isEmpty()){return true;}
		if(videoDomain.isEmpty()){return true;}
		if(videoSkill.isEmpty()){return true;}
		if(videoStatus.isEmpty()){return true;}
		
		return false;
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case loadAboutYou:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadAbout), Toast.LENGTH_SHORT).show();
			break;
		case loadDomain:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadDomain), Toast.LENGTH_SHORT).show();
			break;
		case loadStandSkill:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadStandSkill), Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}

	}
	
}

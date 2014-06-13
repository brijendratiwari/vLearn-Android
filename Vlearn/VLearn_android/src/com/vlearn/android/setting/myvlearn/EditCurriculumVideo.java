package com.vlearn.android.setting.myvlearn;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.vlearn.android.viedo.savevideo.CommonCollection;

public class EditCurriculumVideo extends Fragment implements OnNetworkResult{

	HomeActivity activity;
	Context context;

	NetworkConnection connection;
	VUtil util;
	ImageLoader loader;

	Button nextButton2;
	ImageView videoThumbnail2;
	EditText videoTitle2,videoDescription2;
	Button selectLanguage2, selectState2, grade2, selectSubject,
			selectDomain, selecStandard, selectSkill;
	TextView label2,label1;
	boolean isLocal;
	
	ArrayList<CommonCollection> stageList = new ArrayList<>();
	ArrayList<CommonCollection> gradeList = new ArrayList<>();
	ArrayList<CommonCollection> subList = new ArrayList<>();
	ArrayList<CommonCollection> domainList = new ArrayList<>();
	ArrayList<CommonCollection> standardList = new ArrayList<>();
	ArrayList<CommonCollection> skillList = new ArrayList<>();
	private final int SELECT_STAGE_REQUEST = 103;
	private final int SELECT_GRADE_REQUEST = 104;
	private final int SELECT_SUBJECT_REQUEST = 105;
	private final int SELECT_DOMAIN_REQUEST = 106;
	private final int SELECT_STANDARD_REQUEST = 107;
	private final int SELECT_SKILL_REQUEST = 108;
	private final int SELECT_LANGUAGE_REQUEST = 109;
	
	MyVlearnCollection col = MyVlearn.collectionBackUp;
	private String columnname;
	
	public static EditCurriculumVideo Create() {
		EditCurriculumVideo editCurriculumVideo = new EditCurriculumVideo();
		return editCurriculumVideo;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		connection = activity.getNetworkConnection();
		util = activity.getVUtil();
		loader = activity.getImageLoader();
		initDataBase();
		isLocal = col.isLocal;
		columnname = "englishName";
		if (!VUtil.IS_APP_LANG_ENG)
			columnname = "spanishName";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.save_video_2, null, false);
		//view.setBackgroundResource(R.drawable.bg);
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
		videoDescription2 = (EditText) view.findViewById(R.id.videoDescription_2);
		label2 = (TextView) view.findViewById(R.id.label_2);
		label1 = (TextView) view.findViewById(R.id.label_1);
		selectLanguage2.setOnClickListener(pickerClick);
		selectState2.setOnClickListener(pickerClick);
		grade2.setOnClickListener(pickerClick); 
		selectSubject.setOnClickListener(pickerClick);
		selectDomain.setOnClickListener(pickerClick);
		selecStandard.setOnClickListener(pickerClick);
		selectSkill.setOnClickListener(pickerClick);
		
		nextButton2.setOnClickListener(onClickListener);
		
		selectState2.setTag("");
		grade2.setTag("");
		selectDomain.setTag("");
		selecStandard.setTag("");
		selectSkill.setTag("");
		
		loadState();
		loadGrade();
		loadSubject();
		loadDomain(col.subject);
		loadStandardSkill(col.standard);
		loadView();
		return view;
	}
	
	
	private void loadView(){
		//set language
		selectLanguage2.setText(col.language.equalsIgnoreCase("0")?"English":"Spanish");
		
		//set VlearnName
		videoTitle2.setText(col.name);
		
		//set video thumbnail
		if(isLocal){
			videoThumbnail2.setImageBitmap(BitmapFactory.decodeFile(col.icon));
		}else{
			loader.DisplayImage(col.icon, videoThumbnail2);
		}
		
		//set video Description
		String arr[] = VUtil.findTag(col.desc); 
		String txt1 = arr[0]==null?"":arr[0];
		String txt2 = arr[1]==null?"":arr[1];
		label1.setText(txt1);
		label2.setText(txt2); 
		videoDescription2.setText(VUtil.removeHashTag(col.desc));
		
	
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 doEdit();
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
			
			case R.id.seleceLanguage_2:
				requestCode = SELECT_LANGUAGE_REQUEST;
				array = new String[]{"English","Spanish"};
				break;
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
				if(col.stage.equalsIgnoreCase(stage_id)){
					selectState2.setText(name);
					selectState2.setTag(stage_id);
				}
				stageList.add(new CommonCollection(stage_id, name, ""));
				cursor.moveToNext();
			}
		}

	}

	
	protected void doEdit() {
		// TODO Auto-generated method stub
		
		String description = videoDescription2.getText().toString().trim();
		String domainId = (String) selectDomain.getTag();
		String domain_name = selectDomain.getText().toString().trim();
		String gradeId = (String) grade2.getTag();
		String gradeId_name = (String) grade2.getText().toString().trim();
		String language = selectLanguage2.getText().toString().trim();
		String name = videoTitle2.getText().toString().trim();
		String skillId = (String) selectSkill.getTag();
		String stageId = (String) selectState2.getTag();
		String standard = (String) selecStandard.getTag();
		String subjectId = (String) selectSubject.getTag();
		String skillId_name = (String) selectSkill.getText().toString().trim();
		String stageId_name = (String) selectState2.getText().toString().trim();
		String standard_name = (String) selecStandard.getText().toString().trim();
		String subjectId_name = (String) selectSubject.getText().toString().trim();
		if(checkForEmpty()){
			Toast.makeText(context, ""+context.getString(R.string.allFiledsRequired), Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(col.isLocal){
			
			ContentValues cv = new ContentValues();
		    cv.put("stage", stageId_name);
			cv.put("stageid", stageId);
			cv.put("description", description);
			cv.put("grade", gradeId_name);
			cv.put("grade_id", gradeId);
			cv.put("subject", subjectId_name);
			cv.put("subject_id", subjectId);
			cv.put("domain", domain_name);
			cv.put("domain_id", domainId);
			cv.put("standard", standard_name);
			cv.put("standard_id", standard);
			cv.put("skill", skillId_name);
			cv.put("skill_id", skillId);
			cv.put("video_title", name);
			cv.put("video_language", language);
 
			int i = database.update("video_table", cv, "id="+col.id, null);
			activity.goBack();
//			database.insert("", "", cv)			
		}else{
			// TODO Auto-generated method stub

			al.clear();
			al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
			al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
			al.add(new BasicNameValuePair("app_name", "vlearn"));
			al.add(new BasicNameValuePair("app_type", "video"));
			al.add(new BasicNameValuePair("catId", col.video_server_id));
			al.add(new BasicNameValuePair("description", description));
			al.add(new BasicNameValuePair("domainId", domainId));
			al.add(new BasicNameValuePair("domain_name", domain_name));
			al.add(new BasicNameValuePair("gradeId", gradeId));
			al.add(new BasicNameValuePair("language", language));
			al.add(new BasicNameValuePair("name", name));
			al.add(new BasicNameValuePair("private", "0"));
			al.add(new BasicNameValuePair("skillId", skillId));
			al.add(new BasicNameValuePair("stageId", stageId));
			al.add(new BasicNameValuePair("standard", standard));
			al.add(new BasicNameValuePair("subjectId", subjectId));
		
			task = connection.getData("user/update_set", "POST", al, EditCurriculumVideo.this, editVideo);
		}
	}

	private boolean checkForEmpty() {
		// TODO Auto-generated method stub
		
		String description = videoDescription2.getText().toString().trim();
		if(description.isEmpty())return true;
		
//		String domainId = (String) selectDomain.getTag();
//		if(domainId.isEmpty())return true;
		
		String domain_name = selectDomain.getText().toString().trim();
		if(domain_name.isEmpty())return true;
		
		String gradeId = (String) grade2.getTag();
		String stageId = (String) selectState2.getTag();
		String stageId_name = (String) selectState2.getText().toString().trim();
		String gradeId_name = (String) grade2.getText().toString().trim();
		if(( gradeId_name.isEmpty()) && (stageId_name.isEmpty()))return true;
		
		String language = selectLanguage2.getText().toString().trim();
		if(language.isEmpty())return true;
		
		String name = videoTitle2.getText().toString().trim();
		if(name.isEmpty())return true;
		
		String skillId = (String) selectSkill.getTag();
		String skillId_name = (String) selectSkill.getText().toString().trim();
		if(skillId_name.isEmpty())return true;
		
		String standard = (String) selecStandard.getTag();
		String standard_name = (String) selecStandard.getText().toString().trim();
		if(standard_name.isEmpty())return true;
		
		String subjectId = (String) selectSubject.getTag();
		String subjectId_name = (String) selectSubject.getText().toString().trim();
		if(subjectId_name.isEmpty())return true;
		
		return false;
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
				if(col.subject.equalsIgnoreCase(stage_id)){
					selectSubject.setText(name);
					selectSubject.setTag(stage_id);
				}
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
				if(col.grade.equalsIgnoreCase(stage_id)){
					grade2.setText(name);
					grade2.setTag(stage_id);
				}
				gradeList.add(new CommonCollection(stage_id, name, ""));
				cursor.moveToNext();
			}
		}

	}

	String hashtag;
	public void onActivityResult(int requestCode, int resultCode,
			android.content.Intent data) {
		String title= "";
		int position = 0;
		
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case SELECT_STAGE_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				grade2.setText("");
				grade2.setTag("");
				selectState2.setText(stageList.get(position).name);
				selectState2.setTag(stageList.get(position).id);
				break;
			case SELECT_GRADE_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				selectState2.setText("");
				selectState2.setTag("");
				grade2.setText(gradeList.get(position).name);
				grade2.setTag(gradeList.get(position).id);
				break;
			case SELECT_SUBJECT_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				if(!title.equalsIgnoreCase(selectSubject.getText().toString().trim())){
					selectSubject.setText(subList.get(position).name);
					selectSubject.setTag(subList.get(position).id);
					loadDomain(subList.get(position).id);
					selectDomain.setText("");
					selectDomain.setTag("");
					domainList.clear();
				}
				break;
			case SELECT_DOMAIN_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				if(!title.equalsIgnoreCase(selectDomain.getText().toString().trim())){
					selectDomain.setText(title);
					selectDomain.setTag(domainList.get(position).id);
					loadStandardSkill(domainList.get(position).id);
					selecStandard.setText("");
					selecStandard.setTag("");
					selectSkill.setText("");
					selectSkill.setTag("");
					standardList.clear();
					skillList.clear();
				}
				break;
			case SELECT_STANDARD_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				selecStandard.setText(title);
				selecStandard.setTag(standardList.get(position).id);
				hashtag = "#"+VUtil.convertToTag(title); 
				if(!hashtag.equalsIgnoreCase("#IDon'tKnow")) label1.setText(hashtag);
				break;
			case SELECT_SKILL_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				selectSkill.setTag(skillList.get(position).id);
				selectSkill.setText(title);
				hashtag = "#"+VUtil.convertToTag(title); 
				if(!hashtag.equalsIgnoreCase("#IDon'tKnow")) label2.setText(hashtag);
				break;
			case SELECT_LANGUAGE_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				selectLanguage2.setText(title);
				break;

			default:
				break;
			}
		}
	};
	
	List<NameValuePair> al = new ArrayList<NameValuePair>();
	private AsyncTask<String, Void, String> task;
	private void loadDomain(String subjectId){
		util.showLoading("Loading...");
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("subject_id", subjectId));
		
		util.showLoading();
		task = connection.getData("user/getDomains", "POST", al,EditCurriculumVideo.this, loadDomain);
	}
	
	private void loadStandardSkill(String domainId){
		util.showLoading("Loading...");
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("domain_id", domainId));
		al.add(new BasicNameValuePair("language", VUtil.IS_APP_LANG_ENG?"0":"1"));
		
		util.showLoading();
		task = connection.getData("user/getSkills", "POST", al,EditCurriculumVideo.this, loadStandSkill);
	}

	
	private void onDomainLoad(String result) {
		// TODO Auto-generated method stub

		JSONObject object = null;
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
				
				if(col.standard.equalsIgnoreCase(id)){
					if(VUtil.IS_APP_LANG_ENG)
						selectDomain.setText(name);
					else
						selectDomain.setText(name_spanish);
					selectDomain.setTag(id);
				}
				
				domainList.add(new CommonCollection(id, name, name_spanish));
			}

		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
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
				if(col.substandard.equalsIgnoreCase(id)){
					
					selecStandard.setText(name);
					selecStandard.setTag(id);
				}
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
				if(col.skill.equalsIgnoreCase(id)){
					if(VUtil.IS_APP_LANG_ENG)
						selectSkill.setText(name);
					else
						selectSkill.setText(name_spanish);
					selectSkill.setTag(id);
				}
				skillList.add(new CommonCollection(id, name, name_spanish));
			}

		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	
	String[] getArray(List<CommonCollection> list){
		String array[] = new String[list.size()];
		for(int i=0;i<list.size();i++){
			array[i] = list.get(i).name;
		}
		return array;
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		activity.setHeader(true, R.drawable.button_selector_round_blue, 0, context.getString(R.string.back), null, "Career vLearn", null, 0);
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
		case loadDomain:
			onDomainLoad(result);
			break;
		case loadStandSkill:
			onLoadStandSkill(result);
			break;
		case editVideo:
			onEditDone(result);
		default:
			break;
		}
	}

	private void onEditDone(String result) {
		// TODO Auto-generated method stub
		boolean error = false;
		String errorMsg = "";
		String message = "";
		JSONObject object = null;
		try{
			object = new JSONObject(result);
			
			if(object.has("error")) error = object.getBoolean("error");
			if(object.has("errorMsg")) errorMsg = object.getString("errorMsg");
			
			if(error){
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
				return;
			}else{
				if(object.has("message")) message = object.getString("message");
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
				activity.goBack();
				return;
			}
			
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(context, "Exception in output :- "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case loadDomain:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadDomain), Toast.LENGTH_SHORT).show();
			break;
		case loadStandSkill:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadStandSkill), Toast.LENGTH_SHORT).show();
			break;
		case editVideo:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.unabletoupdatedetails), Toast.LENGTH_SHORT).show();
		default:
			break;
		}
	}

}

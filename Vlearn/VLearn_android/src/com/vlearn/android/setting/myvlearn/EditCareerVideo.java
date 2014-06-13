package com.vlearn.android.setting.myvlearn;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
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
import com.vlearn.android.viedo.savevideo.CommonCollection;

public class EditCareerVideo extends Fragment implements OnNetworkResult {

	HomeActivity activity;
	Context context;

	NetworkConnection connection;
	VUtil util;
	ImageLoader loader;

	Button nextButton1;
	ImageView videoThumbnail1;
	EditText videoTitle1,videoDescription1;
	Button selectLanguage1,  selectCareers1, tellUsAboutYou, selectState1;
	TextView label;
	
	ArrayList<CommonCollection> aboutYourCollection = new ArrayList<>();
	ArrayList<CommonCollection> stageList = new ArrayList<>();
	private final int SELECT_CAREER_REQUEST = 101;
	private final int SELECT_TELL_ABOUT_YOU_REQUEST = 102;
	private final int SELECT_STAGE_REQUEST = 103;
	private final int SELECT_LANGUAGE_REQUEST = 109;

	MyVlearnCollection col = MyVlearn.collectionBackUp;
	
	boolean isLocal = false;
	private String columnname;
	
	public static EditCareerVideo Create() {
		EditCareerVideo editCareerVideo = new EditCareerVideo();

		return editCareerVideo;
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
		View view = inflater.inflate(R.layout.save_video_1, null, false);
		//view.setBackgroundResource(R.drawable.bg);
		
		nextButton1 = (Button) view.findViewById(R.id.nextButton_1);
		videoThumbnail1 = (ImageView) view.findViewById(R.id.videoThumbnail_1);
		selectLanguage1 = (Button) view.findViewById(R.id.seleceLanguage_1);
		videoTitle1 = (EditText) view.findViewById(R.id.videoTitle_1);
		selectCareers1 = (Button) view.findViewById(R.id.selectCareers_1);
		tellUsAboutYou = (Button) view.findViewById(R.id.tellAboutUs_1);
		selectState1 = (Button) view.findViewById(R.id.selectState_1);
		videoDescription1 = (EditText) view.findViewById(R.id.videoDescription_1);
		label = (TextView) view.findViewById(R.id.label);
		
		selectLanguage1.setOnClickListener(pickerClick);
		selectCareers1.setOnClickListener(pickerClick);
		tellUsAboutYou.setOnClickListener(pickerClick);
		selectState1.setOnClickListener(pickerClick);
		
		nextButton1.setOnClickListener(onClickListener);
		loadAboutYou();
		loadState();
		loadView(); 
		return view;
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
				if(col.stage.equalsIgnoreCase(stage_id)){
					selectState1.setText(name);
					selectState1.setTag(stage_id);
				}
				stageList.add(new CommonCollection(stage_id, name, ""));
				cursor.moveToNext();
			}
		}

	}

	
	private void loadView(){
		//set language
		selectLanguage1.setText(col.language.equalsIgnoreCase("0")?"English":"Spanish");
		
		//set VlearnName
		videoTitle1.setText(col.name);
		
		//set video thumbnail
		if(isLocal){
			videoThumbnail1.setImageBitmap(BitmapFactory.decodeFile(col.icon));
		}else{
			loader.DisplayImage(col.icon, videoThumbnail1);
		}
		
		//set video Description
		String arr[] = VUtil.findTag(col.desc);
		String txt1 = arr[0]==null?"":arr[0];
		String txt2 = arr[1]==null?"":arr[1];
		label.setText(txt1+" "+txt2);
		videoDescription1.setText(VUtil.removeHashTag(col.desc));
		
		//set career
		setCareerValue();
		
	
	}
	
	private void setCareerValue() {
		// TODO Auto-generated method stub
		cursor = database.rawQuery("SELECT name FROM career WHERE careers_id="+col.career, null);
		if(cursor.getCount()==1){
			cursor.moveToFirst();
			selectCareers1.setText(cursor.getString(cursor.getColumnIndex("name")));
			selectCareers1.setTag(col.career);
		}
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			doEdit();
		}

	};

	private void doEdit() {
		// TODO Auto-generated method stub
	
		String description = videoDescription1.getText().toString().trim();
		String careerID = (String) selectCareers1.getTag();
		String career_name = (String) selectCareers1.getText().toString().trim();
		String language = selectLanguage1.getText().toString().trim();
		String name = videoTitle1.getText().toString().trim();
		String stageId = (String) selectState1.getTag();
		String stageId_name = (String) selectState1.getText().toString().trim();
		
		String tellus = tellUsAboutYou.getText().toString();
		String tellusId = (String) tellUsAboutYou.getTag();
		
		if(checkForEmpty()){
			Toast.makeText(context, ""+context.getString(R.string.allFiledsRequired), Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(col.isLocal){
			
			ContentValues cv = new ContentValues();
		    cv.put("stage", stageId_name);
			cv.put("stageid", stageId);
			cv.put("description", description);
			cv.put("tell_us", tellus);
			cv.put("tell_us_id", tellusId);
			cv.put("career_id", careerID);
			cv.put("video_title", name);
			cv.put("video_language", language);
 
			int i = database.update("video_table", cv, "id="+col.id, null);
			activity.goBack();
//			database.insert("", "", cv)			
		}
		
	}
	
	private boolean checkForEmpty() {
		// TODO Auto-generated method stub
		
		String description = videoDescription1.getText().toString().trim();
		if(description.isEmpty())return true;
		
		String domain_name = selectCareers1.getText().toString().trim();
		if(domain_name.isEmpty())return true;
		
		String stageId = (String) selectState1.getTag();
		String stageId_name = (String) selectState1.getText().toString().trim();
		if(stageId_name.isEmpty())return true;
		
		String language = selectLanguage1.getText().toString().trim();
		if(language.isEmpty())return true;
		
		String name = videoTitle1.getText().toString().trim();
		if(name.isEmpty())return true;
		
		String tellUsId = (String) tellUsAboutYou.getTag();
		String tellUs = (String) tellUsAboutYou.getText().toString();
		if(tellUs.isEmpty())return true;
		
		String careerID = (String) selectCareers1.getTag();
		String career = (String) selectCareers1.getText().toString();
		if(careerID.isEmpty())return true; 
		
		return false;
	}
	
	
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
				requestCode = SELECT_STAGE_REQUEST;
				array = getArray(stageList);
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
				label.setText("#Vcareers #"+career_name);
				selectCareers1.setText(career_name);
				selectCareers1.setTag(career_careers_id);
				break;
			case SELECT_TELL_ABOUT_YOU_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				tellUsAboutYou.setText(aboutYourCollection.get(position).name);
				tellUsAboutYou.setTag(aboutYourCollection.get(position).id);
				break;
			case SELECT_STAGE_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				selectState1.setText(stageList.get(position).name);
				selectState1.setTag(stageList.get(position).id);
				break;
			case SELECT_LANGUAGE_REQUEST:
				title = data.getStringExtra("result");
				position = data.getIntExtra("position",0);
				selectLanguage1.setText(title);
				break;
			default:
				break;
			}
		}
	};
	
	String[] getArray(List<CommonCollection> list){
		String array[] = new String[list.size()];
		for(int i=0;i<list.size();i++){
			array[i] = list.get(i).name;
		}
		return array;
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
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		activity.setHeader(true, R.drawable.button_selector_round_blue, 0, context.getString(R.string.back), null, "Career vLearn", null, 0);
	}
	List<NameValuePair> al = new ArrayList<NameValuePair>();
	private AsyncTask<String, Void, String> task;
	private void loadAboutYou() {
		// TODO Auto-generated method stub
		util.showLoading();
		task = connection.getData("user/getParams/aboutyou", "GET", null,
				EditCareerVideo.this, loadAboutYou);
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
				if(col.aboutyou.equalsIgnoreCase(id)){
					tellUsAboutYou.setText(name);
					tellUsAboutYou.setTag(id);
				}
			}

		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	
	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case loadAboutYou:
			onloadAboutYou(result);
			break;
		
		default:
			break;
		}
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case loadAboutYou:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadAbout), Toast.LENGTH_SHORT).show();
			break;
		
		default:
			break;
		}
	}

}

package com.vlearn.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.vlearn.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

public class VUtil {

	Context context;
	private final String sfName = "SharedPreferenceVlearn";

	private SharedPreferences preferences;
	private Editor editor;

	public static final String ISDATASAVED = "DATASAVE";
	public static final String USER_ID = "user_id";
	public static final String USER_FULLNAME = "user_fullName";
	public static final String USER_FIRSTNAME = "user_fisrtName";
	public static final String USER_LASTNAME = "user_lastName";
	public static final String USER_NAME = "user_name";
	public static final String USER_PASSWORD = "user_password";
	public static final String USER_AUTOLOGIN = "user_autoLogin";
	public static final String USER_DOB = "user_dob";
	public static final String USER_EMAIL = "user_email";
	public static final String USER_ROLE = "mom";
	public static final String USER_GRADNAME = "user_gradeName";
	public static final String USER_GRADID = "user_gradeId";
	public static final String USER_AVTAAR = "user_avtaar";
	public static final String USER_ZIPCODE = "user_zipCode";
	public static final String USER_ACCESS = "user_access";
	public static final String USER_BIO = "user_bio";
	public static final String USER_VIDEOS = "user_videos";
	public static final String USER_KIDS = "user_kids";
	public static final String USER_CAREERINFO = "user_careerInfo";
	public static final String USER_CLASSES = "user_classes";

	public static final String PREF_KEY_FACEBOOK_LOGIN = "isFacebookLogedIn";
	public static final String PREF_KEY_ACCESSTOKEN = "facebookaccesstoken";
	
	public static boolean IS_APP_LANG_ENG;
	boolean IS_LOADERVISIBLE = false;
	
	public VUtil(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public static boolean isEmailValid(String email) {
		String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
				+ "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
				+ "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
				+ "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches())
			return true;
		else
			return false;
	}
	
	
	public static boolean isStringHasHTML(String message) {
		String regExpn = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";

		CharSequence inputStr = message;

		Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches())
			return true;
		else
			return false;
	}


	public void saveInPreference(HashMap<String, Object> list) {
		if (preferences == null)
			preferences = context.getSharedPreferences(sfName,
					Context.MODE_PRIVATE);
		editor = preferences.edit();
		Iterator<String> keySet = list.keySet().iterator();

		while (keySet.hasNext()) {
			String key = keySet.next();
			Object val = list.get(key);

			if (val instanceof String)
				editor.putString(key, (String) val);
			else if (val instanceof Integer)
				editor.putInt(key, (Integer) val);
			else if (val instanceof Long)
				editor.putLong(key, (Long) val);
			else if (val instanceof Float)
				editor.putFloat(key, (Float) val);
			else if (val instanceof Set<?>)
				editor.putStringSet(key, (Set<String>) val);
			else if (val instanceof Boolean)
				editor.putBoolean(key, (Boolean) val);
			editor.commit();
		}
	}

	public void saveInPreference(String key, String value) {
		if (preferences == null)
			preferences = context.getSharedPreferences(sfName,
					Context.MODE_PRIVATE);
		editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void saveInPreference(String key, Boolean value) {
		if (preferences == null)
			preferences = context.getSharedPreferences(sfName,
					Context.MODE_PRIVATE);
		editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public Object getFromSharedPreference(VariableType type, String key) {
		if (preferences == null)
			preferences = context.getSharedPreferences(sfName,
					Context.MODE_PRIVATE);
		if (type == VariableType.BOOLEAN)
			return preferences.getBoolean(key, false);
		else if (type == VariableType.STRING)
			return preferences.getString(key, "");
		else if (type == VariableType.STRINGSET)
			return preferences.getStringSet(key, new HashSet<String>());
		else if (type == VariableType.FLOAT)
			return preferences.getFloat(key, 0.0f);
		else if (type == VariableType.INTEGER)
			return preferences.getInt(key, 0);
		else if (type == VariableType.LONG)
			return preferences.getLong(key, 0);
		else
			return null;
	}

	View loaderView;
	TextView LoadingText;

	public void initLoader(Activity activity) {
		initLoader(activity, "");
	}

	public void initLoader(Activity activity, String customMessage) {
		loaderView = activity.findViewById(R.id.loaderView);
		LoadingText = (TextView) activity.findViewById(R.id.progressBarText);
		if (!customMessage.isEmpty())
			LoadingText.setText(customMessage);
	}

	public void destroyLoader() {
		loaderView = null;
	}

	public void showLoading(String customeMessage) {
		try{
		if (loaderView != null) {
			IS_LOADERVISIBLE = true;
			if (!customeMessage.isEmpty())
				LoadingText.setText(customeMessage);
			loaderView.setVisibility(View.VISIBLE);
		}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public boolean isLoading() {
		return IS_LOADERVISIBLE;
	}

	public void showLoading() {
		showLoading("");
	}

	public void hideLoading() {
		try{
		if (loaderView != null) {
			IS_LOADERVISIBLE = false;
			loaderView.setVisibility(View.INVISIBLE);
		}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void releaseDaabase(SQLiteDatabase database) {
		if (database != null) {
			database.close();
			database = null;
		}

	}
	
	public static String convertToTag(String message){
		message = message.replaceAll("[-+.^:,]","");
		StringBuilder builder = new StringBuilder();
		String[] splited = message.split("\\s+");
		for(int i=0;i<splited.length;i++)
		    builder.append(Character.toUpperCase(splited[i].charAt(0)) + splited[i].substring(1));
		return builder.toString();
	}
	
	public static String[] findTag(String message){
		String[] arr = new String[2];
		Pattern MY_PATTERN = Pattern.compile("#(\\w+)");
		Matcher mat = MY_PATTERN.matcher(message);
		int i=0;
		while(mat.find() && i<2){
			arr[i] = mat.group();
			i++;
		}
		return arr;
	}

	public static String removeHashTag(String message){
		return message.replaceFirst("#(\\w+)", "").replaceFirst("#(\\w+)", "");
	}

	
	public static Bitmap decodeFile(File f, int WIDTH, int HIGHT) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	public static String getCompressFile(File f, int WIDTH, int HIGHT) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;

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
				Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f),
						null, o2);
				bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (Throwable ignore) {
				}
			}

			return vFile.getAbsolutePath();
		
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	
	
}

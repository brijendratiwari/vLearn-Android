package com.vlearn.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class VlearnDataBase extends SQLiteOpenHelper{

	Context context;
	public static String dbName = "vlearndatb";
	CursorFactory factory = null;
	public static int version = 1;
	
	
	public VlearnDataBase(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		this.context = context;
	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		createStagesTable(db);
		createGradeTable(db);
		createSubjectTable(db);
		createRoleTable(db);
		createCareersTable(db);

		createPadrenoTable(db);
		createPadrenoSocialTable(db);
		createSchoolLevelTable(db);
		
		createMyKid(db);
		createclassType(db);
		createVideoTable(db);
		
	}

	
	private void createVideoTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS video_table(id INTEGER PRIMARY KEY AUTOINCREMENT , video_type TEXT,video_user_id TEXT,video_language TEXT,video_title TEXT,video_thumb TEXT,video_file TEXT, career_id TEXT, tell_us TEXT, tell_us_id TEXT,stage TEXT,stageid TEXT,description TEXT, grade TEXT, grade_id TEXT, subject TEXT,subject_id TEXT, domain TEXT, domain_id TEXT, standard TEXT,standard_id TEXT, skill TEXT,skill_id TEXT, video_status TEXT,video_server_id TEXT);");
	}
	
	private void createMyKid(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS mykid(id INTEGER PRIMARY KEY , kid_id TEXT , first_name TEXT , username TEXT , zip_code TEXT , email TEXT , career_id TEXT , dob TEXT , last_name TEXT , role TEXT , access TEXT , grade_level_id TEXT , password TEXT , class_id TEXT);");
	}

	
	private void createSchoolLevelTable(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS school_level(id INTEGER PRIMARY KEY ,school_levelid TEXT, level_name TEXT);");
	}
	
	private void createPadrenoSocialTable(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS padreno_social(id INTEGER PRIMARY KEY , data TEXT);");
	}
	
	private void createPadrenoTable(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS padreno_indivdual(id INTEGER PRIMARY KEY , data TEXT);");
	}

	private void createStagesTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS stages(id INTEGER PRIMARY KEY , stage_id TEXT, englishName TEXT, spanishName TEXT);");
	}
	
	private void createGradeTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS grades(id INTEGER PRIMARY KEY , grade_id TEXT, englishName TEXT, spanishName TEXT);");
	}
	
	private void createSubjectTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS subject(id INTEGER PRIMARY KEY , subject_id TEXT, englishName TEXT, spanishName TEXT);");
	}
	
	private void createRoleTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS role(id INTEGER PRIMARY KEY , role_id TEXT, name TEXT);");
	}
	
	private void createclassType(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS classType(id INTEGER PRIMARY KEY , name TEXT);");
	}
	
	private void createCareersTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS career(id INTEGER PRIMARY KEY , careers_id TEXT, name TEXT, imgUrl TEXT);");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}

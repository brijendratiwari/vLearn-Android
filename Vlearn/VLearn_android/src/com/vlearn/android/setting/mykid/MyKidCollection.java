package com.vlearn.android.setting.mykid;


public class MyKidCollection {
	
	public int dbId;
	public String id;
	public String first_name;
	public String username;
	public String zip_code;
	public String email;
	public String career_id;
	public String dob;
	public String last_name;
	public String role;
	public String access;
	public String grade_level_id;
	public String password;
	public boolean isCross;
	public boolean isDelete;
	public String imgUrl;
	public String classId;
	public boolean isChecked;
	
	public MyKidCollection(int dbId, String id, String first_name, String username, String zip_code, String email, String career_id, String dob, String last_name, String role, String access, String grade_level_id, String password,String imgUrl,boolean isCross,boolean isDelete,String classId,boolean isChecked) {
		// TODO Auto-generated constructor stub
		this.dbId = dbId;
		this.id = id;
		this.first_name = first_name;
		this.username = username;
		this.zip_code = zip_code;
		this.email = email;
		this.career_id = career_id;
		this.dob = dob;
		this.last_name = last_name;
		this.role = role;
		this.access = access;
		this.grade_level_id = grade_level_id;
		this.password = password;
		this.isCross = isCross;
		this.isDelete = isDelete;
		this.imgUrl = imgUrl;
		this.classId = classId;
		this.isChecked = isChecked;
	}

}

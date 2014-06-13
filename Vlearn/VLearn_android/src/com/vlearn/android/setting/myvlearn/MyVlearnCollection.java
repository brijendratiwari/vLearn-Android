package com.vlearn.android.setting.myvlearn;


public class MyVlearnCollection {

	public String id;
	public String uid;
	public String type;
	public String career;
	public String aboutyou;
	public String message;
	public String name;
	public String desc;
	public String embed_code;
	public String videofile;
	public String icon;
	public String stage;
	public String grade;
	public String subject;
	public String standard;
	public String substandard;
	public String skill;
	public String approval;
	public String added_datetime;
	public String page;
	public String language;
	public String favorite;
	public String career_name;
	public String hits;
	public String f_convert;
	public String f_convert_failed;
	public String na_to_learning_bank;
	public String sife_learning_object;
	public int VideoType;
	public String domainName;
	public boolean isDraft;
	public boolean isLocal;
	public String domainId;
	public String video_server_id;

	public MyVlearnCollection(String domainname,String id, String uid, String type, String career, String aboutyou, String message, String name, String desc, String embed_code, String videofile, String icon, String stage, String grade, String subject, String standard, String substandard, String skill, String approval, String added_datetime, String page, String language, String favorite, String career_name, String hits, String f_convert, String f_convert_failed, String na_to_learning_bank, String sife_learning_object,int VideoType,boolean isDraft,boolean isLocal,String domainId,String video_server_id) {
		// TODO Auto-generated constructor stub
		this.domainName = domainname;
		this.id = id;
		this.uid = uid;
		this.type = type;
		this.career = career;
		this.aboutyou = aboutyou;
		this.message = message;
		this.name = name;
		this.desc = desc;
		this.embed_code = embed_code;
		this.videofile = videofile;
		this.icon = icon;
		this.stage = stage;
		this.grade = grade;
		this.subject = subject;
		this.standard = standard;
		this.substandard = substandard;
		this.skill = skill;
		this.approval = approval;
		this.added_datetime = added_datetime;
		this.page = page;
		this.language = language;
		this.favorite = favorite;
		this.career_name = career_name;
		this.hits = hits;
		this.f_convert = f_convert;
		this.f_convert_failed = f_convert_failed;
		this.na_to_learning_bank = na_to_learning_bank;
		this.sife_learning_object = sife_learning_object;
		this.VideoType = VideoType;
		this.isDraft = isDraft;
		this.isLocal = isLocal;
		this.domainId = domainId;
		this.video_server_id = video_server_id;
	}
}

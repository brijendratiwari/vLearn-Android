package com.vlearn.android.setting;


public class SettingsCollection {

	public String videoTitle;
	public String description;
	public String videopreview;
	public String videoUrl;
	public int noofrating;
	public int star_no;

	public SettingsCollection(String videoTitle, String description, String videopreview,String videoUrl, int noofrating, int star_no) {
		// TODO Auto-generated constructor stub
		this.videopreview = videopreview;
		this.videoTitle = videoTitle;
		this.description = description;
		this.noofrating = noofrating;
		this.videoUrl = videoUrl;
		this.star_no = star_no;
	}
	
}

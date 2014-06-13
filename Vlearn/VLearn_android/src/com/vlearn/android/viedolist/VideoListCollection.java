package com.vlearn.android.viedolist;

import org.json.JSONArray;


public class VideoListCollection {

	public String id;
	public String title;
	public String url;
	public String filename;
	public String thumbnail;
	public String username;
	public String fullname;
	public String description;
	public String bio;
	public String userId;
	public String timestamp;
	public String approval;
	public String avatar;
	public String avg_rating;
	public String total_comments;
	public JSONArray kids_assigned;
	public JSONArray user_rating;
	public boolean isFlagActivate;
	protected String hashTag;
	
	public VideoListCollection(String id, String title, String url, String filename, String thumbnail, String username, String fullname, String description, String bio, String userId, String timestamp, String approval, String avatar, String avg_rating, String total_comments, JSONArray kids_assigned, JSONArray user_rating, boolean isFlagActivate, String hashTag) {
		// TODO Auto-generated constructor stub
		
		this.id=id;
		this.title=title;
		this.url=url;
		this.filename=filename;
		this.thumbnail=thumbnail;
		this.username=username;
		this.fullname=fullname;
		this.description=description;
		this.bio=bio;
		this.userId=userId;
		this.timestamp=timestamp;
		this.approval=approval;
		this.avatar=avatar;
		this.avg_rating=avg_rating;
		this.total_comments=total_comments;
		this.kids_assigned=kids_assigned;
		this.user_rating=user_rating;
		this.isFlagActivate = isFlagActivate;
		this.hashTag = hashTag;
		
	}
	
}

package com.vlearn.android.myCommunity;

import android.graphics.Bitmap;

public class MyCommunityCollection {
	public String comment;
	public String description;
	public String profilepic;
	public String VideoPic;
	public int starValue;
	
	public MyCommunityCollection( String comment,String description,String profilepic,String VideoPic, int startValue) {
		// TODO Auto-generated constructor stub
		this.comment = comment;
		this.description = description;
		this.profilepic = profilepic;
		this.VideoPic = VideoPic;
		this.starValue = startValue;
	}
}

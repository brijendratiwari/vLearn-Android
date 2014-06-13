package com.vlearn.android.ui.picker;

public class CareerPickerCollection {

	public String id;
	public String careers_id;
	public String name;
	public String imgUrl;
	public boolean isChecked;
	
	public CareerPickerCollection(String id ,String careers_id ,String name ,String imgUrl, boolean isChecked) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.careers_id = careers_id;
		this.name = name;
		this.imgUrl = imgUrl;
		this.isChecked = isChecked;
	}
	
}

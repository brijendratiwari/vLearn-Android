package com.vlearn.android.setting.myvlearn;

import java.util.List;

import com.vlearn.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ParentTeacherAdapter extends BaseAdapter{

	List<ParentTeacherCollection> list;
	Context context;
	
	LayoutInflater inflater;
	public ParentTeacherAdapter(Context context,List<ParentTeacherCollection> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public ParentTeacherCollection getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView;
		ParentTeacherCollection col = list.get(position);
		
		if(view == null){
			view = inflater.inflate(R.layout.text_view, null, false);
			
		}
		TextView tv = (TextView) view;
		tv.setText(col.first_name+" "+col.last_name);
		return view;
	}
	
}

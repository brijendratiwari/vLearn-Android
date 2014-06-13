package com.vlearn.android.setting.mykid;

import java.util.List;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ClassCollectionAdapter extends BaseAdapter{

	HomeActivity activity;
	Context context;
	List<ClassCollection> list;
	LayoutInflater inflater;
	
	public ClassCollectionAdapter(Context context, List<ClassCollection> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
		this.activity = (HomeActivity) context;
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public ClassCollection getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ClassCollectionHolder holder = null;
		ClassCollection col = list.get(position);
		
		if(view == null){
			
			view = inflater.inflate(R.layout.text_view, null, false);
			holder = new ClassCollectionHolder();
			holder.kidName = (TextView) view;
			view.setTag(holder);
			
		}else{
			holder = (ClassCollectionHolder) view.getTag();
		}
		
		holder.kidName.setText(col.class_name);
		return view;
	}


	class ClassCollectionHolder{
		TextView kidName;
	}
	
}

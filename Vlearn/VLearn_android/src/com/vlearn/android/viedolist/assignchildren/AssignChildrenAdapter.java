package com.vlearn.android.viedolist.assignchildren;

import java.util.List;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.setting.mykid.MyKidCollection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

public class AssignChildrenAdapter extends BaseAdapter{

	List<MyKidCollection> list;
	Context context;
	HomeActivity activity;
	LayoutInflater inflater;
	ImageLoader loader;
	public AssignChildrenAdapter(Context context,List<MyKidCollection> list,ImageLoader loader) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
		this.activity = (HomeActivity) this.context;
		this.inflater = LayoutInflater.from(context);
		this.loader = loader;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public MyKidCollection getItem(int position) {
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
		MyKidCollection col = list.get(position);
		AssignChildrenHolder holder = null;
		if(view == null){
			view = inflater.inflate(R.layout.assign_listview_row, null, false);
			holder = new AssignChildrenHolder();
			
			holder.profilePic = (ImageView) view.findViewById(R.id.profilePic);
			holder.textView = (CheckedTextView) view.findViewById(R.id.checkedtextview);
			view.setTag(holder);
		}else{
			holder = (AssignChildrenHolder) view.getTag();
		}

		holder.textView.setText(col.first_name+" "+col.last_name);
		holder.textView.setChecked(col.isChecked);
		
		loader.DisplayImage(col.imgUrl, holder.profilePic);
		return view;
	}

	class AssignChildrenHolder{
		ImageView profilePic;
		CheckedTextView textView;
	}

}

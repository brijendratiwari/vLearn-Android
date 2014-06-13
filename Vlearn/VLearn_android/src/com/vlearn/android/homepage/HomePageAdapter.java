package com.vlearn.android.homepage;

import java.util.List;

import com.vlearn.android.R;

import android.content.Context;
import android.hardware.Camera.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomePageAdapter extends BaseAdapter{

	private List<HomePageCollection> list;
	private Context context;
	private int resources;
	private LayoutInflater inflater;
	
	public HomePageAdapter(Context context, int resources, List<HomePageCollection> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.resources = resources;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	
	private LayoutInflater getLayoutInflater() {
		// TODO Auto-generated method stub
		return LayoutInflater.from(context);
	}

	@Override
	public HomePageCollection getItem(int position) {
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
		
		HomePageCollection collection = list.get(position);
		HomePageHolder holder = null;
		
		if(convertView == null){
			
			convertView = getLayoutInflater().inflate(resources, null, false);
			convertView.setLayoutParams(new AbsListView.LayoutParams((int)context.getResources().getDimension(R.dimen.margintop_65),(int) context.getResources().getDimension(R.dimen.margintop_65)));
			holder = new HomePageHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.grad = (TextView) convertView.findViewById(R.id.subtitle);
			convertView.setTag(holder);
			
		}else{
			holder = (HomePageHolder) convertView.getTag();
		}

		if(!collection.isGrade) holder.grad.setVisibility(View.INVISIBLE);
		if(collection.title.equalsIgnoreCase("Teacher")){
			holder.title.setTextSize(11f);
		}
		holder.title.setText(collection.title);
		
		return convertView;
	}

	class HomePageHolder{
		TextView title;
		TextView grad;
	}
	
}

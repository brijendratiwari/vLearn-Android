package com.vlearn.android.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String> {

		String ttfName;
		Context context;
		String[] values;
		int resource;
		LayoutInflater inflater;

		public CustomAdapter(Context context, int resource, String[] objects,
				String ttfName) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			this.context = context;
			this.resource = resource;
			this.values = objects;
			this.ttfName = ttfName;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView tv = (TextView) convertView;
			if (tv == null) {
				tv = (TextView) inflater.inflate(resource, null, false);
				tv.setTypeface(Typeface.createFromAsset(context.getAssets(),
						ttfName));
			}

			tv.setText(values[position]);

			return tv;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			TextView tv = (TextView) convertView;
			if (tv == null) {
				tv = (TextView) inflater.inflate(resource, null, false);
				tv.setTypeface(Typeface.createFromAsset(context.getAssets(),
						ttfName));
			}

			tv.setText(values[position]);

			return tv;
		}

	}
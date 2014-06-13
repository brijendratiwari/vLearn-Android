package com.vlearn.android.list;

import com.facebook.LoginActivity;
import com.vlearn.android.MainActivity;
import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.homepage.HomePage;
import com.vlearn.android.myCommunity.MyCommunity;
import com.vlearn.android.setting.Settings;
import com.vlearn.android.util.VUtil;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Navigationlist extends Fragment {

	HomeActivity activity;
	Context context;
	TextView vlearningBank, mycommunity, settings;
	Button logout;
	VUtil util;

	public static Navigationlist Create() {
		Navigationlist list = new Navigationlist();
		return list;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		util = new VUtil(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.navigation_list, null, false);

		vlearningBank = (TextView) view.findViewById(R.id.vlearningBank);
		mycommunity = (TextView) view.findViewById(R.id.mycommunity);
		settings = (TextView) view.findViewById(R.id.setting);
		logout = (Button) view.findViewById(R.id.logout);
		
		logout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				util.saveInPreference(VUtil.USER_AUTOLOGIN, false);
				Intent intent = new Intent(context, MainActivity.class);
				intent.putExtras(new Bundle());
				activity.startActivity(intent);
				activity.finish();
				activity.overridePendingTransition(R.anim.slideright1, R.anim.slideright2);
				
			} 
		}); 
		Drawable img = writeOnDrawable( R.drawable.setting_ico, "ME");
		img.setBounds( 0, 0, (int)context.getResources().getDimension(R.dimen.margintop_30), (int)context.getResources().getDimension(R.dimen.margintop_30) );
		settings.setCompoundDrawables( img, null, null, null );
		
		/*img = context.getResources().getDrawable(R.drawable.vlearn_text);
		img.setBounds( 0, 0, (int)context.getResources().getDimension(R.dimen.margintop_102), (int)context.getResources().getDimension(R.dimen.margintop_50) );
		vlearningBank.setCompoundDrawables( img, null, null, null );*/
		
		img = context.getResources().getDrawable(R.drawable.card_baloon);
		img.setBounds( 0, 0, (int)context.getResources().getDimension(R.dimen.margintop_30), (int)context.getResources().getDimension(R.dimen.margintop_30) );
		mycommunity.setCompoundDrawables( img, null, null, null );
		 
		vlearningBank.setOnClickListener(onClickListener);
		mycommunity.setOnClickListener(onClickListener);
		settings.setOnClickListener(onClickListener);

		return view;
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.vlearningBank:
				activity.changePage(HomePage.Create());
				break;
			case R.id.mycommunity:
				activity.changePage(MyCommunity.Create());
				break;
			case R.id.setting:
				activity.changePage(Settings.Create());
				break;

			default:
				break;
			}
		}
	};
	
	public BitmapDrawable writeOnDrawable(int drawableId, String text){
		
        Bitmap img = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true), (int)context.getResources().getDimension(R.dimen.margintop_20), (int)context.getResources().getDimension(R.dimen.margintop_20), true);
        Bitmap bm = Bitmap.createBitmap((int)context.getResources().getDimension(R.dimen.margintop_30),(int) context.getResources().getDimension(R.dimen.margintop_30), Bitmap.Config.ARGB_8888);
        
        Paint paint = new Paint(); 
        paint.setStyle(Style.FILL);  
        paint.setColor(Color.WHITE); 
        paint.setTextSize(context.getResources().getDimension(R.dimen.textSizeMedium)); 
        paint.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Cookies.ttf"));

        Canvas canvas = new Canvas(bm);
        canvas.drawBitmap(img, 0, 0, paint);
        canvas.drawText(text, 7, bm.getHeight(), paint);

        return new BitmapDrawable(getResources(),bm);
    }
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		activity.setHeader(false, 0, 0, null, null, null, null, 0);
	}

}

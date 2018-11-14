package com.lingjie.qqslidingmenu;

import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lingjie.qqslidingmenu.View.SlidingMenu;
import com.lingjie.qqslidingmenu.View.SlidingMenu.OnDragStateChangeListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class MainActivity extends Activity {

	private ListView menu_listview;
	private ListView main_listview;
	private SlidingMenu slidingMenu;
	private ImageView iv_head;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		menu_listview = (ListView) findViewById(R.id.menu_listview);
		main_listview = (ListView) findViewById(R.id.main_listview);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		menu_listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.sCheeseStrings){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView = (TextView) super.getView(position, convertView, parent);
				textView.setTextColor(Color.WHITE);
				return textView;
			}
		});
		main_listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.NAMES));
		slidingMenu.setOnDragStateChangeListener(new OnDragStateChangeListener() {
			
			@Override
			public void onOpen() {
				menu_listview.smoothScrollToPosition(new Random().nextInt(menu_listview.getCount()));
			}
			
			@Override
			public void onDrag(float fraction) {
				ViewHelper.setAlpha(iv_head, 1-fraction);
			}
			
			@Override
			public void onClose() {
				ViewPropertyAnimator.animate(iv_head).translationXBy(15).setInterpolator(new CycleInterpolator(500));
			}
		});
	}
	

}

package com.ustc.engineering.lab410.HeartMonitor.ui.main;


//import com.ustc.engineering.lab410.HeartMonitor.ui.widget.CustomToast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ustc.engineering.lab410.HeartMonitor.R;
import com.ustc.engineering.lab410.HeartMonitor.ui.widget.CustomToast;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;


/**
 * 
 * @author hqq
 * 
 */
public class MainActivity extends FragmentActivity implements
OnClickListener {
	
	private ImageView mImgAvatar;
	public static  FragmentTabHost mTabHost;//实际tab定义
	public static LinearLayout mCurrTab;//自定义的控件定义
	private long firstClickTime;
	private long secondClickTime;
	private final long MAX_CLICK_TIME = 3 * 1000;
	public static TextView mTxt_home;
	public static ImageView mImg_home;
	public static ImageView mImg_record;
	public static TextView mTxt_record;
	
	public static int[] mPreDrawable = { R.drawable.ic_home_pre,
			R.drawable.ic_marking_pre, R.drawable.ic_report_pre,
			R.drawable.ic_interactive_pre , R.drawable.ic_home_pre};
	public static  int[] mNorDrawable = { R.drawable.ic_home_nor,
			R.drawable.ic_marking_nor, R.drawable.ic_report_nor,
			R.drawable.ic_interactive_nor, R.drawable.ic_home_nor};

	private static int mCurrTabIndex = 0;
	
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initialize();
  }
  /**
	 * 初始化界面
	 */
	private void initialize() {
	
		//初始化控件并设置监听，在下面的onClick()中设置每个tab的view显示，其实布局文件的btn_home,btn_mark等不是实际产生的tab，只是
		//在后面的该控件的onClick()中设置对应tab的view显示
		mImgAvatar = (ImageView) findViewById(R.id.head_image);
		DisplayImageOptions option = new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.bg_smallhead_home)
				.showImageForEmptyUri(R.drawable.bg_smallhead_home).build();
		
		LinearLayout btnRecord = (LinearLayout) this
				.findViewById(R.id.btn_record);
		btnRecord.setOnClickListener(this);
		this.findViewById(R.id.btn_analysis).setOnClickListener(this);
		this.findViewById(R.id.btn_three_dimensional).setOnClickListener(this);
		this.findViewById(R.id.btn_interaction).setOnClickListener(this);
		this.findViewById(R.id.btn_help).setOnClickListener(this);

		
		mTabHost = (FragmentTabHost) this.findViewById(R.id.main_tabhost);
		mTabHost.setup((Context) this, this.getSupportFragmentManager(),
				R.id.tab_content_layout);
		mTabHost.getTabWidget().setVisibility(View.GONE);
		mTabHost.getTabWidget().setDividerDrawable(null);
		
		mTabHost.addTab(mTabHost.newTabSpec("0").setIndicator("record"),
				RecordFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("1").setIndicator("analysis"),
				AnalysisFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("2").setIndicator("three_dimensional"),
				ThreeDimensionalFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("3").setIndicator("interaction"),
				Interaction.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("4").setIndicator("head_image"),
				HeadImage.class, null);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {

			}
		});
		mCurrTab = btnRecord;
		setTabStyle(true, 0);
		mImgAvatar.setOnClickListener(this);
	}
	
	public static void onTabChanged(int pageIndex, View view) {
		if (mCurrTabIndex != 4) {
			setTabStyle(false, mCurrTabIndex);
		}
		if (pageIndex != 4) {
			mCurrTab = (LinearLayout) view;
			setTabStyle(true, pageIndex);
		}
		mCurrTabIndex = pageIndex;
		mTabHost.setCurrentTab(pageIndex);
	}

	public static  void setTabStyle(Boolean selected, int pageIndex) {
		String bgColor = "#f2f2f3";
		String txtColor = "#3D3D3D";
		int img = mNorDrawable[pageIndex];
		if (selected) {
			bgColor = "#45c298";
			txtColor = "#ffffff";
			img = mPreDrawable[pageIndex];
		}
		// mCurrTab.setBackgroundColor(Color.parseColor(bgColor));
		ImageView imgView = (ImageView) mCurrTab.getChildAt(0);
		if (imgView instanceof ImageView) {
			((ImageView) imgView).setImageResource(img);
		}
		View text = mCurrTab.getChildAt(1);
		if (text instanceof TextView) {
			((TextView) text).setTextColor(Color.parseColor(txtColor));
		}
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {


		case R.id.head_image:
			if (mCurrTabIndex != 4) {
				onTabChanged(4, v);
			}
//			 Intent hIntent = new Intent();
//			 hIntent.setClass(getBaseContext(), HomeActivity.class);
//			 startActivity(hIntent);
			break;

		case R.id.btn_record:
			if (mCurrTabIndex != 0) {
				onTabChanged(0, v);
			}
			break;
		case R.id.btn_analysis:
			if (mCurrTabIndex != 1) {
				onTabChanged(1, v);
			}
			break;
		case R.id.btn_three_dimensional:
			if (mCurrTabIndex != 2) {
				onTabChanged(2, v);
			}
			break;
		case R.id.btn_interaction:
			if (mCurrTabIndex != 3) {
				onTabChanged(3, v);
			}
			break;
		case R.id.btn_help:
//			Intent intent = new Intent(this, AboutUsActivity.class);
//			this.startActivity(intent);
			break;
		default:
			break;
		}

	}
	
	@Override
	public void onBackPressed() {
		doubleClick();
	}
	
	private void doubleClick() {
		// 第一次点击
		if (firstClickTime == 0) {
			firstClickTime = System.currentTimeMillis();
			CustomToast.showToast(this, "再次点击退出", CustomToast.LENGTH_LONG);
		} else {
			secondClickTime = System.currentTimeMillis();

			// 超出最长约定时间，不退出应用
			if (secondClickTime - firstClickTime > MAX_CLICK_TIME) {
				firstClickTime = secondClickTime;
				CustomToast.showToast(this, "再次点击退出", CustomToast.LENGTH_LONG);
			} else {
				
				finish();
			}
		}
	}
	
}
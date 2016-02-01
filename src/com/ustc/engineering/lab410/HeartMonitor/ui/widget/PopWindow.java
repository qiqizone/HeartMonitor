package com.ustc.engineering.lab410.HeartMonitor.ui.widget;

/**
 * Created by ustc on 2016/1/6.
 */

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.ustc.engineering.lab410.HeartMonitor.R;

public class PopWindow extends PopupWindow {
    private View mainView;
    private LinearLayout layout1, layout2,layout3,layout4,layout5,layout6,layout7,
                         layout8,layout9,layout10,layout11,layout12;

    public PopWindow(Activity paramActivity, View.OnClickListener paramOnClickListener, int paramInt1, int paramInt2){
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(R.layout.popwin_share, null);
        //导联布局设置
        layout1 = (LinearLayout)mainView.findViewById(R.id.layout1);
        layout2 = (LinearLayout)mainView.findViewById(R.id.layout2);
        layout3 = (LinearLayout)mainView.findViewById(R.id.layout3);
        layout4 = (LinearLayout)mainView.findViewById(R.id.layout4);
        layout5 = (LinearLayout)mainView.findViewById(R.id.layout5);
        layout6 = (LinearLayout)mainView.findViewById(R.id.layout6);
        layout7 = (LinearLayout)mainView.findViewById(R.id.layout7);
        layout8 = (LinearLayout)mainView.findViewById(R.id.layout8);
        layout9 = (LinearLayout)mainView.findViewById(R.id.layout9);
        layout10 = (LinearLayout)mainView.findViewById(R.id.layout10);
        layout11 = (LinearLayout)mainView.findViewById(R.id.layout11);
        layout12 = (LinearLayout)mainView.findViewById(R.id.layout12);
        //设置每个子布局的事件监听器
        if (paramOnClickListener != null){
            layout1.setOnClickListener(paramOnClickListener);
            layout2.setOnClickListener(paramOnClickListener);
            layout3.setOnClickListener(paramOnClickListener);
            layout4.setOnClickListener(paramOnClickListener);
            layout5.setOnClickListener(paramOnClickListener);
            layout6.setOnClickListener(paramOnClickListener);
            layout7.setOnClickListener(paramOnClickListener);
            layout8.setOnClickListener(paramOnClickListener);
            layout9.setOnClickListener(paramOnClickListener);
            layout10.setOnClickListener(paramOnClickListener);
            layout11.setOnClickListener(paramOnClickListener);
            layout12.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(paramInt2);
        //设置显示隐藏动画
        setAnimationStyle(R.style.AnimTools);
        //设置背景透明
        setBackgroundDrawable(new ColorDrawable(0));
    }
}




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
    private LinearLayout layout1, layout2;

    public PopWindow(Activity paramActivity, View.OnClickListener paramOnClickListener, int paramInt1, int paramInt2){
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(R.layout.popwin_share, null);
        //分享布局
        layout1 = ((LinearLayout)mainView.findViewById(R.id.layout1));
        //复制布局
        layout2 = (LinearLayout)mainView.findViewById(R.id.layout2);
        //设置每个子布局的事件监听器
        if (paramOnClickListener != null){
            layout1.setOnClickListener(paramOnClickListener);
            layout2.setOnClickListener(paramOnClickListener);
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




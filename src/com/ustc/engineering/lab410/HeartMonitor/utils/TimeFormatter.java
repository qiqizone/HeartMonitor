package com.ustc.engineering.lab410.HeartMonitor.utils;


import java.util.Calendar;
import java.util.Locale;

/**
 * 处理时间格式，根据不同设置匹配不同格式的时间值
 * 
 * @author Tiny
 * @version [版本号, 2012-4-22]
 */
public abstract class TimeFormatter
{

    /**
     * 普通模式HH:mm:ss
     */
    public static final byte TIME_REGULAR = 0;

    /**
     * 精确模式yyyy-MM-dd HH:mm:ss
     */
    public static final byte TIME_ACCURATE = 1;

    /**
     * 空格
     */
    private static String space = " ";

    /**
     * 冒号
     */
    private static String colon = ":";

    /**
     * 水平线
     */
    private static String horizontalline = "-";

    /**
     * 返回当前时间
     * 
     * @param mode 
     *            所返回的时间模式，TIME_REGULAR为普通模式HH:mm:ss，TIME_ACCURATE为精确模式yyyy-MM
     *            -dd HH:mm:ss
     * @param locale Locale
     * @return 当前时间
     */
    public static String nowTime(final int mode, final Locale locale)
    {
        final Calendar calendar = Calendar.getInstance(locale);

        // 在手机上月份需要加1才可以正确显示
        final int yInt = calendar.get(Calendar.YEAR);
        final int moInt = calendar.get(Calendar.MONTH) + 1;
        final int dInt = calendar.get(Calendar.DATE);
        final int hInt = calendar.get(Calendar.HOUR_OF_DAY);
        final int mInt = calendar.get(Calendar.MINUTE);
        final int sInt = calendar.get(Calendar.SECOND);

        String month = String.valueOf(moInt);
        String day = String.valueOf(dInt);
        String hour = String.valueOf(hInt);
        String minute = String.valueOf(mInt);
        String second = String.valueOf(sInt);

        if (moInt <= 9)
        {
            month += "0";
        }
        if (dInt <= 9)
        {
            day += "0";
        }
        if (hInt <= 9)
        {
            hour += "0";
        }
        if (mInt <= 9)
        {
            minute += "0";
        }
        if (sInt <= 9)
        {
            second += "0";
        }

        final StringBuffer time = new StringBuffer();
        if (mode == TIME_REGULAR)
        {
            time.append(hour);
            time.append(colon);
            time.append(minute);
            time.append(colon);
            time.append(second);
        }
        else
        {
            time.append(yInt);
            time.append(horizontalline);
            time.append(month);
            time.append(horizontalline);
            time.append(day);
            time.append(space);
            time.append(hour);
            time.append(colon);
            time.append(minute);
            time.append(colon);
            time.append(second);
        }

        return time.toString();
    }

    /**
     * 返回当前时间
     * 
     * @param mode 
     *            所返回的时间模式，TIME_REGULAR为普通模式HH:mm:ss，TIME_ACCURATE为精确模式yyyy-MM
     *            -dd HH:mm:ss
     * @return 当前时间
     */
    public static String nowTime(final int mode)
    {
        return nowTime(mode, Locale.CHINA);
    }
    
    /**
     * 是否今天之後
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static boolean checkBeyondToday(int year,int month, int day){

    	Calendar ca = Calendar.getInstance();
    	int nowYear = ca.get(Calendar.YEAR);
    	int nowMonth = ca.get(Calendar.MONTH) + 1;
    	int nowDay = ca.get(Calendar.DATE);
    	String now = nowYear + "/" + nowMonth + "/" + nowDay;
    	if(year>nowYear){
    		return true;
    	}else if(year < nowYear){
    		return false;
    	}
    	
    	if(month > nowMonth){
    		return true;
    	}else if(month < nowMonth){
    		return false;
    	}
    	
    	if(day > nowDay){
    		return true;
    	}else{
    		return false;
    	}		
    }
}

package com.ustc.engineering.lab410.HeartMonitor.utils;


import java.util.Calendar;
import java.util.Locale;

/**
 * ����ʱ���ʽ�����ݲ�ͬ����ƥ�䲻ͬ��ʽ��ʱ��ֵ
 * 
 * @author Tiny
 * @version [�汾��, 2012-4-22]
 */
public abstract class TimeFormatter
{

    /**
     * ��ͨģʽHH:mm:ss
     */
    public static final byte TIME_REGULAR = 0;

    /**
     * ��ȷģʽyyyy-MM-dd HH:mm:ss
     */
    public static final byte TIME_ACCURATE = 1;

    /**
     * �ո�
     */
    private static String space = " ";

    /**
     * ð��
     */
    private static String colon = ":";

    /**
     * ˮƽ��
     */
    private static String horizontalline = "-";

    /**
     * ���ص�ǰʱ��
     * 
     * @param mode 
     *            �����ص�ʱ��ģʽ��TIME_REGULARΪ��ͨģʽHH:mm:ss��TIME_ACCURATEΪ��ȷģʽyyyy-MM
     *            -dd HH:mm:ss
     * @param locale Locale
     * @return ��ǰʱ��
     */
    public static String nowTime(final int mode, final Locale locale)
    {
        final Calendar calendar = Calendar.getInstance(locale);

        // ���ֻ����·���Ҫ��1�ſ�����ȷ��ʾ
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
     * ���ص�ǰʱ��
     * 
     * @param mode 
     *            �����ص�ʱ��ģʽ��TIME_REGULARΪ��ͨģʽHH:mm:ss��TIME_ACCURATEΪ��ȷģʽyyyy-MM
     *            -dd HH:mm:ss
     * @return ��ǰʱ��
     */
    public static String nowTime(final int mode)
    {
        return nowTime(mode, Locale.CHINA);
    }
    
    /**
     * �Ƿ����֮��
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

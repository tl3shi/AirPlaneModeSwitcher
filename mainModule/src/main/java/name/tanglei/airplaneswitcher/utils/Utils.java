package name.tanglei.airplaneswitcher.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.entity.TaskEntity;
import name.tanglei.airplaneswitcher.receiver.AlarmReceiver;


public class Utils
{
	private static final String PreferenceKey = Utils.class.getName();
	private static final String TAG = Utils.class.getName();
	

	public static boolean getStoredPreferenceRooted(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(
				PreferenceKey, Context.MODE_PRIVATE);
		return preferences.getBoolean("rooted", true);
	}
	
	public static void setStoredPreferenceRooted(Context context, boolean root)
	{
		SharedPreferences preferences = context.getSharedPreferences(
				PreferenceKey, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences
				.edit();
		editor.putBoolean("rooted", root);
		editor.apply();
		Log.i(TAG, "rooted ? " + root + " write to preference.");
	}
	
	

	//only the first time invoked return true, or else false
	public static boolean isFirstTime(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(
				PreferenceKey, Context.MODE_PRIVATE);
		long time = preferences.getLong("installtime", -1L);
		if(time == -1L)
		{
			SharedPreferences.Editor editor = preferences
					.edit();
			editor.putLong("installtime", System.currentTimeMillis());
			editor.apply();
			return true;
		}
		return false;
	}
	
	

	public static String formatTime(Context context, long mini)
    {
    	Date date = new Date(mini);
    	SimpleDateFormat formatter = new SimpleDateFormat(context.getString(R.string.timeFormat), Locale.getDefault());  
        return formatter.format(date); 
    }

    public static String formatTime(Context context, Calendar time)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(context.getString(R.string.timeFormat), Locale.getDefault());
        return formatter.format(time.getTime());
    }

    public static String formatDateTime(Context context, Calendar time)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(context.getString(R.string.datetimeFormat), Locale.getDefault());
        return formatter.format(time.getTime());
    }

    public static String formatDateTime(Context context, long mini)
    {
        Date date = new Date(mini);
        SimpleDateFormat formatter = new SimpleDateFormat(context.getString(R.string.datetimeFormat), Locale.getDefault());
        return formatter.format(date);
    }

	public static boolean isRooted()
	{
		boolean root = false;
		try
		{
			if ((!new File("/system/bin/su").exists())
					&& (!new File("/system/xbin/su").exists()))
			{
				return false;
			} else
			{
				String result = ShellUtil.runRootCmd("ls /system/bin");
				if(result.length() == 0)
				     return false;
				else
					return true;
			}
		} catch (Exception e)
		{
			root = false;
			Log.i(TAG, e.getLocalizedMessage());
		}
		return root;
	}

    public static void showDialog(Context context, String title,
                                       String content, int iconid, DialogInterface.OnClickListener okListener)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setIcon(iconid);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(content);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(
                context.getString(android.R.string.ok), okListener);
        alertDialogBuilder.show();
    }
    public static void showAlertDialog(Context context, String title,
                                       String content, DialogInterface.OnClickListener okListener)
    {
        showDialog(context, title, content, android.R.drawable.ic_dialog_alert, okListener);
    }

	public static void showInfoDialog(Context context, String title,
			String content, DialogInterface.OnClickListener okListener)
	{
        showDialog(context, title, content, android.R.drawable.ic_dialog_info, okListener);
	}

    public static void showAlertDialog(Context context, String title,
    String content, DialogInterface.OnClickListener positiveListener,
                    DialogInterface.OnClickListener negativeListener)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(content);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(
                context.getString(android.R.string.ok), positiveListener);
        alertDialogBuilder.setNegativeButton(context.getString(android.R.string.cancel), negativeListener);
        alertDialogBuilder.show();
    }

	public final static boolean isScreenLocked(Context c) {
        /*
		//does not take effeckt
		android.app.KeyguardManager mKeyguardManager = (KeyguardManager) c.getSystemService(Context.KEYGUARD_SERVICE);
        boolean locked = mKeyguardManager.inKeyguardRestrictedInputMode();
        return locked;
        */
		PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
		boolean locked = !pm.isScreenOn();
        Log.i(TAG, "screen locked? "+locked);
        return locked;
    }
	
	public static boolean GetWeekdaysFromInt(int repeatday, int weeknum)
	{
	    return ((repeatday >> (weeknum-1)) & 0x1) == 0x1;
	}
	
	public static TaskEntity createTask(Context context)
    {
        Calendar time = Calendar.getInstance(Locale.getDefault());
	    return new TaskEntity(true, 0, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE),
	            context.getResources().getString(R.string.task_default_name), true);
    }

    public static Calendar getCalendar(int hour, int minute)
    {
        Calendar time = Calendar.getInstance(Locale.getDefault());
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        return time;
    }

	public static void showToast(Context con, String text)
	{
        showToast(con, text, false);
	}

    public static void showToast(Context con, String text, boolean lengthLong)
    {
        Toast.makeText(con, text, lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }



}

package name.tanglei.www;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class Utils
{
	private static final String PreferenceKey = FlightModeSwitcher.class.getName();
	private static final String TAG = Utils.class.getName();
	
	private static final String START_HOUR_KEY  = "startHour";
	private static final String STOP_HOUR_KEY  = "stopHour";
	private static final String START_MINUTE_KEY  = "startMinute";
	private static final String STOP_MINUTE_KEY  = "stopMinute";
	private static final String CURRENT_STATE_KEY  = "currentState";
	
	
	public static Config getStoredPreference(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(
				PreferenceKey, Context.MODE_PRIVATE);
		int startHour = preferences.getInt(START_HOUR_KEY, 0);
		int startMinute = preferences.getInt(START_MINUTE_KEY, 30);
		int stopHour = preferences.getInt(STOP_HOUR_KEY, 7);
		int stopMinute = preferences.getInt(STOP_MINUTE_KEY, 0);
		boolean currentState = preferences.getBoolean(CURRENT_STATE_KEY, false);
		
		return new Config(startHour, startMinute, stopHour, stopMinute, currentState);
	}
	
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
		editor.commit();
		Log.i(TAG, "rooted ? " + root + " write to preference.");
	}
	
	
	public static boolean updatePreference(Context context, Config config)
	{
		SharedPreferences preferences = context.getSharedPreferences(
				PreferenceKey, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences
				.edit();
		editor.putInt(START_HOUR_KEY, config.getStartHour());
		editor.putInt(START_MINUTE_KEY, config.getStartMinute());
		editor.putInt(STOP_HOUR_KEY, config.getStopHour());
		editor.putInt(STOP_MINUTE_KEY, config.getStopMinute());
		editor.putBoolean(CURRENT_STATE_KEY, config.isCurrentState());
		
		Log.i(TAG, "save time to preference:" + config.toString());
		editor.commit();
		
		return true;
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
			editor.commit();
			return true;
		}
		return false;
	}
	
	
	public static boolean startSchedule(Context context, Config config, boolean showTip)
	{
		if(config.isCurrentState() == false)
			return true;
		
		Calendar calendar = Calendar.getInstance();
		
		long currentTime = System.currentTimeMillis();
		Log.i(TAG, "current:" + currentTime);
		
		calendar.setTimeInMillis(currentTime);
		calendar.set(Calendar.HOUR_OF_DAY, config.getStartHour());
		calendar.set(Calendar.MINUTE, config.getStartMinute());
		long nextStarttime = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, config.getStopHour());
		calendar.set(Calendar.MINUTE, config.getStopMinute());
		long nextEndtime = calendar.getTimeInMillis();
		
		boolean rightNow = false;
		if(currentTime < nextEndtime && currentTime > nextStarttime)
		{
			rightNow = true;//no add, apply to airmode
		}
		else 
		{
			if(nextStarttime < currentTime)
				nextStarttime += 24 * 60 * 60 * 1000;
			if(nextEndtime < currentTime)
				nextEndtime += 24 * 60 * 60 * 1000;
		}
		
		Log.i(TAG, "next start:" + nextStarttime);
		Log.i(TAG, "next end:" + nextEndtime);
		
		
		Intent startIntent = new Intent(context, AlarmReceiver.class);
		startIntent.putExtra("startState", 1);
		PendingIntent startPendingIntent = PendingIntent.getBroadcast(
				context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent endIntent = new Intent(context, AlarmReceiver.class);
		endIntent.putExtra("endState", 1);
		PendingIntent endPendingIntent = PendingIntent.getBroadcast(
				context, 1, endIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				nextStarttime - 55*1000, 24 * 60 * 60 * 1000, startPendingIntent);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				nextEndtime - 55*1000, 24 * 60 * 60 * 1000,
				endPendingIntent);
		
		if(showTip)
		{
			Toast.makeText(context, context.getString(R.string.setup_on),
				Toast.LENGTH_SHORT).show();
			if(!rightNow)
				Toast.makeText(context, context.getString(R.string.nextStartTipPref) + formatTime(context, nextStarttime), 
					Toast.LENGTH_LONG).show();
		}
		
		return true;
	}
	
	public static boolean stopSchedule(Context context, boolean showTip)
	{
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Intent startIntent = new Intent(context, AlarmReceiver.class);
		PendingIntent startPendingIntent = PendingIntent.getBroadcast(
				context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Intent endIntent = new Intent(context, AlarmReceiver.class);
		PendingIntent endPendingIntent = PendingIntent.getBroadcast(
				context, 1, endIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		alarmManager.cancel(startPendingIntent);
		alarmManager.cancel(endPendingIntent);
		
		AirplaneModeService.setAirplane(context, false);
		if(showTip)
			Toast.makeText(context, context.getString(R.string.setup_off),
					Toast.LENGTH_SHORT).show();
		
		return true;
	}
	
	public static void delayOnSchedule(Context context, long old_mini_seconds, long delay_mini_seconds)
	{
		Intent startIntent = new Intent(context, AlarmReceiver.class);
		startIntent.putExtra("startState", 1);
		PendingIntent startPendingIntent = PendingIntent.getBroadcast(
				context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Log.i(TAG, "old:" + old_mini_seconds + " delay:" + delay_mini_seconds);
		
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				old_mini_seconds + delay_mini_seconds, 24 * 60 * 60 * 1000, startPendingIntent);
	}
	
	public static String formatTime(Context context, long mini)
    {
    	Date date = new Date(mini);
    	SimpleDateFormat formatter = new SimpleDateFormat(context.getString(R.string.timeFormat), Locale.getDefault());  
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
	
	public static void showAlertDialog(Context context, String title,
			String content, DialogInterface.OnClickListener listener)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(content);
		alertDialogBuilder.setCancelable(true);
		alertDialogBuilder.setPositiveButton(
				context.getString(android.R.string.ok), listener);
		alertDialogBuilder.show();
	}
	
	public static void sendBroadcastNow(Context context, boolean onairplaemode)
	{
		Intent startIntent = new Intent(context, AlarmReceiver.class);
		startIntent.putExtra("startState", onairplaemode ? 1: 0);
		startIntent.putExtra("useraction", true);
		context.sendBroadcast(startIntent);
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
}

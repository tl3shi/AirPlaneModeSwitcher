package name.tanglei.www;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class AirplaneModeService
{
	// http://developer.android.com/about/versions/android-4.2.html
	// Some device settings defined by Settings.System are now read-only.
	// If your app attempts to write changes to settings defined in Settings.
	// System that have moved to Settings.Global, the write operation will
	// silently fail
	// when running on Android 4.2 and higher.
	
	private static String TAG = AirplaneModeService.class.getName();
	
	public static int ALLOW_WRITE_SECURE_SETTINGS = 17; // my defy ok.miui
														// 2.3.7, os kerner 2.2

	public static void setAirplane(Context context, boolean enable)
	{
		boolean isEnabled = isAirplaneModeOn(context);
		if (isEnabled == enable)
			return;

		changeAirplaneMode(context, enable ? 1 : 0);
	}

	public static boolean isAirplaneModeOn(Context context)
	{
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
		{
			return isAirplaneModeOnLow(context);
		} else
		{
			return isAirplaneModeOnHigh(context);
		}
	}

	@SuppressWarnings("deprecation")
	private static boolean isAirplaneModeOnLow(Context context)
	{
		return Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	}

	private static void changeAirplaneMode(Context context, int value)
	{
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
			setSettingsOnLow(context, value);
		else
			setSettingsOnHigh(context, value);
		try{
			// should send a broad cast, or else, it will not take effect
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", value == 1);
			context.sendBroadcast(intent);
			Log.i(TAG, "ACTION_AIRPLANE_MODE_CHANGED: " + value);
		}catch(Exception e)
		{
			//TODO java.lang.SecurityException: Permission Denial: 
			//not allowed to send broadcast android.intent.action.AIRPLANE_MODE from pid=912, uid=10039
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private static boolean isAirplaneModeOnHigh(Context context)
	{
		return Settings.Global.getInt(context.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
	}

	private static void setSettingsOnHigh(Context context, int value)
	{
		// this should execute as system app, with write_secure_settings
		// permission
		// common app, can NOT do this
		// Settings.Global.putInt(
		// context.getContentResolver(),
		// Settings.Global.AIRPLANE_MODE_ON, value);
		String commond  = HigherAirplaneModePref1 + value + ";";
		//settings put global airplane_mode_on 1;am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true
		//settings put global airplane_mode_on 0;am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false
		if(value == 1)
			commond += HigherAirplaneModePref2 + "true";
		else
			commond += HigherAirplaneModePref2 + "false";
		Log.i(TAG, commond);
		String result = ShellUtil.runRootCmd(commond);
		Log.i(TAG, result);
	}

	@SuppressWarnings("deprecation")
	private static void setSettingsOnLow(Context context, int value)
	{
		Settings.System.putInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, value);
	}

	// settings put global airplane_mode_on 1(0)
	// am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true(false)

	public static String HigherAirplaneModePref1 = "settings put global airplane_mode_on ";
	public static String HigherAirplaneModePref2 = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state ";

}
package name.tanglei.www;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import de.ankri.views.Switch;

public class FlightModeSwitcher extends Activity implements OnTimeChangedListener
{
	private static final String TAG = FlightModeSwitcher.class.getName();
	
	
	//private RadioButton startBtn = null;
	//private RadioButton stopBtn = null;
	
	private de.ankri.views.Switch btn_switch;
	
	private TimePicker startTimePicker = null;
	private TimePicker stopTimePicker = null;
	
	/*
	private int startHour = 0;
	private int startMinute = 0;
	private int stopHour = 0;
	private int stopMinute = 0;
	*/
	private Config config = null;
	
	//private boolean currentState = false; //current on/off state
	private boolean currentAirplaneOn = false;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if(! Utils.getStoredPreferenceRooted(this) && android.os.Build.VERSION.SDK_INT >= 17)//no root
		{
			iCannotDoit();
		}
		
		setContentView(R.layout.main);
		startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
		stopTimePicker = (TimePicker) findViewById(R.id.endTimePicker);
		
		startTimePicker.setIs24HourView(true);
		stopTimePicker.setIs24HourView(true);
		
		config = Utils.getStoredPreference(this);
		
		Log.i(TAG, "load time data from preference:" + config);
		startTimePicker.setCurrentHour(config.getStartHour()); //this should put before the set on timechangelisten
		startTimePicker.setCurrentMinute(config.getStartMinute());//for it will invoke the timechange
		stopTimePicker.setCurrentHour(config.getStopHour());
		stopTimePicker.setCurrentMinute(config.getStopMinute());

//		startBtn = (RadioButton) findViewById(R.id.start);
//		startBtn.setOnClickListener(controlBtnClickListener);
//		stopBtn = (RadioButton) findViewById(R.id.stop);
//		stopBtn.setOnClickListener(controlBtnClickListener);
//		if(firsttime)
//		{
//			startBtn.setChecked(false);
//			stopBtn.setChecked(false);
//		}else
//		{
//			startBtn.setChecked(currentState);
//			stopBtn.setChecked(!currentState);
//		}
		
		btn_switch = (Switch) findViewById(R.id.btn_switch);
		btn_switch.setChecked(config.isCurrentState());
		btn_switch.setOnCheckedChangeListener(switchChangeListerner);
		
		startTimePicker.setOnTimeChangedListener(this);
		stopTimePicker.setOnTimeChangedListener(this);
		
		if(Utils.isFirstTime(this))
		{
			showAlertDialog(getString(R.string.firstTimeTipTitle), getString(R.string.firstTimeTipNormal));
			
			if(android.os.Build.VERSION.SDK_INT >= 17 && !Utils.isRooted())
			{
				Utils.setStoredPreferenceRooted(this, false);
				iCannotDoit();
			}
			
			Utils.setStoredPreferenceRooted(this, true);
		}
		
		startSchedule(false);
	}
	
	
	void test()
	{
		Log.i(TAG, "testxxxxx");
		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);  
		Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);  
		int requestCode = 0;  
		PendingIntent pendIntent = PendingIntent.getBroadcast(getApplicationContext(),  
		        requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
		long triggerAtTime = SystemClock.elapsedRealtime()  + 5 * 1000;  
		alarmMgr.set(AlarmManager.ELAPSED_REALTIME, triggerAtTime, pendIntent);  
	}

	public void onResume()
	{
		currentAirplaneOn = AirplaneModeService.isAirplaneModeOn(this);
		super.onResume();
	}
	
	//when activity is not visible
	public void onStop()
	{
		//startSchedule(false);, if user has cancel
		super.onStop();
	}
	
	
	private OnCheckedChangeListener switchChangeListerner = new OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked)
		{
			startSchedule(true);
		}
		
	};
	
	
	public void startSchedule(boolean showTip)
	{
		Log.d(TAG, " auto flight mode start ? " + btn_switch.isChecked());
		//currentState = btn_switch.isChecked();
		config.setCurrentState(btn_switch.isChecked());
		
		if(btn_switch.isChecked())
		{
			Utils.startSchedule(this, config, showTip);
		} else
		{
			Utils.stopSchedule(this, showTip);
		}
		
		setTimeToPreference();
	}
	
	public void setTimeToPreference()
	{
		Utils.updatePreference(this, config);
	}


	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
	{
		
		if(view == this.startTimePicker)
		{
			Log.i(TAG, "start time change to  " + (hourOfDay) + ":" + minute);
			//startHour = hourOfDay;
			//startMinute = minute;
			config.setStartHour(hourOfDay);
			config.setStartMinute(minute);
		}else
		{
			Log.i(TAG, "end time change to  " + (hourOfDay) + ":" + minute);
			//stopHour = hourOfDay;
			//stopMinute = minute;
			config.setStopHour(hourOfDay);
			config.setStopMinute(minute);
		}
		startSchedule(false);
	}
	
	private final int MENU_HELP = 1;
	private final int MENU_ABOUT = 2;
	private final int MENU_TOGGLE_RIGHTNOW = 3;
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, MENU_HELP, 1, getString(R.string.menuHelp));
		menu.add(Menu.NONE, MENU_ABOUT, 2, getString(R.string.menuAbout));
		menu.add(Menu.NONE, MENU_TOGGLE_RIGHTNOW, 3, getString(R.string.menuSetOffRightnow));
		return true;
	}
	
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		currentAirplaneOn = AirplaneModeService.isAirplaneModeOn(this);
		if(currentAirplaneOn)//menu.findItem(MENU_TOGGLE_RIGHTNOW).getTitle().equals(getString(R.string.menuSetOffRightnow)))
			menu.findItem(MENU_TOGGLE_RIGHTNOW).setTitle(R.string.menuSetOffRightnow);
		else
			menu.findItem(MENU_TOGGLE_RIGHTNOW).setTitle(R.string.menuSetOnRightnow);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case MENU_HELP:
				//showAlertDialog(getString(R.string.helpTitle), getString(R.string.helpContent));
				showHelpDialog();
				break;
			case MENU_ABOUT:
				//showAlertDialog(getString(R.string.aboutTitle), getString(R.string.aboutContent));
				showAboutDialog();
				break;
			case MENU_TOGGLE_RIGHTNOW:
				//AirplaneModeService.setAirplane(this, !currentAirplaneOn);
				//Utils.sendBroadcastNow(this, !currentAirplaneOn);
				Intent i = new Intent(this, ReceivedAction.class); 
			    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			    i.putExtra(ReceivedAction.ACTION_TAG, !currentAirplaneOn);
			    i.putExtra(ReceivedAction.USERACTION_TAG, true);
			    this.startActivity(i);
			    
				break;
		}
		return true;
	}
	
	public void showAlertDialog(String title, String content) {
        Utils.showAlertDialog(this, title, content, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
	
	public void iCannotDoit()
	{
		Log.i(TAG, "no root and sdk =" + android.os.Build.VERSION.SDK_INT);
		
		Utils.showAlertDialog(this, getString(R.string.noRootErrorTitle), getString(R.string.noRootError), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
    			FlightModeSwitcher.this.finish();
    			System.exit(0);
            }
        });
	}
	
	public void showHelpDialog()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(this.getString(R.string.helpTitle));
		alertDialogBuilder.setIcon(R.drawable.ic_action_help);
		TextView txtView = new TextView(this);
		txtView.setTextSize(15f);
		Spanned text = Html.fromHtml(this.getString(R.string.helpContentHhml));
		txtView.setText(text);
		txtView.setClickable(true);
		txtView.setMovementMethod(LinkMovementMethod.getInstance());
		alertDialogBuilder.setView(txtView);

		alertDialogBuilder.setPositiveButton(
				this.getString(R.string.helpOkButton), new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
	}
    
	public void showAboutDialog()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(this.getString(R.string.aboutTitle));
		alertDialogBuilder.setIcon(R.drawable.ic_action_about);
		TextView txtView = new TextView(this);
		txtView.setTextSize(15f);
		Spanned text = Html.fromHtml(this.getString(R.string.aboutContentHtml));
		txtView.setText(text);
		txtView.setClickable(true);
		txtView.setMovementMethod(LinkMovementMethod.getInstance());
		alertDialogBuilder.setView(txtView);

		alertDialogBuilder.setPositiveButton(
				this.getString(R.string.aboutOkButton), new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
	}
    
    
}
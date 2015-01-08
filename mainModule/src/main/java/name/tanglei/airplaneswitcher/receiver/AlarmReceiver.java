package name.tanglei.airplaneswitcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import name.tanglei.airplaneswitcher.dao.DatabaseHelper;
import name.tanglei.airplaneswitcher.entity.TaskEntity;
import name.tanglei.airplaneswitcher.ui.ReceivedAction;
import name.tanglei.airplaneswitcher.utils.AirplaneModeUtils;
import name.tanglei.airplaneswitcher.utils.TaskManagerUtils;

public class AlarmReceiver extends BroadcastReceiver
{
	private String TAG = AlarmReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d(TAG, "the alarm time is up");
		
		boolean isModeOn = intent.getBooleanExtra("isModeOn", false);
        int taskid = intent.getIntExtra("taskId", -1);
		Log.d(TAG, "isModeOn:" + isModeOn);
		boolean isEnabled = AirplaneModeUtils.isAirplaneModeOn(context);

        TaskEntity task = DatabaseHelper.query(context, taskid);
        if(task == null) return ;
        Log.i(TAG, "alarm recevied: " + task.toFormatString(context) + "Now timeInMillis : " + System.currentTimeMillis());

        if(task.isOnce())
            DatabaseHelper.disableTask(context, task);
        else
            TaskManagerUtils.addOrUpdateTask(context, task); //next
        //set next alarm
		if (isModeOn)
		{
			if (isEnabled == true)
				return;
			
			Intent i = new Intent(context, ReceivedAction.class);
		    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		    i.putExtra(ReceivedAction.ACTION_TAG, true);
		    context.startActivity(i);
		}
        else // modeOff, closed
		{
			if(isEnabled == false)
				return;
			Intent i = new Intent(context, ReceivedAction.class); 
		    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		    i.putExtra(ReceivedAction.ACTION_TAG, false);
		    context.startActivity(i);
		}
	}
	
	
}

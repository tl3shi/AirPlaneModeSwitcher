package name.tanglei.airplaneswitcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import name.tanglei.airplaneswitcher.dao.DatabaseHelper;
import name.tanglei.airplaneswitcher.entity.TaskEntity;
import name.tanglei.airplaneswitcher.utils.TaskManagerUtils;

public class BootBroadcastReceiver extends BroadcastReceiver
{
	public static final String TAG = BootBroadcastReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction().toString();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED))
		{
            List<TaskEntity> tasks = DatabaseHelper.queryAllTasks(context);
            int count = 0;
            for(TaskEntity alarm : tasks) {
                if (alarm.isActive()) {
                    TaskManagerUtils.addOrUpdateTask(context, alarm);
                    count ++;
                }
            }
            Log.i(TAG, "All active alarms are on schedule: " + count);
			return;
		}
	}
}

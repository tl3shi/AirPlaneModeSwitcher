package name.tanglei.airplaneswitcher.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import name.tanglei.airplaneswitcher.AlarmReceiver;
import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.entity.TaskEntity;

/**
 * Created by TangLei on 14/12/28.
 */
public class TaskManagerUtils
{
    private final static String TAG = TaskManagerUtils.class.getName();


    public static boolean addOrUpdateTask(Context context, TaskEntity task)
    {
        return addOrUpdateTask(context, task, false);
    }

    public static boolean addOrUpdateTask(Context context, TaskEntity task, boolean showTip)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("isModeOn", task.isModeOn());
        intent.putExtra("taskId", task.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(! task.isActive())
        {
            if(pendingIntent != null)
                alarmManager.cancel(pendingIntent);
            Log.i(TAG, "alarm canceled: " + task.toFormatString(context));
        }
        else //
        {
            long nextAlarm = task.getNextAlarmCalendar().getTimeInMillis();
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextAlarm,
                    pendingIntent);

            if(showTip) {
                String pre = context.getString(R.string.nextStartTipPref);
                Utils.showToast(context, pre + Utils.formatDateTime(context, nextAlarm), true);
            }
            Log.i(TAG, "alarm seted : " + task.toFormatString(context) + " timeInMillis : " + nextAlarm);
        }
        return true;
    }

}

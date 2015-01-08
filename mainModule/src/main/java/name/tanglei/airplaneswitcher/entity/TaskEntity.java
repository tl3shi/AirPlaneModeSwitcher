package name.tanglei.airplaneswitcher.entity;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Calendar;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.utils.Utils;

public class TaskEntity implements Serializable  
{
	private static final long serialVersionUID = -4211414366117177910L;

    private static final String TAG = TaskEntity.class.getName();

    @DatabaseField(generatedId=true)
    private int _id;//primary key in db;

    @DatabaseField
	private boolean modeOn;

    @DatabaseField
    private int repeat; //repeat
    //private Calendar time;
    //private TaskTime time;
    @DatabaseField
    private int hour;
    @DatabaseField
    private int minute;
    @DatabaseField
    private String title;
    @DatabaseField
    private boolean isActive;

    public TaskEntity() //used in ormlite
    {
        this._id = -1;
    }

    public TaskEntity(TaskEntity other)
    {
        this._id = other._id;
        this.modeOn = other.modeOn;
        this.repeat = other.repeat;
        this.hour = other.hour;
        this.minute = other.minute;
        this.title = other.title;
        this.isActive = other.isActive;
    }

    @Override
    public String toString() {
        return "TaskEntity{" +
                "modeOn=" + modeOn +
                ", repeat=" + repeat +
                ", time='" + toTimeString() + '\'' +
                ", title='" + title + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    public int getId()
    {
        return _id;
    }

    public int getHour()
    {
        return hour;
    }

    public void setHour(int hour)
    {
        this.hour = hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public void setMinute(int minute)
    {
        this.minute = minute;
    }

    public void setTime(int hour, int minute)
    {
        this.setHour(hour);
        this.setMinute(minute);
    }

    public TaskEntity(boolean modeOn, int repeat, int hour, int minute,
            String title, boolean isActive)
    {
        super();

        this.modeOn = modeOn;
        this.repeat = repeat;
        this.hour = hour;
        this.minute = minute;
        this.title = title;
        this.isActive = isActive;
    }

    
    public int getRepeat()
    {
        return repeat;
    }
    public void setRepeat(int repeat)
    {
        this.repeat = repeat;
    }

    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public boolean isActive()
    {
        return isActive;
    }
    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }

    public boolean isModeOn()
    {
        return modeOn;
    }
    
    
    public void setModeOn(boolean modeOn)
    {
        this.modeOn = modeOn;
    }
    
    public int getWeekdays()
    {
        return this.repeat;
    }


    public void toggleWeekday(int day)
    {
        if (isSet(day))//(((this.repeat >> (day-1)) & 0x1) == 0x1)
            this.repeat &= 0xFFFFFFFF ^ (1 << (day-1)); //1-->0
        else
            this.repeat |= 1 << (day-1); //0 --> 1
    }

    public static int getRepeatWorkday()
    {
        return 0x0000001F;//0000,0000,0001,1111
    }

    public static int getRepeatEveryday()
    {
        return 0x0000007F;// 0000, 0000, 0111,1111
    }

    public static int getRepeatOnce()
    {
        return 0;
    }

    public String getRepeatStr(Context context)
    {
        String repeatDesc = "";

        if (this.isEveryday())
            repeatDesc = context.getString(R.string.txtRepeatEveryday);
        else if(this.isWorkday())
            repeatDesc = context.getString(R.string.txtRepeatWeekday);
        else if(this.isOnce())
            repeatDesc = context.getString(R.string.txtRepeatNone);
        else
        {
            String [] constDesc = context.getString(R.string.txtRepeatDesc).split(",");
            repeatDesc = context.getString(R.string.txtRepeatDescPre);
            for(int i = 0; i < 7; i++)
            {
                if(((repeat >> i) & 0x1) == 0x1)
                    repeatDesc += constDesc[i] + ",";
            }
            repeatDesc = repeatDesc.substring(0, repeatDesc.length()-1);
        }
        return repeatDesc;
    }

    public boolean isSet(int weekday)
    {
        return (((this.repeat >> (weekday-1)) & 0x1) == 0x1);
    }

    public boolean isRepeatable()
    {
        return !this.isOnce();
    }

    public boolean isOnce()
    {
        return this.repeat == 0;
    }

    public boolean isWorkday(){return this.repeat == TaskEntity.getRepeatWorkday();}

    public boolean isEveryday(){return this.repeat == TaskEntity.getRepeatEveryday();}

    public String toFormatString(Context context)
    {
        return "TaskEntity{" +
                "modeOn=" + modeOn +
                ", repeat=" + getRepeatStr(context) +
                ", time='" + getTimeStr(context) + '\'' +
                ", title='" + title + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    public Calendar getCalendarNow()
    {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        return time;
    }


    public String getTimeStr(Context context)
    {
        return Utils.formatTime(context, this.getCalendarNow());
    }

    public String toTimeString()
    {
        return hour + " : " + minute;
    }

    private int getNextAlarm(Calendar c)
    {
        if (this.isOnce())
            return -1;

        //start as SUNDAY:1, Mon 2  -->
        //Monday 1, sunday 7
        int today = c.get(Calendar.DAY_OF_WEEK) - 1 ;
        if (today == 0) today = 7;

        int day = 0;
        int dayCount = 0;
        for (; dayCount < 7; dayCount++) {
            day = (today + dayCount) % 7;
            if (isSet(day)) {
                break;
            }
        }
        return dayCount;
    }

    /*public String getNextAlarmDateTime(Context context)
    {
        return Utils.formatDateTime(context, this.getNextAlarmCalendar());
    }*/

    public Calendar getNextAlarmCalendar()
    {
        // start with now
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        int nowHour = c.get(Calendar.HOUR_OF_DAY);
        int nowMinute = c.get(Calendar.MINUTE);

        // if alarm is behind current time, advance one day
        if (hour < nowHour  ||
                hour == nowHour && minute <= nowMinute) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        int addDays = getNextAlarm(c);
        if (addDays > 0) {
            c.add(Calendar.DAY_OF_WEEK, addDays);
            Log.i(TAG, "add next day: " + addDays);
        }
        return c;
    }
}

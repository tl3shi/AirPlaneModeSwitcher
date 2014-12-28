package name.tanglei.airplaneswitcher.entity;

import java.io.Serializable;
import java.util.Calendar;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.Utils;
import android.content.Context;
import com.j256.ormlite.field.DatabaseField;

public class TaskEntity implements Serializable  
{
	private static final long serialVersionUID = -4211414366117177910L;

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
        if(((this.repeat >> (day-1)) & 0x1) == 0x1)
            this.repeat &= 0xFFFFFFFF ^ (1 << (day-1)); //1-->0
        else
            this.repeat |= 1 << (day-1); //0 --> 1
    }



    public String getRepeatStr(Context context)
    {
        String repeatDesc = "";
        // 0000, 0000, 0111,1111
        if (repeat == 0x0000007F)
            repeatDesc = context.getString(R.string.txtRepeatEveryday);
        else if(repeat == 0x0000001F) //0000,0000,0001,1111
            repeatDesc = context.getString(R.string.txtRepeatWeekday);
        else if(repeat == 0)
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
        return hour + ":" + minute;
    }

}

package name.tanglei.airplaneswitcher.entity;

import java.io.Serializable;
import java.util.Calendar;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.Utils;
import android.content.Context;

public class TaskEntity implements Serializable  
{
	private static final long serialVersionUID = -4211414366117177910L;

	private boolean modeOn;
    private int repeat; //repeat
    //private String time;
    private Calendar time;
    private String title;
    private boolean isActive;
    
    @Override
    public String toString() {
        return "TaskEntity{" +
                "modeOn=" + modeOn +
                ", repeat=" + repeat +
                ", time='" + time + '\'' +
                ", title='" + title + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    public TaskEntity(boolean modeOn, int repeat, Calendar time,
            String title, boolean isActive)
    {
        super();
        this.setModeOn(modeOn);
        this.repeat = repeat;
        this.time = time;
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
    public Calendar getTime()
    {
        return time;
    }
    public void setTime(Calendar time)
    {
        this.time = time;
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
        if((this.repeat >> (day-1)) == 0x1)
            this.repeat &= (0xFFFFFFFF ^ 1 << (day-1));
        else
            this.repeat |= 1 << (day-1);
    }

    public int getHour()
    {
        return time.get(Calendar.HOUR_OF_DAY);
    }

    public  int getMinute()
    {
        return time.get(Calendar.MINUTE);
    }

    public String getTimeStr(Context context)
    {
        return Utils.formatTime(context, this.time);
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


}

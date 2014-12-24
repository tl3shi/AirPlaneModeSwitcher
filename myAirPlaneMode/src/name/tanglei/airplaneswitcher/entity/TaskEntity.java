package name.tanglei.airplaneswitcher.entity;
 
public class TaskEntity
{
    private boolean modeOn;
    private int repeat; //repeat
    private String time;
    private String title;
    private boolean isActive;
    
    public TaskEntity(boolean modeOn, int repeat, String time,
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
    public String getTime()
    {
        return time;
    }
    public void setTime(String time)
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
}

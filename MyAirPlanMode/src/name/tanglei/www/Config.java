package name.tanglei.www;

public class Config
{
	private int startHour;
	private int stopHour;
	private int startMinute;
	private int stopMinute;
	private boolean currentState;
	
	public int getStartHour()
	{
		return startHour;
	}

	public int getStopHour()
	{
		return stopHour;
	}

	public void setStopHour(int stopHour)
	{
		this.stopHour = stopHour;
	}

	public int getStopMinute()
	{
		return stopMinute;
	}

	public void setStopMinute(int stopMinute)
	{
		this.stopMinute = stopMinute;
	}

	public void setStartHour(int startHour)
	{
		this.startHour = startHour;
	}

	public int getStartMinute()
	{
		return startMinute;
	}

	public void setStartMinute(int startMinute)
	{
		this.startMinute = startMinute;
	}

	public boolean isCurrentState()
	{
		return currentState;
	}

	@Override
	public String toString()
	{
		return "Config [startHour=" + startHour + ", stopHour=" + stopHour
				+ ", startMinute=" + startMinute + ", stopMinute=" + stopMinute
				+ ", currentState=" + currentState + "]";
	}

	public void setCurrentState(boolean currentState)
	{
		this.currentState = currentState;
	}

	public Config(int startHour, int startMinute, int endHour, int endMinute,
			boolean currentState)
	{
		super();
		this.startHour = startHour;
		this.stopHour = endHour;
		this.startMinute = startMinute;
		this.stopMinute = endMinute;
		this.currentState = currentState;
	}

}

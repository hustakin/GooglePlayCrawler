package src;

public class ThreadMessage {

	protected boolean stopOrder;
	protected int threadNumber;
	
	public ThreadMessage()
	{
		threadNumber = 0;
		stopOrder = false;
	}
	
	public void setStopOrder(boolean shouldStop)
	{
		stopOrder = shouldStop;
	}
	
	public boolean getStopOrder()
	{
		return stopOrder;
	}
	
	public void setThreadNumber(int num)
	{
		threadNumber = num;
	}
	
	public void addThread()
	{
		++threadNumber;
	}
	
	public void decThread()
	{
		--threadNumber;
	}
	
	public int getThreadNumber()
	{
		return threadNumber; 
	}
}


import java.net.*;
import src.*;

public class MyCrawler extends Thread {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			MyCrawler myCrawler = new MyCrawler();
	//		myCrawler.PushPageQueue(new URL("http://v.blog.sohu.com/u/vw/2662170"));
			myCrawler.PushPageQueue(new URL("http://v.blog.sohu.com/"));
			myCrawler.PushPageQueue(new URL("http://video.sina.com.cn/"));
	//		myCrawler.PushPageQueue(new URL("http://www.tudou.com"));
			myCrawler.PushPageQueue(new URL("http://www.youku.com"));
    //		myCrawler.PushPageQueue(new URL("http://www.ku6.com/"));
    //		myCrawler.PushPageQueue(new URL("http://you.video.sina.com.cn/pg/topicdetail/topicPlay.php?tid=2803872&uid=1214341274&t=1#21224276"));
    //		myCrawler.PushPageQueue(new URL("http://you.video.sina.com.cn/pg/topicdetail/topicPlay.php?tid=2655016&uid=1170557422&t=2#20228511"));
   	//		myCrawler.PushPageQueue(new URL("http://you.video.sina.com.cn/b/21213320-1214341274.html"));


		
			
			//debug
			System.out.println("init size of m_page_que is " + myCrawler.m_page_que.size());
			
			myCrawler.setMaxURLProcThreads(10);
			myCrawler.setMaxDwonloadThreads(5);
			myCrawler.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected URLQueue m_page_que;
	protected URLQueue m_file_que;
	protected URLProcessManager m_proc_mgr;
	protected DownloadManager m_dnld_mgr;
	protected ThreadMessage m_proc_msg;
	protected ThreadMessage m_dnld_msg;
	
	/**
	���캯��
	*/
	public MyCrawler()
	{
		//������ʼ��
		m_page_que = new URLQueue("pageUrlQueue.log");
		m_file_que = new URLQueue("fileUrlQueue.log");
		m_proc_msg = new ThreadMessage();
		m_dnld_msg = new ThreadMessage();
		m_proc_mgr = new URLProcessManager(m_page_que,m_file_que,m_proc_msg);
		m_dnld_mgr = new DownloadManager(m_file_que,m_dnld_msg);
	}
	
	/**
	����ҳURL���������Ԫ��
	*/
	public void PushPageQueue(URL url)
	{
		m_page_que.put(url);
	}

	/**
	���������ַ�����߳���
	*/
	public void setMaxURLProcThreads(int maxThreads)
	{
		m_proc_mgr.setMaxThreads(maxThreads);
	}
	
	/**
	��������ļ������߳���
	*/
	public void setMaxDwonloadThreads(int maxThreads)
	{
		m_dnld_mgr.setMaxThreads(maxThreads);
	}
	
	/**
	ִ�н���
	*/
	public void run()
	{	
		//�������Ƚ���
		m_proc_mgr.start();
		m_dnld_mgr.start();
		
		//�����Զ���ֹ
/*		try {
			sleep(1000*60*5);	//����5���ӣ��Զ�ֹͣ
			terminate();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//debug
		System.out.println("all processes will terminate a short time later.");
*/	}
	
	/**
	��ֹ�������ʹ�������������н�����ֹ
	 */
	public void terminate()
	{
		m_proc_msg.setStopOrder(true);
		m_dnld_msg.setStopOrder(true);
	}
}

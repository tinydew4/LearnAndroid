package kr.co.wikibook.bakery_service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import kr.co.wikibook.bakery_service_interfaces.IBakeryService;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

public class BakeryService extends Service implements Runnable {
	
	private Queue<String> mBreads = new LinkedList<String>();
	private boolean runThread = false;
	private ArrayList<ComponentName> acn = new ArrayList<ComponentName>();
	
	private IBakeryService.Stub mBinder = new IBakeryService.Stub() {
	
		public String getBread(android.content.ComponentName cn)
			throws android.os.RemoteException {
			
			if (acn.indexOf(cn) == -1)
				return null;
			
			String bread = mBreads.poll();
			if (bread == null)
				bread = "";
			return bread;
		}
		
		public boolean enterBakery(android.content.ComponentName cn)
			throws android.os.RemoteException {
			
			if (acn.indexOf(cn) != -1)
				return true;
			
			if (acn.size() < 3)
			{
				acn.add(cn);
				return true;
			}
			return false;
		}
		
		public boolean leaveBakery(android.content.ComponentName cn)
			throws android.os.RemoteException {
			
			if (acn.indexOf(cn) == -1)
				return false;
			
			acn.remove(cn);
			return true;
		}
		
		public int getCustomerCount() {
			return acn.size();
		}
	};
	
	@Override
	public void onCreate() {
		runThread = true;
		
		for (int i = 3; i < 3; i++)
			mBreads.offer(getBread());
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void onDestroy() {
		runThread = false;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public void run() {
		while (runThread) {
			synchronized (BakeryService.this) {
				if (mBreads.size() < 3)
					mBreads.offer(getBread());
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getBread() {
		String bread[] = {
			"국화빵",
			"식빵",
			"곰보빵",
		};
		
		Random random = new Random();
		int i = random.nextInt(2);
		if (0 > i || i >= 3)
			i = 0;
		return bread[i];
	}
}

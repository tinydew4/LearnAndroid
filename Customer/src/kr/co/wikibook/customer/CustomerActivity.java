package kr.co.wikibook.customer;

import kr.co.wikibook.bakery_service_interfaces.IBakeryService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CustomerActivity extends Activity {

	private IBakeryService mBinder;
	private TextView message;

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBinder = IBakeryService.Stub.asInterface(service);
		}
		@Override
		public void onServiceDisconnected(ComponentName className) {
			mBinder = null;
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Intent intent = new Intent(IBakeryService.class.getName());
		startService(intent);
		bindService(intent, mConnection, BIND_AUTO_CREATE);

		message = (TextView)findViewById(R.id.message);

		Button enter = (Button)findViewById(R.id.enter);
		enter.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							if (mBinder.enterBakery(getComponentName())) {
								message.setText("빵집에 들어갔습니다.");
							} else {
								message.setText("빵집이 손님으로 가득차 들어갈 수 없습니다.");
							}
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
				);

		Button get_bread = (Button)findViewById(R.id.get_bread);
		get_bread.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							String bread = mBinder.getBread(getComponentName());
							if (bread != null) {
								if (bread.equals("")) {
									message.setText("빵이 모두 팔렸습니다.");
								} else {
									message.setText(bread + "을(를) 구매하였습니다.");
								}
							} else {
								message.setText("빵을 사려면 먼저 빵집에 들어가야 합니다.");
							}
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
				);

		Button leave = (Button)findViewById(R.id.leave);
		leave.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							if (mBinder.leaveBakery(getComponentName())) {
								message.setText("빵집에서 나왔습니다.");
							}
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
				);

		Button customer_count = (Button)findViewById(R.id.customer_count);
		customer_count.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							message.setText("빵집에 총 "  + mBinder.getCustomerCount() + "명의 손님이 있습니다.");
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
				);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mConnection != null) {
			unbindService(mConnection);
			mConnection = null;
		}
	}
}
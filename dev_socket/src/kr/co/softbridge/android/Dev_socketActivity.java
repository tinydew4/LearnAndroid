package kr.co.softbridge.android;

import java.net.SocketException;
import java.net.UnknownHostException;

import kr.co.softbridge.android.IInterfaces.IExceptionReceiver;
import kr.co.softbridge.android.IInterfaces.ISocketReceiver;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class Dev_socketActivity extends Activity {

	private UDPClientSocket m_Socket = null;
	private Handler mHandler = new MainHandler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			m_Socket = new UDPClientSocket(mHandler, "210.220.167.80", 54321);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		Button Button1 = (Button)findViewById(R.id.Button1);
		if (Button1 != null)
			Button1.setOnClickListener(Button1Click);
	}

	View.OnClickListener Button1Click = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			m_Socket.send("Example", (IExceptionReceiver)mHandler);
		}
	};

	private class MainHandler extends Handler implements ISocketReceiver, IExceptionReceiver {

		@Override
		public void DoException(Exception e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void DoReceivePacket(byte[] buffer) {
			Button Button1 = (Button)findViewById(R.id.Button1);
			if (Button1 != null)
				Button1.setText(new String(buffer));
		}
	}
}
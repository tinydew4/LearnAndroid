package kr.co.softbridge.NDKEcho;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

interface NDKCallbackFunction {
	void function(Byte[] ABuffer, int AReceiveLength);
}

public class NDKEchoShell extends Activity {

	private EditText edtMessage;
	private ListView lvChat;
	private ArrayAdapter<String> mAdapter;

	private final int ADD_MESSAGE = 0;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ADD_MESSAGE:
					mAdapter.add((String)msg.obj);
					break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.shell);
		
	    edtMessage = (EditText)findViewById(R.id.edtMessage);
	      
	    Button btnSend = (Button)findViewById(R.id.btnSend);
	    btnSend.setOnClickListener(btnSendClick);

	    lvChat = (ListView)findViewById(R.id.message_timeline);
    	ArrayList<String> arrayList = new ArrayList<String>();
    	mAdapter = new ArrayAdapter<String>(NDKEchoShell.this, android.R.layout.simple_list_item_1, arrayList);
	    lvChat.setAdapter(mAdapter);

		Intent intent = getIntent();
		String sHost = intent.getStringExtra("Host");
		int iPort = intent.getIntExtra("Port", R.string.defPort);
		int iType = intent.getIntExtra("Type", 0);
		
		if (iType == 0) {
			String sFmtTCPConnect = getResources().getString(R.string.fmtTCPConnect);
			addMessage(String.format(sFmtTCPConnect, sHost, iPort));

			NDKSocket.TCPConnect(sHost, iPort);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		NDKSocket.TCPDisconnect();
	}

	View.OnClickListener btnSendClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			NDKSocket.TCPSendString(edtMessage.getText().toString());
		}
	};
	
	private void addMessage(String data) {
		if (data != null & data.length() > 0) {
			Message message = handler.obtainMessage();
			message.what = ADD_MESSAGE;
			message.arg1 = 0;
			message.arg2 = 0;
			message.obj = data;
			handler.sendMessage(message);
		}
	}

	private void TCPOnConnect() {
		addMessage("Connected");
	}
	
	private void TCPOnDisconnect() {
		addMessage("Disconnected");
	}
	
	private NDKCallbackFunction TCPReceivePacket = new NDKCallbackFunction() { 
		@Override
		public void function(Byte[] ABuffer, int AReceiveLength) {
			
		}
	};
}

package kr.co.wikibook.socket_messenger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Chat extends Activity {

	private final int ADD_MESSAGE = 0;
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ADD_MESSAGE:
					mAdapter.add((String)msg.obj);
					break;
			}
		}
	};

	private int PORT = 8888;
	private String REMOTE_SERVER_IP = "";
	private ListView listView;
	private EditText message_box;
	private ArrayAdapter<String> mAdapter;
	
	private final String head_my_message = "나: ";
	private final String head_your_message = "너: ";
	private boolean runningMessageReceiver = false;
	private boolean runningMessageSender = false;
	
	private ServerSocket mServerSocket = null;
	private Socket mClientSocket = null;
	
	private int mode;
	
	Queue<String> sendMessageQueue = new LinkedList<String>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.chat_screen);

	    // TODO Auto-generated method stub
	    ArrayList<String> arrayList = new ArrayList<String>();
	    
	    listView = (ListView)findViewById(R.id.message_timeline);
	    mAdapter = new ArrayAdapter<String>(Chat.this, android.R.layout.simple_list_item_1, arrayList);
	    listView.setAdapter(mAdapter);
	    
	    message_box = (EditText)findViewById(R.id.message_box);
	    
	    Button send_message = (Button)findViewById(R.id.send_message);
	    send_message.setOnClickListener(new OnClickListener() {
	    		public void onClick(View v) {
	    			String message = message_box.getText().toString();
	    			if (message.length() > 0) {
	    				sendMessage(message);
	    				message_box.setText("");
	    			}
	    		}
	    	}
	    );
	    
 	    Intent it = getIntent();
	    Bundle extra = it.getExtras();
	    
	    mode = extra.getInt("mode");
	    
	    switch (mode){
	    	case SocketMessengerActivity.SERVER_MODE:
	    		runAsServer();
	    		break;
	    	case SocketMessengerActivity.CLIENT_MODE:
	    		REMOTE_SERVER_IP = extra.getString("ip");
	    		PORT = Integer.parseInt(extra.getString("port"));
	    		runAsClient();
	    		break;
	    }
	    
	    runningMessageReceiver = true;
	    MessageReceiver mr = new MessageReceiver();
	    Thread threadReceiver = new Thread(mr);
	    threadReceiver.start();
	    
	    runningMessageSender = true;
	    MessageSender ms = new MessageSender();
	    Thread threadSender = new Thread(ms);
	    threadSender.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (runningMessageReceiver)
			runningMessageReceiver = false;
		
		if (runningMessageSender)
			runningMessageSender = false;
	}
	
	private void runAsServer() {
		try {
			if (mServerSocket == null) {
				mServerSocket = new ServerSocket(PORT);
				mAdapter.add("서버로 실행되었습니다. 서버의 IP는 " + getLocalIpAddress() + " 입니다.");
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	private void runAsClient() {
		try {
			if (mClientSocket == null) {
				mClientSocket = new Socket(REMOTE_SERVER_IP, PORT);
				mAdapter.add("서버에 접속되었습니다. 서버의 IP는 " + REMOTE_SERVER_IP + ", 포트는 " + PORT + " 입니다.");
			}
		} catch (IOException ie) {
				ie.printStackTrace();
		}
	}
	
	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (! inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private void sendMessage(String message) {
		sendMessageQueue.offer(message);
	}
	
	private void addMessageToListView(String data) {
		if (data != null & data.length() > 0) {
			Message message = handler.obtainMessage();
			message.what = ADD_MESSAGE;
			message.arg1 = 0;
			message.arg2 = 0;
			message.obj = data;
			handler.sendMessage(message);
		}
	}
	
	class MessageReceiver extends Thread {
		
		public void run() {
			
			BufferedReader br = null;
			InputStream is = null;
			InputStreamReader isr = null;
			
			while (runningMessageReceiver) {
				try {
					if (mClientSocket == null && mode == SocketMessengerActivity.SERVER_MODE) {
						mClientSocket = mServerSocket.accept();
						addMessageToListView(mClientSocket.getInetAddress().getHostAddress() + "와(과) 연결되었습니다.");
					}
					
					if (mClientSocket == null)
						continue;
					
					is = mClientSocket.getInputStream();
					isr = new InputStreamReader(is);
					
					if (br == null)
						br = new BufferedReader(isr);
					
					String data = br.readLine();
					
					if (data == null) {
						br.close();
						br = null;
						addMessageToListView("상대와의 접속이 끊겼습니다.");
						
						if (mode == SocketMessengerActivity.CLIENT_MODE) {
							finish();
						}
					} else {
						addMessageToListView(head_your_message + data);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					
				}
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
				
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				isr = null;
			}
			
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				is = null;
			}
		}
	}
		
	class MessageSender extends Thread {
		
		public void run() {
			String message = null;
			PrintWriter out = null;
			BufferedWriter bw = null;
			OutputStream os = null;
			OutputStreamWriter osw = null;
			
			while (runningMessageSender) {
				if (mClientSocket != null && out == null) {
					try {
						os = mClientSocket.getOutputStream();
						osw = new OutputStreamWriter(os);
						bw = new BufferedWriter(osw);
						out = new PrintWriter(bw, true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (mClientSocket == null && out != null) {
					out.close();
					out = null;
				}
			
				if (mClientSocket != null && out != null) {
					message = sendMessageQueue.poll();
					if (message != null) {
						out.println(message);
						out.flush();
						addMessageToListView(head_my_message + message);
					}
				}
			
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
  			if (mode == SocketMessengerActivity.CLIENT_MODE) {
 				if (mClientSocket != null) {
					try {
						mClientSocket.shutdownOutput();
						mClientSocket.close();
						mClientSocket = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else if (mode == SocketMessengerActivity.SERVER_MODE) {
				if (mServerSocket != null) {
					try {
						if (mClientSocket != null)
							mClientSocket.shutdownInput();
						mServerSocket.close();
						mServerSocket = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			if (out != null) {
				out.close();
				out = null;
			}
			
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bw = null;
			}
			
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				osw = null;
			}
			
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				os = null;
			}
		}
	}
}

package kr.co.softbridge.NDKEcho;


public class NDKSocket {
	
	static {
		try {
			System.loadLibrary("ndk-socket");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static public native int TCPConnect(String AHost, int APort);
	static public native int TCPDisconnect();

	static public native int TCPSendString(String AMessage);
}

package kr.co.softbridge.android;

class IInterfaces {
	public interface IExceptionReceiver {
		public void DoException(Exception e);
	}

	public interface ISocketReceiver {
		public void DoReceivePacket(byte[] buffer);
	}
}

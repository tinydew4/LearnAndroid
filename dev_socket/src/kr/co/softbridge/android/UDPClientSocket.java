package kr.co.softbridge.android;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import kr.co.softbridge.android.IInterfaces.IExceptionReceiver;
import kr.co.softbridge.android.IInterfaces.ISocketReceiver;
import android.os.Handler;

public class UDPClientSocket {

	private InetAddress m_Addr;
	private int m_Port = 0;

	private Thread m_SendThread = null;
	private Thread m_RecvThread = null;
	private Thread m_CastThread = null;

	private class SendData {
		public byte[] data;
		public IExceptionReceiver receiver;
		public SendData(byte[] data, IExceptionReceiver receiver) {
			this.data = data;
			this.receiver = receiver;
		}
	}

	private Queue<SendData> m_SendQueue = new LinkedList<SendData>();
	private Queue<byte[]> m_RecvQueue = new LinkedList<byte[]>();

	private DatagramSocket m_Socket = null;

	private boolean m_Active = false;

	private ArrayList<Handler> m_Receivers = new ArrayList<Handler>();

	public UDPClientSocket() throws SocketException {
		init();
		start();
	}

	public UDPClientSocket(String host, int port) throws SocketException, UnknownHostException {
		this();
		setHost(host);
		setPort(port);
	}

	public UDPClientSocket(Handler receiver) throws SocketException, UnknownHostException {
		this();
		registReceiver(receiver);
	}

	public UDPClientSocket(Handler receiver, String host, int port) throws SocketException, UnknownHostException {
		this();
		setHost(host);
		setPort(port);
		registReceiver(receiver);
	}

	public void init() throws SocketException {
		if (m_Socket == null)
			m_Socket = new DatagramSocket();

		if (m_SendThread == null) {
			(m_SendThread = new Thread(new DatagramSocketSender())).setDaemon(true);
		}
		if (m_RecvThread == null) {
			DatagramSocketReceiver sendRunnable = new DatagramSocketReceiver();
			sendRunnable.setBufferSize(m_Socket.getReceiveBufferSize());
			(m_RecvThread = new Thread(new DatagramSocketReceiver())).setDaemon(true);
		}
		if (m_CastThread == null)
			(m_CastThread = new Thread(new UDPCastRunnable())).setDaemon(true);
	}

	public void start() {
		m_Active = true;
		m_RecvThread.start();
		m_CastThread.start();
	}

	public void stop() {
		m_Active = false;
	}

	public String getHost() {
		return m_Addr.getHostAddress();
	}

	public void setHost(String AValue) throws UnknownHostException {
		m_Addr = InetAddress.getByName(AValue);
	}

	public int getPort() {
		return m_Port;
	}

	public void setPort(int AValue) {
		m_Port = AValue;
	}

	public void send(byte[] buffer, IExceptionReceiver receiver) {
		synchronized (m_SendQueue) {
			m_SendQueue.add(new SendData(buffer, receiver));
		}
	}

	public void send(String AValue, IExceptionReceiver receiver) {
		send(AValue.getBytes(), receiver);
	}

	public void registReceiver(Handler object) {
		if (object instanceof ISocketReceiver) {
			synchronized (m_Receivers) {
				if (m_Receivers.contains(object) == false)
					m_Receivers.add(object);
			}
		}
	}

	public void unregistReceiver(Handler object) {
		synchronized (m_Receivers) {
			if (m_Receivers.contains(object) == true)
				m_Receivers.remove(object);
		}
	}

	private class DatagramSocketSender implements Runnable {

		@Override
		public void run() {
			SendData sendData;
			while (m_Active) {
				if (m_SendQueue != null) {
					sendData = null;
					synchronized (m_SendQueue) {
						if (m_SendQueue.isEmpty() == false) {
							sendData = m_SendQueue.poll();
						}
					}
					if (sendData != null) {
						DatagramPacket sendPacket = new DatagramPacket(sendData.data, sendData.data.length, m_Addr, m_Port);
						try {
							synchronized (m_Socket) {
								m_Socket.send(sendPacket);
							}
						} catch (IOException E) {
							sendData.receiver.DoException(E);
						}
					}
				}
			}
		}
	}

	private class DatagramSocketReceiver implements Runnable {

		private byte[] m_SocketBuffer = null;
		private DatagramPacket m_SocketPacket = null;

		@Override
		public void run() {
			int iRecvSize;
			byte[] queueData;
			while (m_Active) {
				try {
					m_Socket.receive(m_SocketPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
				iRecvSize = m_SocketPacket.getLength();
				if (iRecvSize > 0) {
					queueData = new byte[iRecvSize];
					System.arraycopy(m_SocketPacket.getData(), 0, queueData, 0, iRecvSize);
					synchronized (m_RecvQueue) {
						m_RecvQueue.add(queueData);
					}
				}
			}
		}

		public void setBufferSize(int AValue) {
			if (AValue > 0) {
				if ((m_SocketBuffer == null) || (m_SocketBuffer.length < AValue)) {
					m_SocketBuffer = new byte[AValue];
					m_SocketPacket = new DatagramPacket(m_SocketBuffer, m_SocketBuffer.length);
				}
			}
		}
	}

	private class UDPCastRunnable implements Runnable {

		private byte[] m_Check = new byte[3];
		private byte[] m_Buffer = null;
		private int m_BeginOffset = 0;
		private int m_EndIndex = 0;
		private int m_EndOffset = 0;

		@Override
		public void run() {
			while (m_Active) {
				if (m_RecvQueue != null) {
					byte[] data = null;
					synchronized (m_RecvQueue) {
						if (m_RecvQueue.isEmpty() == false) {
							LinkedList<byte[]> list = (LinkedList<byte[]>) m_RecvQueue;
							m_Buffer = list.get(m_EndIndex);

							m_Check[0] = m_Check[1];
							m_Check[1] = m_Check[2];
							m_Check[2] = m_Buffer[m_EndOffset++];

							if ((m_Check[0] == 0x0F) && (m_Check[1] == 0x0D)
									&& (m_Check[2] == 0x0A)) {
								int iSize = m_EndOffset;
								for (int a = m_EndIndex - 1, i = 0; a-- > 0; i++) {
									if ((i > 0) || (m_BeginOffset <= 0))
										iSize += list.get(i).length;
									else
										iSize += (list.get(i).length - m_BeginOffset);
								}

								data = new byte[iSize];
								int iOffset = 0;
								for (int a = m_EndIndex - 1, i = 0; a-- > 0; i++) {
									byte[] buf = m_RecvQueue.poll();
									System.arraycopy(buf, (i > 0? 0: m_BeginOffset), data, iOffset, buf.length);
									iOffset += buf.length;
								}
								byte[] buf = list.get(0);
								System.arraycopy(buf, 0, data, iOffset, m_EndOffset);

								m_EndIndex = 0;
								m_BeginOffset = m_EndOffset + 1;
							}
						}
					}
					if (data != null) {
						synchronized (m_Receivers) {
							for (Handler handler : m_Receivers)
								handler.post(new UDPCaster((ISocketReceiver) handler, data));
						}
					}
				}
			}
		}

		private class UDPCaster implements Runnable {

			private ISocketReceiver m_Receiver = null;
			private byte[] m_Data;

			public UDPCaster(ISocketReceiver receiver, byte[] data) {
				m_Receiver = receiver;
			}

			@Override
			public void run() {
				if ((m_Data != null) && (m_Receiver != null))
					m_Receiver.DoReceivePacket(m_Data);
			}
		}
	}
}

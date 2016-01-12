package com.tankwar;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * �ͻ���������
 * @author WanHung
 *
 */
public class NetClient {
	//���ܽ�UDP�˿ڶ�����һ̨���ϻس�ͻ��
	private int udpPort;
	String IP;//��������IP
	TankClient tc;
	DatagramSocket ds=null;//���ڷ�����
	
	//������������һ����̵߳��õĻ���UDP_PORT_START��û��++���Ϳ��ܱ���ֵ����һ���̣߳�����߳�����
	public NetClient(TankClient tc){
		//udpPort=UDP_PORT_START++;//û����һ�ζ˿ںžͼ�һ����
		this.tc=tc;
	}
 
	public void connect(String IP,int port){
		this.IP=IP;
		try {
			ds=new DatagramSocket(udpPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		Socket s=null;
		DataOutputStream dos=null;
		DataInputStream dis=null;
	try {
		s=new Socket(IP,port);
		//��udp�˿ں�д�������
		dos=new DataOutputStream(s.getOutputStream());
		dos.writeInt(udpPort);
		//��ȡ����˴�������ID��
		dis=new DataInputStream(s.getInputStream());
		int id=dis.readInt();
		tc.myTank.id=id;//����������������ID�����¸���̹����ȥ
		//����id����ż���ж�̹�˵����ͣ�
		if(id%2==0){
			tc.myTank.setGood(false);//ż����һ��
		}else{
			tc.myTank.setGood(true);
		}
		System.out.println("���ӷ������ɹ���");
	}  catch (Exception e) {
		e.printStackTrace();
	}finally{
		if(dos!=null){
			try {
				dos.close();
				dos=null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		if(s!=null){
			try {
				s.close();
				s=null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		}
	}
	//ʵ�����ҷ�̹�˵���Ϣ
	TankNewMsg msg=new TankNewMsg(tc.myTank);
	//msg.send(ds,TankServer.IP,TankServer.UDP_PORT);//����TankNewMsg�����ݳ�ȥ�ķ���
	 send(msg);
	new Thread(new UDPRevThread()).start();
}
	
	public void send(Msg msg) {
		msg.send(ds, IP, TankServer.UDP_PORT);
	}
	//���շ���������������Ϣ����
	private class UDPRevThread implements Runnable{
		byte[] buf=new byte[1024];
		@Override
		public void run() {
			while(ds!=null){
				DatagramPacket dp=new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
					System.out.println("�ӷ��������յ�һ������Ϣ�����н���~");
					parse(dp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		private void parse(DatagramPacket dp) {
			ByteArrayInputStream bais=new ByteArrayInputStream(buf,0,dp.getLength());
			DataInputStream dis=new DataInputStream(bais);
			int msgType = 0;
			try {
				msgType = dis.readInt();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Msg msg=null;//������Ϣ������ȷ�����պͽ����ķ�������̬������
			switch (msgType) {
			case Msg.TANK_NEW_MSG:
				msg=new TankNewMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Msg.TANK_MOVE_MSG:
				msg=new TankMoveMsg(NetClient.this.tc);
				//����Ϣ���Լ�ȥ����
				msg.parse(dis);
				break;
			case Msg.MISSILE_NEW_MSG:
				msg=new MissileNewMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Msg.TANK_DEAD_MSG:
				msg=new TankDeadMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Msg.MISSILE_DEAD_MSG:
				msg=new MissileDeadMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			default:
				break;
			}
		} 
	}
	/**
	 * @return the udpPort
	 */
	public int getUdpPort() {
		return udpPort;
	}

	/**
	 * @param udpPort the udpPort to set
	 */
	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}	
}

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
 * 客户端连接类
 * @author WanHung
 *
 */
public class NetClient {
	//不能将UDP端口定死。一台机上回冲突。
	private int udpPort;
	String IP;//服务器的IP
	TankClient tc;
	DatagramSocket ds=null;//用于发数据
	
	//存在隐患，万一多个线程调用的话，UDP_PORT_START还没有++，就可能被赋值给另一个线程，造成线程阻塞
	public NetClient(TankClient tc){
		//udpPort=UDP_PORT_START++;//没创建一次端口号就加一次了
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
		//将udp端口号写给服务端
		dos=new DataOutputStream(s.getOutputStream());
		dos.writeInt(udpPort);
		//读取服务端传过来的ID；
		dis=new DataInputStream(s.getInputStream());
		int id=dis.readInt();
		tc.myTank.id=id;//将服务器传过来的ID号重新给到坦克里去
		//根据id的奇偶来判断坦克的类型！
		if(id%2==0){
			tc.myTank.setGood(false);//偶数是一队
		}else{
			tc.myTank.setGood(true);
		}
		System.out.println("连接服务器成功！");
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
	//实例化我方坦克的信息
	TankNewMsg msg=new TankNewMsg(tc.myTank);
	//msg.send(ds,TankServer.IP,TankServer.UDP_PORT);//调用TankNewMsg发数据出去的方法
	 send(msg);
	new Thread(new UDPRevThread()).start();
}
	
	public void send(Msg msg) {
		msg.send(ds, IP, TankServer.UDP_PORT);
	}
	//接收服务器发过来的信息的类
	private class UDPRevThread implements Runnable{
		byte[] buf=new byte[1024];
		@Override
		public void run() {
			while(ds!=null){
				DatagramPacket dp=new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
					System.out.println("从服务器端收到一个包消息，进行解析~");
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
			Msg msg=null;//根据消息的类型确定接收和解析的方法，多态的作用
			switch (msgType) {
			case Msg.TANK_NEW_MSG:
				msg=new TankNewMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Msg.TANK_MOVE_MSG:
				msg=new TankMoveMsg(NetClient.this.tc);
				//让消息类自己去分析
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

package com.tankwar;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
 
/** 
 * @author WanHung
 *
 */
public class TankServer {
	private static int ID=100;//坦克ID
	public static final int TCP_PORT=9999;//监听端口号，监听客户端连接！
	public static final int  UDP_PORT=7777;//UDP端口号
	List<Client> clients = new ArrayList<Client>();//保存一系列的坦克
	
	public void start(){
		//启动数据包的线程
		new Thread(new UDPThread()).start();
		Socket s=null;
		ServerSocket ss=null;
		try {
				ss=new ServerSocket(TCP_PORT);
				while(true){
				System.out.println("等待客户端连接");
				s=ss.accept();
				System.out.println("客户端连接成功");
				System.out.println("==========");
				//接收到客户端的udp端口号
				DataInputStream dis=new DataInputStream(s.getInputStream());
			    //保留IP,udpPort装到Client类 
				String IP=s.getInetAddress().getHostAddress();
				int udpPort=dis.readInt();
				Client c=new Client(IP,udpPort);
				//添加到数组里面去
				clients.add(c);
				//将ID号写给客户端
				DataOutputStream dos=new DataOutputStream(s.getOutputStream());
				dos.writeInt(ID++);
				//System.out.println("A Client connect Addr:"+s.getInetAddress()+":"+s.getPort()+" -----UPDport:"+udpPort);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
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
	public static void main(String[] args) {
		new TankServer().start();
	}
	
	/**
	 * 用内部类保存客户端的信息
	 * @author Administrator
	 *
	 */
	private class Client{
		String IP;
		int udpPort;
		
		public Client(String IP ,int udpPort){
			this.IP=IP;
			this.udpPort=udpPort;
		}
	}
	
	
	/**
	 * 接收客户端发过来的信息，转发给其他客户端
	 * @author Wanhung
	 *
	 */
	private class UDPThread implements Runnable{
		byte[] buf=new byte[1024];//用来将信息发给客户端的数组
		@Override
		public void run() {
			//此类表示用来发送和接收数据报包的套接字,DatagramSocket 上总是启用 UDP 广播发送
			DatagramSocket ds=null;
			try {
				ds=new DatagramSocket(UDP_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			//System.out.println("数据包的线程启动了，端口号udpPort"+UDP_PORT);
			while(ds!=null){
				//DatagramPacket 此类表示数据报包。 数据报包用来实现无连接包投递服务。每条报文仅根据该包中包含的信息从一台机器路由到另一台机器
				DatagramPacket dp=new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
					//System.out.println("从客户端收到了一个数据包");
					//接收到一个包，就发给一个客户端
					for(int i=0;i<clients.size();i++){
						Client c=clients.get(i);
						String IP=c.IP;
						dp.setSocketAddress(new InetSocketAddress(c.IP,c.udpPort));
						ds.send(dp);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}
}

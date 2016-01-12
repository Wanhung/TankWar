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
	private static int ID=100;//̹��ID
	public static final int TCP_PORT=9999;//�����˿ںţ������ͻ������ӣ�
	public static final int  UDP_PORT=7777;//UDP�˿ں�
	List<Client> clients = new ArrayList<Client>();//����һϵ�е�̹��
	
	public void start(){
		//�������ݰ����߳�
		new Thread(new UDPThread()).start();
		Socket s=null;
		ServerSocket ss=null;
		try {
				ss=new ServerSocket(TCP_PORT);
				while(true){
				System.out.println("�ȴ��ͻ�������");
				s=ss.accept();
				System.out.println("�ͻ������ӳɹ�");
				System.out.println("==========");
				//���յ��ͻ��˵�udp�˿ں�
				DataInputStream dis=new DataInputStream(s.getInputStream());
			    //����IP,udpPortװ��Client�� 
				String IP=s.getInetAddress().getHostAddress();
				int udpPort=dis.readInt();
				Client c=new Client(IP,udpPort);
				//��ӵ���������ȥ
				clients.add(c);
				//��ID��д���ͻ���
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
	 * ���ڲ��ౣ��ͻ��˵���Ϣ
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
	 * ���տͻ��˷���������Ϣ��ת���������ͻ���
	 * @author Wanhung
	 *
	 */
	private class UDPThread implements Runnable{
		byte[] buf=new byte[1024];//��������Ϣ�����ͻ��˵�����
		@Override
		public void run() {
			//�����ʾ�������ͺͽ������ݱ������׽���,DatagramSocket ���������� UDP �㲥����
			DatagramSocket ds=null;
			try {
				ds=new DatagramSocket(UDP_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			//System.out.println("���ݰ����߳������ˣ��˿ں�udpPort"+UDP_PORT);
			while(ds!=null){
				//DatagramPacket �����ʾ���ݱ����� ���ݱ�������ʵ�������Ӱ�Ͷ�ݷ���ÿ�����Ľ����ݸð��а�������Ϣ��һ̨����·�ɵ���һ̨����
				DatagramPacket dp=new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
					//System.out.println("�ӿͻ����յ���һ�����ݰ�");
					//���յ�һ�������ͷ���һ���ͻ���
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

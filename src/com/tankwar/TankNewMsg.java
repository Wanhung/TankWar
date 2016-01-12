package com.tankwar;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * �����������̹�˵���Ϣ����������ϣ�
 * @author WanHung
 *
 */
public class TankNewMsg implements Msg {
	Tank tank;
	TankClient tc;
	int msgType=Msg.TANK_NEW_MSG;
	public TankNewMsg(Tank tank){
		this.tank=tank;
	}
	public TankNewMsg(TankClient tc){
		this.tc=tc;
		
	}
	public void send(DatagramSocket ds,String IP,int udpPort) {
		//byte[] buf=new byte[1024];
		//��DtagramSocket �����ݷ���ȥ�ķ���Ӧ��ʹ�ã�
		
		//ByteArrayOutputStream����ʵ����һ������������е����ݱ�д��һ�� byte ���顣
		//���������������ݵĲ���д����Զ�������
		//��ʹ�� toByteArray() �� toString() ��ȡ����
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tank.id);
			dos.writeInt(tank.x);
			dos.writeInt(tank.y);
			//int ordinal() 
	        // ����ö�ٳ���������������ö�������е�λ�ã����г�ʼ��������Ϊ�㣩�� 
			dos.writeInt(tank.dir.ordinal());
			dos.writeBoolean(tank.isGood());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//�����Ϣ
		byte[] buf=baos.toByteArray();
		try {
			DatagramPacket dp=new DatagramPacket(buf,buf.length,new InetSocketAddress(IP, udpPort));
			ds.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void parse(DataInputStream dis) {
		try {
			int id=dis.readInt();
			if(tc.myTank.id==id){
				return;
			}	
		
			int x=dis.readInt();
			int y=dis.readInt();
			//��ö����ķ���ת����int����
			Direction dir=Direction.values()[dis.readInt()];
			Boolean good=dis.readBoolean();
		    //System.out.println("̹�˵�id��"+id+"  ̹�˵�x��"+x+"  ̹�˵�y:"+y+"  ̹�˵ķ���"+dir+"  ̹�˵�����"+good);
			
			boolean exsit=false;
			for(int i=0;i<tc.tanks.size();i++){
				Tank t=tc.tanks.get(i);
				if(t.id==id){
					exsit=true;
					break;
				}
			}
			if(!exsit){
				TankNewMsg tkMsg=new TankNewMsg(tc.myTank);
				tc.nc.send(tkMsg);
				Tank t=new Tank(x,y,good,dir,tc);
				t.id=id;
				tc.tanks.add(t);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

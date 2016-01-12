package com.tankwar;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * 子弹死亡类
 * @author WanHung
 *
 */
public class MissileDeadMsg implements Msg {
	int id;
	int msgType=Msg.MISSILE_DEAD_MSG;
	TankClient tc;
	int tankId;
	
	public MissileDeadMsg(int tankId,int id){
		this.id=id;
		this.tankId=tankId;
	}
	public MissileDeadMsg(TankClient tc){
		this.tc=tc;
	}

	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tankId);
			dos.writeInt(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//打包信息
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
			int tankId=dis.readInt();
			int id=dis.readInt();
			 
			for(int i =0;i<tc.missiles.size();i++){
				Missile m=tc.missiles.get(i);
				if(m.tankId==tankId&&m.id==id){
					m.setMlive(false);
					tc.explode.add(new Explode(m.x,m.y,tc));
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

package com.tankwar;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * 这个类用来把坦克的信息发给服务端上！
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
		//将DtagramSocket 的数据发出去的方法应该使用：
		
		//ByteArrayOutputStream此类实现了一个输出流，其中的数据被写入一个 byte 数组。
		//缓冲区会随着数据的不断写入而自动增长。
		//可使用 toByteArray() 和 toString() 获取数据
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tank.id);
			dos.writeInt(tank.x);
			dos.writeInt(tank.y);
			//int ordinal() 
	        // 返回枚举常量的序数（它在枚举声明中的位置，其中初始常量序数为零）。 
			dos.writeInt(tank.dir.ordinal());
			dos.writeBoolean(tank.isGood());
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
			int id=dis.readInt();
			if(tc.myTank.id==id){
				return;
			}	
		
			int x=dis.readInt();
			int y=dis.readInt();
			//将枚举类的方向转换成int类型
			Direction dir=Direction.values()[dis.readInt()];
			Boolean good=dis.readBoolean();
		    //System.out.println("坦克的id："+id+"  坦克的x："+x+"  坦克的y:"+y+"  坦克的方向："+dir+"  坦克的类型"+good);
			
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

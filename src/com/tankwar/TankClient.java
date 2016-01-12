package com.tankwar;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.text.StyledEditorKit.ForegroundAction;
/**
 * 
 * @author WanHung
 * 2015-12-27
 *
 */

public class TankClient extends JFrame {
	//设置游戏界面长宽！ 
	public static final int GAME_WIDTH=800;
	public static final int GAME_HEIGHT=600;
	
	List<Missile> missiles=new ArrayList<Missile>();//多个子弹，用List<>接收
	List<Explode> explode=new ArrayList<Explode>();//多个爆炸，用List<>接收
	List<Tank> tanks=new ArrayList<Tank>();//多辆敌方坦克
	Tank myTank=new Tank(50,50,true,Direction.STOP,this);//我放坦克
	Image offScreenImage=null;//背面畫板
	//连接服务器对象
	NetClient nc=new NetClient(this);
	//内部类连接对话框
	ConnDialog dialog=new ConnDialog();
	
	public void lauchFrame(){
		this.setTitle("坦克大战 ");
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setLocation(100, 100);
		this.setResizable(false);
		this.setVisible(true);
		
		//估计这个setDefaultCloseOperation();是swing里才有的方法，awt没有
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		//匿名类的操作方法关闭
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		//添加键盘监听事件
		this.addKeyListener(new KeyMonitor());
		//启动线程
		new Thread(new PaintThread()).start();
	}

	private class PaintThread implements Runnable{
		//线程内部类只为外部包装类服务！ 所以用内部类就可以了！
		@Override
		public void run() {
			while(true){
				repaint();//每调用一次线程就重画一次，使得坦克动起来！
				try {
					Thread.sleep(100);//相隔重画时间
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	 
	@Override
	public void paint(Graphics g) { 
		 g.setColor(Color.GREEN);//直接重新设置背景色
		 g.fillRect(0, 0, GAME_WIDTH, GAME_WIDTH);//填充。
			//g.drawString("tanks count:"+ tanks.size(), 100, 140);
			for(int i =0;i<tanks.size();i++){
				 Tank t=tanks.get(i);
				 t.draw(g);
			 }
		 //显示子弹在游戏界面内的数目
		// g.drawString("missiles count:"+ missiles.size(), 100, 100);
		 for (int i = 0; i < missiles.size(); i++) {
			Missile m=missiles.get(i);
				if(m.hitTank(myTank)){
					//击中了坦克就发送坦克死亡的消息和子弹死亡的消息。
					TankDeadMsg msg=new TankDeadMsg(myTank.id);
					nc.send(msg);
					MissileDeadMsg msgMissile=new MissileDeadMsg(m.tankId, m.id);
					nc.send(msgMissile);
				}
		 		m.draw(g);	
			}
		 //显示爆炸的数目；
		// g.drawString("explode count:"+ explode.size(), 100, 120);
		 for(int i=0;i<explode.size();i++){
			  Explode e=explode.get(i);
			 e.draw(g);
		 } 
		 myTank.draw(g);
	}
   
	@Override
	public void update(Graphics g) { 
		if(offScreenImage==null){
			offScreenImage=this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen =offScreenImage.getGraphics();
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}
	private class KeyMonitor extends KeyAdapter{
		 
		public void keyPressed(KeyEvent e) {
			//按下键的时候可以调出输入端口的对话框
			int key=e.getKeyCode();
			if(key==KeyEvent.VK_F1){
				dialog.setVisible(true);
			}
			else{
				myTank.keyPressed(e);
			}
		}
	 
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}	
	}
	//内部类对话框。
 class ConnDialog extends Dialog{
	 JButton b=new JButton("确定");
	 
	 TextField tfIP=new TextField("127.0.0.1",12);
	 TextField tfPort=new TextField(""+TankServer.TCP_PORT,4);
	 TextField tfMyUDPPort=new TextField("2223",4);
	 public ConnDialog(){
		 super(TankClient.this,true);
		 this.setLayout(new FlowLayout());
		 this.add(new Label("IP:"));
		 this.add(tfIP);
		 this.add(new Label("Port:"));
		 this.add(tfPort);
		 this.add(new Label("My UDP Port:"));
		 this.add(tfMyUDPPort);
		 this.setLocation(300,300);
		 this.add(b);
		 this.pack();
		 this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
			 
		});
		 b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String IP=tfIP.getText();
				int port=Integer.parseInt(tfPort.getText().trim());
				int myUDPPort=Integer.parseInt(tfMyUDPPort.getText().trim());
				nc.setUdpPort(myUDPPort);
				nc.connect(IP, port);
				setVisible(false);
				
			}
		});
	 }
 }
	
}

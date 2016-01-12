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
	//������Ϸ���泤�� 
	public static final int GAME_WIDTH=800;
	public static final int GAME_HEIGHT=600;
	
	List<Missile> missiles=new ArrayList<Missile>();//����ӵ�����List<>����
	List<Explode> explode=new ArrayList<Explode>();//�����ը����List<>����
	List<Tank> tanks=new ArrayList<Tank>();//�����з�̹��
	Tank myTank=new Tank(50,50,true,Direction.STOP,this);//�ҷ�̹��
	Image offScreenImage=null;//���残��
	//���ӷ���������
	NetClient nc=new NetClient(this);
	//�ڲ������ӶԻ���
	ConnDialog dialog=new ConnDialog();
	
	public void lauchFrame(){
		this.setTitle("̹�˴�ս ");
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setLocation(100, 100);
		this.setResizable(false);
		this.setVisible(true);
		
		//�������setDefaultCloseOperation();��swing����еķ�����awtû��
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		//������Ĳ��������ر�
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		//��Ӽ��̼����¼�
		this.addKeyListener(new KeyMonitor());
		//�����߳�
		new Thread(new PaintThread()).start();
	}

	private class PaintThread implements Runnable{
		//�߳��ڲ���ֻΪ�ⲿ��װ����� �������ڲ���Ϳ����ˣ�
		@Override
		public void run() {
			while(true){
				repaint();//ÿ����һ���߳̾��ػ�һ�Σ�ʹ��̹�˶�������
				try {
					Thread.sleep(100);//����ػ�ʱ��
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	 
	@Override
	public void paint(Graphics g) { 
		 g.setColor(Color.GREEN);//ֱ���������ñ���ɫ
		 g.fillRect(0, 0, GAME_WIDTH, GAME_WIDTH);//��䡣
			//g.drawString("tanks count:"+ tanks.size(), 100, 140);
			for(int i =0;i<tanks.size();i++){
				 Tank t=tanks.get(i);
				 t.draw(g);
			 }
		 //��ʾ�ӵ�����Ϸ�����ڵ���Ŀ
		// g.drawString("missiles count:"+ missiles.size(), 100, 100);
		 for (int i = 0; i < missiles.size(); i++) {
			Missile m=missiles.get(i);
				if(m.hitTank(myTank)){
					//������̹�˾ͷ���̹����������Ϣ���ӵ���������Ϣ��
					TankDeadMsg msg=new TankDeadMsg(myTank.id);
					nc.send(msg);
					MissileDeadMsg msgMissile=new MissileDeadMsg(m.tankId, m.id);
					nc.send(msgMissile);
				}
		 		m.draw(g);	
			}
		 //��ʾ��ը����Ŀ��
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
			//���¼���ʱ����Ե�������˿ڵĶԻ���
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
	//�ڲ���Ի���
 class ConnDialog extends Dialog{
	 JButton b=new JButton("ȷ��");
	 
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

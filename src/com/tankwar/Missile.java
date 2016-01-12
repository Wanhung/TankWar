package com.tankwar;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

 /**
  * �ӵ���
  * @author Administrator
  *
  */
public class Missile {
	
	int x,y;//�ӵ���λ��
	public static final int XSPEED=10;//�ӵ��ĺ����ٶ�
	public static final int YSPEED=10;//�ӵ��������ٶ�
	public static final int MWIDTH=10;//�ӵ����
	public static final int MHEIGHT=10;//�ӵ��߶�
	private static int ID=1;//�ӵ��ĳ�ʼID������ָ��
	Direction dir;//�ӵ�����
	TankClient tc;
	int id;//�����ӵ���ID������ָ��
	boolean good;//�����ӵ�������
	private boolean mlive=true;//�����ӵ���״̬��
	int tankId;//̹�˵�id
	
	//���췽��
	public Missile(int tankId,int x, int y,boolean good, Direction dir) {
		this.tankId=tankId;
		this.x = x;
		this.y = y;
		this.good=good;
		this.dir = dir;
		this.id=ID++;
	}
	public Missile(int tankId,int x,int y,boolean good, Direction dir,TankClient tc){
		 this(tankId,x,y,good,dir);
		this.tc=tc;
	}
	
	//������
	public void draw(Graphics g){
		//�ж��ӵ���״̬
		if(!mlive){
			tc.missiles.remove(this);//�������״̬�����ӵ�������missiles���Ƴ�
			return;
		}
		//����̹�˵�ID��ȷ���ӵ���ɫ
		if(tankId % 2==0){	
			//IDż�����ӵ���ɫΪ��ɫ
			g.setColor(Color.BLUE);
		}else{	
			//�Է��ӵ���ɫΪ��ɫ
			g.setColor(Color.RED);
		}
		  g.fillOval(x, y, MWIDTH, MHEIGHT);
		  move();
	}
	private void move() {
		switch (dir) {
		case L:
			x-=XSPEED;
			break;
		case LU:
			x-=XSPEED;
			y-=YSPEED;
			break;
		case U:
			y-=YSPEED;
			break;
		case RU:
			x+=XSPEED;
			y-=YSPEED;
			break;
		case R:
			x+=XSPEED;
			break;
		case RD:
			x+=XSPEED;
			y+=YSPEED;
			break;
		case D:	
			y+=YSPEED;
			break;
		case LD:
			x-=XSPEED;
			y+=YSPEED;
			break;
		 
		default:
			break;
		}
		//����ӵ���������Ϸ����Ļ�����Ϊ����״̬
		if(x<0||y<0||x>tc.GAME_WIDTH||y>tc.GAME_HEIGHT){
			mlive=false;
		}
	}
	 
	//��ȡ�ӵ�����
	public Rectangle getRect(){
		return new Rectangle(x,y,MWIDTH,MHEIGHT);
	}
	//��̹�˷���
	public boolean hitTank(Tank t){
	   if(this.isMlive()&&this.getRect().intersects(t.getRect())&&t.isTlive()&&this.good!=t.isGood())
	   {
		   t.setTlive(false);;
		   this.mlive=false;
		   //����̹��������λ�ò���һ����ը
		   Explode e=new Explode(x,y,tc);
		   tc.explode.add(e);
		   return true;
	   }
	    return false;
	}
	
	public boolean hitTanks(List<Tank> tanks){
		for(int i=0;i<tanks.size();i++){
			if(hitTank(tanks.get(i))){
				return true;
			}
		}
		return false;
		
	}
	 
	public boolean isMlive() {
		return mlive;
	}
	 
	public void setMlive(boolean mlive) {
		this.mlive = mlive;
	}
	 
	public boolean isGood() {
		return good;
	}
		 
	 }
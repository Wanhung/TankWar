package com.tankwar;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

 /**
  * 子弹类
  * @author Administrator
  *
  */
public class Missile {
	
	int x,y;//子弹的位置
	public static final int XSPEED=10;//子弹的横向速度
	public static final int YSPEED=10;//子弹的纵向速度
	public static final int MWIDTH=10;//子弹宽度
	public static final int MHEIGHT=10;//子弹高度
	private static int ID=1;//子弹的初始ID，用来指定
	Direction dir;//子弹方向
	TankClient tc;
	int id;//定义子弹的ID，用来指定
	boolean good;//定义子弹的类型
	private boolean mlive=true;//定义子弹的状态，
	int tankId;//坦克的id
	
	//构造方法
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
	
	//画方法
	public void draw(Graphics g){
		//判断子弹的状态
		if(!mlive){
			tc.missiles.remove(this);//如果死亡状态，从子弹的数组missiles中移除
			return;
		}
		//利用坦克的ID，确定子弹颜色
		if(tankId % 2==0){	
			//ID偶数的子弹颜色为蓝色
			g.setColor(Color.BLUE);
		}else{	
			//对方子弹颜色为红色
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
		//如果子弹超出了游戏界面的话设置为死亡状态
		if(x<0||y<0||x>tc.GAME_WIDTH||y>tc.GAME_HEIGHT){
			mlive=false;
		}
	}
	 
	//获取子弹矩形
	public Rectangle getRect(){
		return new Rectangle(x,y,MWIDTH,MHEIGHT);
	}
	//打坦克方法
	public boolean hitTank(Tank t){
	   if(this.isMlive()&&this.getRect().intersects(t.getRect())&&t.isTlive()&&this.good!=t.isGood())
	   {
		   t.setTlive(false);;
		   this.mlive=false;
		   //根据坦克所处的位置产生一个爆炸
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
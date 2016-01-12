package com.tankwar;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;


public class Tank {
	public int id;//坦克的ID号
	public int x,y;//位置坐標
	public static final int XSPEED=5;//坦克横向速度
	public static final int YSPEED=5;//坦克纵向方向
	public static final int WIDTH=30;//坦克宽度
	public static final int HEIGHT=30;
	
	private   boolean tlive=true;//代表坦克的存活
	
	private boolean bL=false;
	private boolean bR=false;
	private boolean bU=false;
	private boolean bD=false;
	TankClient tc=null;
	
	private boolean good;
	public Direction dir=Direction.STOP;
	public Direction ptDir=Direction.D;//炮筒方向。
	
	public Tank(int x, int y,boolean good) {
		this.x = x;
		this.y = y;
		this.good=good;
		
	}
	public Tank(int x,int y,boolean good,Direction dir,TankClient tc){
		this(x,y,good);
		this.dir=dir;
		this.tc=tc;
	}
	public void draw(Graphics g){
		if(!tlive) {
			if(!good){
				tc.tanks.remove(this);
			}
			return;
		}
			//开始遇到了覆盖的问题，原因是没调用一次draw()就会刷新一次背景色，所以，背景色的刷新一次就行了，用在创造自己的坦克的时候刷新背景一次就OK
			 if(good){
				 g.setColor(Color.RED);
			 }else{
				 g.setColor(Color.BLUE);
			 }
			 g.fillOval(x,y,WIDTH,HEIGHT);
			 g.drawString("id="+id,x, y-10);
			 
			 g.setColor(Color.BLACK);//炮筒的颜色设置
			 //下面的是炮筒的方向，根据坦克的位置确定。确定好两点，画一条黑线代表炮筒
		 switch(ptDir){
	case L:
		
		g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y+Tank.HEIGHT/2);
		break;
	case LU:
		g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y);
		break;
	case U:
		g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH/2, y);
		break;
	case RU:
		g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH,y);
		break;
	case R:
		g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y+Tank.HEIGHT/2);
		break;
	case RD:
		g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y+Tank.HEIGHT);
		break;
	case D:	
		g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH/2, y+Tank.HEIGHT);
		break;
	case LD:
		g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y+Tank.HEIGHT);
		break;
	case STOP:
		break;
	default:
		break;
		 }
		 move();
	}
	public void move(){
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
		case STOP:
			break;
		default:
			break;
		}
		if(this.dir!=Direction.STOP){
			this.ptDir=this.dir;
		}
		//设置坦克的活动范围
		if(x<0 ){x=0;}
		if(y<30) {y=30;}
		if(x+Tank.WIDTH>TankClient.GAME_WIDTH){
			x=TankClient.GAME_WIDTH-Tank.WIDTH;
		}
		if(y+Tank.HEIGHT>TankClient.GAME_HEIGHT) {
			y=TankClient.GAME_HEIGHT-Tank.HEIGHT;
		}
		//下面的代吗是单机版本里面为了让坏蛋坦克自己动起来的
		/*if(!good){
			Direction[] dirs=Direction.values();//讲enum的方向轉換成數組
			if(step==0){
			step=r.nextInt(10)+3;
			int rn=r.nextInt(dirs.length);//随机产生一个数
			dir=dirs[rn];
			}
			
			step--;
			if(r.nextInt(40)>38){
				this.fire();
			}
		}*/
	}
	
	public void keyPressed(KeyEvent e){

		int key=e.getKeyCode();
		switch (key) {
		//使坦克动起来，设置方向！
		case KeyEvent.VK_LEFT:
			 bL=true;
			break;
		case KeyEvent.VK_RIGHT:
			bR=true;
			break;
		case KeyEvent.VK_UP:
			bU=true;
			break;
		case KeyEvent.VK_DOWN:
			bD=true;
			break;
	 
		default:
			break;
		}
		locateDirection();
	}
	public void keyReleased(KeyEvent e) {

		int key=e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_CONTROL:
			 fire();
			break;
		//使坦克动起来，设置方向！
		case KeyEvent.VK_LEFT:
			 bL=false;
			break;
		case KeyEvent.VK_RIGHT:
			bR=false;
			break;
		case KeyEvent.VK_UP:
			bU=false;
			break;
		case KeyEvent.VK_DOWN:
			bD=false;
			break;
	 
		default:
			break;
		}
		locateDirection();
	}
	public void locateDirection(){
		Direction oldDir=this.dir;//记录原来的方向，只要方向以改变就给其他客户端发消息
		if(bL && !bU && !bR && !bD) dir=Direction.L;
		else if(bL && bU && !bR && !bD) dir=Direction.LU;
		else if(!bL && bU && !bR && !bD) dir=Direction.U;
		else if(!bL && bU && bR && !bD) dir=Direction.RU;
		else if(!bL && !bU && bR && !bD) dir=Direction.R;
		else if(!bL && !bU && bR && bD) dir=Direction.RD;
		else if(!bL && !bU && !bR && bD) dir=Direction.D;
		else if(bL && !bU && !bR && bD) dir=Direction.LD;
		else if(!bL && !bU && !bR && !bD) dir=Direction.STOP;
		//如果方向改变了，就发送一个坦克移动的消息给其他客户端，让其他人的界面上知道你的位置
		if(dir!=oldDir){
			TankMoveMsg msg=new TankMoveMsg(id,x,y, dir,ptDir);
			tc.nc.send(msg);
		}
	}
	public Missile fire(){
		if(!tlive){
			return null ;
		}
		int x=this.x+Tank.WIDTH/2-Missile.MWIDTH/2;//子弹的出来的位置
		int y=this.y+Tank.HEIGHT/2-Missile.MHEIGHT/2;
		Missile m=new Missile(id,x,y,this.good,this.ptDir,this.tc);
		tc.missiles.add(m);
		//一开火就发送一个新加入的子弹消息！
		MissileNewMsg msg=new MissileNewMsg(m);
		tc.nc.send(msg);
		return m;
		
	}
	public Rectangle getRect(){
		return new Rectangle(x,y,WIDTH,HEIGHT);
	}
	 
	public   boolean isTlive() {
		return tlive;
	}
	 
	public void setTlive(boolean tlive) {
		this.tlive = tlive;
	}
	 
	public boolean isGood() {
		return good;
	}
	 
	public void setGood(boolean good) {
		this.good = good;
	}
	 
}

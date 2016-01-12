package com.tankwar;
import java.awt.Color;
import java.awt.Graphics;

/**
 * 爆炸类
 * @author WanHung
 *
 */
public class Explode {
	  int x,y;//爆炸顯示的位置
	  private boolean live=true;//爆炸存在状态
	  int [] diameter={4,7,12,18,26,32,59,45,25,6};//代表爆炸圆的直径
	  int step=0;//画到第几步就话第几步的直径圆
	  TankClient tc;
	  //构造方法
	  public Explode(int x,int y,TankClient tc){
		  this.x=x;
		  this.y=y;
		  this.tc=tc;
	  }
	  public void draw(Graphics g){
		  if(!live){
			  tc.explode.remove(this);
			  return;
		  }
		  if(step==diameter.length){//判断画到diameter的最后该怎么做的判断操作
			  live=false;//爆炸状态设置为false，
			  step=0;//步骤归0
			  return;//返回
		  }
		  g.setColor(Color.ORANGE);
		  g.fillOval(x, y, diameter[step], diameter[step]);
		  step++;
	  }
}

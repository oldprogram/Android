package com.pg;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Control {
	Paint paint;//画笔
	//定义两个圆形的中心点坐标与半径
	private float smallCenterX,smallCenterY,smallCenterR;
	private float BigCenterX,BigCenterY,BigCenterR;
	//构造函数，输入中心位置和小圆、大圆半径
	Control(float centerX,float centerY,float sR,float bR){
		smallCenterX=BigCenterX=centerX;
		BigCenterY=smallCenterY=centerY;
		smallCenterR=sR;
		BigCenterR=bR;
		paint=new Paint(Color.RED);
	}
	//重新恢复原状态
	public void reSet(){
		smallCenterX=BigCenterX;
		smallCenterY=BigCenterY;
	}
	//绘图函数
	public void myDraw(Canvas canvas) {
		//绘制大圆
		paint.setAlpha(0x77);
		canvas.drawCircle(BigCenterX, BigCenterY, BigCenterR,paint);
		//绘制小圆
		canvas.drawCircle(smallCenterX, smallCenterY, smallCenterR,paint);
	}
	//触屏监听
	public boolean onTouchEvent(MotionEvent event,Player player) {
		//当用户手指抬起，应该恢复小圆到初始位置
		if (event.getAction() == MotionEvent.ACTION_UP) {
			smallCenterX = BigCenterX;
			smallCenterY = BigCenterY;
			player.setDirect(0);//归位不移动
		} else {
			player.setDirect(0);//归位不移动
			int pointX = (int) event.getX();
			int pointY = (int) event.getY();
			double angle=getRad(BigCenterX, BigCenterY, pointX, pointY);//获取偏转角度弧度
			//判断用户点击的位置是否在大圆内
			if (Math.sqrt(Math.pow((BigCenterX - (int) event.getX()), 2) + Math.pow((BigCenterY - (int) event.getY()), 2)) <= BigCenterR) {
				//让小圆跟随用户触点位置移动
				smallCenterX = pointX;
				smallCenterY = pointY;
			} else {
				setSmallCircleXY(BigCenterX, BigCenterY, BigCenterR,angle);	
			}
			angle=angle/Math.PI*180;//将弧度转换为角度[控制]
			if(angle>=-150 && angle<=-30)player.setDirect(1);
			else if(angle>=30 && angle<=150)player.setDirect(3);
			if(Math.abs(angle)>=120)player.setDirect(2);
			else if(Math.abs(angle)<=60)player.setDirect(4);
		}
		return true;
	}
	/** 
	 * 小圆针对于大圆做圆周运动时，设置小圆中心点的坐标位置
	 * @param centerX 
	 *            围绕的圆形(大圆)中心点X坐标
	 * @param centerY 
	 *            围绕的圆形(大圆)中心点Y坐标
	 * @param R
	 * 			     围绕的圆形(大圆)半径
	 * @param rad 
	 *            旋转的弧度 
	 */
	public void setSmallCircleXY(float centerX, float centerY, float R, double rad) {
		//获取圆周运动的X坐标   
		smallCenterX = (float) (R * Math.cos(rad)) + centerX;
		//获取圆周运动的Y坐标  
		smallCenterY = (float) (R * Math.sin(rad)) + centerY;
	}
	/**
	 * 得到两点之间的弧度
	 * @param px1    第一个点的X坐标
	 * @param py1    第一个点的Y坐标
	 * @param px2    第二个点的X坐标
	 * @param py2    第二个点的Y坐标
	 * @return
	 */
	public double getRad(float px1, float py1, float px2, float py2) {
		//得到两点X的距离  
		float x = px2 - px1;
		//得到两点Y的距离  
		float y = py1 - py2;
		//算出斜边长  
		float Hypotenuse = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		//得到这个角度的余弦值（通过三角函数中的定理 ：邻边/斜边=角度余弦值）  
		float cosAngle = x / Hypotenuse;
		//通过反余弦定理获取到其角度的弧度  
		float rad = (float) Math.acos(cosAngle);
		//当触屏的位置Y坐标<摇杆的Y坐标我们要取反值-0~-180  
		if (py2 < py1) {
			rad = -rad;
		}
		return rad;
	}
}

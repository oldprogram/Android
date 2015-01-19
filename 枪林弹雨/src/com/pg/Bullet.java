package com.pg;

import java.util.Vector;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Bullet {
	public Bitmap bmpBullet,xuanBmp;//子弹图片资源,旋转后的
	public int bulletX, bulletY;//子弹的坐标
	public int speed,speedX;	//子弹的速度
	public int bulletType;//子弹的种类以及常量
	public static final int BULLET_PLAYER = -1;	//主角的
	public static final int BULLET_PLAYER1= 0; //主角跟踪弹
	public static final int BULLET_DUCK = 1;//漂浮物的
	public static final int BULLET_FLY = 2;//章鱼怪的
	public boolean isDead;//子弹是否超屏， 优化处理
	private int angle=0;//旋转角[0-360，数值为0，顺时针加]
	static public int num=0;//跟踪弹得数量
	
	public Bullet(Bitmap bmpBullet, int bulletX, int bulletY, int bulletType) {
		this.bmpBullet = bmpBullet;
		this.bulletX = bulletX;
		this.bulletY = bulletY;
		this.bulletType = bulletType;
		switch (bulletType) {//不同的子弹类型速度不一
		case BULLET_PLAYER:
			speed = 4;
			break;
		case BULLET_PLAYER1:
			speed = 3;
			speedX= 0;
			angle=0;
			num++;
			break;
		case BULLET_DUCK:
			speed = 3;
			break;
		case BULLET_FLY:
			speed = 4;
			break;
		}
	}//子弹当前方向

	//子弹的绘制
	public void draw(Canvas canvas, Paint paint) {
		Matrix matrix = new Matrix();
	    matrix.postRotate(angle);   /*翻转angle度*/
	    int width = bmpBullet.getWidth();
	    int height = bmpBullet.getHeight();
	    xuanBmp = Bitmap.createBitmap(bmpBullet, 0, 0, width, height, matrix, true);
		canvas.drawBitmap(xuanBmp, bulletX, bulletY, paint);
	}

	//子弹的逻辑
	public void logic(Vector<Enemy> vcEnemy) {
		switch (bulletType) {//不同的子弹类型逻辑不一
		case BULLET_PLAYER://主角的子弹垂直向上运动
			bulletY -= speed;
			if (bulletY < -50) {
				isDead = true;
			}
			break;
		case BULLET_PLAYER1:
			double minLength=100000;
			int findPos=-1;
			for (int i=0;i<vcEnemy.size();i++){//找离当前子弹最近的敌人下标[在子弹前面的敌人算]
				if(vcEnemy.elementAt(i).y<bulletY){
					double curLength=vcEnemy.elementAt(i).getLength(bulletX, bulletY);
					if(curLength<minLength){
						minLength=curLength;
						findPos=i;
					}
				}
			}
			if(findPos!=-1){//有目标算出x方向的速度
				double tan=1.0*(vcEnemy.elementAt(findPos).x-bulletX)/(vcEnemy.elementAt(findPos).y-bulletY);
				angle=-(int)(Math.atan(tan)*180/3.1415926);
				//speedX=(int)(speed*tan);
				if(tan<0)speedX=-speed*2;
				else speedX=speed*2;
			}else{
				speedX=0;
				angle=0;
			}
			bulletY -= speed;
			if (bulletY < -50) {
				isDead = true;
				num--;
			}
			bulletX -= speedX;
			if(bulletX<2){
				bulletX=2;
				speedX=0;
			}else if(bulletX>MySurfaceView.screenW-12){
				bulletX=MySurfaceView.screenW-12;
				speedX=0;				
			}
			break;
		case BULLET_DUCK://漂浮物和章鱼怪的子弹都是垂直下落运动
		case BULLET_FLY:
			bulletY += speed;
			if (bulletY > MySurfaceView.screenH) {
				isDead = true;
			}
			break;
		}
	}
}

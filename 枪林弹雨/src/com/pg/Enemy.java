package com.pg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Enemy {
	public int type;//敌人的种类标识
	public static final int TYPE_FLY = 1;//章鱼怪
	public static final int TYPE_DUCKL = 2;//漂浮物(从左往右运动)
	public static final int TYPE_DUCKR = 3;//(从右往左运动)
	public static final int TYPE_BOSS =4;//BOSS
	public static int PH=400;//血量[]
	private static final int BOSS_STATE_H = 5;//平移
	private static final int BOSS_STATE_V_DOWN = 6;//竖着下
	private static final int BOSS_STATE_V_UP   = 7;//竖着上
	private static final int BOSS_STATE_FIRE   = 8;//放大招
	private static final int BOSS_STATE_CUT    = 9;//被击中
	public int ph;//BOSS血量
	private int bosscount;//BOSS计时器
	private int state,oldstate;//BOSS状态
	public Bitmap bmpEnemy;//敌人图片资源
	public int x, y;//敌人坐标
	public int frameW, frameH;//敌人每帧的宽高
	private int frameIndex;//敌人当前帧下标
	private int speed,speed_boss_y;//敌人的移动速度
	public boolean isDead;//判断敌人是否已经出屏
	static public int createEnemyTime = 50;//每次生成敌人的时间(毫秒)
	static public int createBulletTime= 40;//每次生成敌人子弹的时间
	public Enemy(Bitmap bmpEnemy, int enemyType, int x, int y) {
		this.bmpEnemy = bmpEnemy;
		frameW = bmpEnemy.getWidth() / 10;
		frameH = bmpEnemy.getHeight();
		this.type = enemyType;
		this.x = x;
		this.y = y;
		switch (type) {//不同种类的敌人速度不同
		case TYPE_FLY://章鱼怪
			speed = 25;
			break;
		case TYPE_DUCKL:
			speed = 3;
			break;
		case TYPE_DUCKR:
			speed = 3;
			break;
		case TYPE_BOSS:
			speed = 4;
			speed_boss_y=0;
			ph=PH;
			bosscount=0;
			state=BOSS_STATE_H;
			break;
		}
	}//敌人的构造函数
	
	static public void reset(){
		createEnemyTime = 50;//每次生成敌人的时间(毫秒)
		createBulletTime= 40;//每次生成敌人子弹的时间
	}//重置数据函数
	public void draw(Canvas canvas, Paint paint) {
		canvas.save();
		canvas.clipRect(x, y, x + frameW, y + frameH);
		canvas.drawBitmap(bmpEnemy, x - frameIndex * frameW, y, paint);
		canvas.restore();
	}//敌人绘图函数

	public void logic() {
		switch (type) {//不同种类的敌人拥有不同的AI逻辑
		case TYPE_FLY:
			if (isDead == false) {
				//减速出现，加速返回
				speed -= 1;
				y += speed;
				if (y <= -200) {
					isDead = true;
				}
			}
			break;
		case TYPE_DUCKL:
			if (isDead == false) {
				//斜右下角运动
				x += speed / 2;
				y += speed;
				if (x > MySurfaceView.screenW) {
					isDead = true;
				}
			}
			break;
		case TYPE_DUCKR:
			if (isDead == false) {
				//斜左下角运动
				x -= speed / 2;
				y += speed;
				if (x < -50) {
					isDead = true;
				}
			}
			break;
		case TYPE_BOSS:
			if(isDead==false){
				switch(state){
				case BOSS_STATE_H://平移
					x += speed / 2;
					if (x<-100 || x > MySurfaceView.screenW+30){
						speed=-speed;
					}
					break;
				case BOSS_STATE_V_DOWN:
					//减速出现，加速返回
					speed -= 1;
					y += speed;
					break;
				case BOSS_STATE_V_UP:
					//减速出现，加速返回
					speed -= 1;
					y += speed;
					break;
				case BOSS_STATE_FIRE:
					break;
				case BOSS_STATE_CUT://被击中不移动
					bosscount++;
					if(bosscount>10){
						bosscount=0;
						state=BOSS_STATE_H;//恢复原来状态
					}
					break;
				default:break;
				}
			}
		break;
		}
	}//敌人逻辑AI

	public boolean isCollsionWith(Bullet bullet) {
		int x2 = bullet.bulletX;
		int y2 = bullet.bulletY;
		int w2 = bullet.bmpBullet.getWidth();
		int h2 = bullet.bmpBullet.getHeight();
		if (x >= x2 && x >= x2 + w2) {
			return false;
		} else if (x <= x2 && x + frameW <= x2) {
			return false;
		} else if (y >= y2 && y >= y2 + h2) {
			return false;
		} else if (y <= y2 && y + frameH <= y2) {
			return false;
		}
		//发生碰撞，让其死亡
		if(type==TYPE_BOSS && ph!=0){//BOSS就让其减血
			//oldstate=state;// 
			//state=BOSS_STATE_CUT;//被击中就不能动
			//bosscount=0;//开始时
			ph--;
		}else isDead = true;
		return true;
	}//判断碰撞(敌人与主角子弹碰撞)
	
	public double getLength(int x0,int y0){
		return Math.sqrt((x0-x)*(x0-x)+(y0-y)*(y0-y));
	}//获取两点间的距离函数[用于设计追踪弹]
}


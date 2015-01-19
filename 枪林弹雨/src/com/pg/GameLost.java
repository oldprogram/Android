package com.pg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;


public class GameLost {
	
	private Bitmap bmpGameOver;//游戏结束图片
	private Bitmap bmpButton;//重新开始图片
	private int goX,goY,goStopY;//gameover贴图位置
	private int btnX,btnY,btnStopX,btnStopY;//按钮贴图位置
	private Boolean isPress;//是否按了按钮
	private int state;//动画播放状态0-gameover飞入；1延时；2restart飞入；3延时；4等待;5restart移动
	//构造函数
	GameLost(Bitmap bmpGameOver, Bitmap reStart){
		this.bmpGameOver=bmpGameOver;
		this.bmpButton=reStart;
		goX = MySurfaceView.screenW / 2 - bmpGameOver.getWidth() / 2;//居中
		goStopY = MySurfaceView.screenH /2 - bmpGameOver.getHeight()*6/5;//中间偏上
		goY=-200;
		btnStopX = MySurfaceView.screenW + 100;
		btnX = MySurfaceView.screenW / 2 - bmpButton.getWidth() / 2;//居中
		btnStopY = MySurfaceView.screenH - bmpButton.getHeight()*3/2;//中间偏上
		btnY=MySurfaceView.screenH+50;
		isPress=false;//按钮状态
		state=0;
	}
	//绘图函数
	public void draw(Canvas canvas, Paint paint){
		switch(state){
		case 0:
			goY+=20;
			if(goY>=goStopY){
				state=1;
			}
			canvas.drawBitmap(bmpGameOver,goX,goY,paint);
			break;
		case 1:state=2;break;
		case 2:
			btnY-=20;
			if(btnY<=btnStopY){
				state=3;
			}
			canvas.drawBitmap(bmpGameOver,goX,goY,paint);
			canvas.drawBitmap(bmpButton,btnX,btnY,paint);
			break;
		case 3:state=4;
		case 4:
			canvas.drawBitmap(bmpGameOver,goX,goY,paint);
			canvas.drawBitmap(bmpButton,btnX,btnY,paint);
			break;
		case 5:
			btnX+=20;
			if(btnX>=btnStopX){
				isPress = false;
				MySurfaceView.gameState = MySurfaceView.GAME_MENU;//改变当前游戏状态为开始游戏
			}
			canvas.drawBitmap(bmpGameOver,goX,goY,paint);
			canvas.drawBitmap(bmpButton,btnX,btnY,paint);
			break;
		default:break;
		}
	}
	//触屏函数
	public void onTouchEvent(MotionEvent event) {
		//if(isPress)return;
		//获取用户当前触屏位置
		int pointX = (int) event.getX();
		int pointY = (int) event.getY();
		//当用户是按下动作或移动动作
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			//判定用户是否点击了按钮
			if (pointX > btnX && pointX < btnX + bmpButton.getWidth()) {
				if (pointY > btnY && pointY < btnY + bmpButton.getHeight()) {
					isPress = true;
				} else {
					isPress = false;
				}
			} else {
				isPress = false;
			}
			//当用户是抬起动作
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			//抬起判断是否点击按钮，防止用户移动到别处
			if (pointX > btnX && pointX < btnX + bmpButton.getWidth()) {
				if (pointY > btnY && pointY < btnY + bmpButton.getHeight()) {
					state=5;
					//还原Button状态为未按下状态
					//isPress = false;
					//MySurfaceView.gameState = MySurfaceView.GAME_MENU;//改变当前游戏状态为开始游戏
				}
			}
		}
	}
}

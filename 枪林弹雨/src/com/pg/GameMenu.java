package com.pg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

public class GameMenu {
	private Bitmap bmpMenu;//菜单背景图
	private Bitmap bmpButton[]=new Bitmap[2];//按钮图片资源(按下和未按下图)
	private int btnX, btnY;//按钮的坐标
	private Boolean isPress;//按钮是否按下标识位
	private int change;//图片切换
	public GameMenu(Bitmap bmpMenu, Bitmap bmpButton, Bitmap bmpButtonPress) {
		this.bmpMenu = bmpMenu;
		this.bmpButton[0] = bmpButton;
		this.bmpButton[1] = bmpButtonPress;
		//X居中，Y
		btnX = MySurfaceView.screenW / 2 - bmpButton.getWidth() / 2;
		btnY = MySurfaceView.screenH/2 + bmpButton.getHeight();
		change=0;
		isPress = false;
	}//菜单初始化
	public void draw(Canvas canvas, Paint paint) {
		//绘制菜单背景图
		canvas.drawBitmap(bmpMenu, 0, 0, paint);
		//绘制未按下按钮图
		if (isPress) {//根据是否按下绘制不同状态的按钮图
			canvas.drawBitmap(bmpButton[change], btnX, btnY, paint);
			btnY+=4;
			if(btnY>MySurfaceView.screenH - bmpButton[0].getHeight()){
				//还原Button状态为未按下状态
				isPress = false;
				MySurfaceView.gameState = MySurfaceView.GAMEING;//改变当前游戏状态为开始游戏
				paint.setColor(Color.WHITE);
			}
		}else{
			canvas.drawBitmap(bmpButton[change], btnX, btnY, paint);
		}
		change=(change+1)%2;
	}//菜单绘图函数
	//菜单触屏事件函数，主要用于处理按钮事件
	public void onTouchEvent(MotionEvent event) {
		if(isPress)return;
		//获取用户当前触屏位置
		int pointX = (int) event.getX();
		int pointY = (int) event.getY();
		//当用户是按下动作或移动动作
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			//判定用户是否点击了按钮
			if (pointX > btnX && pointX < btnX + bmpButton[0].getWidth()) {
				if (pointY > btnY && pointY < btnY + bmpButton[0].getHeight()) {
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
			if (pointX > btnX && pointX < btnX + bmpButton[0].getWidth()) {
				if (pointY > btnY && pointY < btnY + bmpButton[0].getHeight()) {
					//还原Button状态为未按下状态
					//isPress = false;
					//MySurfaceView.gameState = MySurfaceView.GAMEING;//改变当前游戏状态为开始游戏
				}
			}
		}
	}
}

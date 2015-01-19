package com.pg;

import java.util.Random;
import java.util.Vector;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class MySurfaceView extends SurfaceView implements Callback, Runnable {
	private SurfaceHolder sfh;
	private Paint paint;
	private Thread th;
	private boolean flag;
	private Canvas canvas;
	public static int screenW, screenH;
	//定义游戏状态常量
	public static final int GAME_MENU = 0;//游戏菜单
	public static final int GAMEING = 1;//游戏中
	public static final int GAME_WIN = 2;//游戏胜利[NO]
	public static final int GAME_LOST = 3;//游戏失败
	public static final int GAME_PAUSE = -1;//游戏菜单
	//当前游戏状态(默认初始在游戏菜单界面)
	public static int gameState = GAME_MENU;
	//声明一个Resources实例便于加载图片
	private Resources res = this.getResources();
	//声明游戏需要用到的图片资源(图片声明)
	private Bitmap bmpBackGround;//游戏背景
	private Bitmap bmpBoom;//爆炸效果
	private Bitmap bmpButton;//游戏开始按钮
	private Bitmap bmpButtonPress;//游戏开始按钮被点击
	private Bitmap bmpEnemyDuck;//怪物漂浮物
	private Bitmap bmpEnemyFly;//怪物章鱼怪
	private Bitmap bmpEnemyBoss;//BOSS
	private Bitmap bmpGameOver;//游戏失败背景
	private Bitmap bmpGameReStart;//重新开始
	private Bitmap bmpPlayer;//游戏主角
	private Bitmap bmpPlayerHp;//主角血量
	private Bitmap bmpMenu;//菜单背景
	public static Bitmap bmpBullet;//子弹
	public static Bitmap bmpEnemyBullet;//敌人子弹
	public static Bitmap bmpBossBullet;//Boss子弹
	//对象声明
	private GameMenu gameMenu;//声明一个菜单对象
	private GameBg backGround;//声明一个滚动游戏背景对象
	private Player player;//声明主角对象
	private Vector<Enemy> vcEnemy;//声明一个敌人容器
	private int count;//计数器
	//敌人数组：1和2表示敌人的种类
	//二维数组的每一维都是一组怪物
	private int enemyArray[][] = { { 1, 2,1 }, { 1, 1}, { 1, 3, 1, 2 }, { 1, 2 }, { 2, 3 }, { 3, 1, 3 }, { 2, 2 }, { 1, 2 }, { 2, 2 }, { 1, 3, 1, 1 }, { 2, 1 },
			{ 1, 3 }, { 2, 1 },{ 1, 3, 1, 1 },{ 3, 3, 3, 3 }};
	private int enemyArrayIndex;//当前取出一维数组的下标
	private Random random;//随人库，为创建的敌人赋予随即坐标
	private Vector<Bullet> vcBullet;//敌人子弹容器
	private int countEnemyBullet;//添加子弹的计数器
	private Vector<Bullet> vcBulletPlayer;//主角子弹容器
	private int countPlayerBullet;//添加子弹的计数器
	private Vector<Boom> vcBoom;//爆炸效果容器	
	private Control control;//控制手柄
	private GameLost gamelost;//游戏结束
	public MySurfaceView(Context context) {
		super(context);		
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint(Color.RED);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		//设置背景常亮
		this.setKeepScreenOn(true);
	}//初始化函数
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		screenW = this.getWidth();
		screenH = this.getHeight();
		initGame();
		flag = true;
		th = new Thread(this);//实例线程
		th.start();//启动线程
	}//SurfaceView视图创建，响应此函数
	private void initGame() {
		//放置游戏切入后台重新进入游戏时，游戏被重置!
		//当游戏状态处于菜单时，才会重置游戏
		if (gameState == GAME_MENU) {
			//加载游戏资源
			bmpBackGround = BitmapFactory.decodeResource(res, R.drawable.background);
			bmpBoom = BitmapFactory.decodeResource(res, R.drawable.boom);
			bmpButton = BitmapFactory.decodeResource(res, R.drawable.button);
			bmpButtonPress = BitmapFactory.decodeResource(res, R.drawable.button_press);
			bmpEnemyDuck = BitmapFactory.decodeResource(res, R.drawable.enemy_duck);
			bmpEnemyFly = BitmapFactory.decodeResource(res, R.drawable.enemy_fly);
			bmpEnemyBoss = BitmapFactory.decodeResource(res, R.drawable.enemy_pig);
			bmpGameOver = BitmapFactory.decodeResource(res, R.drawable.gameover);
			bmpGameReStart = BitmapFactory.decodeResource(res, R.drawable.restart);
			bmpPlayer = BitmapFactory.decodeResource(res, R.drawable.player);
			bmpPlayerHp = BitmapFactory.decodeResource(res, R.drawable.hp);
			bmpMenu = BitmapFactory.decodeResource(res, R.drawable.menu);
			bmpBullet = BitmapFactory.decodeResource(res,R.drawable.bullet);
			bmpEnemyBullet = BitmapFactory.decodeResource(res, R.drawable.bullet_enemy);
			
			vcBoom = new Vector<Boom>();//爆炸效果容器实例
			vcBullet = new Vector<Bullet>();//敌人子弹容器实例
			vcBulletPlayer = new Vector<Bullet>();//主角子弹容器实例
			gameMenu = new GameMenu(bmpMenu, bmpButton, bmpButtonPress);//菜1单类实例
			backGround = new GameBg(bmpBackGround);//实例游戏背景
			player = new Player(bmpPlayer, bmpPlayerHp);//实例主角
			vcEnemy = new Vector<Enemy>();//实例敌人容器
			random = new Random();//实例随机库
			control=new Control(screenW-35,screenH-35,10,20);//控制
			gamelost=new GameLost(bmpGameOver,bmpGameReStart);//游戏结束
		}
		control.reSet();//小圆归位
		Enemy.reset();//敌人重置
		Bullet.num=0;
	}//自定义初始化游戏
	public void myDraw() {
		try {
			canvas = sfh.lockCanvas();
			if (canvas != null) {
				canvas.drawColor(Color.WHITE);
				switch (gameState){//绘图函数根据游戏状态不同进行不同绘制
				case GAME_MENU://初始状态
					gameMenu.draw(canvas, paint);//菜单的绘图函数
					break;
				case GAMEING://游戏进行中
					backGround.draw(canvas, paint);//游戏背景
					player.draw(canvas, paint);//主角绘图函数
					for (int i=0;i<vcEnemy.size();i++) {//敌人绘制
						vcEnemy.elementAt(i).draw(canvas,paint);
					}
					for (int i=0;i<vcBullet.size();i++){//敌人子弹绘制
						vcBullet.elementAt(i).draw(canvas, paint);
					}
					for (int i=0; i<vcBulletPlayer.size();i++) {
						vcBulletPlayer.elementAt(i).draw(canvas, paint);
					}//处理主角子弹绘制
					for (int i=0; i<vcBoom.size();i++) {
						vcBoom.elementAt(i).draw(canvas, paint);
					}//爆炸效果绘制
					control.myDraw(canvas);//手柄绘制
					break;
				case GAME_PAUSE://游戏最后状态
					break;
				case GAME_LOST://游戏输掉贴图
					backGround.draw(canvas, paint);//游戏背景
					//player.draw(canvas, paint);//主角绘图函数
					for (int i=0;i<vcEnemy.size();i++) {//敌人绘制
						vcEnemy.elementAt(i).draw(canvas,paint);
					}
					for (int i=0;i<vcBullet.size();i++){//敌人子弹绘制
						vcBullet.elementAt(i).draw(canvas, paint);
					}
					for (int i=0; i<vcBulletPlayer.size();i++) {
						vcBulletPlayer.elementAt(i).draw(canvas, paint);
					}//处理主角子弹绘制
					for (int i=0; i<vcBoom.size();i++) {
						vcBoom.elementAt(i).draw(canvas, paint);
					}//爆炸效果绘制
					//control.myDraw(canvas);//手柄绘制
					gamelost.draw(canvas, paint);
					if(gameState==GAME_MENU){
						initGame();//重置游戏
						enemyArrayIndex = 0;//重置怪物出场
					}
					break;	
				}
			}
		} catch (Exception e){
			// TODO: handle exception
		} finally {
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas);
		}
	}//OnDraw绘图函数
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (gameState) {//触屏监听事件函数根据游戏状态不同进行不同监听
		case GAME_MENU://菜单的触屏事件处理
			gameMenu.onTouchEvent(event);
			break;
		case GAMEING://游戏进行中
			control.onTouchEvent(event,player);//手柄控制
			break;
		case GAME_PAUSE://游戏最后
			break;
		case GAME_WIN://胜利
			break;
		case GAME_LOST://输掉
			gamelost.onTouchEvent(event);
			if(gameState==GAME_MENU){
				initGame();//重置游戏
				enemyArrayIndex = 0;//重置怪物出场
			}
			break;
		}
		return true;
	}//触屏监听函数
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//处理back返回按键,重置游戏
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//游戏胜利、失败、进行时都默认返回菜单
			if (gameState == GAMEING || gameState == GAME_WIN || gameState == GAME_LOST) {
				gameState = GAME_MENU;
				initGame();//重置游戏
				enemyArrayIndex = 0;//重置怪物出场
			} else if (gameState == GAME_MENU) {//当前游戏状态在菜单界面，默认返回按键退出游戏
				MainActivity.instance.finish();
				System.exit(0);
			}
			//表示此按键已处理，不再交给系统处理，
			//从而避免游戏被切入后台
			return true;
		}
		//按键监听事件函数根据游戏状态不同进行不同监听
		switch (gameState) {
		case GAME_MENU:
			break;
		case GAMEING://进行中
			player.onKeyDown(keyCode,event);//主角的按键按下事件
			break;
		case GAME_PAUSE:
			break;
		case GAME_WIN:
			break;
		case GAME_LOST:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}//按键按下监听
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//处理back返回按键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//游戏胜利、失败、进行时都默认返回菜单
			if (gameState == GAMEING || gameState == GAME_WIN || gameState == GAME_LOST) {
				gameState = GAME_MENU;
			}
			//表示此按键已处理，不再交给系统处理，
			//从而避免游戏被切入后台
			return true;
		}
		//按键监听事件函数根据游戏状态不同进行不同监听
		switch (gameState) {
		case GAME_MENU:
			break;
		case GAMEING:
			//按键抬起事件
			player.onKeyUp(keyCode, event);
			break;
		case GAME_PAUSE:
			break;
		case GAME_WIN:
			break;
		case GAME_LOST:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}//按键抬起监听
	private void logic() {
		switch (gameState) {//逻辑处理根据游戏状态不同进行不同处理
		case GAME_MENU:
			break;
		case GAMEING:
			backGround.logic();//背景逻辑
			player.logic();//主角逻辑
			//begin-----敌人逻辑
			for (int i = 0; i < vcEnemy.size(); i++) {//敌人逻辑
				Enemy en = vcEnemy.elementAt(i);
				//因为容器不断添加敌人 ，那么对敌人isDead判定，
				//如果已死亡那么就从容器中删除,对容器起到了优化作用；
				if (en.isDead) {
					vcEnemy.removeElementAt(i);
				} else {
					en.logic();
				}
			}
			//生成敌人
			count++;
			if (count % Enemy.createEnemyTime == 0) {
				for (int i = 0; i < enemyArray[enemyArrayIndex].length; i++) {
					if (enemyArray[enemyArrayIndex][i] == 1){//章鱼怪
						int x = random.nextInt(screenW - 100) + 50;
						vcEnemy.addElement(new Enemy(bmpEnemyFly, 1, x, -50));
					} else if (enemyArray[enemyArrayIndex][i] == 2) {//漂浮物左
						int y = random.nextInt(20);
						vcEnemy.addElement(new Enemy(bmpEnemyDuck, 2, -50, y));
					} else if (enemyArray[enemyArrayIndex][i] == 3) {//漂浮物右
						int y = random.nextInt(20);
						vcEnemy.addElement(new Enemy(bmpEnemyDuck, 3, screenW + 50, y));
					} else if(enemyArray[enemyArrayIndex][i] == 4){//Boss
						vcEnemy.addElement(new Enemy(bmpEnemyBoss,4,-100,5));
					}
				}
				enemyArrayIndex=enemyArrayIndex+1;//15组出现效果....一轮过去提升难度
				if(enemyArrayIndex>=15){
					enemyArrayIndex=0;
					if(Enemy.createBulletTime>5 
							&& Enemy.createBulletTime>=Enemy.createEnemyTime)
						Enemy.createBulletTime-=5;
					else if(Enemy.createEnemyTime>5 
							&& Enemy.createBulletTime<=Enemy.createEnemyTime)
						Enemy.createEnemyTime-=5;
				}				
			}
			//处理敌人与主角的碰撞
			for (int i = 0; i < vcEnemy.size(); i++) {
				if (player.isCollsionWith(vcEnemy.elementAt(i))) {
					player.setPlayerHp(player.getPlayerHp() - 1);//发生碰撞，主角血量-1
					if (player.getPlayerHp() <= -1) {//当主角血量小于0，判定游戏失败
						gameState = GAME_LOST;
					}
				}
			}
			//每2秒添加一个敌人子弹
			countEnemyBullet++;
			if (countEnemyBullet % Enemy.createBulletTime == 0) {
				for (int i=0;i<vcEnemy.size();i++){
					Enemy en=vcEnemy.elementAt(i);
					int bulletType=0;
					switch(en.type){//不同类型敌人不同的子弹运行轨迹
					case Enemy.TYPE_FLY://章鱼怪
						bulletType = Bullet.BULLET_FLY;
						break;
					case Enemy.TYPE_DUCKL://漂浮物
					case Enemy.TYPE_DUCKR:
						bulletType = Bullet.BULLET_DUCK;
						break;
					case Enemy.TYPE_BOSS://boss的子弹
						bulletType = Bullet.BULLET_DUCK;//////，，，。，。，。，
						break;
					}
					vcBullet.add(new Bullet(bmpEnemyBullet, en.x + 10, en.y + 20, bulletType));
				}
			}
			for (int i = 0; i < vcBullet.size(); i++) {//处理敌人子弹逻辑
				Bullet b = vcBullet.elementAt(i);
				if (b.isDead) {
					vcBullet.removeElement(b);
				} else {
					b.logic(vcEnemy);
				}
			}
			for (int i = 0; i < vcBullet.size(); i++) {//处理敌人子弹与主角碰撞
				if (player.isCollsionWith(vcBullet.elementAt(i))) {
					player.setPlayerHp(player.getPlayerHp() - 1);//发生碰撞，主角血量-1
					if (player.getPlayerHp() <= -1) {
						gameState = GAME_LOST;
					}//当主角血量小于0，判定游戏失败
				}
			}
			for (int i = 0; i < vcBulletPlayer.size(); i++) {//处理主角子弹与敌人碰撞
				Bullet blPlayer = vcBulletPlayer.elementAt(i);//取出主角子弹容器的每个元素
				for (int j = 0; j < vcEnemy.size(); j++) {
					//添加爆炸效果
					if (vcEnemy.elementAt(j).isCollsionWith(blPlayer)) {//取出敌人容器的每个元与主角子弹遍历判断
						vcBoom.add(new Boom(bmpBoom, vcEnemy.elementAt(j).x, vcEnemy.elementAt(j).y, 7));
						switch(vcEnemy.elementAt(j).type){
						case 1://章鱼怪
							player.addPlayerGoals(20);
							break;
						case 2:;//漂浮物
						case 3:
							player.addPlayerGoals(10);
							break;
						case 4://Boss
							break;
						default:break;
						}
					}
				}
			}
			//-------------end-敌人逻辑
			//每1秒添加一个主角子弹
			countPlayerBullet++;
			if (countPlayerBullet % 20 == 0) {
				switch(player.bulletKind){//玩家武器选择
				case 0://单发子弹
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 20, Bullet.BULLET_PLAYER));
					break;
				case 1://双发子弹
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 10, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 20, player.y - 20, Bullet.BULLET_PLAYER));
					break;
				case 2://双发普通+1发跟踪
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 10, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 20, player.y - 20, Bullet.BULLET_PLAYER));
					if(Bullet.num==0)vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 3://三发子弹+1发跟踪
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 8, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 23, player.y - 20, Bullet.BULLET_PLAYER));
					if(Bullet.num==0)vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 4://3发+2发跟踪
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 8, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 23, player.y - 20, Bullet.BULLET_PLAYER));
					if(Bullet.num<2)vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 5://4发2跟踪
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 4, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 11, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 18, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 25, player.y - 20, Bullet.BULLET_PLAYER));
					if(Bullet.num<=2)vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 6://5发2跟踪
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 3, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 9, player.y - 22, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 21, player.y - 22, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 27, player.y - 20, Bullet.BULLET_PLAYER));
					if(Bullet.num<=2)vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 7://全跟踪
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 8://双全跟踪
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 10, player.y - 20, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 20, player.y - 20, Bullet.BULLET_PLAYER1));
				case 9://三个双跟踪
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 8, player.y - 20, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 23, player.y - 20, Bullet.BULLET_PLAYER1));
					break;
				case 10://4发+2全跟踪
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 4, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 11, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 18, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 25, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 4, player.y - 25, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 25, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 11://5发+2全跟踪
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 3, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 9, player.y - 22, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 21, player.y - 22, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 27, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 3, player.y - 25, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 27, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				default://5发全跟踪
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 3, player.y - 20, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 9, player.y - 22, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 21, player.y - 22, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 27, player.y - 20, Bullet.BULLET_PLAYER1));
					break;
				}				
			}
			//处理主角子弹逻辑
			for (int i = 0; i < vcBulletPlayer.size(); i++) {
				Bullet b = vcBulletPlayer.elementAt(i);
				if (b.isDead) {
					vcBulletPlayer.removeElement(b);
				} else {
					b.logic(vcEnemy);
				}
			}
			//爆炸效果逻辑
			for (int i = 0; i < vcBoom.size(); i++) {
				Boom boom = vcBoom.elementAt(i);
				if (boom.playEnd) {
					//播放完毕的从容器中删除
					vcBoom.removeElementAt(i);
				} else {
					vcBoom.elementAt(i).logic();
				}
			}
			break;
		case GAME_PAUSE:
			break;
		case GAME_WIN:
			break;
		case GAME_LOST:
			break;
		}
	}//游戏逻辑
	@Override
	public void run() {
		while (flag) {
			long start = System.currentTimeMillis();
			myDraw();
			logic();
			long end = System.currentTimeMillis();
			try {
				if (end - start < 50) {//时间均衡处理
					Thread.sleep(50 - (end - start));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}//run函数
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}//SurfaceView视图状态发生改变，响应此函数
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
	}//SurfaceView视图消亡时，响应此函数
}

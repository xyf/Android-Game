package com.example.birdfly;

import static com.example.birdfly.Constant.RATE;

import java.util.ArrayList;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class BirdSurfaceView extends SurfaceView implements Callback, Runnable, ContactListener {
	

	
	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint paint;
	
	private World world;
	private AABB world_aabb;
	private Vec2 gravity;
	private boolean isSleep;
	
	private float timeStep;
	private int iterations;
	private static int SCREEN_WIDTH, SCREEN_HEIGHT;
	
	public static boolean FLAG;
	
	private RectBody pipeDown1, pipeDown2;
	private RectBody pipeUp1, pipeUp2;
	
	private CircleBody circle;
	
	private RectBody bottom, left, top;

	private Vec2 position;
	private ArrayList<RectBody> array_rect;
	private ArrayList<CircleBody> array_circle;
	
	private final float gravity_y;
	private final float circle_v_y;

	
	private boolean pipe1C;
	private boolean pipe2C;
	
	private float EMPTY_LENGTH;
	
	private float PIPE_WIDTH, PIPE_HEIGHT;
	private float PIPE_HEIGHT_BASE;
	private float LEFT_HEIGHT;
	
	private int counter;
	private boolean pipe1Counter;
	private boolean pipe2Counter;
	private float pipe_v_y;
	private float min_pipe_height;
	
	private float GAME_OVER_X, GAME_OVER_Y, GAME_OVER_WIDTH, GAME_OVER_HEIGHT;
	float distance;
	
//	private boolean isAlive;
	private boolean isAgain;
	private boolean isRunning;
	private boolean game_over;
	public static boolean THREAD_FLAG;
	private boolean isFalling;
	
	public BirdSurfaceView(Context context)
	{
		super(context);
		
		gravity_y = 60.0f; 
		circle_v_y = -20.0f;
		pipe_v_y = -15.0f;
		
		holder = this.getHolder();
		holder.addCallback(this);
		
		world_aabb = new AABB();
		world_aabb.lowerBound.set(-100, -100);
		world_aabb.upperBound.set(100, 100);
		gravity = new Vec2(0, gravity_y);
		isSleep = true;
		
		world = new World(world_aabb, gravity, isSleep);
		world.setContactListener(this);
		
		timeStep = 1.0f / 60.0f;
		iterations = 10;
		paint = new Paint();
		array_rect = new ArrayList<RectBody>();
		array_circle = new ArrayList<CircleBody>();
		isAgain = false;
	
		init();
	}

	public void init()
	{
		FLAG = true;
		
		isRunning = false;
		pipe1C = false;
		pipe2C = false;
		counter = 0;
		pipe1Counter = true;
		pipe2Counter = true;
		min_pipe_height = 100;
		//isAlive = true;
		game_over = false;
		isFalling = false;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		if (FLAG) {
			
			SCREEN_WIDTH = this.getWidth();
			SCREEN_HEIGHT = this.getHeight();
		
			bottom = new RectBody(world, -SCREEN_WIDTH, SCREEN_HEIGHT - SCREEN_HEIGHT / 6, SCREEN_WIDTH * 3, SCREEN_HEIGHT / 6, Color.GRAY, true);
			bottom.createDefaultRectBody();
			array_rect.add(bottom);
			left = new RectBody(world, -SCREEN_WIDTH / 5, 0, 10, SCREEN_HEIGHT - bottom.HEIGHT, Color.RED, true);
			left.createDefaultRectBody();
			array_rect.add(left);
			top = new RectBody(world, -SCREEN_WIDTH, -10, SCREEN_WIDTH * 3, 10, Color.RED, true);
			top.createDefaultRectBody();
			array_rect.add(top);
			
			//创建pipe1
			LEFT_HEIGHT = SCREEN_HEIGHT - bottom.HEIGHT;
			EMPTY_LENGTH = LEFT_HEIGHT / 5;
			PIPE_WIDTH = SCREEN_WIDTH / 5;
			PIPE_HEIGHT_BASE = LEFT_HEIGHT * 3 / 5;
			
			GAME_OVER_X = SCREEN_WIDTH / 2;
			GAME_OVER_Y = LEFT_HEIGHT / 7 * 3;
			GAME_OVER_WIDTH = SCREEN_WIDTH / 5 * 3;
			GAME_OVER_HEIGHT = LEFT_HEIGHT / 4;
					
			pipeDown1 = new RectBody(world, SCREEN_WIDTH - 100, LEFT_HEIGHT * 2 / 3, SCREEN_WIDTH / 5, LEFT_HEIGHT / 3, Color.GREEN, false);
			pipeDown1.createDefaultRectBody();
			array_rect.add(pipeDown1);
			
			pipeUp1 = new RectBody(world, SCREEN_WIDTH - 100, 0, SCREEN_WIDTH / 5, LEFT_HEIGHT / 3, Color.GREEN, false);
			pipeUp1.createDefaultRectBody();
			array_rect.add(pipeUp1);
		
			//创建pipe2
			pipeDown2 = new RectBody(world, pipeDown1.X + pipeDown1.WIDTH + SCREEN_WIDTH / 2, LEFT_HEIGHT * 2 / 3, SCREEN_WIDTH / 5, LEFT_HEIGHT / 3, Color.GREEN, false);
			pipeDown2.createDefaultRectBody();
			array_rect.add(pipeDown2);
			
			pipeUp2 = new RectBody(world, pipeDown1.X + pipeDown1.WIDTH + SCREEN_WIDTH / 2, 0, SCREEN_WIDTH / 5, LEFT_HEIGHT / 3, Color.GREEN, false);
			pipeUp2.createDefaultRectBody();
			array_rect.add(pipeUp2);
			
			circle = new CircleBody(world, SCREEN_WIDTH / 3, SCREEN_HEIGHT / 2, 50, Color.BLACK, false);
		
			circle.createDefaultCirlceBody();
			array_circle.add(circle);
			
		}
		
		THREAD_FLAG = true;
		new Thread(this).start();
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	
		FLAG = false;
		THREAD_FLAG = false;
		
	}

	public PolygonDef createRectShape(RectBody rect, float width, float height)
	{
		rect.HEIGHT = height;
		rect.WIDTH = width;
		
		PolygonDef shape = new PolygonDef();
		
		shape.density = 1.0f;
		shape.friction = 0.0f;
		shape.restitution = 0.0f;
		shape.setAsBox(width/2/RATE, height/2/RATE);
		rect.body.createShape(shape);
		rect.body.setMassFromShapes();
		
		return shape;
	}
	
	public void logic()
	{
		pipeUp1.body.applyForce(new Vec2(0, -pipeUp1.body.getMass() * gravity_y * 10), pipeUp1.body.getWorldCenter());
		pipeUp2.body.applyForce(new Vec2(0, -pipeUp2.body.getMass() * gravity_y * 10), pipeUp2.body.getWorldCenter());
		world.step(timeStep, iterations);
		
		if (isAgain)
		{
			PIPE_HEIGHT = (float)(Math.random() * PIPE_HEIGHT_BASE) + min_pipe_height;
			pipeUp1.body.destroyShape(pipeUp1.body.getShapeList());
			createRectShape(pipeUp1, PIPE_WIDTH, PIPE_HEIGHT);
			pipeUp1.body.applyForce(new Vec2(0, - pipeUp1.body.getMass() * gravity_y), pipeUp1.body.getWorldCenter());
			pipeDown1.body.destroyShape(pipeDown1.body.getShapeList());
			createRectShape(pipeDown1, PIPE_WIDTH, LEFT_HEIGHT - PIPE_HEIGHT - EMPTY_LENGTH);
			
			pipeUp1.body.setXForm(new Vec2((SCREEN_WIDTH + PIPE_WIDTH / 2) / RATE, (0 + pipeUp1.HEIGHT / 2) / RATE), 0);
			pipeDown1.body.setXForm(new Vec2((SCREEN_WIDTH + PIPE_WIDTH / 2)/ RATE, (pipeUp1.HEIGHT + EMPTY_LENGTH + pipeDown1.HEIGHT / 2)/ RATE), 0);
			
			PIPE_HEIGHT = (float)(Math.random() * PIPE_HEIGHT_BASE) + min_pipe_height;
			
			pipeUp2.body.destroyShape(pipeUp2.body.getShapeList());
			createRectShape(pipeUp2, PIPE_WIDTH, PIPE_HEIGHT);
			pipeUp2.body.applyForce(new Vec2(0, -pipeUp2.body.getMass() * gravity_y), pipeUp2.body.getWorldCenter());
			pipeDown2.body.destroyShape(pipeDown2.body.getShapeList());
			createRectShape(pipeDown2, PIPE_WIDTH, LEFT_HEIGHT - PIPE_HEIGHT - EMPTY_LENGTH);
			
			pipeUp2.body.setXForm(new Vec2((SCREEN_WIDTH + SCREEN_WIDTH / 2 + PIPE_WIDTH + PIPE_WIDTH / 2) / RATE, (0 + pipeUp2.HEIGHT / 2) / RATE), 0);
			pipeDown2.body.setXForm(new Vec2((SCREEN_WIDTH + SCREEN_WIDTH / 2 + PIPE_WIDTH + PIPE_WIDTH / 2) / RATE, (pipeUp2.HEIGHT + EMPTY_LENGTH + pipeDown2.HEIGHT / 2) / RATE), 0);
			
			
			circle.body.setXForm(new Vec2((SCREEN_WIDTH / 3)/RATE, (SCREEN_HEIGHT / 2) / RATE), 0);  //要先设置好管道的位置之后再设置再设置小球的位置，如果小球的位置设置放在管道的前面会导致先前的管道与小球产生碰撞，从而使小球结束运动。
			isAgain = false;
			
			
		}
		else {
			
			if (pipeUp1.body.getPosition().x * RATE - circle.body.getPosition().x * RATE <= 0 && pipe1Counter )
			{
				counter++;
				pipe1Counter = false;
				
			}
			
			if (pipeUp2.body.getPosition().x * RATE - circle.body.getPosition().x * RATE <= 0 && pipe2Counter )
			{
				counter++;
				pipe2Counter = false;
				
			}
			
			if (pipe1C)
			{
			
				PIPE_HEIGHT = (float)(Math.random() * PIPE_HEIGHT_BASE) + min_pipe_height;
				Log.i("test", " "+PIPE_HEIGHT);
				
				pipeUp1.body.destroyShape(pipeUp1.body.getShapeList());
				createRectShape(pipeUp1, PIPE_WIDTH, PIPE_HEIGHT);
				pipeUp1.body.applyForce(new Vec2(0, - pipeUp1.body.getMass() * gravity_y), pipeUp1.body.getWorldCenter());
				
				pipeDown1.body.destroyShape(pipeDown1.body.getShapeList());
				createRectShape(pipeDown1, PIPE_WIDTH, LEFT_HEIGHT - PIPE_HEIGHT - EMPTY_LENGTH);
				
				pipeUp1.body.setXForm(new Vec2((SCREEN_WIDTH + pipeDown2.WIDTH + pipeDown1.WIDTH / 2) / RATE, (0 + pipeUp1.HEIGHT / 2) / RATE), 0);
				pipeDown1.body.setXForm(new Vec2((SCREEN_WIDTH + pipeDown2.WIDTH +pipeDown1.WIDTH / 2)/ RATE, (pipeUp1.HEIGHT + EMPTY_LENGTH + pipeDown1.HEIGHT / 2)/ RATE), 0);
		
				pipeDown1.body.setLinearVelocity(new Vec2(pipe_v_y, 0));
				pipeUp1.body.setLinearVelocity(new Vec2(pipe_v_y, 0));
				
				pipe1C = false;
				pipe1Counter = true;
				
			}
			
			if (pipe2C)
			{
				 
				PIPE_HEIGHT = (float)(Math.random() * PIPE_HEIGHT_BASE) + min_pipe_height;
				Log.i("test", " "+PIPE_HEIGHT);
				
				pipeUp2.body.destroyShape(pipeUp2.body.getShapeList());
				createRectShape(pipeUp2, PIPE_WIDTH, PIPE_HEIGHT);
				pipeUp2.body.applyForce(new Vec2(0, -pipeUp2.body.getMass() * gravity_y), pipeUp2.body.getWorldCenter());
				
				pipeDown2.body.destroyShape(pipeDown2.body.getShapeList());
				createRectShape(pipeDown2, PIPE_WIDTH, LEFT_HEIGHT - PIPE_HEIGHT - EMPTY_LENGTH);
				
				pipeUp2.body.setXForm(new Vec2((SCREEN_WIDTH + pipeDown1.WIDTH + pipeDown2.WIDTH / 2) / RATE, (0 + pipeUp2.HEIGHT / 2) / RATE), 0);
				pipeDown2.body.setXForm(new Vec2((SCREEN_WIDTH + pipeDown1.WIDTH +pipeDown2.WIDTH / 2)/ RATE, (pipeUp2.HEIGHT + EMPTY_LENGTH + pipeDown2.HEIGHT / 2)/ RATE), 0);
			
				pipeDown2.body.setLinearVelocity(new Vec2(pipe_v_y, 0));
				pipeUp2.body.setLinearVelocity(new Vec2(pipe_v_y, 0));
				
				pipe2C = false;
				pipe2Counter = true;
				
			}
			
		}
		for (RectBody rect:array_rect)
		{
			position = rect.body.getPosition();
			rect.X = position.x * RATE - rect.WIDTH / 2;
			rect.Y = position.y * RATE - rect.HEIGHT / 2;
	
		}
		
		for (CircleBody circle:array_circle)
		{
			position = circle.body.getPosition();
			circle.X = position.x * RATE;
			circle.Y = position.y * RATE;
		}
		
	}
	
	public void drawShapes()
	{
		try 
		{
			canvas = holder.lockCanvas();
			if (canvas != null)
			{
				canvas.drawColor(Color.WHITE);
				for (RectBody rect:array_rect)
				{
					rect.drawSelf(canvas, paint);
				}
				
				for (CircleBody circle:array_circle)
				{
					circle.drawSelf(canvas, paint);
				}
				
				drawScores(canvas, paint, counter);
				
				if (game_over)
				{
					drawGameOver(canvas, paint);
				}
			
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
		}
		finally {
			if (canvas != null)
			{
				holder.unlockCanvasAndPost(canvas);
			}
		}
		
	}
	
	@Override
	public void run() {
		
		while (THREAD_FLAG)
		{
			try {

				Log.i("test2", "this is run!");
				
				Thread.sleep((long)timeStep * 1000);
				
				logic();
				drawShapes();      //这是个注意点 注意是模拟完之后绘图还是 绘图之后模拟这个在时间上有误差，先模拟后绘图
				
				if (game_over)
				{
					THREAD_FLAG = false;
				}

			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
		}
		
		Log.i("button", "over!");
	}
	
	public void drawGameOver(Canvas canvas, Paint paint)
	{
		paint.setColor(Color.RED);
		canvas.drawRect(GAME_OVER_X - GAME_OVER_WIDTH / 2, GAME_OVER_Y - GAME_OVER_HEIGHT / 2, GAME_OVER_X + GAME_OVER_WIDTH / 2, GAME_OVER_Y + GAME_OVER_HEIGHT / 2, paint);
		paint.setColor(Color.YELLOW);
		RectF rectF = new RectF(GAME_OVER_X - GAME_OVER_WIDTH / 2, GAME_OVER_Y - GAME_OVER_HEIGHT / 2, GAME_OVER_X + GAME_OVER_WIDTH / 2, GAME_OVER_Y + GAME_OVER_HEIGHT / 2);
		canvas.drawRoundRect(rectF, 50.0f, 50.0f, paint);
		
		paint.reset();
		paint.setColor(Color.RED);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(GAME_OVER_HEIGHT / 4);
		canvas.drawText("GAME OVER", GAME_OVER_X, GAME_OVER_Y - GAME_OVER_HEIGHT / 4, paint);
		paint.setColor(Color.BLACK);
		paint.setTextSize(GAME_OVER_HEIGHT / 6);
		
		canvas.drawText("分    数：  "+counter,  GAME_OVER_X, GAME_OVER_Y, paint);
		canvas.drawText("最高分：  "+counter, GAME_OVER_X, GAME_OVER_Y + GAME_OVER_HEIGHT / 4, paint);
		
		distance = GAME_OVER_HEIGHT / 5;
		paint.setColor(Color.CYAN);
		rectF = new RectF(GAME_OVER_X - GAME_OVER_WIDTH / 8 * 3, GAME_OVER_Y + GAME_OVER_HEIGHT /2 + distance, GAME_OVER_X - GAME_OVER_WIDTH / 8 * 3 + GAME_OVER_WIDTH / 4, distance + GAME_OVER_Y + GAME_OVER_HEIGHT / 2 + GAME_OVER_WIDTH / 4);
		canvas.drawRoundRect(rectF, 20.0f, 20.0f, paint);
		paint.reset();
		paint.setColor(Color.BLACK);
		paint.setTextSize(GAME_OVER_X / 8);
		paint.setTextAlign(Align.CENTER);

		canvas.drawText("重来", GAME_OVER_X - GAME_OVER_WIDTH / 8 * 3 + GAME_OVER_WIDTH / 8, GAME_OVER_Y + GAME_OVER_HEIGHT / 2 + distance + GAME_OVER_WIDTH / 8, paint);
		paint.reset();
		
		
		paint.setColor(Color.CYAN);
		rectF = new RectF(GAME_OVER_X + GAME_OVER_WIDTH / 8, GAME_OVER_Y + GAME_OVER_HEIGHT / 2 + distance, GAME_OVER_X + GAME_OVER_WIDTH / 8 + GAME_OVER_WIDTH / 4, distance + GAME_OVER_Y + GAME_OVER_HEIGHT / 2 + GAME_OVER_WIDTH / 4);
		canvas.drawRoundRect(rectF, 20.0f, 20.0f, paint);
		paint.reset();
		paint.setColor(Color.BLACK);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(GAME_OVER_X / 8);
		
		canvas.drawText("退出", GAME_OVER_X + GAME_OVER_WIDTH / 8 + GAME_OVER_WIDTH / 8, GAME_OVER_Y + GAME_OVER_HEIGHT /2 + distance + GAME_OVER_WIDTH / 8, paint);
		paint.reset();
		
	}

	public void button(MotionEvent event)
	{
		 
		if (event.getY() >= GAME_OVER_Y + GAME_OVER_HEIGHT /2 + distance && event.getY() <= distance + GAME_OVER_Y + GAME_OVER_HEIGHT / 2 + GAME_OVER_WIDTH / 4 )
		{
			if (event.getX() >= GAME_OVER_X - GAME_OVER_WIDTH / 8 * 3 && event.getX() <= GAME_OVER_X - GAME_OVER_WIDTH / 8 * 3 + GAME_OVER_WIDTH / 4) {
				
				begin_again();
				
				THREAD_FLAG = true;
				new Thread(this).start();
				
			
			}
			if (event.getX() >= GAME_OVER_X + GAME_OVER_WIDTH / 8 && event.getX() <= GAME_OVER_X + GAME_OVER_WIDTH / 8 +GAME_OVER_WIDTH / 4)
			{
				Log.i("button", "this is 2!");
				
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		
		}

	}

	public void begin_again()
	{
		isAgain = true;
		for (RectBody rect:array_rect)
		{
			rect.body.setLinearVelocity(new Vec2(0, 0));
			
		}
		
		init();
		


	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		if (event.getAction() == MotionEvent.ACTION_DOWN && game_over)
		{
			button(event);
		}
		else
		{
			if (!isFalling) {
				
				if (event.getAction() == MotionEvent.ACTION_DOWN && !isRunning)
				{		
						pipeUp1.body.applyForce(new Vec2(0, 0), pipeUp1.body.getWorldCenter());
						pipeUp1.body.setLinearVelocity(new Vec2(pipe_v_y, 0));
						
						pipeDown1.body.applyForce(new Vec2(0, 0), pipeDown1.body.getWorldCenter());
						pipeDown1.body.setLinearVelocity(new Vec2(pipe_v_y, 0));
					
						pipeDown2.body.applyForce(new Vec2(0, 0), pipeDown2.body.getWorldCenter());
						pipeDown2.body.setLinearVelocity(new Vec2(pipe_v_y, 0));
						
						pipeUp2.body.applyForce(new Vec2(0, 0), pipeUp2.body.getWorldCenter());
						pipeUp2.body.setLinearVelocity(new Vec2(pipe_v_y, 0));
						isRunning = true;
					}
					
					if (event.getAction() == MotionEvent.ACTION_DOWN && isRunning)
					{
						circle.body.applyForce(new Vec2(0,0), circle.body.getWorldCenter());
						circle.body.setLinearVelocity(new Vec2(0, circle_v_y));
					}		
			}
			
		}
		return true;
	}
	
	@Override
	public void add(ContactPoint point) {
	
		Body body1 = point.shape1.getBody();
		Body body2 = point.shape2.getBody();
		
		if (CollisionTest.isCircleToRect(body1 , body2, circle, bottom))
		{
			Log.i("test1", "this is circle to bottom!");
			circle.body.setLinearVelocity(new Vec2(0, 0));
			THREAD_FLAG = false;
			game_over = true;
			
		}
		 
			
		if (CollisionTest.isCircleToRect(body1, body2, circle, pipeDown1) || 
				CollisionTest.isCircleToRect(body1, body2, circle, pipeDown2))
		{
			circle.body.setLinearVelocity(new Vec2(0,0));
			pipeDown1.body.setLinearVelocity(new Vec2(0, 0));
			pipeUp1.body.setLinearVelocity(new Vec2(0, 0));
			pipeDown2.body.setLinearVelocity(new Vec2(0, 0));
			pipeUp2.body.setLinearVelocity(new Vec2(0, 0));
				
			isFalling = true;
			
			Log.i("test3", "this is test3");
			if (pipeDown1.Y - circle.Y >= circle.radius || pipeDown2.Y - circle.Y >= circle.radius) {
				
				THREAD_FLAG = false;
				game_over = true;
					
			}
				
		}
			
		if (CollisionTest.isCircleToRect(body1, body2, circle, pipeUp1) ||
				CollisionTest.isCircleToRect(body1, body2, circle, pipeUp2))
		{
			circle.body.setLinearVelocity(new Vec2(0,0));
			pipeDown1.body.setLinearVelocity(new Vec2(0, 0));
			pipeUp1.body.setLinearVelocity(new Vec2(0, 0));
			pipeDown2.body.setLinearVelocity(new Vec2(0, 0));
			pipeUp2.body.setLinearVelocity(new Vec2(0, 0));
		
			isFalling = true;
				
		}
		
		if (CollisionTest.isRectToRect(body1, body2, pipeDown1, left))
		{
			pipe1C = true;
		}
		
		if (CollisionTest.isRectToRect(body1, body2, pipeDown2, left))
		{
			pipe2C = true;
		}
	}

	@Override
	public void persist(ContactPoint point) {

	}

	@Override
	public void remove(ContactPoint point) {
		
		
	}

	@Override
	public void result(ContactResult point) {
	
		
	}

	public void drawScores(Canvas canvas, Paint paint, int score)
	{
		paint.setColor(Color.BLUE);
		paint.setAntiAlias(true);
		paint.setTextSize(80);
		paint.setTextAlign(Align.CENTER);
		canvas.drawText(""+score, SCREEN_WIDTH / 2, LEFT_HEIGHT / 5, paint);
		paint.reset();
		
	}
	
	
	

}

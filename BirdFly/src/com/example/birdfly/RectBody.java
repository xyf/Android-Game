package com.example.birdfly;

import static com.example.birdfly.Constant.RATE;

import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import android.graphics.Canvas;
import android.graphics.Paint;


public class RectBody {
	
	public Body body;
	
	public float X, Y, WIDTH, HEIGHT;
	public boolean isStatic;
	private World world;
	private int color;
	
	//Default Value;
	private final static float DefaultDensity = 1.0f;   //default density
	private final static float DefaultFriction = 0.0f;  //default friction
	private final static float DefaultRestitution = 0.0f; //default restitution
	private final static boolean isRect = true;
	
	public RectBody(World world, float x, float y, float width, float height, int color, boolean isStatic)
	{
		this.X = x;
		this.Y = y;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.color = color;
		
		this.world = world;
		this.isStatic = isStatic;
		
	
	}
	
	public void createDefaultRectBody()
	{
		PolygonDef rect_shape = new PolygonDef();
		
		if (isStatic)
		{
			rect_shape.density = 0;
		}
		else
		{
			rect_shape.density = DefaultDensity;
		}
		
		rect_shape.friction = DefaultFriction;
		rect_shape.restitution = DefaultRestitution;
		rect_shape.setAsBox(WIDTH/2/RATE, HEIGHT/2/RATE);

		BodyDef body_def = new BodyDef();
		body_def.position.set((X + WIDTH / 2) / RATE, (Y + HEIGHT / 2) / RATE);
		
	    body = world.createBody(body_def);
		body.createShape(rect_shape);
		//body.m_userData = this;    //สนำร world.getBodyCount(); world.getBodyList();
		body.setMassFromShapes();
		
	}
	
	public void createRectBody(float density, float friction, float restitution) {
		
		PolygonDef rect_shape = new PolygonDef();
		if (isStatic)
		{
			rect_shape.density = 0;
		}
		else
		{
			rect_shape.density = density;
		}
		rect_shape.friction = friction;
		rect_shape.restitution = restitution;
		rect_shape.setAsBox(WIDTH / 2 / RATE, HEIGHT / 2 / RATE);
		
		BodyDef body_def = new BodyDef();
		body_def.position.set((X + WIDTH / 2) / RATE, (Y + HEIGHT / 2) / RATE);
		
		body = world.createBody(body_def);
		body.createShape(rect_shape);
		body.setMassFromShapes();
		
	}
	
	public void drawSelf(Canvas canvas, Paint paint)
	{
		paint.setColor(color);
		paint.setAntiAlias(true);
		canvas.drawRect(X, Y, X + WIDTH, Y + HEIGHT, paint); 
		paint.reset();
		
	}

}

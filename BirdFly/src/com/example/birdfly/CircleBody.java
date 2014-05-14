package com.example.birdfly;

import static com.example.birdfly.Constant.RATE;

import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CircleBody {
	
	public float X, Y, radius;
	private World world;
	private boolean isStatic;
	private int color;
	public Body body;
	
	//Defautl Value
	private final float DefaultDensity;
	private final float DefaultFriction;
	private final float DefaultRestitution;
	
	
	public CircleBody(World world, float x, float y, float radius, int color, boolean isStatic)
	{
		this.world = world;
		this.X = x;
		this.Y = y;
		this.radius = radius;
		this.color = color;
		this.isStatic = isStatic;
		DefaultDensity = 1;
		DefaultFriction = 0.5f;
		DefaultRestitution = 0.0f;
	}
	
	public void createDefaultCirlceBody()
	{
		CircleDef circle_shape = new CircleDef();
		if (isStatic)
		{
			circle_shape.density = 0;
		}
		else
		{
			circle_shape.density = DefaultDensity;
		}
		circle_shape.friction = DefaultFriction;
		circle_shape.restitution = DefaultRestitution;
		circle_shape.radius = radius / RATE;
		
		BodyDef body_def = new BodyDef();
		body_def.position.set(X/RATE, Y/RATE);
		
		body = world.createBody(body_def);
		body.createShape(circle_shape);
		body.setMassFromShapes();
		
	}
	
	public void createCircleBody(float density, float friction, float restitution)
	{
		CircleDef circle_shape = new CircleDef();
		if (isStatic)
		{
			circle_shape.density = 0;
		}
		else
		{
			circle_shape.density = density;
		}
		circle_shape.friction = friction;
		circle_shape.restitution = restitution;
		circle_shape.radius = radius / RATE;
		
		BodyDef body_def = new BodyDef();
		body_def.position.set(X/RATE, Y/RATE);
		
		body = world.createBody(body_def);
		body.createShape(circle_shape);
		body.setMassFromShapes();
	}
	
	public void drawSelf(Canvas canvas, Paint paint)
	{
		paint.setColor(color);
		canvas.drawCircle(X, Y, radius, paint);
		paint.reset();
	}
	

}

package com.example.birdfly;

import java.util.ArrayList;

import org.jbox2d.dynamics.Body;

public class CollisionTest {

	//Test circle and rectangle is collisioned
	public static boolean isCircleToRect(Body body1, Body body2, CircleBody circle, RectBody rect)
	{
		if (body1 == circle.body || body2 == circle.body) {
			
			if (body1 == rect.body || body2 == rect.body)
			{
				return true;
				
			}
			
		}
		
		return false;
		
	}
	
	//Test rectangle and rectangle is collisioned
	public static boolean isRectToRect(Body body1, Body body2, RectBody rect1, RectBody rect2)
	{
		if (body1 == rect1.body || body2 == rect1.body)
		{
			if (body1 == rect2.body || body2 == rect2.body)
			{
				return true;
			}
			
		}
		return false;
		 
	}
	
}

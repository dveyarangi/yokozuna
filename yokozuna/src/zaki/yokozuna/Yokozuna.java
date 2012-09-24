package zaki.yokozuna;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;

import com.badlogic.gdx.physics.box2d.Body;
 
public class Yokozuna extends Entity
{
	
	public static final float CHARGE_FORCE = 1000; 
	
	private Body bellyBody;
	private Body [] bodies;
	private Sprite shape;

	
	public Yokozuna(Body bellyBody, Body leftBody, Body rightBody, Sprite shape)
	{
		this.bellyBody = bellyBody;
		this.bodies = new Body [2];
		bodies[0] = leftBody;
		bodies[1] = rightBody;
		this.shape = shape;
	}
	
	
	
	public Body getBelly() { return bellyBody; }
	public Body [] getBodies() { return bodies; }
	
	public Sprite getSprite() { return shape; }

}

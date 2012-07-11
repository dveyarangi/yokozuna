package zaki.yokozuna;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

import com.badlogic.gdx.physics.box2d.Body;
 
public class Yokozuna extends Entity
{
	
	private Body body;
	private Sprite shape;
	
	public Yokozuna(Body body, Sprite shape)
	{
		this.body = body;
		this.shape = shape;
	}
	
	public Body getBody() { return body; }
	
	public Sprite getSprite() { return shape; }

}

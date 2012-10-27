package zaki.yokozuna;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class StupidComputerControl extends IControlStrategy
{
	
	private final Yokozuna mEnemy;

	public StupidComputerControl(Yokozuna pEntity, Yokozuna pEnemy)
	{
		super( pEntity );

		mEnemy = pEnemy;
	}

	@Override
	public void onUpdate(float pSecondsElapsed)
	{
		Body fatBody = getEntity().getBelly();
		Vector2 chargeVec = Vector2Pool.obtain( 
				mEnemy.getBelly().getPosition().x - fatBody.getPosition().x, 
				mEnemy.getBelly().getPosition().y - fatBody.getPosition().y );
		chargeVec.mul( Yokozuna.CHARGE_FORCE*4 / chargeVec.len() * pSecondsElapsed);
		fatBody.applyForce( chargeVec, fatBody.getPosition() );
//		Debug.d( "at StupidComputerControl: charge to: " + chargeVec);
		Vector2Pool.recycle( chargeVec );
	}

	@Override
	public void onControlEvent(final TouchEvent pSceneTouchEvent)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

}

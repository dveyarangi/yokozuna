package zaki.yokozuna;


import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;

import zaki.yokozuna.controls.BaseOnScreenControl;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class StupidHumanControl extends IControlStrategy
{
	
	private Vector2 leftChargeVec, rightChargeVec;

	public StupidHumanControl(Yokozuna pEntity)
	{
		super( pEntity );
		
		leftChargeVec = Vector2Pool.obtain( 0, 0 );
		rightChargeVec = Vector2Pool.obtain( 0, 0 );
	}

	@Override
	public void onUpdate(float pSecondsElapsed)
	{
		
	}
	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onControlEvent(final TouchEvent pSceneTouchEvent)
	{
/*		Body fatBody = getEntity().getBelly();
		Vector2 chargeVec = Vector2Pool.obtain( 
				pSceneTouchEvent.getX()/PIXEL_TO_METER_RATIO_DEFAULT - fatBody.getPosition().x, 
				pSceneTouchEvent.getY()/PIXEL_TO_METER_RATIO_DEFAULT - fatBody.getPosition().y );
		chargeVec.mul( Yokozuna.CHARGE_FORCE / chargeVec.len());
		fatBody.applyForce( chargeVec, fatBody.getPosition() );
		Vector2Pool.recycle( chargeVec );
		Debug.d( "touch: " + pSceneTouchEvent.getX() + "," + pSceneTouchEvent.getY() + "; yokozuna: " + fatBody.getPosition() + "; charge: " + chargeVec);
	*/		
	}

	public void leftControlUpdate(BaseOnScreenControl pBaseOnScreenControl, float pValueX, float pValueY)
	{
		if(pValueX == 0 && pValueY == 0)
			return;
		Body fatBody = getEntity().getBodies()[0];
		
		leftChargeVec.set( pValueX, pValueY );
		leftChargeVec.mul( Yokozuna.CHARGE_FORCE / leftChargeVec.len());
		fatBody.applyForce( leftChargeVec, fatBody.getPosition() );
//		Vector2Pool.recycle( rightChargeVec );
//		Debug.d( "left: " + pValueX + "," + pValueX + "; yokozuna: " + fatBody.getPosition() + "; charge: " + leftChargeVec);

	}

	public void rightControlUpdate(BaseOnScreenControl pBaseOnScreenControl, float pValueX, float pValueY)
	{
		if(pValueX == 0 && pValueY == 0)
			return;
		Body fatBody = getEntity().getBodies()[1];
		rightChargeVec.set( pValueX, pValueY );
		rightChargeVec.mul( Yokozuna.CHARGE_FORCE / rightChargeVec.len());
		fatBody.applyForce( rightChargeVec, fatBody.getPosition() );
//		Vector2Pool.recycle( chargeVec );
//		Debug.d( "right: " + pValueX + "," + pValueX + "; yokozuna: " + fatBody.getPosition() + "; charge: " + rightChargeVec);
	}
	
	public Vector2 getLeftChargeVector() { return leftChargeVec; }
	public Vector2 getRightChargeVector() { return rightChargeVec; }

}

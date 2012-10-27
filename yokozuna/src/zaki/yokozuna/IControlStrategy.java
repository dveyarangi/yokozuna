package zaki.yokozuna;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.input.touch.TouchEvent;

public abstract class IControlStrategy implements IUpdateHandler
{
	
	protected Yokozuna mEntity;

	public IControlStrategy(Yokozuna pEntity)
	{
		mEntity = pEntity;
	}
	
	@Override
	public abstract void onUpdate(float pSecondsElapsed);

	public abstract void onControlEvent(final TouchEvent pSceneTouchEvent);

	@Override
	public abstract void reset();
	
	protected final Yokozuna getEntity() { return mEntity; } 
}

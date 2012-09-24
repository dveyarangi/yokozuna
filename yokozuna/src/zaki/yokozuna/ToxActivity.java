package zaki.yokozuna;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouchController;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import zaki.yokozuna.controls.AnalogOnScreenControl;
import zaki.yokozuna.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import zaki.yokozuna.controls.BaseOnScreenControl;
import android.view.Display;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 18:47:08 - 19.03.2010
 */
public class ToxActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private Scene mScene;

	protected ITiledTextureRegion mBrickTextureRegion1;
//	protected ITiledTextureRegion mCircleFaceTextureRegion;
//	protected TiledTextureRegion mTriangleFaceTextureRegion;
//	protected TiledTextureRegion mHexagonFaceTextureRegion;
	protected PhysicsWorld mPhysicsWorld;
	
	private ToxFactory factory = new ToxFactory();
	
	private Yokozuna myYokozuna;
	private StupidHumanControl myControl;
	
	private Line leftChargeLine;
	private Line rightChargeLine;
	
	private AnalogOnScreenControl leftControl, rightControl;
	
	private Yokozuna hisYokozuna;
	private IControlStrategy hisControl;
	
	int cameraWidth, cameraHeight;
	Camera mCamera = null;
	

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
//		Toast.makeText(this, "Touch the screen to add objects.", Toast.LENGTH_LONG).show();
		final Display display = getWindowManager().getDefaultDisplay();
	    cameraWidth = display.getWidth();
	    cameraHeight = display.getHeight();
		mCamera = new Camera(0, 0, cameraWidth, cameraHeight);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(cameraWidth, cameraHeight), mCamera);
	}

	@Override
	public void onCreateResources() {
		
		factory.init( this );
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		mEngine.setTouchController(new MultiTouchController());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));
		this.mScene.setOnSceneTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), true);

		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		float RING_RADIUS = Math.min( cameraWidth, cameraHeight );
		float wallx = RING_RADIUS, wally = 0;
		float wallStep = (float)(Math.PI/10.);
		 
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
/*		int cornersNum = (int)(2*Math.PI/wallStep);
		Vector2 [] wallCorners = new Vector2 [2];
		for(float a = 0; a < 2*Math.PI; a += wallStep) {
			float nextWallx = (float)(RING_RADIUS * Math.cos( a )) + cameraWidth/2;
			float nextWally = (float)(RING_RADIUS * Math.sin( a )) + cameraHeight/2;
			wallCorners[0] = Vector2Pool.obtain(nextWallx, nextWally);
			wallCorners[1] = Vector2Pool.obtain(wallx, wally);
			PolygonShape wall = new PolygonShape();
			wall.set(wallCorners);
//			final Rectangle wall = new Rectangle(wallx, wally, nextWallx, nextWally, vertexBufferObjectManager);
			PhysicsFactory.createBoxBody(this.mPhysicsWorld, wall, BodyType.StaticBody, wallFixtureDef);
			this.mScene.attachChild(wall);
			
			
		}*/
		final Rectangle ground = new Rectangle(0, cameraHeight - 200, cameraWidth,                  2, vertexBufferObjectManager);
		final Rectangle roof   = new Rectangle(0,                200, cameraWidth,                  2, vertexBufferObjectManager);
		final Rectangle left   = new Rectangle(0,                200,           2, cameraHeight - 400, vertexBufferObjectManager);
		final Rectangle right  = new Rectangle(cameraWidth-2,    200,           2, cameraHeight - 400, vertexBufferObjectManager);

		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
		myYokozuna = factory.createYokozuna( cameraWidth/2-50, cameraHeight/2,  3);
		myControl = new StupidHumanControl(myYokozuna);
		myYokozuna.registerUpdateHandler(myControl);
		myYokozuna.setIgnoreUpdate(false);
		mScene.attachChild( myYokozuna );
		
		Vector2 leftArm = myYokozuna.getBodies()[0].getWorldCenter();
		leftChargeLine = new Line( leftArm.x, leftArm.y, leftArm.x, leftArm.y, vertexBufferObjectManager );
		Vector2 rightArm = myYokozuna.getBodies()[1].getWorldCenter();
		rightChargeLine = new Line( rightArm.x, rightArm.y, rightArm.x, rightArm.y, vertexBufferObjectManager );
		
		mScene.attachChild( leftChargeLine );
		mScene.attachChild( rightChargeLine );
		
		
		mScene.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed)
			{
				Vector2 leftArm = myYokozuna.getBodies()[0].getWorldCenter();
				leftChargeLine.setPosition( leftArm.x * PIXEL_TO_METER_RATIO_DEFAULT, leftArm.y * PIXEL_TO_METER_RATIO_DEFAULT, 
						(leftArm.x + myControl.getLeftChargeVector().x*0.005f) * PIXEL_TO_METER_RATIO_DEFAULT, 
						(leftArm.y + myControl.getLeftChargeVector().y*0.005f) * PIXEL_TO_METER_RATIO_DEFAULT);
				Vector2 rightArm = myYokozuna.getBodies()[1].getWorldCenter();
				rightChargeLine.setPosition( rightArm.x * PIXEL_TO_METER_RATIO_DEFAULT, rightArm.y * PIXEL_TO_METER_RATIO_DEFAULT, 
						(rightArm.x + myControl.getRightChargeVector().x*0.005f) * PIXEL_TO_METER_RATIO_DEFAULT, 
						(rightArm.y + myControl.getRightChargeVector().y*0.005f) * PIXEL_TO_METER_RATIO_DEFAULT);
				}

			@Override
			public void reset()
			{
				// TODO Auto-generated method stub
				
			}
			
		});
		
		hisYokozuna = factory.createYokozuna( cameraWidth/2+50, cameraHeight/2, 7);
		hisControl = new StupidComputerControl(hisYokozuna, myYokozuna);
		hisYokozuna.registerUpdateHandler(hisControl);
		hisYokozuna.setIgnoreUpdate(false);
		mScene.attachChild( hisYokozuna );
		
		leftControl = factory.createControl( 50, 50, new IAnalogOnScreenControlListener (){
			@Override
			public void onControlChange(BaseOnScreenControl pBaseOnScreenControl, float pValueX, float pValueY)
			{
				myControl.leftControlUpdate(pBaseOnScreenControl, pValueX, pValueY );
		}

			@Override
			public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl)
			{
				// TODO Auto-generated method stub
				
			}});
		mScene.attachChild( leftControl );
		
		rightControl = factory.createControl( 50, cameraHeight - 150, new IAnalogOnScreenControlListener (){
			@Override
			public void onControlChange(BaseOnScreenControl pBaseOnScreenControl, float pValueX, float pValueY)
			{
				myControl.rightControlUpdate(pBaseOnScreenControl, pValueX, pValueY );
				}

			@Override
			public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl)
			{
				// TODO Auto-generated method stub
				
			}} );
		mScene.attachChild( rightControl );
		
		
//		factory.attachRotatingBrick( 120, 150 );
//		factory.attachRotatingBrick( 150, 250 );
//		factory.attachBucket( 150, 350 );

		return this.mScene;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			if(pSceneTouchEvent.isActionDown()) {
//				this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				leftControl.onSceneTouchEvent( pScene, pSceneTouchEvent );
				rightControl.onSceneTouchEvent( pScene, pSceneTouchEvent );
				return true;
			}
		}
		return false;
	}

	@Override
	public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

	}

	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
		myYokozuna.getBelly().applyForce( 
				pAccelerationData.getX(), pAccelerationData.getY(), // force
				myYokozuna.getBelly().getPosition().x, myYokozuna.getBelly().getPosition().y // point
			);
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	protected final PhysicsWorld getPhysics() 
	{
		return mPhysicsWorld;
	}
	
	protected final Scene getScene() 
	{
		return mScene;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
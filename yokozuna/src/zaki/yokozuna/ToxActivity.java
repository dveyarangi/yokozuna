package zaki.yokozuna;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.hardware.SensorManager;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

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

	protected static final int CAMERA_HEIGHT = 720;
	protected static final int CAMERA_WIDTH = 480;
	
	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;

	private Scene mScene;

	protected ITiledTextureRegion mBrickTextureRegion1;
//	protected ITiledTextureRegion mCircleFaceTextureRegion;
//	protected TiledTextureRegion mTriangleFaceTextureRegion;
//	protected TiledTextureRegion mHexagonFaceTextureRegion;
	protected PhysicsWorld mPhysicsWorld;
	
	private ToxFactory factory = new ToxFactory();
	
	private Yokozuna myYokozuna;
	private Yokozuna hisYokozuna;

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

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		
		factory.init( this );
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));
		this.mScene.setOnSceneTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), true);

		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		float RING_RADIUS = Math.min( CAMERA_WIDTH, CAMERA_HEIGHT );
		float wallx = RING_RADIUS, wally = 0;
		float wallStep = (float)(Math.PI/10.);
		 
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
/*		int cornersNum = (int)(2*Math.PI/wallStep);
		Vector2 [] wallCorners = new Vector2 [2];
		for(float a = 0; a < 2*Math.PI; a += wallStep) {
			float nextWallx = (float)(RING_RADIUS * Math.cos( a )) + CAMERA_WIDTH/2;
			float nextWally = (float)(RING_RADIUS * Math.sin( a )) + CAMERA_HEIGHT/2;
			wallCorners[0] = Vector2Pool.obtain(nextWallx, nextWally);
			wallCorners[1] = Vector2Pool.obtain(wallx, wally);
			PolygonShape wall = new PolygonShape();
			wall.set(wallCorners);
//			final Rectangle wall = new Rectangle(wallx, wally, nextWallx, nextWally, vertexBufferObjectManager);
			PhysicsFactory.createBoxBody(this.mPhysicsWorld, wall, BodyType.StaticBody, wallFixtureDef);
			this.mScene.attachChild(wall);
			
			
		}*/
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);

		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
		myYokozuna = factory.createYokozuna( CAMERA_WIDTH/2-50, CAMERA_HEIGHT/2,  3);
		
		hisYokozuna = factory.createYokozuna( CAMERA_WIDTH/2+50, CAMERA_HEIGHT/2, 7);

//		factory.attachRotatingBrick( 120, 150 );
//		factory.attachRotatingBrick( 150, 250 );
//		factory.attachBucket( 150, 350 );

		return this.mScene;
	}
	
	public static final float CHARGE_FORCE = 200; 

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			if(pSceneTouchEvent.isActionDown()) {
//				this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				Body fatBody = myYokozuna.getBody();
				Vector2 chargeVec = Vector2Pool.obtain( 
						pSceneTouchEvent.getX()/PIXEL_TO_METER_RATIO_DEFAULT - fatBody.getPosition().x, 
						pSceneTouchEvent.getY()/PIXEL_TO_METER_RATIO_DEFAULT - fatBody.getPosition().y );
				chargeVec.mul( CHARGE_FORCE / chargeVec.len());
				fatBody.applyForce( chargeVec, fatBody.getPosition() );
				Vector2Pool.recycle( chargeVec );
				Debug.d( "touch: " + pSceneTouchEvent.getX() + "," + pSceneTouchEvent.getY() + "; yokozuna: " + fatBody.getPosition() + "; charge: " + chargeVec);
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
		myYokozuna.getBody().applyForce( 
				pAccelerationData.getX(), pAccelerationData.getY(), // force
				myYokozuna.getBody().getPosition().x, myYokozuna.getBody().getPosition().y // point
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
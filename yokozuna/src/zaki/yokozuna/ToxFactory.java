package zaki.yokozuna;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.Constants;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import zaki.yokozuna.controls.AnalogOnScreenControl;
import zaki.yokozuna.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import zaki.yokozuna.texture.TextureException;
import zaki.yokozuna.texture.TextureManager;
import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;


public class ToxFactory
{

	VertexBufferObjectManager vertexManager;
	
	ToxActivity activity;
	
	final static FixtureDef BRICK_FIXTURE_DER = PhysicsFactory.createFixtureDef(10, 0.2f, 0.9f);
	final static FixtureDef BALL_FIXTURE_DER = PhysicsFactory.createFixtureDef(20, 0.5f, 0.5f);
	final static FixtureDef YOKOZUNA_FIXTURE_DER = PhysicsFactory.createFixtureDef(1, 0.5f, 10f);
	
	
	final static String [] BALL_TEXTURES = new String [] {
		"ball-blue",
		"ball-green",
		"ball-magenta",
		"ball-orange",
		"ball-pink",
		"ball-red",
		"ball-teal",
		"ball-violet",
		"ball-yellow"
	};
	
	private TextureManager textureManager;
	
	
	private int ballsCount = 0;
	
	public void init(final ToxActivity activity) 
	{
//		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.activity = activity;
		
		vertexManager = activity.getVertexBufferObjectManager();
		
		textureManager = new TextureManager(activity);
		
		textureManager.addTexture( "brick1", "gfx/brickline1.png" );
		textureManager.addTexture( "bucket1", "gfx/bucket1.png" );
		textureManager.addTexture( "onscreen_control_base", "gfx/onscreen_control_base.png" );
		textureManager.addTexture( "onscreen_control_knob", "gfx/onscreen_control_knob.png" );
		
		for(String ballName : BALL_TEXTURES)
		{
			textureManager.addTexture( ballName, "gfx/" + ballName + ".png" );
		}
		
	}
	
	public Sprite createSprite(final float x, final float y, final ITextureRegion textureRegion) 
	{
		
		return new Sprite(x, y, textureRegion, vertexManager);
	}
	public Sprite createSprite(final float x, final float y, float width, float height, final ITextureRegion textureRegion) 
	{
		
		return new Sprite(x-width/2, y-height/2, width, height, textureRegion, vertexManager);
	}
	
	public void attachRotatingBrick(final float x, final float y)
	{
		ITextureRegion region;
		try
		{
			region = textureManager.getTextureRegion( "brick1" );
		} catch ( TextureException e )
		{
			Debug.e( e );
			return;
		}
		
		Sprite movingShape = createSprite( x, y, region );
		final Body movingBody = PhysicsFactory.createBoxBody(activity.getPhysics(), movingShape, BodyType.DynamicBody, BRICK_FIXTURE_DER);
		IAreaShape anchorShape = new Rectangle(movingShape.getX()+movingShape.getWidth()/2,movingShape.getY()+movingShape.getHeight()/2,1,1, vertexManager);
		final Body anchorBody = PhysicsFactory.createBoxBody(activity.getPhysics(), anchorShape, BodyType.StaticBody, BRICK_FIXTURE_DER);
		
		activity.getScene().attachChild(anchorShape);
		activity.getScene().attachChild(movingShape);

		
		activity.getPhysics().registerPhysicsConnector(new PhysicsConnector(movingShape, movingBody, true, true));
		final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.initialize(anchorBody, movingBody, anchorBody.getWorldCenter());
		revoluteJointDef.enableMotor = true;
		revoluteJointDef.motorSpeed = 0.5f;
		revoluteJointDef.maxMotorTorque = 2000;

		activity.getPhysics().createJoint(revoluteJointDef);

	}
	
	public void attachBucket(final float x, final float y)
	{
		ITextureRegion region;
		try
		{
			region = textureManager.getTextureRegion( "bucket1" );
		} catch ( TextureException e )
		{
			Debug.e( e );
			return;
		}		
		
		Sprite movingShape = createSprite( x, y, region );
		final Body movingBody = ToxFactory.createBucketBody(activity.getPhysics(), movingShape, BodyType.DynamicBody, BRICK_FIXTURE_DER);
		IAreaShape anchorShape = new Rectangle(movingShape.getX(), movingShape.getY()-movingShape.getHeight()/2,1,1, vertexManager);
		final Body anchorBody = PhysicsFactory.createBoxBody(activity.getPhysics(), anchorShape, BodyType.StaticBody, BRICK_FIXTURE_DER);
		
		activity.getScene().attachChild(anchorShape);
		activity.getScene().attachChild(movingShape);

		
		activity.getPhysics().registerPhysicsConnector(new PhysicsConnector(movingShape, movingBody, true, true));
		final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.initialize(anchorBody, movingBody, anchorBody.getWorldCenter());
		revoluteJointDef.enableMotor = false;
//		revoluteJointDef.motorSpeed = 0.5f;
//		revoluteJointDef.maxMotorTorque = 2000;

		activity.getPhysics().createJoint(revoluteJointDef);

	}
	
	public static final int FATNESS = 100;
	
	private static final float SPAN = 10;
	
	public Yokozuna createYokozuna (final float x, final float y, int color) 
	{
		
		Vector2 center = Vector2Pool.obtain( x, y );
		
		ITextureRegion region;
		try
		{
			region = textureManager.getTextureRegion( BALL_TEXTURES[color] );
		} catch ( TextureException e )
		{
			Debug.e( e );
			return null;
		}			
		
//		Sprite ballShape = createSprite( x, y, FATNESS, FATNESS, region);
		
		ballsCount ++;
		
		Sprite bellyShape = createSprite( x, y, FATNESS, FATNESS, region);
		final Body bellyBody = ToxFactory.createCircleBody( activity.getPhysics(), bellyShape, BodyType.DynamicBody, BRICK_FIXTURE_DER);
		bellyBody.setLinearDamping( 1f );
		bellyBody.setAngularDamping( 1f );
		activity.getPhysics().registerPhysicsConnector(new PhysicsConnector(bellyShape, bellyBody, true, true));
		
		
		Sprite leftArmShape = createSprite( x-FATNESS/2, y, FATNESS/2, FATNESS/2, region );
		final Body leftArmBody = ToxFactory.createCircleBody( activity.getPhysics(), leftArmShape, BodyType.DynamicBody, BRICK_FIXTURE_DER);
		activity.getPhysics().registerPhysicsConnector(new PhysicsConnector(leftArmShape, leftArmBody, true, true));
		
		Sprite rightArmShape = createSprite( x+FATNESS/2, y, FATNESS/2, FATNESS/2,region );
		final Body rightArmBody = ToxFactory.createCircleBody( activity.getPhysics(), rightArmShape, BodyType.DynamicBody, BRICK_FIXTURE_DER);
		activity.getPhysics().registerPhysicsConnector(new PhysicsConnector(rightArmShape, rightArmBody, true, true));
		
		
//		IAreaShape anchorShape = new Rectangle(movingShape.getX(), movingShape.getY()-movingShape.getHeight()/2,1,1, vertexManager);
//		final Body anchorBody = PhysicsFactory.createBoxBody(activity.getPhysics(), anchorShape, BodyType.StaticBody, BRICK_FIXTURE_DER);
		

		
//		activity.getPhysics().registerPhysicsConnector(new PhysicsConnector(movingShape, movingBody, true, true));
		final RevoluteJointDef rightArmJoint = new RevoluteJointDef();
		rightArmJoint.initialize(bellyBody, rightArmBody, rightArmBody.getWorldCenter());
		activity.getPhysics().createJoint(rightArmJoint);
		final RevoluteJointDef leftArmJoint = new RevoluteJointDef();
		leftArmJoint.initialize(bellyBody, leftArmBody, leftArmBody.getWorldCenter());
		activity.getPhysics().createJoint(leftArmJoint);
//		revoluteJointDef.enableMotor = false;
//		revoluteJointDef.motorSpeed = 0.5f;
//		revoluteJointDef.maxMotorTorque = 2000;


		Vector2Pool.recycle( center );
		
		Yokozuna entity = new Yokozuna(bellyBody, leftArmBody, rightArmBody, bellyShape);
		entity.attachChild(bellyShape);
		entity.attachChild(leftArmShape);
		entity.attachChild(rightArmShape);
		
		return entity;

	}
	
	/**
	 * Creates a {@link Body} based on a {@link PolygonShape} in the form of a triangle:
	 * <pre>
	 *  /\
	 * /__\
	 * </pre>
	 */
	public static Body createTriangleBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		/* Remember that the vertices are relative to the center-coordinates of the Shape. */
		final float halfWidth = pAreaShape.getWidthScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float halfHeight = pAreaShape.getHeightScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;

		final float top = -halfHeight;
		final float bottom = halfHeight;
		final float left = -halfHeight;
		final float centerX = 0;
		final float right = halfWidth;

		final Vector2[] vertices = {
				new Vector2(centerX, top),
				new Vector2(right, bottom),
				new Vector2(left, bottom)
		};

		return PhysicsFactory.createPolygonBody(pPhysicsWorld, pAreaShape, vertices, pBodyType, pFixtureDef);
	}

	/**
	 * Creates a {@link Body} based on a {@link PolygonShape} in the form of a hexagon:
	 * <pre>
	 *  /\
	 * /  \
	 * |  |
	 * |  |
	 * \  /
	 *  \/
	 * </pre>
	 */
	public static Body createBucketBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		/* Remember that the vertices are relative to the center-coordinates of the Shape. */
		final float halfWidth = pAreaShape.getWidthScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float halfHeight = pAreaShape.getHeightScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;

		/* The top and bottom vertex of the hexagon are on the bottom and top of hexagon-sprite. */
		final float top = -halfHeight;
		final float bottom = halfHeight;
		
		final float sideOutterTop = halfWidth / PIXEL_TO_METER_RATIO_DEFAULT;
		final float sideOutterBottom = (halfWidth-1) / PIXEL_TO_METER_RATIO_DEFAULT;
		final float sideInnerTop = (halfWidth-1) / PIXEL_TO_METER_RATIO_DEFAULT;
		final float sideInnerBottom = (halfWidth-2) / PIXEL_TO_METER_RATIO_DEFAULT;
		
		final float roofOutterSide = -halfHeight / PIXEL_TO_METER_RATIO_DEFAULT;
		final float floorOutterSide = (halfWidth) / PIXEL_TO_METER_RATIO_DEFAULT;
		final float roofInnerSide = -halfWidth / PIXEL_TO_METER_RATIO_DEFAULT;
		final float floorInnerSide = (halfWidth-1) / PIXEL_TO_METER_RATIO_DEFAULT;


		final Vector2[] vertices = {
				new Vector2(sideOutterTop, roofOutterSide),
				new Vector2(sideOutterBottom, floorOutterSide),
				new Vector2(-sideOutterBottom, floorOutterSide),
				new Vector2(-sideOutterTop, roofOutterSide),
				new Vector2(-sideInnerTop, roofInnerSide),
				new Vector2(-sideInnerBottom, floorInnerSide),
				new Vector2(sideInnerBottom, floorInnerSide),
				new Vector2(sideInnerTop, roofInnerSide),
		};

		return PhysicsFactory.createPolygonBody(pPhysicsWorld, pAreaShape, vertices, pBodyType, pFixtureDef);
	}

	public static Body createCircleBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		return PhysicsFactory.createCircleBody(pPhysicsWorld, pAreaShape, pBodyType, pFixtureDef, PIXEL_TO_METER_RATIO_DEFAULT);
	}
	public static Body createCircleBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef, final float pPixelToMeterRatio) {
		final float[] sceneCenterCoordinates = pAreaShape.getSceneCenterCoordinates();
		final float centerX = sceneCenterCoordinates[Constants.VERTEX_INDEX_X];
		final float centerY = sceneCenterCoordinates[Constants.VERTEX_INDEX_Y];
		return PhysicsFactory.createCircleBody(pPhysicsWorld, centerX, centerY, pAreaShape.getWidthScaled() * 0.5f, pAreaShape.getRotation(), pBodyType, pFixtureDef, pPixelToMeterRatio);
	}
	public static Body createCircleBody(final PhysicsWorld pPhysicsWorld, final float pCenterX, final float pCenterY, final float pRadius, final float pRotation, final BodyType pBodyType, final FixtureDef pFixtureDef, final float pPixelToMeterRatio) {
		final BodyDef circleBodyDef = new BodyDef();
		circleBodyDef.type = pBodyType;

		circleBodyDef.position.x = pCenterX / pPixelToMeterRatio;
		circleBodyDef.position.y = pCenterY / pPixelToMeterRatio;

		circleBodyDef.angle = MathUtils.degToRad(pRotation);

		final Body circleBody = pPhysicsWorld.createBody(circleBodyDef);

		final CircleShape circlePoly = new CircleShape();
		pFixtureDef.shape = circlePoly;

		final float radius = pRadius / pPixelToMeterRatio;
		circlePoly.setRadius(radius);

		circleBody.createFixture(pFixtureDef);

		circlePoly.dispose();

		return circleBody;
	}
	
	public static Body createBoxBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		return PhysicsFactory.createBoxBody(pPhysicsWorld, pAreaShape, pBodyType, pFixtureDef, PIXEL_TO_METER_RATIO_DEFAULT);
	}

	public static Body createBoxBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef, final float pPixelToMeterRatio) {
		final float[] sceneCenterCoordinates = pAreaShape.getSceneCenterCoordinates();
		final float centerX = sceneCenterCoordinates[Constants.VERTEX_INDEX_X];
		final float centerY = sceneCenterCoordinates[Constants.VERTEX_INDEX_Y];
		return PhysicsFactory.createBoxBody(pPhysicsWorld, centerX, centerY, pAreaShape.getWidthScaled(), pAreaShape.getHeightScaled(), pAreaShape.getRotation(), pBodyType, pFixtureDef, pPixelToMeterRatio);
	}
	public static Body createBoxBody(final PhysicsWorld pPhysicsWorld, final float pCenterX, final float pCenterY, final float pWidth, final float pHeight, final float pRotation, final BodyType pBodyType, final FixtureDef pFixtureDef, final float pPixelToMeterRatio) {
		final BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = pBodyType;

		boxBodyDef.position.x = pCenterX / pPixelToMeterRatio;
		boxBodyDef.position.y = pCenterY / pPixelToMeterRatio;

		final Body boxBody = pPhysicsWorld.createBody(boxBodyDef);

		final PolygonShape boxPoly = new PolygonShape();

		final float halfWidth = pWidth * 0.5f / pPixelToMeterRatio;
		final float halfHeight = pHeight * 0.5f / pPixelToMeterRatio;

		boxPoly.setAsBox(halfWidth, halfHeight);
		pFixtureDef.shape = boxPoly;

		boxBody.createFixture(pFixtureDef);

		boxPoly.dispose();

		boxBody.setTransform(boxBody.getWorldCenter(), MathUtils.degToRad(pRotation));

		return boxBody;
	}

	public AnalogOnScreenControl createControl(int x, int y, IAnalogOnScreenControlListener listener)
	{
		BitmapTextureAtlas mOnScreenControlTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		ITextureRegion baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mOnScreenControlTexture, activity, "gfx/onscreen_control_base.png", 0, 0);
		ITextureRegion knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mOnScreenControlTexture, activity, "gfx/onscreen_control_knob.png", 128, 0);
		mOnScreenControlTexture.load();

		
	/* The On-Screen Controls to control the direction of the snake. */
		AnalogOnScreenControl control = new AnalogOnScreenControl(activity.getScene(), x, y, baseTextureRegion, knobTextureRegion, 0.1f, activity.getVertexBufferObjectManager(), listener);
		/* Make the controls semi-transparent. */
		control.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		control.getControlBase().setAlpha(0.5f);

		return control;
	}

}

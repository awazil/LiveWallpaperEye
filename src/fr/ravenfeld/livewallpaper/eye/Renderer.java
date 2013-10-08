package fr.ravenfeld.livewallpaper.eye;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import fr.ravenfeld.livewallpaper.library.objects.simple.BackgroundFixed;
import fr.ravenfeld.livewallpaper.library.objects.simple.Image;
import fr.ravenfeld.livewallpaper.library.objects.simple.ImageSpriteSheet;
import rajawali.Camera2D;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.renderer.RajawaliRenderer;
import rajawali.util.RajLog;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;


public class Renderer extends RajawaliRenderer implements
        SensorEventListener {
    private boolean mTouch=false;
    private Image mEye;
    private BackgroundFixed mSkin;
    private ImageSpriteSheet mSprite;
    private final int SENSITIVITY_X = 20;
    private final int SENSITIVITY_Y = 50;
    private final float ALPHA = 0.8f;
    private final float CENTER = 0.5f;
    private SensorManager mSensorManager;
    private float mGravity[];
    public Renderer(Context context) {
        super(context);
        mGravity = new float[2];
        mSensorManager = (SensorManager) context
                .getSystemService(
                        Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void initScene() {
        RajLog.systemInformation();

        Camera2D cam = new Camera2D();
        this.replaceAndSwitchCamera(getCurrentCamera(), cam);
        getCurrentScene().setBackgroundColor(Color.RED);
        getCurrentCamera().setLookAt(0, 0, 0);

        try {
            mEye = new Image("oeil", R.drawable.oeil,0.5f);
            mSprite = new ImageSpriteSheet( "sprite", R.drawable.sprite,0.5f, 4, 2,  new long[]{7000,50,50,50,50,50,50,75});
            mSprite.setPingPong(true);
            mSkin = new BackgroundFixed("peau", R.drawable.front);
        } catch (TextureException e) {
            e.printStackTrace();
        }
        addChild(mEye.getObject3D());
        addChild(mSprite.getObject3D());
        addChild(mSkin.getObject3D());
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);

        if (mEye != null && !mTouch) {
            mEye.setPosition(mGravity[0], mGravity[1]);
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (mEye != null) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouch=true;
                moveEye(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mTouch=true;
                moveEye(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                moveEye(event.getX(), event.getY());
                mTouch=false;
                break;
        }
        }
    }

    private void moveEye(float x,float y){

        float posX = x/mViewportWidth-CENTER;
        float posY =1-y/mViewportHeight-CENTER;

        if(posX>0){
            posX = Math.min(posX,0.1f);
        }else{
            posX = Math.max(posX,-0.1f);
        }
        if(posY>0){
            posY = Math.min(posY,0.1f);
        }else{
            posY = Math.max(posY,-0.1f);
        }

        mEye.setPosition(posX,posY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        if (mEye != null) {
            mEye.surfaceChanged(width, height);
        }
        if (mSprite != null) {
            mSprite.surfaceChanged(width, height);
        }
        if (mSkin != null) {
            mSkin.surfaceChanged(width, height);
        }
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (mSprite != null) {
            if (visible) {
                mSprite.animate();
            } else {
                mSprite.stopAnimation();
            }
        }
        if(visible){
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_UI);
        }else{
            mSensorManager.unregisterListener(this);
        }

    }

    @Override
    public void onSurfaceDestroyed() {
        super.onSurfaceDestroyed();
    }



    @Override
    public void onOffsetsChanged(float xOffset, float yOffset,
                                 float xOffsetStep, float yOffsetStep, int xPixelOffset,
                                 int yPixelOffset) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity[0] = (ALPHA * mGravity[0] + (1 - ALPHA) * event.values[0])/SENSITIVITY_X;
            mGravity[1] = (ALPHA * mGravity[1] + (1 - ALPHA) * event.values[1])/SENSITIVITY_Y;
        }
    }

}
package dev.eah.hellosensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensorGyro;
    private Sensor mSensorLinAccel;
    private Sensor mSensorAccel;
    private Sensor mSensorMagneto;

    private float currentDeg = 0f;
    private float[] accelArr = new float[3];
    private float[] magnetoArr = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private boolean hasAccelArr = false;
    private boolean hasMagnetoArr = false;

    private ImageView imgCompass;
    private TextView txtGyroStatus;
    private TextView txtAccelStatus;
    private TextView txtMagneto;
    private TextView txtXRads;
    private TextView txtYRads;
    private TextView txtZRads;
    private TextView txtXForce;
    private TextView txtYForce;
    private TextView txtZForce;

    private final static float LIN_ACCEL_DELTA = 1.5f;
    private final static int LIN_SENSOR_DELAY = 250000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        txtGyroStatus = (TextView) findViewById(R.id.txtGyroStatus);
        txtAccelStatus = (TextView) findViewById(R.id.txtAccelStatus);
        imgCompass = (ImageView) findViewById(R.id.imgCompass);
        txtMagneto = (TextView) findViewById(R.id.txtMagneto);
        txtXRads = (TextView) findViewById(R.id.txtXRads);
        txtYRads = (TextView) findViewById(R.id.txtYRads);
        txtZRads = (TextView) findViewById(R.id.txtZRads);
        txtXForce = (TextView) findViewById(R.id.txtXForce);
        txtYForce = (TextView) findViewById(R.id.txtYForce);
        txtZForce = (TextView) findViewById(R.id.txtZForce);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorLinAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagneto = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mSensorGyro, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorLinAccel, LIN_SENSOR_DELAY);
        mSensorManager.registerListener(this, mSensorAccel, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorMagneto, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorGyro, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorLinAccel, LIN_SENSOR_DELAY);
        mSensorManager.registerListener(this, mSensorAccel, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorMagneto, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mSensorGyro) {
            float xRads = event.values[0];
            float yRads = event.values[1];
            float zRads = event.values[2];

            txtXRads.setText("" + xRads);
            txtYRads.setText("" + yRads);
            txtZRads.setText("" + zRads);

            if (Math.abs(zRads) > 3) {
                txtGyroStatus.setText("You are spinning insanely fast!");
            } else if (Math.abs(zRads) > 1) {
                txtGyroStatus.setText("You are spinning!");
            } else {
                txtGyroStatus.setText("Still.");
            }
        } else if (event.sensor == mSensorLinAccel) {
            float xForce = event.values[0];
            float yForce = event.values[1];
            float zForce = event.values[2];

            txtXForce.setText("" + xForce);
            txtYForce.setText("" + yForce);
            txtZForce.setText("" + zForce);

            if (zForce < -LIN_ACCEL_DELTA) {
                txtAccelStatus.setText("Falling!");
            } else if (zForce > LIN_ACCEL_DELTA) {
                txtAccelStatus.setText("Elevating!");
            } else {
                txtAccelStatus.setText("Still.");
            }
        } else if (event.sensor == mSensorMagneto) {
            txtMagneto.setText(event.values[0] + "\n" +
                    event.values[1] + "\n" +
                    event.values[2]);

            System.arraycopy(event.values, 0, magnetoArr, 0, event.values.length);
            hasMagnetoArr = true;
        } else if (event.sensor == mSensorAccel) {
            System.arraycopy(event.values, 0, accelArr, 0, event.values.length);
            hasAccelArr = true;
        }

        if (hasAccelArr && hasMagnetoArr) {
            SensorManager.getRotationMatrix(rotationMatrix, null, accelArr, magnetoArr);
            SensorManager.getOrientation(rotationMatrix, orientation);
            float azimuthInRadians = orientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            RotateAnimation ra = new RotateAnimation(
                    currentDeg,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            imgCompass.startAnimation(ra);
            currentDeg = -azimuthInDegress;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}

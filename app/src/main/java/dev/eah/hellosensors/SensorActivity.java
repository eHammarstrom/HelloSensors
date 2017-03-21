package dev.eah.hellosensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.media.ToneGenerator;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensorGyro;

    private ToneGenerator toneGen;

    private TextView txtGyroStatus;
    private TextView txtXRads;
    private TextView txtYRads;
    private TextView txtZRads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);

        txtGyroStatus = (TextView) findViewById(R.id.txtGyroStatus);
        txtXRads = (TextView) findViewById(R.id.txtXRads);
        txtYRads = (TextView) findViewById(R.id.txtYRads);
        txtZRads = (TextView) findViewById(R.id.txtZRads);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorManager.registerListener(this, mSensorGyro, 100000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorGyro, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float xRads = event.values[0];
            float yRads = event.values[1];
            float zRads = event.values[2];

            txtXRads.setText("" + event.values[0]);
            txtYRads.setText("" + event.values[1]);
            txtZRads.setText("" + event.values[2]);

            if (Math.abs(zRads) > 3) {
                txtGyroStatus.setText("You are spinning insanely fast!");
            } else if (Math.abs(zRads) > 1) {
                txtGyroStatus.setText("You are spinning!");
            } else {
                txtGyroStatus.setText("Still.");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}

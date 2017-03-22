package dev.eah.hellosensors;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView txtCounter;
    private Button btnSensors;
    private int counter = 0;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();

        btnSensors = (Button) findViewById(R.id.btnSensors);
        txtCounter = (TextView) findViewById(R.id.txtCounter);

        handler.post(new Runnable() {
            @Override
            public void run() {
                counter++;
                txtCounter.setText(String.format(Locale.getDefault(), "%d", counter));
                handler.postDelayed(this, 500);
            }
        });

        btnSensors.setOnClickListener(new btnSensorsListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private class btnSensorsListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                txtCounter.setText("CLICK!");

                Intent sensorActivity = new Intent(getBaseContext(), SensorActivity.class);
                startActivity(sensorActivity);
            }
    }
}

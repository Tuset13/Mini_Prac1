package udl.eps.testaccelerometre;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TestAccelerometreActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private SensorManager sensorManagerLlum;
    private boolean color = false;
    private TextView viewAcc;
    private TextView viewText;
    private ListView viewLlum;
    private long lastUpdate;
    private long lastUpdateLlum;


    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        viewAcc = findViewById(R.id.textView);
        viewAcc.setBackgroundColor(Color.GREEN);
        viewText = findViewById(R.id.textView2);
        viewLlum = findViewById(R.id.listView3);
        viewLlum.setBackgroundColor(Color.YELLOW);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {

            Sensor sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

            if(sensorAcc != null)
            {
                sensorManager.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_NORMAL);
            }
            else
            {
                viewAcc.setText(R.string.no_acce);
            }

            if(sensorLight != null)
            {
                sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
            }
            else
            {
                viewLlum.setTextAlignment(R.string.no_light);
            }

            lastUpdate = System.currentTimeMillis();
            lastUpdateLlum = System.currentTimeMillis();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            getAccelerometer(event);
        }

        if(event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            getLight(event);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 2)
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;

            Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show();
            if (color) {
                viewAcc.setBackgroundColor(Color.GREEN);

            } else {
                viewAcc.setBackgroundColor(Color.RED);
            }
            color = !color;
        }
    }

    private void getLight(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
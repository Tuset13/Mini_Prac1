package udl.eps.testaccelerometre;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.text.method.ScrollingMovementMethod;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TestAccelerometreActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private boolean color = false;
    private TextView viewAcc;
    private TextView viewText;
    private TextView viewLlum;
    private long lastUpdate;
    private long lastUpdateLlum;

    private float maxLumRange, lowLumRange, mediumLumRang;
    private float lastLightValue = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        viewAcc = findViewById(R.id.textView);
        viewAcc.setBackgroundColor(Color.GREEN);
        viewText = findViewById(R.id.textView2);
        viewLlum = findViewById(R.id.textView3);
        viewLlum.setBackgroundColor(Color.YELLOW);
        viewLlum.setMovementMethod(new ScrollingMovementMethod());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        createListeners();
    }

    private void createListeners() {
        if (sensorManager != null) {

            Sensor sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

            if(sensorAcc != null)
            {
                sensorManager.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_NORMAL);
            }
            else
            {
                viewText.setText(R.string.no_acce);
            }

            if(sensorLight != null)
            {
                sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
                maxLumRange = sensorLight.getMaximumRange();
                lowLumRange = maxLumRange / 3;
                mediumLumRang = lowLumRange * 2;
                viewLlum.setText(getText(R.string.yes_light) + "\n" + getText(R.string.max_light) + maxLumRange + "\n \n");
            }
            else
            {
                viewLlum.setText(R.string.no_light);
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
        float lightValue = event.values[0];
        long actualTime = System.currentTimeMillis();

        if(lastLightValue == lightValue)
        {
            return;
        }
        else
        {
            if((actualTime - lastUpdateLlum) < 3000)
            {
                return;
            }
            else
            {
                //Cos codi sensor de llum
                lastUpdateLlum = actualTime;
                lastLightValue = lightValue;

                viewLlum.append(getText(R.string.new_light_value));
                //Introduir el nou valor
                viewLlum.append(" " + lastLightValue);

                if (lastLightValue < lowLumRange){
                    viewLlum.append("\n LOW INTENSITY \n");
                }else if(lastLightValue < mediumLumRang){
                    viewLlum.append("\n MEDIUM INTENSITY \n");
                }else {
                    viewLlum.append("\n HIGH INTENSITY \n");
                }
            }
        }
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

    @Override
    protected void onResume() {
        //Register the listener again
        super.onResume();
        createListeners();
    }
}
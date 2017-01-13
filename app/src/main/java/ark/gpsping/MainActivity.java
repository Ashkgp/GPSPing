package ark.gpsping;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    EditText IP;
    EditText port;
    TextView display;
    Button set;
    String host = "None";
    int socket = 0;
    BufferedWriter b = null;
    Boolean ready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IP = (EditText) findViewById(R.id.IP);
        port = (EditText) findViewById(R.id.port);
        display = (TextView) findViewById(R.id.display);
        set = (Button) findViewById(R.id.button);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        IP.setText("");
        port.setText("");
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                host = IP.getText().toString();
                socket = Integer.parseInt(port.getText().toString());
                Client c = new Client(getApplicationContext(), host, socket);
                try {
                   b = c.execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                try {
                    publish(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void publish(Location location) throws IOException {
        try {

            String loca = Double.toString(location.getLatitude());
            loca = loca + "," + Double.toString(location.getLongitude());
            //Toast.makeText(MainActivity.this, loca, Toast.LENGTH_SHORT).show();
            if (b != null) {
                ready = true;
                b.write(loca);
                b.flush();
            }
            display.setText(loca);
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "Unable to publish data", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
       // Toast.makeText(MainActivity.this, Double.toString(location.getLatitude()), Toast.LENGTH_SHORT).show();
       // if (b != null) {
            try {
                publish(location);
                //display.setText(Double.toString(location.getLatitude()));
            } catch (IOException e) {
                e.printStackTrace();
            }
       // }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"GPS permission granted",Toast.LENGTH_LONG).show();

                } else {

                    // Close the app or disable the location function

                }
                break;
        }
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

package com.alcineo.bonappetit.android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat; // you can change it to appsupport if you are not using androidx
import androidx.core.content.ContextCompat;
import static android.content.Context.LOCATION_SERVICE;

import com.alcineo.utils.common.StringUtils;

import java.util.function.Consumer;

public class LocationHelper {

    final int LOCATION_REFRESH_TIME = 3000; // 3 seconds. The Minimum Time to get location update
    final int LOCATION_REFRESH_DISTANCE = 30; // 30 meters. The Minimum Distance to be changed to get location update
    final int MY_PERMISSIONS_REQUEST_LOCATION = 100;

    Activity activity;
    Context context;

    private static byte[] longitude = new byte[8];
    private static byte[] latitude = new byte[8];

    public static byte[] getLongitude() {
        return longitude;
    }


    public static byte[] getLatitude() {
        return latitude;
    }

    public LocationHelper (Activity activity) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
    }

    private static byte[] doubletoBytes(double dblValue) {
        long data = Double.doubleToRawLongBits(dblValue);
        return new byte[]{
                (byte) ((data >> 56) & 0xff),
                (byte) ((data >> 48) & 0xff),
                (byte) ((data >> 40) & 0xff),
                (byte) ((data >> 32) & 0xff),
                (byte) ((data >> 24) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 0) & 0xff),
        };
    }


    final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            longitude = doubletoBytes(location.getLongitude());
            latitude = doubletoBytes(location.getLatitude());
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public void startListeningUserLocation() {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission( context,android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&  ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // permission is denined by user, you can show your alert dialog here to send user to App settings to enable permission
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }

    }


}

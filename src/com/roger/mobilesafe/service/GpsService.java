package com.roger.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import com.roger.mobilesafe.utils.MyConstants;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Roger on 2014/10/21.
 */
public class GpsService extends Service{
    private LocationManager locationManager;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double lon = location.getLongitude();
            double lat = location.getLatitude();
            double accuracy = location.getAccuracy();
            //标准GPS，转换成火星坐标
            InputStream is = null;
            try {
                is = getAssets().open("axisoffset.dat");
                ModifyOffset offset = ModifyOffset.getInstance(is);
                PointDouble pd = offset.s2c(new PointDouble(lon,lat));
                lon = pd.x;
                lat = pd.y;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            SharedPreferences preferences = getSharedPreferences("config",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(MyConstants.LOCATION,"longitute:"+lon+"\nlatitute:"+lat+"\naccuracy:"+accuracy);
            editor.commit();
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
    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最 大精度
        String proveder = locationManager.getBestProvider(criteria,true);
        locationManager.requestLocationUpdates(proveder,0,0,listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}

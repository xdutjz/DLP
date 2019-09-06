package com.nds.dlp;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
public class GetLocation extends Service {

    private SharedPreferences FakeSP;
    double RealLocationLonLast = 0;
    double RealLocationLonNow = 0;
    double RealLocationLatLast = 0;
    double RealLocationLatNow = 0;
    double FakeLocationLonLast;
    double FakeLocationLatLast;
    double RunningLocation;
    int RunningTime;
    int RunningSpeed;
    int RunningDistance;

    public GetLocation() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //make service foreground
        final Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0 );
        Notification notification = new NotificationCompat.Builder(this, "default")
                .setContentTitle("Hook5").
                        setContentText("Running").
                        setWhen(System.currentTimeMillis()).
                        setSmallIcon(R.mipmap.ic_launcher).
                        setLargeIcon(BitmapFactory.decodeResource(
                                getResources(), R.mipmap.ic_launcher)).
                        setContentIntent(pi).
                        build();
        startForeground(1, notification);

        //Set Location Listener
        final Context context = this;
        AMapLocationClient mLocationClient = new AMapLocationClient(getApplicationContext());
        AMapLocationListener mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        FakeLocationLonLast = TheFake(aMapLocation.getLongitude());
                        FakeLocationLatLast = TheFake(aMapLocation.getLatitude());
                        CountDistance countDistance = new CountDistance();
                        ArrayList cellinfos = countDistance.distanceCount
                                (FakeLocationLonLast, FakeLocationLatLast);
                        cellinfos.add(FakeLocationLonLast);
                        cellinfos.add(FakeLocationLatLast);

                        //Open Shared Preference
                        FakeSP = getSharedPreferences("FakeSP", -1);
                        FakeSP.edit().clear().commit();
                        SharedPreferences.Editor FakeSPEditor = FakeSP.edit();
                        for (int x=0;x<cellinfos.size();x++){
                            FakeSPEditor.putString(String.valueOf(x),
                                    cellinfos.get(x).toString());
                        }
                        FakeSPEditor.commit();


                        //context.sendBroadcast(intentToMain);

                        //System.out.println("fake lon " + FakeLocationLonLast +
                        // " fake last " + FakeLocationLatLast);
                        /*if (aMapLocation.getLongitude() == RealLocationLonLast &&
                                aMapLocation.getLatitude() == RealLocationLatLast){
                            System.out.println("it is same");
                        }
                        else {
                            RealLocationLonLast = aMapLocation.getLongitude();
                            RealLocationLatLast = aMapLocation.getLatitude();
                            System.out.println("it is, lon " +
                                    aMapLocation.getLongitude() + "lat" +
                                    aMapLocation.getLatitude()
                            );
                        }*/

                    }
                }
            }
        };
        mLocationClient.setLocationListener(mLocationListener);
        mLocationClient.startLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Looper.prepare();
                ArrayList returnArray = TheRequest();
                if (returnArray.size() == 0){
                    System.out.println("return no location");
                }
                else {
                    for (int x=0;x<returnArray.size();x++){
                        System.out.println(returnArray.get(x));
                    }
                }
                Looper.loop();
            }
        };
        timer.schedule(timerTask, 0, 3000);*/
        return super.onStartCommand(intent, flags, startId);

    }

    //Fake the GPS
    public double TheFake(double given){
        double change = Math.random() * 1E-3 *5;
        double fake = given - (1E-3 * 2.5) + change;
        return fake;
    }

}

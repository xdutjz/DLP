package com.nds.dlp;

import android.app.ActivityManager;
import android.app.AndroidAppHelper;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class Main implements IXposedHookLoadPackage {

    public int area;
    public int cell;
    public int net;
    public String radio;
    public double CellLon;
    public double CellLat;
    public double longitude;
    public double latitude;
    public int tagno;

    private SharedPreferences SpecificAppSP;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                XSharedPreferences FakeSP = new XSharedPreferences
                        ("com.nds.dlp"
                                , "FakeSP");
                Map<String, ?> map = FakeSP.getAll();
                if (map.isEmpty()){
                    System.out.println("map is empty");
                }
                else {
                    /*for (Object o : map.values()){
                        System.out.println(o);
                    }*/
                    area = Integer.valueOf(map.get("0").toString());
                    cell = Integer.valueOf(map.get("1").toString());
                    net = Integer.valueOf(map.get("2").toString());
                    radio = map.get("3").toString();
                    CellLon = Double.valueOf(map.get("4").toString());
                    CellLat = Double.valueOf(map.get("5").toString());
                    longitude = Double.valueOf(map.get("7").toString());
                    latitude = Double.valueOf(map.get("8").toString());
                }
            }
        };
        timer.schedule(timerTask,10,2000);

        //Make GPS Only then set other providers to null
        //Get All Providers
        /*XposedBridge.hookAllMethods(LocationManager.class,
                "getAllProviders", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //old providers list contains[passive,gps,network]
                //XposedBridge.log(param.getResult().toString()+"it is the old list of get all providers");
                List<String> List = new ArrayList<String>();
                List.add("gps");
                param.setResult(List);
                super.afterHookedMethod(param);
            }
        });*/

        //Hook Get Best Providers
        XposedHelpers.findAndHookMethod(LocationManager.class,
                "getBestProvider",
                Criteria.class, Boolean.TYPE, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        //old best providers, but tested returns no results.
                        //XposedBridge.log(param.getResult().toString());
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)){
                            case "yes" :
                                param.setResult("gps");
                                break;
                            case "no" :
                                break;
                            default :
                                break;
                        }
                        super.afterHookedMethod(param);
                    }
                });

        //Hook get Providers, both two method
        /*XposedBridge.hookAllMethods(LocationManager.class,
                "getProviders", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //old all providers
                //XposedBridge.log(param.getResult().toString()+"it is the old list of get providers");
                List<String> list = new ArrayList<>();
                list.add("gps");
                param.setResult(list);
                super.afterHookedMethod(param);
            }
        });*/

        //todo Hook Get Provider, but I can't get it done
        XposedHelpers.findAndHookMethod(LocationManager.class,
                "getProvider", String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);

                        LocationProvider locationProvider = (LocationProvider) param.getResult();
                        XposedBridge.log(appname +
                                " location provider name by return " + locationProvider.getName() +
                                " location provider name by par " + param.args[0].toString() +
                                " location provider info " + String.valueOf(param.getResult()));

                    }
                });

        //Trying on add fake APS Status Listener
        XposedHelpers.findAndHookMethod(LocationManager.class, "addGpsStatusListener",
                GpsStatus.Listener.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":
                                if (param.args[0] != null) {
                                    XposedHelpers.callMethod
                                            (param.args[0], "onGpsStatusChanged", 1);
                                    XposedHelpers.callMethod
                                            (param.args[0], "onGpsStatusChanged", 3);
                                }
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                    }
                });

        //hook add NMEA listener
        XposedHelpers.findAndHookMethod(LocationManager.class, "addNmeaListener",
                GpsStatus.NmeaListener.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":

                                param.setResult(false);
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                    }
                });

        //todo Hook Get GPS status
        XposedHelpers.findAndHookMethod(LocationManager.class,
                "getGpsStatus", GpsStatus.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":

                                GpsStatus gss = (GpsStatus) param.getResult();
                                if (gss != null){
                                    Class<?> clazz = GpsStatus.class;
                                    Method m = null;
                                    for (Method method : clazz.getDeclaredMethods()) {
                                        if (method.getName().equals("setStatus")) {
                                            if (method.getParameterTypes().length > 1) {
                                                m = method;
                                                break;
                                            }
                                        }
                                    }
                                    if (m != null){
                                        //access the private setStatus function of GpsStatus
                                        m.setAccessible(true);

                                        //make the apps belive GPS works fine now
                                        int svCount = 5;
                                        int[] prns = {1, 2, 3, 4, 5};
                                        float[] snrs = {0, 0, 0, 0, 0};
                                        float[] elevations = {0, 0, 0, 0, 0};
                                        float[] azimuths = {0, 0, 0, 0, 0};
                                        int ephemerisMask = 0x1f;
                                        int almanacMask = 0x1f;

                                        //5 satellites are fixed
                                        int usedInFixMask = 0x1f;

                                        XposedHelpers.callMethod(gss, "setStatus",
                                                svCount, prns, snrs, elevations, azimuths,
                                                ephemerisMask, almanacMask, usedInFixMask);
                                        param.args[0] = gss;
                                        param.setResult(gss);
                                        try {
                                            m.invoke(gss, svCount, prns, snrs, elevations,
                                                    azimuths, ephemerisMask, almanacMask, usedInFixMask);
                                            param.setResult(gss);
                                        }
                                        catch (Exception e) {
                                            XposedBridge.log(e);
                                        }
                                    }
                                }
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                    }
                });

        //requires cell
        /*XposedHelpers.findAndHookMethod(LocationProvider.class,
                "requiresCell",  new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context)AndroidAppHelper.currentApplication();
                String message = getProgramNameByPackageName(context) + " requires cell.";
                //XposedBridge.log(message);
                param.setResult(false);
                super.afterHookedMethod(param);
            }
        });*/

        //Hook requires network
        /*XposedHelpers.findAndHookMethod(LocationProvider.class,
                "requiresNetwork",  new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context)AndroidAppHelper.currentApplication();
                String message = getProgramNameByPackageName(context) + " requires network.";
                //XposedBridge.log(message);
                param.setResult(false);
                super.beforeHookedMethod(param);
            }
        });*/

        //getScanResult
        XposedHelpers.findAndHookMethod(WifiManager.class,
                "getScanResults", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":

                                param.setResult(null);
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                        super.afterHookedMethod(param);
                    }
                });

        //Get BssId
        XposedHelpers.findAndHookMethod(WifiInfo.class,
                "getBSSID", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":

                                param.setResult("00-00-00-00-00-00-00-00");
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                        super.afterHookedMethod(param);
                    }
                });

        //Get ssID
        XposedHelpers.findAndHookMethod(WifiInfo.class,
                "getSSID", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":

                                param.setResult("null");
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                    }
                });

        //get MAC address
        XposedHelpers.findAndHookMethod(WifiInfo.class,
                "getMacAddress", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":

                                param.setResult("00-00-00-00-00-00-00-00");
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                        super.afterHookedMethod(param);
                    }
                });

        //Get Wifi Enabled
        XposedHelpers.findAndHookMethod(WifiManager.class,
                "isWifiEnabled", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":

                                param.setResult(false);
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                        super.afterHookedMethod(param);
                    }
                });

        //getCellLocation
        XposedHelpers.findAndHookMethod(TelephonyManager.class,
                "getCellLocation", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":
                                GsmCellLocation gsmCellLocation = new GsmCellLocation();
                                gsmCellLocation.setLacAndCid(area, cell);
                                param.setResult(gsmCellLocation);
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                        super.afterHookedMethod(param);
                    }
                });

        //getNeighborhoodLocation
        XposedHelpers.findAndHookMethod(TelephonyManager.class,
                "getNeighboringCellInfo", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":
                                param.setResult(null);
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                        super.afterHookedMethod(param);
                    }
                });

        //get all cell info
        XposedHelpers.findAndHookMethod(TelephonyManager.class,
                "getAllCellInfo", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":
                                //Todo All cell info maybe should come back
                                param.setResult
                                        (getCell(460, 0, area, cell, 0, 0));
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                        super.afterHookedMethod(param);
                    }
                });



        // 纬度
        XposedHelpers.findAndHookMethod(Location.class,
                "getLatitude", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":
                                param.setResult(latitude);
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                        super.beforeHookedMethod(param);

                    }

                });

        // 经度
        XposedHelpers.findAndHookMethod(Location.class,
                "getLongitude", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":
                                param.setResult(longitude);
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                        super.beforeHookedMethod(param);
                    }

                });

        //Hook get last known location
        XposedHelpers.findAndHookMethod(LocationManager.class,
                "getLastKnownLocation",
                String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":
                                if (param.args.length == 1) {
                                    if (param.args[0].toString().equals("gps")){
                                        Location location = new Location(LocationManager.GPS_PROVIDER);
                                        location.setLongitude(longitude);
                                        location.setLatitude(latitude);
                                        param.setResult(location);
                                    }
                                    else if (param.args[0].toString().equals("network")){
                                        Location location = new Location(LocationManager.NETWORK_PROVIDER);
                                        location.setLongitude(longitude);
                                        location.setLatitude(latitude);
                                        param.setResult(location);
                                    }
                                    else if (param.args[0].toString().equals("passive")){
                                        Location location = new Location(LocationManager.PASSIVE_PROVIDER);
                                        location.setLongitude(longitude);
                                        location.setLatitude(latitude);
                                        param.setResult(location);
                                    }
                                    else {
                                        XposedBridge.log("GetLastKnownLocation using forth Provider");
                                    }
                                }
                                else {
                                    XposedBridge.log("get last known location using more than one param");
                                }
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                        super.afterHookedMethod(param);

                    }
                });


        //Hook Request Location Updates
        for (Method method : LocationManager.class.getDeclaredMethods()){
            if (method.getName().equals("requestLocationUpdates")
                    && !Modifier.isAbstract(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers())){
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) AndroidAppHelper.currentApplication();
                        String appname = getProgramNameByPackageName(context);
                        TypeJudgement typeJudgement = new TypeJudgement();
                        switch (typeJudgement.judge(appname, context)) {
                            case "yes":
                                if (param.args.length == 4 &&
                                        (param.args[0] instanceof String) &&
                                        (param.args[3] instanceof LocationListener)){

                                    //对GPSProvider进行修改
                                    if (param.args[0].toString().equals("gps")){
                                        LocationListener LL = (LocationListener)param.args[3];
                                        Class<?> clazz = LocationListener.class;
                                        Method m = null;
                                        for (Method method : clazz.getDeclaredMethods()){
                                            if (method.getName().equals("onLocationChanged")){
                                                m = method;
                                                break;
                                            }
                                        }
                                        try {
                                            if (m != null){
                                                Object[] args = new Object[1];
                                                Location location = new
                                                        Location(LocationManager.GPS_PROVIDER);
                                                double lo = 116.41667;
                                                double la = 39.91667;
                                                location.setLongitude(longitude);
                                                location.setLatitude(latitude);
                                                args[0] = location;
                                                m.invoke(LL, args);
                                            }
                                            else if (m==null){
                                                XposedBridge.log(
                                                        "四参数首StringGPS尝试获取方法失败，m = null" +
                                                                m.getName());
                                            }
                                        }
                                        catch (Exception e){
                                            XposedBridge.log(e + "四参数首StringGPS抛出异常");
                                        }
                                    }

                                    //对NetworkProvider进行修改
                                    else if (param.args[0].toString().equals("network")){
                                        LocationListener LL = (LocationListener)param.args[3];
                                        Class<?> clazz = LocationListener.class;
                                        Method m = null;
                                        for (Method method : clazz.getDeclaredMethods()){
                                            if (method.getName().equals("onLocationChanged")){
                                                m = method;
                                                break;
                                            }
                                        }
                                        try {
                                            if (m != null){
                                                Object[] args = new Object[1];
                                                Location location = new
                                                        Location(LocationManager.NETWORK_PROVIDER);
                                                double lo = 116.41667;
                                                double la = 39.91667;
                                                location.setLongitude(longitude);
                                                location.setLatitude(latitude);
                                                args[0] = location;
                                                m.invoke(LL, args);
                                            }
                                            else if (m==null){
                                                XposedBridge.log(
                                                        "四参数首StringNetwork尝试获取方法失败，" +
                                                                "m = null"+ m.getName());
                                            }
                                        }
                                        catch (Exception e){
                                            XposedBridge.log(e + "四参数首StringNetwork抛出异常");
                                        }
                                    }

                                    //对PassiveProvider进行修改
                                    else if (param.args[0].toString().equals("passive")){
                                        LocationListener LL = (LocationListener)param.args[3];
                                        Class<?> clazz = LocationListener.class;
                                        Method m = null;
                                        for (Method method : clazz.getDeclaredMethods()){
                                            if (method.getName().equals("onLocationChanged")){
                                                m = method;
                                                break;
                                            }
                                        }
                                        try {
                                            if (m != null){
                                                Object[] args = new Object[1];
                                                Location location = new
                                                        Location(LocationManager.PASSIVE_PROVIDER);
                                                double lo = 116.41667;
                                                double la = 39.91667;
                                                location.setLongitude(longitude);
                                                location.setLatitude(latitude);
                                                args[0] = location;
                                                m.invoke(LL, args);
                                            }
                                            else if (m==null){
                                                XposedBridge.log(
                                                        "四参数首StringPassive尝试获取方法失败，" +
                                                                "m = null" +
                                                                m.getName());
                                            }
                                        }
                                        catch (Exception e){
                                            XposedBridge.log(e + "四参数首StringPassive抛出异常");
                                        }
                                    }

                                    else {
                                        XposedBridge.log("四参数首String使用第四种Provider");
                                    }

                                }
                                else if (param.args.length == 5 && (param.args[0] instanceof String)){

                                    //对GPS进行修改
                                    if (param.args[0].toString().equals("gps")){
                                        LocationListener LL = (LocationListener)param.args[3];
                                        Class<?> clazz = LocationListener.class;
                                        Method m = null;
                                        for (Method method : clazz.getDeclaredMethods()){
                                            if (method.getName().equals("onLocationChanged")){
                                                m = method;
                                                break;
                                            }
                                        }
                                        try {
                                            if (m != null){
                                                Object[] args = new Object[1];
                                                Location location = new
                                                        Location(LocationManager.GPS_PROVIDER);
                                                double lo = 116.41667;
                                                double la = 39.91667;
                                                location.setLongitude(longitude);
                                                location.setLatitude(latitude);
                                                args[0] = location;
                                                m.invoke(LL, args);
                                            }
                                            else if (m==null){
                                                XposedBridge.log(
                                                        "五参数首StringGPS尝试获取方法失败，m = null"+
                                                                m.getName());
                                            }
                                        }
                                        catch (Exception e){
                                            XposedBridge.log(e+ "五参数首StringGPS抛出异常");
                                        }
                                    }

                                    //对Network进行修改
                                    else if (param.args[0].toString().equals("network")){
                                        LocationListener LL = (LocationListener)param.args[3];
                                        Class<?> clazz = LocationListener.class;
                                        Method m = null;
                                        for (Method method : clazz.getDeclaredMethods()){
                                            if (method.getName().equals("onLocationChanged")){
                                                m = method;
                                                break;
                                            }
                                        }
                                        try {
                                            if (m != null){
                                                Object[] args = new Object[1];
                                                Location location = new
                                                        Location(LocationManager.NETWORK_PROVIDER);
                                                double lo = 116.41667;
                                                double la = 39.91667;
                                                location.setLongitude(longitude);
                                                location.setLatitude(latitude);
                                                args[0] = location;
                                                m.invoke(LL, args);
                                            }
                                            else if (m==null){
                                                XposedBridge.log(
                                                        "五参数首StringNetwork尝试获取方法失败，" +
                                                                "m = null"+
                                                                m.getName());
                                            }
                                        }
                                        catch (Exception e){
                                            XposedBridge.log(e+"五参数首StringNetwork抛出异常");
                                        }
                                    }

                                    //对Passive进行修改
                                    else if (param.args[0].toString().equals("passive")){
                                        LocationListener LL = (LocationListener)param.args[3];
                                        Class<?> clazz = LocationListener.class;
                                        Method m = null;
                                        for (Method method : clazz.getDeclaredMethods()){
                                            if (method.getName().equals("onLocationChanged")){
                                                m = method;
                                                break;
                                            }
                                        }
                                        try {
                                            if (m != null){
                                                Object[] args = new Object[1];
                                                Location location = new Location
                                                        (LocationManager.PASSIVE_PROVIDER);
                                                double lo = 116.41667;
                                                double la = 39.91667;
                                                location.setLongitude(longitude);
                                                location.setLatitude(latitude);
                                                args[0] = location;
                                                m.invoke(LL, args);
                                            }
                                            else if (m==null){
                                                XposedBridge.log(
                                                        "五参数首StringPassive尝试获取方法失败，" +
                                                                "m = null" +
                                                                m.getName());
                                            }
                                        }
                                        catch (Exception e){
                                            XposedBridge.log(e + "五参数首StringPassive抛出异常");
                                        }
                                    }

                                    else {
                                        XposedBridge.log("五参数首String使用第四种Provider");
                                    }

                                }

                                else if (param.args.length == 5 && (param.args[0] instanceof Long)){
                                    XposedBridge.log(appname + "这是被捕捉到的五参数且首参数为Long");
                                }
                                else if (param.args.length == 4 &&
                                        (param.args[3] instanceof PendingIntent) &&
                                        (param.args[0] instanceof String )){
                                    XposedBridge.log(appname +
                                            "这是被捕捉到的四参数且末PendingIntent，首String");
                                }
                                else if (param.args.length == 4 && (param.args[0] instanceof Long)){
                                    XposedBridge.log(appname + "这是被捕捉到的四参数且首参数为Long");
                                }
                                else{
                                    XposedBridge.log(appname + "这是其他种类");
                                    for (int x=0;x<param.args.length;x++){

                                        //含有LocationListener
                                        if (param.args[x] instanceof LocationListener){

                                            //首参包括GPS
                                            if (param.args[0].toString().contains("gps")){
                                                XposedBridge.log("其他种类第" + x +
                                                        "个参数为LocationListener，第一个参数为GPS");
                                                LocationListener LL = (LocationListener)param.args[1];
                                                Class<?> clazz = LocationListener.class;
                                                Method m = null;
                                                for (Method method : clazz.getDeclaredMethods()){
                                                    if (method.getName().equals("onLocationChanged")){
                                                        m = method;
                                                        break;
                                                    }
                                                }
                                                try {
                                                    if (m != null){
                                                        XposedBridge.log("这是其他方法中的注入gps");
                                                        Object[] args = new Object[1];
                                                        Location location =
                                                                new Location(LocationManager.GPS_PROVIDER);
                                                        double lo = 116.41667;
                                                        double la = 39.91667;
                                                        location.setLongitude(longitude);
                                                        location.setLatitude(latitude);
                                                        args[0] = location;
                                                        m.invoke(LL, args);
                                                    }
                                                    else if (m==null){
                                                        XposedBridge.log(
                                                                "其他方法中的尝试获取方法失败gps，" +
                                                                        m.getName());
                                                    }
                                                }
                                                catch (Exception e){
                                                    XposedBridge.log(e + "其他方法抛出异常gps");
                                                }
                                            }

                                            //首参包括Network
                                            else if (param.args[0].toString().contains("network")){
                                                XposedBridge.log("其他种类第"+ x +
                                                        "个参数为LocationListener，第一个参数为network");
                                                LocationListener LL = (LocationListener)param.args[1];
                                                Class<?> clazz = LocationListener.class;
                                                Method m = null;
                                                for (Method method : clazz.getDeclaredMethods()){
                                                    if (method.getName().equals("onLocationChanged")){
                                                        m = method;
                                                        break;
                                                    }
                                                }
                                                try {
                                                    if (m != null){
                                                        XposedBridge.log("这是其他方法中的注入network");
                                                        Object[] args = new Object[1];
                                                        Location location =
                                                                new Location
                                                                        (LocationManager.NETWORK_PROVIDER);
                                                        double lo = 116.41667;
                                                        double la = 39.91667;
                                                        location.setLongitude(longitude);
                                                        location.setLatitude(latitude);
                                                        args[0] = location;
                                                        m.invoke(LL, args);
                                                    }
                                                    else if (m==null){
                                                        XposedBridge.log(
                                                                "其他方法中的尝试获取方法失败network，"
                                                                        + m.getName());
                                                    }
                                                }
                                                catch (Exception e){
                                                    XposedBridge.log(e + "其他方法抛出异常network");
                                                }
                                            }

                                            //首参包括Passive
                                            else if (param.args[0].toString().contains("passive")){
                                                XposedBridge.log("其他种类第"+ x +
                                                        "个参数为LocationListener，第一个参数为passive");
                                                LocationListener LL = (LocationListener)param.args[1];
                                                Class<?> clazz = LocationListener.class;
                                                Method m = null;
                                                for (Method method : clazz.getDeclaredMethods()){
                                                    if (method.getName().equals("onLocationChanged")){
                                                        m = method;
                                                        break;
                                                    }
                                                }
                                                try {
                                                    if (m != null){
                                                        XposedBridge.log("这是其他方法中的注入passive");
                                                        Object[] args = new Object[1];
                                                        Location location =
                                                                new Location
                                                                        (LocationManager.NETWORK_PROVIDER);
                                                        double lo = 116.41667;
                                                        double la = 39.91667;
                                                        location.setLongitude(longitude);
                                                        location.setLatitude(latitude);
                                                        args[0] = location;
                                                        m.invoke(LL, args);
                                                    }
                                                    else if (m==null){
                                                        XposedBridge.log(
                                                                "其他方法中的尝试获取方法失败passive，" +
                                                                        m.getName());
                                                    }
                                                }
                                                catch (Exception e){
                                                    XposedBridge.log(e + "其他方法抛出异常passive");
                                                }
                                            }
                                        }
                                        String argName=param.args[x].toString();
                                        XposedBridge.log(argName + "参数" + x);
                                    }
                                }
                                break;
                            case "no":
                                break;
                            default:
                                break;
                        }
                        super.beforeHookedMethod(param);
                    }
                });
            }
        }

    }


    public static String getProgramNameByPackageName(Context context) {
        PackageManager pm = context.getPackageManager();
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static boolean queryUsageStats(Context context, String packageName) {
        class RecentUseComparator implements Comparator<UsageStats> {
            @Override
            public int compare(UsageStats lhs, UsageStats rhs) {
                return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : (lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1;
            }
        }
        RecentUseComparator mRecentComp = new RecentUseComparator();
        long ts = System.currentTimeMillis();
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 1000 * 10, ts);
        if (usageStats == null || usageStats.size() == 0) {
            return false;
        }
        Collections.sort(usageStats, mRecentComp);
        String currentTopPackage = usageStats.get(0).getPackageName();
        return currentTopPackage.equals(packageName);
    }

    public static String getTopAppPackageName(Context context) {

        String packageName = "";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        final long end = System.currentTimeMillis();
        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService( Context.USAGE_STATS_SERVICE);
        if (null == usageStatsManager) {
            return packageName;
        }
        final UsageEvents events = usageStatsManager.queryEvents((end - 60 * 1000), end);
        if (null == events) {
            return packageName;
        }
        UsageEvents.Event usageEvent = new UsageEvents.Event();
        UsageEvents.Event lastMoveToFGEvent = null;
        while (events.hasNextEvent()) {
            events.getNextEvent(usageEvent);
            if (usageEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                lastMoveToFGEvent = usageEvent;
            }
        }
        if (lastMoveToFGEvent != null) {
            packageName = lastMoveToFGEvent.getPackageName();
        }
        return packageName;
    }

    private String getForegroundTask(Context context) {

        String currentApp = "";

        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 2*1000, time);
        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
            }
        }

        return currentApp;
    }

    //Todo get all cell info property
    private static ArrayList getCell(int mcc, int mnc, int lac, int cid, int sid, int networkType) {
        ArrayList arrayList = new ArrayList();
        CellInfoGsm cellInfoGsm = (CellInfoGsm) XposedHelpers.newInstance(CellInfoGsm.class);
        XposedHelpers.callMethod
                (cellInfoGsm, "setCellIdentity",
                        XposedHelpers.newInstance
                                (CellIdentityGsm.class,
                                        new Object[]{Integer.valueOf(mcc),
                                                Integer.valueOf(mnc),
                                                Integer.valueOf(lac),
                                                Integer.valueOf(cid)}));
        CellInfoCdma cellInfoCdma = (CellInfoCdma) XposedHelpers.newInstance(CellInfoCdma.class);
        XposedHelpers.callMethod
                (cellInfoCdma, "setCellIdentity",
                        XposedHelpers.newInstance
                                (CellIdentityCdma.class,
                                        new Object[]{Integer.valueOf(lac),
                                                Integer.valueOf(sid),
                                                Integer.valueOf(cid),
                                                Integer.valueOf(0),
                                                Integer.valueOf(0)}));
        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) XposedHelpers.newInstance(CellInfoWcdma.class);
        XposedHelpers.callMethod
                (cellInfoWcdma, "setCellIdentity",
                        XposedHelpers.newInstance
                                (CellIdentityWcdma.class,
                                        new Object[]{Integer.valueOf(mcc),
                                                Integer.valueOf(mnc),
                                                Integer.valueOf(lac),
                                                Integer.valueOf(cid),
                                                Integer.valueOf(300)}));
        CellInfoLte cellInfoLte = (CellInfoLte) XposedHelpers.newInstance(CellInfoLte.class);
        XposedHelpers.callMethod
                (cellInfoLte, "setCellIdentity",
                        XposedHelpers.newInstance
                                (CellIdentityLte.class,
                                        new Object[]{Integer.valueOf(mcc),
                                                Integer.valueOf(mnc),
                                                Integer.valueOf(cid),
                                                Integer.valueOf(300),
                                                Integer.valueOf(lac)}));
        if (networkType == 1 || networkType == 2) {
            arrayList.add(cellInfoGsm);
        }
        else if (networkType == 13) {
            arrayList.add(cellInfoLte);
        }
        else if (networkType == 4 || networkType == 5 || networkType == 6 ||
                networkType == 7 || networkType == 12 || networkType == 14) {
            arrayList.add(cellInfoCdma);
        }
        else if (networkType == 3 || networkType == 8 || networkType == 9 ||
                networkType == 10 || networkType == 15) {
            arrayList.add(cellInfoWcdma);
        }
        return arrayList;
    }

}
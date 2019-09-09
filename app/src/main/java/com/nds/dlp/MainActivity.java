package com.nds.dlp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.*;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity{

    public ArrayList<String> AllApps = new ArrayList<String>();
    public ArrayList<String> AllAppsPackage = new ArrayList<>();
    public ArrayList<String> AllAppsIcon = new ArrayList<>();
    public ArrayList<String> AllProtectApps = new ArrayList<String>();
    public ArrayList<String> NoProtectApps = new ArrayList<String>();
    public ArrayList<String> NormalProtectApps = new ArrayList<String>();

    private SharedPreferences AllAppsSP;
    public SharedPreferences AllProtectSP;
    public SharedPreferences NoProtectSP;
    public SharedPreferences NormalProtectSP;

    private ListView listView;
    private listadapter mAdapter;
    private List<RollItem> rollItems = new ArrayList<>();

    private String[] mPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, GetLocation.class);
        startService(intent);

        requestPermission();
        CountDistance countDistance= new CountDistance();
        ArrayList returnArray = countDistance.distanceCount
                (108.835373, 34.122849);
        for (int x=0;x<returnArray.size();x++){
            System.out.println(returnArray.get(x));
        }
        /*countDistance.distanceCount(108.80, 34.05);
        countDistance.distanceCount(108.825, 34.075);*/

        Context SettingContext = this;

        //Accessibility
        /*if (!isAccessibilitySettingsOn(this)) {
            // 引导至辅助功能设置页面
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        } else {
            // 执行辅助功能服务相关操作
        }*/

        //Usage Access
        /*Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        SettingContext.startActivity(intent);*/

        //System.out.print(packageInfos);

        /*//依次判断并编辑All Apps Shared Preference
        for(ApplicationInfo applicationInfo : applicationInfos){
            String AppName = applicationInfo.loadLabel(pm).toString();
            String Icon = applicationInfo.loadIcon(pm).toString();

                System.out.print(Icon);
            }
        }*/

        //initLayout();

        initRollItems();
        RollItemAdapter rollItemAdapter = new RollItemAdapter(MainActivity.this,
                R.layout.rollitem, rollItems);
        ListView listView = (ListView)findViewById(R.id.listview);
        listView.setAdapter(rollItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                RollItem rollItem = rollItems.get(position);
                Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                detailIntent.putExtra("PackageName", rollItem.getPackageName());
                detailIntent.putExtra("AppName",rollItem.getAppName());
                startActivity(detailIntent);

            }
        });
    }

    private void initRollItems(){
        PackageManager pm = this.getPackageManager();
        List<PackageInfo> packageInfos = pm.getPackagesHoldingPermissions(mPermissions,0);

        AllAppsSP = getSharedPreferences("AllApps", 0);
        SharedPreferences.Editor AllAppsSPEditor = AllAppsSP.edit();
        AllAppsSPEditor.clear();

        for (int i = 1; i < packageInfos.size(); i++) {
            PackageInfo packageInfo = packageInfos.get(i);
            if (packageInfo.versionName != null && packageInfo.applicationInfo.uid > 10000)
            {
                String AppName = packageInfo.applicationInfo.loadLabel(getApplicationContext().
                        getPackageManager()).toString();
                String PackageName = packageInfo.packageName;
                if (AllAppsSP.contains(AppName) == false) {
                    AllAppsSPEditor.putString(AppName, PackageName);
                    AllAppsSPEditor.commit();
                }
            }
        }

        Map<String, ?> getallappsfromsp = AllAppsSP.getAll();
        for(Map.Entry<String, ?> entry : getallappsfromsp.entrySet()){
            AllApps.add(entry.getKey());
            AllAppsPackage.add(entry.getValue().toString());
        }

        for (int j=0; j<AllApps.size(); j++){
            String appName2Add = AllApps.get(j);
            String packageName2Add = AllAppsPackage.get(j);
            PackageManager pm2Add = this.getPackageManager();
            try {
                ApplicationInfo info2Add = pm2Add.
                        getApplicationInfo(packageName2Add, PackageManager.GET_META_DATA);
                Drawable icon2Add = info2Add.loadIcon(pm2Add);

                rollItems.add(new RollItem(appName2Add, packageName2Add, icon2Add));
            }
            catch (PackageManager.NameNotFoundException e){
                return;
            }

        }

    }

    /*private void initLayout() {

        listView = findViewById(R.id.listview);
        mAdapter = new listadapter(AllApps, AllAppsPackage, MainActivity.this,this);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }*/


    /*@Override
    public void onClick(View item, View widget, int position, int which) {
        switch (which){

            //In button NoProtect, test NoProtect first,
            //then judge AllProtect and NormalProtect.
            case R.id.button21:
                String noprotect = AllApps.get(position);

                NoProtectSP = getSharedPreferences("NoProtect", -1);
                if (NoProtectSP.contains(noprotect) == true){
                    Toast.makeText(this,
                            noprotect+ " has no protection already.",
                            Toast.LENGTH_LONG).show();
                }
                else if (NoProtectSP.contains(noprotect) == false){
                    SharedPreferences.Editor NoProtectSPEditor = NoProtectSP.edit();
                    NoProtectSPEditor.putString(noprotect, noprotect);
                    NoProtectSPEditor.commit();
                }

                AllProtectSP = getSharedPreferences("AllProtect", -1);
                if (AllProtectSP.contains(noprotect) == true){
                    SharedPreferences.Editor AllProtectSPEditor = AllProtectSP.edit();
                    AllProtectSPEditor.remove(noprotect);
                    AllProtectSPEditor.commit();
                }

                NormalProtectSP = getSharedPreferences("NormalProtect", -1);
                if (NormalProtectSP.contains(noprotect) == true){
                    SharedPreferences.Editor NormalProtectSPEditor = NormalProtectSP.edit();
                    NormalProtectSPEditor.remove(noprotect);
                    NormalProtectSPEditor.commit();
                }

                *//*System.out.println("No" + NoProtectApps +
                        "All" + AllProtectApps +
                        "Half" + NormalProtectApps);*//*
                break;

            case R.id.button2:
                String allprotect = AllApps.get(position);

                AllProtectSP = getSharedPreferences("AllProtect", -1);
                if (AllProtectSP.contains(allprotect) == true){
                    Toast.makeText(this,
                            allprotect + " has all protection already.",
                            Toast.LENGTH_LONG).show();
                }
                else if (AllProtectSP.contains(allprotect) == false){
                    SharedPreferences.Editor AllProtectSPEditor = AllProtectSP.edit();
                    AllProtectSPEditor.putString(allprotect, allprotect);
                    AllProtectSPEditor.commit();
                }

                NoProtectSP = getSharedPreferences("NoProtect", -1);
                if (NoProtectSP.contains(allprotect) == true){
                    SharedPreferences.Editor NoProtectSPEditor = NoProtectSP.edit();
                    NoProtectSPEditor.remove(allprotect);
                    NoProtectSPEditor.commit();
                }

                NormalProtectSP = getSharedPreferences("NormalProtect", -1);
                if (NormalProtectSP.contains(allprotect) == true){
                    SharedPreferences.Editor NormalProtectSPEditor = NoProtectSP.edit();
                    NormalProtectSPEditor.remove(allprotect);
                    NormalProtectSPEditor.commit();
                }

                *//*System.out.println("No" + NoProtectApps +
                        "All" + AllProtectApps +
                        "Half" + NormalProtectApps);*//*
                break;

            case R.id.button3:
                String normalprotect = AllApps.get(position);

                NormalProtectSP = getSharedPreferences("NormalProtect", -1);
                if (NormalProtectSP.contains(normalprotect) == true){
                    Toast.makeText(this,
                            normalprotect + "has normal protection already.",
                            Toast.LENGTH_LONG).show();
                }
                else if (NormalProtectSP.contains(normalprotect) == false){
                    SharedPreferences.Editor NormalProtectSPEditor = NormalProtectSP.edit();
                    NormalProtectSPEditor.putString(normalprotect, normalprotect);
                    NormalProtectSPEditor.commit();
                }

                NoProtectSP = getSharedPreferences("NoProtect", -1);
                if (NoProtectSP.contains(normalprotect) == true){
                    SharedPreferences.Editor NoProtectSPEditor = NoProtectSP.edit();
                    NoProtectSPEditor.remove(normalprotect);
                    NoProtectSPEditor.commit();
                }

                AllProtectSP = getSharedPreferences("AllProtect", -1);
                if (AllProtectSP.contains(normalprotect) == true){
                    SharedPreferences.Editor AllProtectSPEditor = AllProtectSP.edit();
                    AllProtectSPEditor.remove(normalprotect);
                    AllProtectSPEditor.commit();
                }

                *//*System.out.println("No" + NoProtectApps +
                        "All" + AllProtectApps +
                        "Half" + NormalProtectApps);*//*
                break;

            case  R.id.button4:
                TextView getTextView = (TextView)findViewById(R.id.textView2);
                String passAppName = getTextView.getText().toString();
                Toast.makeText(this,passAppName, Toast.LENGTH_SHORT).show();
                *//*Intent detailIntent = new Intent(this, DetailActivity.class);
                detailIntent.putExtra("AppName", passAppName);
                startActivity(detailIntent);*//*
                break;

            default:break;
        }
    }*/

    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.i("is AS on", e.getMessage());
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }

        return false;
    }

    private void requestPermission(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "get location permission", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(this, "not get location permission", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission_group.LOCATION},1);
        }


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "get permission", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(this, "not get permission", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult
            (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(this,"权限被申请了",Toast.LENGTH_SHORT).show();
                }else { //拒绝权限申请
                    Toast.makeText(this,"权限被拒绝了",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


}

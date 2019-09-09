package com.nds.dlp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    public SharedPreferences AllProtectSP;
    public SharedPreferences NoProtectSP;
    public SharedPreferences NormalProtectSP;


    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        Intent getAppIntent = getIntent();
        String PackageName = getAppIntent.getStringExtra("PackageName");
        final String AppName = getAppIntent.getStringExtra("AppName");

        TextView textView = findViewById(R.id.textView2);
        textView.setText(AppName);
        PackageManager pm = this.getPackageManager();
        try{
            ApplicationInfo applicationInfo = pm.getApplicationInfo(PackageName,0);
            Drawable drawable = applicationInfo.loadIcon(pm);
            ImageView imageView = (ImageView)findViewById(R.id.imageView2);
            imageView.setImageDrawable(drawable);
        }
        catch (PackageManager.NameNotFoundException e){
            return;
        }

        Button button21 = findViewById(R.id.button21);
        button21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NoProtectSP = getSharedPreferences("NoProtect", -1);
                if (NoProtectSP.contains(AppName) == true){
                    Toast.makeText(context,
                            AppName + " has no protection already.",
                            Toast.LENGTH_LONG).show();
                }
                else if (NoProtectSP.contains(AppName) == false){
                    SharedPreferences.Editor NoProtectSPEditor = NoProtectSP.edit();
                    NoProtectSPEditor.putString(AppName, AppName);
                    NoProtectSPEditor.commit();
                }

                AllProtectSP = getSharedPreferences("AllProtect", -1);
                if (AllProtectSP.contains(AppName) == true){
                    SharedPreferences.Editor AllProtectSPEditor = AllProtectSP.edit();
                    AllProtectSPEditor.remove(AppName);
                    AllProtectSPEditor.commit();
                }

                NormalProtectSP = getSharedPreferences("NormalProtect", -1);
                if (NormalProtectSP.contains(AppName) == true){
                    SharedPreferences.Editor NormalProtectSPEditor = NormalProtectSP.edit();
                    NormalProtectSPEditor.remove(AppName);
                    NormalProtectSPEditor.commit();
                }

                }
        });

        Button button22 = findViewById(R.id.button22);
        button22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AllProtectSP = getSharedPreferences("AllProtect", -1);
                if (AllProtectSP.contains(AppName) == true){
                    Toast.makeText(context,
                            AppName + " has all protection already.",
                            Toast.LENGTH_LONG).show();
                }
                else if (AllProtectSP.contains(AppName) == false){
                    SharedPreferences.Editor AllProtectSPEditor = AllProtectSP.edit();
                    AllProtectSPEditor.putString(AppName, AppName);
                    AllProtectSPEditor.commit();
                }

                NoProtectSP = getSharedPreferences("NoProtect", -1);
                if (NoProtectSP.contains(AppName) == true){
                    SharedPreferences.Editor NoProtectSPEditor = NoProtectSP.edit();
                    NoProtectSPEditor.remove(AppName);
                    NoProtectSPEditor.commit();
                }

                NormalProtectSP = getSharedPreferences("NormalProtect", -1);
                if (NormalProtectSP.contains(AppName) == true){
                    SharedPreferences.Editor NormalProtectSPEditor = NoProtectSP.edit();
                    NormalProtectSPEditor.remove(AppName);
                    NormalProtectSPEditor.commit();
                }

            }
        });

        Button button23 = findViewById(R.id.button23);
        button23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NormalProtectSP = getSharedPreferences("NormalProtect", -1);
                if (NormalProtectSP.contains(AppName) == true){
                    Toast.makeText(context,
                            AppName + "has normal protection already.",
                            Toast.LENGTH_LONG).show();
                }
                else if (NormalProtectSP.contains(AppName) == false){
                    SharedPreferences.Editor NormalProtectSPEditor = NormalProtectSP.edit();
                    NormalProtectSPEditor.putString(AppName, AppName);
                    NormalProtectSPEditor.commit();
                }

                NoProtectSP = getSharedPreferences("NoProtect", -1);
                if (NoProtectSP.contains(AppName) == true){
                    SharedPreferences.Editor NoProtectSPEditor = NoProtectSP.edit();
                    NoProtectSPEditor.remove(AppName);
                    NoProtectSPEditor.commit();
                }

                AllProtectSP = getSharedPreferences("AllProtect", -1);
                if (AllProtectSP.contains(AppName) == true){
                    SharedPreferences.Editor AllProtectSPEditor = AllProtectSP.edit();
                    AllProtectSPEditor.remove(AppName);
                    AllProtectSPEditor.commit();
                }
            }
        });
    }
}

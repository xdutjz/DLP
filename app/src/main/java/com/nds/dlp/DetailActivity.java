package com.nds.dlp;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent getAppIntent = getIntent();
        String PackageName = getAppIntent.getStringExtra("PackageName");
        String AppName = getAppIntent.getStringExtra("AppName");
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

    }
}

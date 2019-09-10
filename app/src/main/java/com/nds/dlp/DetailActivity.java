package com.nds.dlp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    public SharedPreferences AllProtectSP;
    public SharedPreferences NoProtectSP;
    public SharedPreferences NormalProtectSP;
    public SharedPreferences SpeAppSP;




    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        Intent getAppIntent = getIntent();
        String PackageName = getAppIntent.getStringExtra("PackageName");
        final String AppName = getAppIntent.getStringExtra("AppName");
        SpeAppSP = getSharedPreferences("SpeAppSP", -1);
        SharedPreferences.Editor SpeAppSPEditor = SpeAppSP.edit();
        SpeAppSPEditor.clear();
        SpeAppSPEditor.putString(AppName, AppName);
        SpeAppSPEditor.commit();

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


    class YesReceiver extends BroadcastReceiver {

        LinearLayout innerLayout = findViewById(R.id.innerLayout);

        @Override
        public void onReceive(Context context, Intent intent){
            String AppName = intent.getStringExtra("message1");
            String Time = intent.getStringExtra("message2");
            AllProtectSP = getSharedPreferences("AllProtect", 0);

            if (AllProtectSP.contains(AppName)){
                TextView textView1 = new TextView(DetailActivity.this);
                textView1.setTextColor(Color.BLACK);
                textView1.setText("Detected invoking and modified at " + Time);
                innerLayout.addView(textView1);

                final ScrollView scrollView1 = findViewById(R.id.ScrollView);
                scrollView1.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView1.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

            else {
                TextView textView2 = new TextView(DetailActivity.this);
                textView2.setTextColor(Color.BLACK);
                textView2.setText("Detected invoking in background and modified at " + Time);
                innerLayout.addView(textView2);

                final ScrollView scrollView2 = findViewById(R.id.ScrollView);
                scrollView2.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView2.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

        }
    }

    class NoReceiver extends BroadcastReceiver {

        LinearLayout innerLayout = findViewById(R.id.innerLayout);

        @Override
        public void onReceive(Context context, Intent intent){
            String AppName = intent.getStringExtra("message1");
            String Time = intent.getStringExtra("message2");
            NormalProtectSP = getSharedPreferences("NormalProtect", 0);

            if (NormalProtectSP.contains(AppName)){
                TextView textView3 = new TextView(DetailActivity.this);
                textView3.setTextColor(Color.BLACK);
                textView3.setText("Detected invoking and modified at " + Time);
                innerLayout.addView(textView3);

                final ScrollView scrollView3 = findViewById(R.id.ScrollView);
                scrollView3.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView3.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

            else {
                TextView textView4 = new TextView(DetailActivity.this);
                textView4.setTextColor(Color.BLACK);
                textView4.setText("Detected invoking in background and modified at " + Time);
                innerLayout.addView(textView4);

                final ScrollView scrollView4 = findViewById(R.id.ScrollView);
                scrollView4.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView4.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

        }
    }
}

package com.nds.dlp;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;

import de.robv.android.xposed.XposedBridge;

public class DetailActivity extends AppCompatActivity {

    public SharedPreferences AllProtectSP;
    public SharedPreferences NoProtectSP;
    public SharedPreferences NormalProtectSP;
    public SharedPreferences SpeAppSP;
    public SharedPreferences MessageSP;

    Context context = this;

    private IntentFilter intentFilter1;
    private IntentFilter intentFilter2;

    private YesReceiver yesReceiver;
    private NoReceiver noReceiver;

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

        intentFilter1 = new IntentFilter();
        intentFilter2 = new IntentFilter();

        intentFilter1.addAction("com.nds.dlp.Yes_Receiver");
        intentFilter2.addAction("com.nds.dlp.No_Receiver");

        yesReceiver = new YesReceiver();
        noReceiver = new NoReceiver();

        registerReceiver(yesReceiver, intentFilter1);
        registerReceiver(noReceiver, intentFilter2);

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

                onCreateDialog();
            }
        });
    }


    //Todo we got another problem here, in Normal Style we failed on hooking various Applications.
    class YesReceiver extends BroadcastReceiver {

        LinearLayout innerLayout = findViewById(R.id.innerLayout);

        @Override
        public void onReceive(Context context, Intent intent){
            String AppName = intent.getStringExtra("Message");
            String Time = getTime();
            AllProtectSP = getSharedPreferences("AllProtect", 0);
            NormalProtectSP = getSharedPreferences("NormalProtect", 0);
            MessageSP = getSharedPreferences("Message", 0);


            if (AllProtectSP.contains(AppName)){
                String LastMessage = "Detected invoking and modified at " + Time;
                if (!MessageSP.contains(LastMessage)){
                    TextView textView1 = new TextView(DetailActivity.this);
                    textView1.setTextColor(Color.RED);
                    textView1.setText(LastMessage);
                    innerLayout.addView(textView1);

                    final ScrollView scrollView1 = findViewById(R.id.ScrollView);
                    scrollView1.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView1.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                    SharedPreferences.Editor MessageEditor = MessageSP.edit();
                    MessageEditor.clear();
                    MessageEditor.putString(LastMessage, LastMessage);
                    MessageEditor.commit();
                }

            }

            else if(NormalProtectSP.contains(AppName)){
                String LastMessage = "Detected invoking in background and modified at " + Time;
                if (!MessageSP.contains(LastMessage)){
                    TextView textView = new TextView(DetailActivity.this);
                    textView.setTextColor(Color.BLUE);
                    textView.setText(LastMessage);
                    innerLayout.addView(textView);

                    final ScrollView scrollView = findViewById(R.id.ScrollView);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                    SharedPreferences.Editor MessageEditor = MessageSP.edit();
                    MessageEditor.clear();
                    MessageEditor.putString(LastMessage, LastMessage);
                    MessageEditor.commit();
                }
            }
        }
    }

    class NoReceiver extends BroadcastReceiver {

        LinearLayout innerLayout2 = findViewById(R.id.innerLayout);

        @Override
        public void onReceive(Context context, Intent intent){
            String AppName = intent.getStringExtra("Message");
            String Time = getTime();
            NormalProtectSP = getSharedPreferences("NormalProtect", 0);
            MessageSP = getSharedPreferences("Message", 0);

            if (NormalProtectSP.contains(AppName)){
                String LastMessage = "Detected invoking in foreground and unmodified at " + Time;
                if (!MessageSP.contains(LastMessage)){
                    TextView textView3 = new TextView(DetailActivity.this);
                    textView3.setTextColor(Color.BLACK);
                    textView3.setText(LastMessage);
                    innerLayout2.addView(textView3);

                    final ScrollView scrollView3 = findViewById(R.id.ScrollView);
                    scrollView3.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView3.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                    SharedPreferences.Editor MessageEditor = MessageSP.edit();
                    MessageEditor.clear();
                    MessageEditor.putString(LastMessage, LastMessage);
                    MessageEditor.commit();

                }
            }
        }
    }

    private String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String getTime = format.format(curDate);

        return getTime;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(yesReceiver);
        unregisterReceiver(noReceiver);
    }

    /*private void inputTitleDialog() {

        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title").setIcon(null).setView(inputServer).setNegativeButton(null), null);
        builder.setPositiveButton(getString(R.string.record_save_dialog_ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String inputName = inputServer.getText().toString();
                    }
                });
        builder.show();
    }*/

    public void alert_edit() {
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("Input Time(t) and α.")
            .setIcon(android.R.drawable.sym_def_app_icon)
            .setView(et)
            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //按下确定键后的事件
                    Toast.makeText(getApplicationContext(), et.getText().toString(),Toast.LENGTH_LONG).show();
                }
            }).setNegativeButton("Cancel",null).show();
    }



    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog, null))
                // Add action buttons
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton("Cancel", null).show();
        return builder.create();
    }
}

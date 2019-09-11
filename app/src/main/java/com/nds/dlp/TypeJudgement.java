package com.nds.dlp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.List;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class TypeJudgement {
    public String judge(String Appname, Context context){
        String type = "";
        XSharedPreferences AllProtect = new XSharedPreferences("com.nds.dlp",
                "AllProtect");
        XSharedPreferences NoProtect = new XSharedPreferences("com.nds.dlp",
                "NoProtect");
        XSharedPreferences NormalProtect = new XSharedPreferences("com.nds.dlp",
                "NormalProtect");
        XSharedPreferences SpeAppSP = new XSharedPreferences("com.nds.dlp",
                "SpeAppSP");

        if (AllProtect.contains(Appname)){
            type = "yes";
            if (SpeAppSP.contains(Appname)){
                Intent intent = new Intent("com.nds.dlp.Yes_Receiver");
                String Message = Appname;
                intent.putExtra("Message", Message);
                context.sendBroadcast(intent);
            }
        }
        else if (NoProtect.contains(Appname)){
            type = "no";
        }
        else if (NormalProtect.contains(Appname)){
            //get ForegroundApps List
            List<AndroidAppProcess> foregroundApps =
                    AndroidProcesses.getRunningForegroundApps(context);

            //judge if it is THE foreground app
            if (foregroundApps.size() != 0){
                //get THE foreground app
                String myForegroundApp = foregroundApps.get(foregroundApps.size()-1).name;

                if (myForegroundApp.equals(context.getPackageName())){
                    type = "no";
                    Intent intent = new Intent("com.nds.dlp.No_Receiver");
                    String Message = Appname;
                    intent.putExtra("Message", Message);
                    context.sendBroadcast(intent);
                }
                else {
                    type = "yes";
                    Intent intent = new Intent("com.nds.dlp.Yes_Receiver");
                    String Message = Appname;
                    intent.putExtra("Message", Message);
                    context.sendBroadcast(intent);
                }
            }
            else {
                type = "no";
            }
        }
        else {
            type = "no";
        }

        return type;

    }
}

package id.co.myproject.yukkajian.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.view.KajianReminderScreenActivity;

public class AlarmScreenReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmScreenReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
        wakeLock.acquire();
        Kajian kajian = intent.getParcelableExtra("kajian");
        Intent intent1 = new Intent(context.getApplicationContext(), KajianReminderScreenActivity.class);
        intent1.putExtra("kajian", kajian);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent1);

        Log.d(TAG, "onReceive: Tangkap Screen");
        
    }
}

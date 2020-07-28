package id.co.myproject.yukkajian.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import id.co.myproject.yukkajian.helper.CekUpdateData;
import id.co.myproject.yukkajian.helper.ReminderCekProses;

public class ReminderCekService extends Service {
    private static final String TAG = "ReminderCekService";
    public ReminderCekService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new ReminderCekProses(getApplicationContext()).mHandler.sendEmptyMessage(0);
        new CekUpdateData(getApplicationContext()).mHandler.sendEmptyMessage(1);
        Log.d(TAG, "onStartCommand: Service aktif");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Service hancur");
    }
}

package id.co.myproject.yukkajian.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Date;

import androidx.core.app.NotificationCompat;
import id.co.myproject.yukkajian.helper.Utils;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.view.KajianReminderScreenActivity;
import id.co.myproject.yukkajian.view.MainActivity;
import id.co.myproject.yukkajian.R;

public class TimeReceiver extends BroadcastReceiver {

    int idTime, typeAlarm;
    Kajian kajian;
    private static final String TAG = "TimeReceiver";
    public static final int TYPE_ALARM_SCREEN = 965;
    public static final int TYPE_ALARM_NOTIF = 975;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
        wakeLock.acquire();
        String action = intent.getAction();
        if (action.equals(Utils.UPDATE_DATA_RECEIVER)){
            Kajian kajian = intent.getParcelableExtra("data_notif");
            Date date = kajian.getTanggal();
            String hari = DateFormat.format("EEEE", date).toString();
            String tanggal = DateFormat.format("dd MMMM yyyy", date).toString();
            sendNotification(context, context.getResources().getString(R.string.app_name), "Kajian terbaru : "+
                    kajian.getJudul_kajian()+"Pada tanggal "+hari+","+tanggal);
        }else {
            String tanggal = intent.getStringExtra("tanggal");
            String judulKajian = intent.getStringExtra("judul_kajian");
            kajian = intent.getParcelableExtra("kajian");
            typeAlarm = intent.getIntExtra(Utils.ALARM_TYPE_INTENT, 0);
            idTime = intent.getIntExtra("id_time", 0);
            sendNotification(context, context.getResources().getString(R.string.app_name), intent.getStringExtra("judul_kajian"));
            new SetWaktu(context).setAlarm(tanggal, judulKajian, idTime, kajian, typeAlarm);
            Log.d(TAG, "onReceive: Tangkap");
        }
    }

    private void sendNotification(Context context, String title, String desc){
        Log.d(TAG, "sendNotification: mucul notif");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent newIntent = new Intent(context, MainActivity.class);
        newIntent.putExtra("notifkey", "notifvalue");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "rasupe_channel_id";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int important = NotificationManager.IMPORTANCE_HIGH;
            String NOTIFICATION_CHANNEL_NAME = "rasupe channel";
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, important
            );
            notificationManager.createNotificationChannel(channel);
        }
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, desc)
                .setSmallIcon(R.mipmap.launcher)
                .setContentTitle(title)
                .setContentText(desc)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(uri)
                .setAutoCancel(true);

        int NOTIF_ID = 998;
        notificationManager.notify(NOTIF_ID, builder.build());

        new SetWaktu(context).stopAlarmManager(idTime);

        if (typeAlarm == TYPE_ALARM_SCREEN) {
            Intent intent = new Intent(context.getApplicationContext(), KajianReminderScreenActivity.class);
            intent.putExtra("kajian", kajian);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }

    }

}

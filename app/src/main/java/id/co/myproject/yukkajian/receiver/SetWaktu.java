package id.co.myproject.yukkajian.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.co.myproject.yukkajian.helper.Utils;
import id.co.myproject.yukkajian.local.ReminderHelper;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.view.KajianReminderScreenActivity;

public class SetWaktu {
    private static final String TAG = "SetWaktu";
    private Context context;
    public SetWaktu(Context context) {
        this.context = context;
    }

    public void setAlarmScreen(String tanggal, Kajian kajian, int idTime){
        long time;

        Intent intent = new Intent(context.getApplicationContext(), KajianReminderScreenActivity.class);
        intent.putExtra("kajian", kajian);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(),
                idTime, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(tanggal));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendarNow = Calendar.getInstance();
        if (calendarNow.before(calendar)){
            time = calendar.getTimeInMillis();
        }else {
            calendar.add(Calendar.DATE, 1);
            time = calendar.getTimeInMillis();
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, time, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager.setExact(AlarmManager.RTC, time, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC, time, pendingIntent);
        }
    }

    public void setAlarm(String tanggal, String judulKajian, int idTime, Kajian kajian, int typeAlarm){
        long time;

        Intent intent = new Intent(context, TimeReceiver.class);
        intent.putExtra("tanggal", tanggal);
        intent.putExtra("kajian", kajian);
        intent.putExtra("judul_kajian", judulKajian);
        intent.putExtra(Utils.ALARM_TYPE_INTENT, typeAlarm);
        intent.putExtra("id_time", idTime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, idTime, intent, 0);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(tanggal));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendarNow = Calendar.getInstance();
        if (calendarNow.before(calendar)){
            time = calendar.getTimeInMillis();
        }else {
            calendar.add(Calendar.DATE, 1);
            time = calendar.getTimeInMillis();
        }



        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
        Log.i(TAG, "set alarm manager");

    }

    public void stopAlarmManager(int idTime){
        Intent alermIntent = new Intent(context, TimeReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, idTime, alermIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        Log.i(TAG, "alarm manager dimatikan");
    }

    public void stopAlarmScreenManager(int idTime){
        Intent alermIntent = new Intent(context, KajianReminderScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, idTime, alermIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Log.i(TAG, "alarm manager screen dimatikan");
    }

}

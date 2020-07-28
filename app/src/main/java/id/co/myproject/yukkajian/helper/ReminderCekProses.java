package id.co.myproject.yukkajian.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.List;

import id.co.myproject.yukkajian.local.ReminderHelper;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.helper.Utils;

public class ReminderCekProses extends View {

    private static final String TAG = "ReminderTimeout";
    
    ReminderHelper reminderHelper;
    SharedPreferences sharedPreferences;
    int idUser;

    public ReminderCekProses(Context context) {
        super(context);
        reminderHelper = ReminderHelper.getINSTANCE(context);
        sharedPreferences = context.getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getInt(Utils.ID_USER_KEY, 0);
    }

    private void prosesHapusReminder() {
        Calendar calendarNow = Calendar.getInstance();
        List<Kajian> kajianList = reminderHelper.getAllKajianReminder(String.valueOf(idUser));
        if (kajianList.size() > 0){
            for (Kajian kajian : kajianList){
                Calendar tanggalKajian = Calendar.getInstance();
                tanggalKajian.setTime(kajian.getTanggal());
                if (calendarNow.after(tanggalKajian)){
                    reminderHelper.hapusReminder(idUser, kajian.getIdKajian());
                }
            }
        }else {
            Log.d(TAG, "init: Tidak ada reminder");
        }
    }

    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            prosesHapusReminder();
            mHandler.sendEmptyMessageDelayed(1, 1000);
            return true;

        }
    });
}

package id.co.myproject.yukkajian.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.model.Waktu;

import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.ID_KAJIAN;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.ID_USER;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.JUDUL_KAJIAN;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.PAMFLET;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.TANGGAL;
import static id.co.myproject.yukkajian.local.DatabaseContract.TABLE_REMINDER;
import static id.co.myproject.yukkajian.local.DatabaseContract.TABLE_WAKTU;
import static id.co.myproject.yukkajian.local.DatabaseContract.WaktuColumns.ID_TIME;

public class WaktuHelper {
    public static DatabaseHelper databaseHelper;
    public static WaktuHelper INSTANCE;

    private static SQLiteDatabase database;
    Context context;

    public WaktuHelper(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public static WaktuHelper getINSTANCE(Context context){
        if (INSTANCE == null){
            synchronized (SQLiteOpenHelper.class){
                if (INSTANCE == null){
                    INSTANCE = new WaktuHelper(context);
                }
            }
        }

        return INSTANCE;
    }

    public void open(){
        database = databaseHelper.getWritableDatabase();
    }

    public void close(){
        databaseHelper.close();
        if (database.isOpen()){
            database.close();
        }
    }

    public long addWaktu(String idKajian, String idTime){
        ContentValues args = new ContentValues();
        args.put(ID_KAJIAN, idKajian);
        args.put(ID_TIME, idTime);
        return database.insert(TABLE_WAKTU, null, args);
    }

    public List<Waktu> getAllWaktu(String idKajian){
        List<Waktu> waktuList = new ArrayList<>();
        Cursor cursor = database.query(TABLE_WAKTU, null,
                ID_KAJIAN+"='"+idKajian+"'",
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        Waktu waktu;
        if (cursor.getCount() > 0){
            do {
                waktu = new Waktu();
                waktu.setIdKajian(cursor.getString(cursor.getColumnIndex(ID_KAJIAN)));
                waktu.setIdTime(cursor.getString(cursor.getColumnIndex(ID_TIME)));
                waktuList.add(waktu);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return waktuList;
    }

    public long hapusWaktu(String idKajian){
        return database.delete(TABLE_WAKTU, ID_KAJIAN+" = '"+idKajian+"'", null);
    }

}

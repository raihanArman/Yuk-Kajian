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

import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.ID_KAJIAN;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.ID_USER;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.JUDUL_KAJIAN;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.PAMFLET;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.TANGGAL;
import static id.co.myproject.yukkajian.local.DatabaseContract.TABLE_REMINDER;

public class ReminderHelper {
    String pattern = "yyyy-MM-dd HH:mm:ss";
    public static DatabaseHelper databaseHelper;
    public static ReminderHelper INSTANCE;

    private static SQLiteDatabase database;
    Context context;

    public ReminderHelper(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public static ReminderHelper getINSTANCE(Context context){
        if (INSTANCE == null){
            synchronized (SQLiteOpenHelper.class){
                if (INSTANCE == null){
                    INSTANCE = new ReminderHelper(context);
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

    public long addKajianReminder(String idUser, Kajian kajian){
        String tanggal = DateFormat.format("yyyy-MM-dd HH:mm:ss", kajian.getTanggal()).toString();
        ContentValues args = new ContentValues();
        args.put(ID_USER, idUser);
        args.put(ID_KAJIAN, kajian.getIdKajian());
        args.put(JUDUL_KAJIAN, kajian.getJudul_kajian());
        args.put(PAMFLET, kajian.getGambar());
        args.put(TANGGAL, tanggal);
        return database.insert(TABLE_REMINDER, null, args);
    }

    public boolean cekReminder(String idKajian, String idUser){
        String args = ID_KAJIAN+" = '"+idKajian+"' AND "+ID_USER+" = '"+idUser+"'";
        Cursor cursor = database.query(TABLE_REMINDER, null,
                args,
                null, null,null,null, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }else {
            cursor.close();
            return true;
        }
    }

    public List<Kajian> getAllKajianReminder(String idUser){
        List<Kajian> kajianList = new ArrayList<>();
        Cursor cursor = database.query(TABLE_REMINDER, null,
                ID_USER+"='"+idUser+"'",
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        Kajian kajian;
        if (cursor.getCount() > 0){
            do {
                String tanggal = cursor.getString(cursor.getColumnIndex(TANGGAL));
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tanggal);
                    kajian = new Kajian();
                    kajian.setIdKajian(cursor.getString(cursor.getColumnIndex(ID_KAJIAN)));
                    kajian.setJudul_kajian(cursor.getString(cursor.getColumnIndex(JUDUL_KAJIAN)));
                    kajian.setGambar(cursor.getString(cursor.getColumnIndex(PAMFLET)));
                    kajian.setTanggal(date);
                    kajianList.add(kajian);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return kajianList;
    }

    public long hapusReminder(int id_user, String idKajian){
        return database.delete(TABLE_REMINDER, ID_USER+" = '"+id_user+"' AND "+ID_KAJIAN+" = '"+idKajian+"'", null);
    }

}

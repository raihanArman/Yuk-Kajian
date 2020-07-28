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
import id.co.myproject.yukkajian.model.Lampiran;

import static id.co.myproject.yukkajian.local.DatabaseContract.LampiranColumns.GAMBAR;
import static id.co.myproject.yukkajian.local.DatabaseContract.LampiranColumns.ID_GAMBAR;
import static id.co.myproject.yukkajian.local.DatabaseContract.LampiranColumns.KETERANGAN;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.ID_KAJIAN;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.ID_USER;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.JUDUL_KAJIAN;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.PAMFLET;
import static id.co.myproject.yukkajian.local.DatabaseContract.ReminderColumns.TANGGAL;
import static id.co.myproject.yukkajian.local.DatabaseContract.TABLE_LAMPIRAN;
import static id.co.myproject.yukkajian.local.DatabaseContract.TABLE_REMINDER;

public class LampiranHelper {
    String pattern = "yyyy-MM-dd HH:mm:ss";
    public static DatabaseHelper databaseHelper;
    public static LampiranHelper INSTANCE;

    private static SQLiteDatabase database;
    Context context;

    public LampiranHelper(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public static LampiranHelper getINSTANCE(Context context){
        if (INSTANCE == null){
            synchronized (SQLiteOpenHelper.class){
                if (INSTANCE == null){
                    INSTANCE = new LampiranHelper(context);
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

    public long addLampiran(String idUser, Lampiran lampiran){
        ContentValues args = new ContentValues();
        args.put(ID_USER, idUser);
        args.put(GAMBAR, lampiran.getGambar());
        args.put(KETERANGAN, lampiran.getKeterangan());
        return database.insert(TABLE_LAMPIRAN, null, args);
    }

    public List<Lampiran> getAllLampiran(String idUser){
        List<Lampiran> lampiranList = new ArrayList<>();
        Cursor cursor = database.query(TABLE_LAMPIRAN, null,
                ID_USER+"='"+idUser+"'",
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        Lampiran lampiran;
        if (cursor.getCount() > 0){
            do {
                lampiran = new Lampiran();
                lampiran.setIdLampiran(cursor.getString(cursor.getColumnIndex(ID_GAMBAR)));
                lampiran.setGambar(cursor.getString(cursor.getColumnIndex(GAMBAR)));
                lampiran.setKeterangan(cursor.getString(cursor.getColumnIndex(KETERANGAN)));
                lampiranList.add(lampiran);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return lampiranList;
    }

    public long hapusLampiran(int id_user, String idGambar){
        return database.delete(TABLE_LAMPIRAN, ID_USER+" = '"+id_user+"' AND "+ID_GAMBAR+" = '"+idGambar+"'", null);
    }

    public long cleanLampiran(int idUser){
        return database.delete(TABLE_LAMPIRAN, ID_USER+" = "+idUser, null);
    }


}

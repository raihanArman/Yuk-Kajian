package id.co.myproject.yukkajian.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "db_kajian";
    public static final int DATABASE_VERSION = 1;
    public static final String SQL_CREATE_TABLE_REMINDER = String.format("CREATE TABLE %s"+
                    "(%s TEXT NOT NULL, "+
                    "%s TEXT NOT NULL, "+
                    "%s TEXT NOT NULL, "+
                    "%s TEXT NOT NULL, "+
                    "%s TEXT NOT NULL)",
            DatabaseContract.TABLE_REMINDER,
            DatabaseContract.ReminderColumns.ID_USER,
            DatabaseContract.ReminderColumns.ID_KAJIAN,
            DatabaseContract.ReminderColumns.JUDUL_KAJIAN,
            DatabaseContract.ReminderColumns.PAMFLET,
            DatabaseContract.ReminderColumns.TANGGAL
    );

    public static final String SQL_CREATE_TABLE_WAKTU = String.format("CREATE TABLE %s"+
                    "(%s TEXT NOT NULL, "+
                    "%s TEXT NOT NULL)",
            DatabaseContract.TABLE_WAKTU,
            DatabaseContract.WaktuColumns.ID_KAJIAN,
            DatabaseContract.WaktuColumns.ID_TIME
    );
//
    public static final String SQL_CREATE_TABLE_LAMPIRAN = String.format("CREATE TABLE %s"+
                    "(%s INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    "%s TEXT NOT NULL, "+
                    "%s TEXT NOT NULL, "+
                    "%s TEXT NOT NULL)",
            DatabaseContract.TABLE_LAMPIRAN,
            DatabaseContract.LampiranColumns.ID_GAMBAR,
            DatabaseContract.LampiranColumns.ID_USER,
            DatabaseContract.LampiranColumns.GAMBAR,
            DatabaseContract.LampiranColumns.KETERANGAN
    );

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_REMINDER);
        db.execSQL(SQL_CREATE_TABLE_LAMPIRAN);
        db.execSQL(SQL_CREATE_TABLE_WAKTU);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DatabaseContract.TABLE_REMINDER);
        db.execSQL("DROP TABLE IF EXISTS "+DatabaseContract.TABLE_LAMPIRAN);
        db.execSQL("DROP TABLE IF EXISTS "+DatabaseContract.TABLE_WAKTU);
        onCreate(db);
    }
}

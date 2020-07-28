package id.co.myproject.yukkajian.local;

import android.provider.BaseColumns;

public class DatabaseContract {
    static String TABLE_REMINDER = "tb_reminder";
    static String TABLE_LAMPIRAN = "tb_lampiran";
    static String TABLE_WAKTU = "tb_waktu";
    static final class ReminderColumns implements BaseColumns{
        static String ID_USER = "id_user";
        static String ID_KAJIAN = "id_kajian";
        static String JUDUL_KAJIAN = "judul_kajian";
        static String PAMFLET = "pamflet";
        static String TANGGAL = "tanggal";
    }

    static final class LampiranColumns implements BaseColumns{
        static String ID_GAMBAR = "id_gambar";
        static String ID_USER = "id_user";
        static String GAMBAR = "gambar";
        static String KETERANGAN = "keterangan";
    }

    static final class WaktuColumns implements BaseColumns{
        static String ID_KAJIAN = "id_kajian";
        static String ID_TIME = "id_user";
    }

}

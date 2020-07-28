package id.co.myproject.yukkajian.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Lampiran {
    @SerializedName("id_lampiran")
    @Expose
    private String idLampiran;

    @SerializedName("keterangan")
    @Expose
    private String keterangan;

    @SerializedName("gambar")
    @Expose
    private String gambar;

    public Lampiran() {
    }

    public Lampiran(String idLampiran, String keterangan, String gambar) {
        this.idLampiran = idLampiran;
        this.keterangan = keterangan;
        this.gambar = gambar;
    }

    public String getIdLampiran() {
        return idLampiran;
    }

    public void setIdLampiran(String idLampiran) {
        this.idLampiran = idLampiran;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}

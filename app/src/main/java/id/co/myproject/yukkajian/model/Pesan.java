package id.co.myproject.yukkajian.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Pesan {

    @SerializedName("id_pesan")
    @Expose
    private String idPesan;

    @SerializedName("id_user")
    @Expose
    private String idUser;

    @SerializedName("pengirim")
    @Expose
    private String pengirim;

    @SerializedName("isi")
    @Expose
    private String isi;

    @SerializedName("tanggal")
    @Expose
    private Date tanggal;

    @SerializedName("jumlah_pesan")
    @Expose
    private String jumlahPesan;

    public Pesan() {
    }

    public Pesan(String idPesan, String idUser, String pengirim, String isi, Date tanggal) {
        this.idPesan = idPesan;
        this.idUser = idUser;
        this.pengirim = pengirim;
        this.isi = isi;
        this.tanggal = tanggal;
    }

    public String getIdPesan() {
        return idPesan;
    }

    public void setIdPesan(String idPesan) {
        this.idPesan = idPesan;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getPengirim() {
        return pengirim;
    }

    public void setPengirim(String pengirim) {
        this.pengirim = pengirim;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getJumlahPesan() {
        return jumlahPesan;
    }

    public void setJumlahPesan(String jumlahPesan) {
        this.jumlahPesan = jumlahPesan;
    }
}

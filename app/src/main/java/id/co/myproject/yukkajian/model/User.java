package id.co.myproject.yukkajian.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id_user")
    @Expose
    private String idUser;

    @SerializedName("nama")
    @Expose
    private String nama;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("alamat")
    @Expose
    private String alamat;

    @SerializedName("no_telp")
    @Expose
    private String noTelp;

    @SerializedName("gambar")
    @Expose
    private String gambar;

    public User() {
    }

    public User(String idUser, String nama, String email, String alamat, String noTelp, String gambar) {
        this.idUser = idUser;
        this.nama = nama;
        this.email = email;
        this.alamat = alamat;
        this.noTelp = noTelp;
        this.gambar = gambar;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}

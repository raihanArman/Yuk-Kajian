package id.co.myproject.yukkajian.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Kajian implements Parcelable {

    @SerializedName("id_kajian")
    @Expose
    private String idKajian;

    @SerializedName("id_jenis_kajian")
    @Expose
    private String idJenisKajian;

    @SerializedName("id_kategori")
    @Expose
    private String idKategori;

    @SerializedName("judul_kajian")
    @Expose
    private String judul_kajian;

    @SerializedName("pemateri")
    @Expose
    private String pemateri;

    @SerializedName("tanggal")
    @Expose
    private Date tanggal;

    @SerializedName("tanggal_upload")
    @Expose
    private Date tanggalUpload;

    @SerializedName("gambar")
    @Expose
    private String gambar;

    @SerializedName("lokasi")
    @Expose
    private String lokasi;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("id_usulan")
    @Expose
    private String idUsulan;

    @SerializedName("no_telp_pemateri")
    @Expose
    private String noTelpPemateri;

    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("latlng")
    @Expose
    private String latlng;

    public Kajian() {
    }

    public Kajian(String idKajian, String idJenisKajian, String idKategori, String judul_kajian, String pemateri, Date tanggal, Date tanggalUpload, String gambar, String lokasi, String status, String idUsulan, String noTelpPemateri, String latlng, String link) {
        this.idKajian = idKajian;
        this.idJenisKajian = idJenisKajian;
        this.idKategori = idKategori;
        this.judul_kajian = judul_kajian;
        this.pemateri = pemateri;
        this.tanggal = tanggal;
        this.tanggalUpload = tanggalUpload;
        this.gambar = gambar;
        this.lokasi = lokasi;
        this.status = status;
        this.idUsulan = idUsulan;
        this.noTelpPemateri = noTelpPemateri;
        this.latlng = latlng;
        this.link = link;
    }


    public String getIdKajian() {
        return idKajian;
    }

    public void setIdKajian(String idKajian) {
        this.idKajian = idKajian;
    }

    public String getIdJenisKajian() {
        return idJenisKajian;
    }

    public void setIdJenisKajian(String idJenisKajian) {
        this.idJenisKajian = idJenisKajian;
    }

    public String getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }

    public String getJudul_kajian() {
        return judul_kajian;
    }

    public void setJudul_kajian(String judul_kajian) {
        this.judul_kajian = judul_kajian;
    }

    public String getPemateri() {
        return pemateri;
    }

    public void setPemateri(String pemateri) {
        this.pemateri = pemateri;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public Date getTanggalUpload() {
        return tanggalUpload;
    }

    public void setTanggalUpload(Date tanggalUpload) {
        this.tanggalUpload = tanggalUpload;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdUsulan() {
        return idUsulan;
    }

    public void setIdUsulan(String idUsulan) {
        this.idUsulan = idUsulan;
    }

    public String getNoTelpPemateri() {
        return noTelpPemateri;
    }

    public void setNoTelpPemateri(String noTelpPemateri) {
        this.noTelpPemateri = noTelpPemateri;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.idKajian);
        dest.writeString(this.idJenisKajian);
        dest.writeString(this.idKategori);
        dest.writeString(this.judul_kajian);
        dest.writeString(this.pemateri);
        dest.writeLong(this.tanggal != null ? this.tanggal.getTime() : -1);
        dest.writeLong(this.tanggalUpload != null ? this.tanggalUpload.getTime() : -1);
        dest.writeString(this.gambar);
        dest.writeString(this.lokasi);
        dest.writeString(this.status);
        dest.writeString(this.idUsulan);
        dest.writeString(this.noTelpPemateri);
        dest.writeString(this.link);
        dest.writeString(this.latlng);
    }

    protected Kajian(Parcel in) {
        this.idKajian = in.readString();
        this.idJenisKajian = in.readString();
        this.idKategori = in.readString();
        this.judul_kajian = in.readString();
        this.pemateri = in.readString();
        long tmpTanggal = in.readLong();
        this.tanggal = tmpTanggal == -1 ? null : new Date(tmpTanggal);
        long tmpTanggalUpload = in.readLong();
        this.tanggalUpload = tmpTanggalUpload == -1 ? null : new Date(tmpTanggalUpload);
        this.gambar = in.readString();
        this.lokasi = in.readString();
        this.status = in.readString();
        this.idUsulan = in.readString();
        this.noTelpPemateri = in.readString();
        this.link = in.readString();
        this.latlng = in.readString();
    }

    public static final Creator<Kajian> CREATOR = new Creator<Kajian>() {
        @Override
        public Kajian createFromParcel(Parcel source) {
            return new Kajian(source);
        }

        @Override
        public Kajian[] newArray(int size) {
            return new Kajian[size];
        }
    };
}

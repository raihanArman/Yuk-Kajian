package id.co.myproject.yukkajian.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JenisKajian {

    @SerializedName("id_jenis_kajian")
    @Expose
    private String idJenisKajian;

    @SerializedName("nama_jenis_kajian")
    @Expose
    private String namaJenisKajian;

    public JenisKajian(String idJenisKajian, String namaJenisKajian) {
        this.idJenisKajian = idJenisKajian;
        this.namaJenisKajian = namaJenisKajian;
    }

    public String getIdJenisKajian() {
        return idJenisKajian;
    }

    public void setIdJenisKajian(String idJenisKajian) {
        this.idJenisKajian = idJenisKajian;
    }

    public String getNamaJenisKajian() {
        return namaJenisKajian;
    }

    public void setNamaJenisKajian(String namaJenisKajian) {
        this.namaJenisKajian = namaJenisKajian;
    }
}

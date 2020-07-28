package id.co.myproject.yukkajian.model;

public class Waktu {

    private String idKajian;
    private String idTime;

    public Waktu() {
    }

    public Waktu(String idKajian, String idTime) {
        this.idKajian = idKajian;
        this.idTime = idTime;
    }

    public String getIdKajian() {
        return idKajian;
    }

    public void setIdKajian(String idKajian) {
        this.idKajian = idKajian;
    }

    public String getIdTime() {
        return idTime;
    }

    public void setIdTime(String idTime) {
        this.idTime = idTime;
    }
}

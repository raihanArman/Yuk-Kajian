package id.co.myproject.yukkajian.view.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import id.co.myproject.yukkajian.BuildConfig;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.local.ReminderHelper;
import id.co.myproject.yukkajian.local.WaktuHelper;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.model.Kategori;
import id.co.myproject.yukkajian.receiver.SetWaktu;
import id.co.myproject.yukkajian.receiver.TimeReceiver;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import id.co.myproject.yukkajian.helper.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.Random;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    LinearLayout lvContainerDetail, lvKoneksi;
    ProgressDialog progressDialog;

    ImageView ivPamflet, ivBack;
    TextView tvJudulKajian, tvPemateri, tvTanggal, tvJam, tvLokasi, tvKategori, tvReminder;
    CardView cvReminder, cvLink;
    TextView tvLink;
    LinearLayout lvSetRemider, lvCekLokasi, lvKunjungi;
    Toolbar toolbar;
    ApiRequest apiRequest;
    ReminderHelper reminderHelper;
    WaktuHelper waktuHelper;
    String idKajian;
    int idUser;
    SharedPreferences sharedPreferences;
    Kajian kajian;
    SetWaktu setWaktu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        progressDialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getInt(Utils.ID_USER_KEY, 0);
        kajian = getIntent().getParcelableExtra("kajian");
        idKajian = getIntent().getStringExtra("id_kajian");
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
        reminderHelper = ReminderHelper.getINSTANCE(this);
        waktuHelper = WaktuHelper.getINSTANCE(this);
        reminderHelper.open();
        waktuHelper.open();

        lvContainerDetail = findViewById(R.id.lv_container_detail);
        lvKoneksi = findViewById(R.id.lv_connection);
        ivPamflet = findViewById(R.id.iv_pamflet);
        ivBack = findViewById(R.id.iv_back);
        tvReminder = findViewById(R.id.tv_reminder);
        cvReminder = findViewById(R.id.cv_set_reminder);
        tvJudulKajian = findViewById(R.id.tv_judul_kajian);
        tvPemateri = findViewById(R.id.tv_pemateri);
        tvTanggal = findViewById(R.id.tv_tanggal);
        tvJam = findViewById(R.id.tv_jam);
        tvLokasi = findViewById(R.id.tv_lokasi);
        tvKategori = findViewById(R.id.tv_kategori);
        lvSetRemider = findViewById(R.id.lv_set_reminder);
        lvCekLokasi = findViewById(R.id.lv_cek_lokasi);
        toolbar = findViewById(R.id.toolbar);
        lvKunjungi = findViewById(R.id.lv_kunjungi);
        cvLink =findViewById(R.id.cv_link);
        tvLink = findViewById(R.id.tv_link);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        setWaktu = new SetWaktu(this);

        lvCekLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(DetailActivity.this);
                progressDialog.setMessage("Proses ...");
                progressDialog.show();
                Intent intent = new Intent(DetailActivity.this, LokasiActivity.class);
                intent.putExtra("kajian", kajian);
                intent.putExtra("type_intent", LokasiActivity.INTENT_LOKASI_KAJIAN);
                startActivity(intent);
                progressDialog.dismiss();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lvKunjungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("http://"+tvLink.getText().toString()));
                startActivity(browserIntent);
            }
        });

        if (Utils.isConnectionInternet(DetailActivity.this)){
            lvContainerDetail.setVisibility(View.VISIBLE);
            lvKoneksi.setVisibility(View.GONE);
            loadData();
        }else {
            lvContainerDetail.setVisibility(View.INVISIBLE);
            lvKoneksi.setVisibility(View.VISIBLE);
            return;
        }


    }

    private void loadData() {
        Call<Kajian> kajianCall = apiRequest.kajianByIdRequest(idKajian);
        kajianCall.enqueue(new Callback<Kajian>() {
            @Override
            public void onResponse(Call<Kajian> call, Response<Kajian> response) {
                Kajian kajian = response.body();
                Glide.with(DetailActivity.this).load(BuildConfig.BASE_URL_GAMBAR+"pamflet/"+kajian.getGambar()).into(ivPamflet);

                tvJudulKajian.setText(kajian.getJudul_kajian());
                tvPemateri.setText(kajian.getPemateri());

                String tanggal = DateFormat.format("dd MM yyyy", kajian.getTanggal()).toString();
                String jam = DateFormat.format("HH:mm", kajian.getTanggal()).toString();
                Calendar c = Calendar.getInstance();
                c.setTime(kajian.getTanggal());
                int hari = c.get(Calendar.DAY_OF_WEEK);

                Calendar nowCalendar = Calendar.getInstance();
                if (c.before(nowCalendar)){
                    lvSetRemider.setVisibility(View.GONE);
                    cvReminder.setVisibility(View.GONE);
                }

                if (kajian.getLink().equals("")){
                    cvLink.setVisibility(View.GONE);
                }else {
                    tvLink.setText(kajian.getLink());
                }

                tvTanggal.setText(Utils.getDayName(hari)+", "+tanggal);
                tvJam.setText("Jam : "+jam);
                tvLokasi.setText(kajian.getLokasi());

                Call<Kategori> callKategoriById = apiRequest.kategoriByIdRequest(kajian.getIdKategori());
                callKategoriById.enqueue(new Callback<Kategori>() {
                    @Override
                    public void onResponse(Call<Kategori> call, Response<Kategori> response) {
                        tvKategori.setText(response.body().getNamaKategori());
                    }

                    @Override
                    public void onFailure(Call<Kategori> call, Throwable t) {

                    }
                });

                cekReminder(kajian, lvSetRemider);
            }

            @Override
            public void onFailure(Call<Kajian> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage());
            }
        });

    }

    private void cekReminder(Kajian kajian, LinearLayout lvSetRemider) {
        if (reminderHelper.cekReminder(kajian.getIdKajian(), String.valueOf(idUser))){
            lvSetRemider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            tvReminder.setText("Sudah di set");
        }else {
            lvSetRemider.setBackgroundColor(getResources().getColor(android.R.color.white));
            lvSetRemider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressDialog.show();
                    long result = reminderHelper.addKajianReminder(String.valueOf(idUser), kajian);
                    if (result > 0){
                        String tanggalKajian = DateFormat.format("yyyy-MM-dd HH:mm", kajian.getTanggal()).toString();

                        Calendar calendarMin30 = Calendar.getInstance();
                        Calendar calendarMin15 = Calendar.getInstance();
                        Calendar calendarMin10 = Calendar.getInstance();
                        Calendar calendarScreen = Calendar.getInstance();
                        calendarMin30.setTime(kajian.getTanggal());
                        calendarMin15.setTime(kajian.getTanggal());
                        calendarScreen.setTime(kajian.getTanggal());

                        calendarMin15.add(Calendar.MINUTE, -15);
                        calendarMin30.add(Calendar.MINUTE, -30);
                        calendarMin10.add(Calendar.MINUTE, -10);
                        calendarScreen.add(Calendar.SECOND, 3);

                        String tanggalKajianMin15 = DateFormat.format("yyyy-MM-dd HH:mm", calendarMin15).toString();
                        String tanggalKajianMin30 = DateFormat.format("yyyy-MM-dd HH:mm", calendarMin30).toString();
                        String tanggalKajianScreen = DateFormat.format("yyyy-MM-dd HH:mm", calendarScreen).toString();


                        Random random = new Random();
                        int randomNumber = random.nextInt(900) + 100;
                        int randomNumber15 = random.nextInt(900) + 100;
                        int randomNumber30 = random.nextInt(900) + 100;
                        int randomNumberScreen = random.nextInt(900) + 100;

                        setWaktu.setAlarm(tanggalKajian,tvJudulKajian.getText().toString()+"\nSudah dimulai", randomNumber, kajian, TimeReceiver.TYPE_ALARM_SCREEN);
                        setWaktu.setAlarm(tanggalKajianMin15,tvJudulKajian.getText().toString()+"\nTinggal 15 menit lagi", randomNumber15, kajian, TimeReceiver.TYPE_ALARM_NOTIF);
                        setWaktu.setAlarm(tanggalKajianMin30,tvJudulKajian.getText().toString()+"\nTinggal 30 menit lagi", randomNumber30, kajian, TimeReceiver.TYPE_ALARM_NOTIF);
                        long resultWaktu = waktuHelper.addWaktu(kajian.getIdKajian(), String.valueOf(randomNumber));
                        if (resultWaktu > 0){
                            long resultWaktu2 = waktuHelper.addWaktu(kajian.getIdKajian(), String.valueOf(randomNumber15));
                            if (resultWaktu2 > 0){
                                long resultWaktu3 = waktuHelper.addWaktu(kajian.getIdKajian(), String.valueOf(randomNumber30));
                                if (resultWaktu3 > 0){
                                    Toast.makeText(DetailActivity.this, "Berhasil di set", Toast.LENGTH_LONG).show();
                                    tvReminder.setText("Sudah di set");
                                    lvSetRemider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                    progressDialog.dismiss();
                                }else {
                                    Toast.makeText(DetailActivity.this, "Gagal waktu 30", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(DetailActivity.this, "Gagal waktu 15", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(DetailActivity.this, "gagal waktu", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(DetailActivity.this, "gagal", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

package id.co.myproject.yukkajian.view.usulan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import id.co.myproject.yukkajian.BuildConfig;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.adapter.LampiranAdapter;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.model.Kategori;
import id.co.myproject.yukkajian.model.Lampiran;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import id.co.myproject.yukkajian.helper.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;

public class DetailKajianUsulanActivity extends AppCompatActivity {

    ImageView ivPamflet, ivBack;
    TextView tvJudulKajian, tvPemateri, tvTanggal, tvJam, tvLokasi, tvKategori, tvStatus;
    CardView cvLink;
    TextView tvLink;
    LinearLayout lvKunjungi;
    ApiRequest apiRequest;
    String idKajian, idUsulan;
    Kajian kajian;
    RecyclerView rvLampiran;
    LampiranAdapter lampiranAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kajian_usulan);

        kajian = getIntent().getParcelableExtra("kajian");
        idKajian = getIntent().getStringExtra("id_kajian");
        idUsulan = getIntent().getStringExtra("id_usulan");
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);

        cvLink =findViewById(R.id.cv_link);
        tvLink = findViewById(R.id.tv_link);
        ivPamflet = findViewById(R.id.iv_pamflet);
        ivBack = findViewById(R.id.iv_back);
        tvJudulKajian = findViewById(R.id.tv_judul_kajian);
        tvPemateri = findViewById(R.id.tv_pemateri);
        lvKunjungi = findViewById(R.id.lv_kunjungi);
        tvTanggal = findViewById(R.id.tv_tanggal);
        tvJam = findViewById(R.id.tv_jam);
        tvLokasi = findViewById(R.id.tv_lokasi);
        tvKategori = findViewById(R.id.tv_kategori);
        tvStatus = findViewById(R.id.tv_status);
        rvLampiran = findViewById(R.id.rv_lampiran);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvLampiran.setLayoutManager(linearLayoutManager);
        lampiranAdapter = new LampiranAdapter(this);
        rvLampiran.setAdapter(lampiranAdapter);

        loadData();
        loadLampiran();

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

    }

    private void loadLampiran() {
        Call<List<Lampiran>> callLampiran = apiRequest.allLampiranRequest(idUsulan);
        callLampiran.enqueue(new Callback<List<Lampiran>>() {
            @Override
            public void onResponse(Call<List<Lampiran>> call, Response<List<Lampiran>> response) {
                if (response.isSuccessful()){
                    List<Lampiran> lampiranList = response.body();
                    lampiranAdapter.setKajianList(lampiranList);
                }
            }

            @Override
            public void onFailure(Call<List<Lampiran>> call, Throwable t) {

            }
        });
    }

    private void loadData() {
        Glide.with(this).load(BuildConfig.BASE_URL_GAMBAR+"pamflet/"+kajian.getGambar()).into(ivPamflet);
        tvJudulKajian.setText(kajian.getJudul_kajian());
        tvPemateri.setText(kajian.getPemateri());

        String tanggal = DateFormat.format("dd MM yyyy", kajian.getTanggal()).toString();
        String jam = DateFormat.format("HH:mm", kajian.getTanggal()).toString();
        Calendar c = Calendar.getInstance();
        c.setTime(kajian.getTanggal());
        int hari = c.get(Calendar.DAY_OF_WEEK);

        tvTanggal.setText(Utils.getDayName(hari)+", "+tanggal);
        tvJam.setText("Jam : "+jam);
        tvLokasi.setText(kajian.getLokasi());
        tvStatus.setText(kajian.getStatus());

        if (kajian.getLink().equals("")){
            cvLink.setVisibility(View.GONE);
        }else {
            tvLink.setText(kajian.getLink());
        }

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
    }
}

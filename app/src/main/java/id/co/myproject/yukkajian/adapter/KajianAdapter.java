package id.co.myproject.yukkajian.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import id.co.myproject.yukkajian.BuildConfig;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.model.Kategori;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.helper.Utils;
import id.co.myproject.yukkajian.view.detail.DetailActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.co.myproject.yukkajian.helper.Utils.getDayName;

public class KajianAdapter extends RecyclerView.Adapter<KajianAdapter.ViewHolder> {
    Context context;
    List<Kajian> kajianList = new ArrayList<>();
    ApiRequest apiRequest;

    public KajianAdapter(Context context, ApiRequest apiRequest) {
        this.context = context;
        this.apiRequest = apiRequest;
    }

    public void setKajianList(List<Kajian> kajianList){
        this.kajianList.clear();
        this.kajianList.addAll(kajianList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KajianAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kajian, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KajianAdapter.ViewHolder holder, int position) {
        Kajian kajian = kajianList.get(position);
        Call<Kategori> callKategori = apiRequest.kategoriByIdRequest(kajian.getIdKategori());
        callKategori.enqueue(new Callback<Kategori>() {
            @Override
            public void onResponse(Call<Kategori> call, Response<Kategori> response) {
                holder.tvkategori.setText("Kategori : "+response.body().getNamaKategori());
            }

            @Override
            public void onFailure(Call<Kategori> call, Throwable t) {

            }
        });
        holder.tvJudulKajian.setText(kajian.getJudul_kajian());
        holder.tvLokasi.setText(kajian.getLokasi());

        String tanggal = DateFormat.format("dd MMM yyyy", kajianList.get(position).getTanggal()).toString();
        Calendar c = Calendar.getInstance();
        c.setTime(kajian.getTanggal());
        int hari = c.get(Calendar.DAY_OF_WEEK);

        Calendar c2 =Calendar.getInstance();
        c2.set(Calendar.DAY_OF_WEEK,Utils.getDayNumber("Senin"));
        c2.set(Calendar.HOUR_OF_DAY,0);
        c2.set(Calendar.MINUTE,0);
        c2.set(Calendar.SECOND,0);
        Date date = c2.getTime();

        Calendar now = Calendar.getInstance();
        Date dateNow = now.getTime();
        if (date.before(dateNow)){
            c2.add(Calendar.DATE, 7);
        }


        String cobah = DateFormat.format("dd/MM/yyyy", c2.getTime()).toString();
//        Toast.makeText(context, ""+cobah, Toast.LENGTH_SHORT).show();

        String jam = DateFormat.format("HH:mm", kajianList.get(position).getTanggal()).toString();
        holder.tvTanggal.setText(getDayName(hari)+", "+tanggal);
        holder.tvJam.setText("Jam : "+jam);

        Glide.with(context).load(BuildConfig.BASE_URL_GAMBAR+"pamflet/"+kajian.getGambar()).override(300, 200).into(holder.ivPamflet);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("id_kajian", kajian.getIdKajian());
                intent.putExtra("kajian", kajian);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return kajianList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivPamflet;
        TextView tvkategori, tvJudulKajian, tvTanggal, tvJam, tvLokasi;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPamflet = itemView.findViewById(R.id.iv_pamflet);
            tvJudulKajian = itemView.findViewById(R.id.tv_judul_kajian);
            tvkategori = itemView.findViewById(R.id.tv_kategori);
            tvJam = itemView.findViewById(R.id.tv_jam);
            tvLokasi = itemView.findViewById(R.id.tv_lokasi);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
        }
    }
}

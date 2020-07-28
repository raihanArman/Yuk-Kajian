package id.co.myproject.yukkajian.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import id.co.myproject.yukkajian.BuildConfig;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.local.ReminderHelper;
import id.co.myproject.yukkajian.local.WaktuHelper;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.model.Kategori;
import id.co.myproject.yukkajian.model.Waktu;
import id.co.myproject.yukkajian.receiver.SetWaktu;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.helper.TimeStampFormatter;
import id.co.myproject.yukkajian.view.detail.DetailActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.co.myproject.yukkajian.helper.Utils.getDayName;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    Context context;
    List<Kajian> kajianList = new ArrayList<>();
    ApiRequest apiRequest;
    WaktuHelper waktuHelper;
    ReminderHelper reminderHelper;
    int idUser;

    public ReminderAdapter(Context context, ApiRequest apiRequest, WaktuHelper waktuHelper, ReminderHelper reminderHelper, int idUser) {
        this.context = context;
        this.apiRequest = apiRequest;
        this.waktuHelper = waktuHelper;
        this.reminderHelper = reminderHelper;
        this.idUser = idUser;
    }

    public void setKajianList(List<Kajian> kajianList){
        this.kajianList.clear();
        this.kajianList.addAll(kajianList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderAdapter.ViewHolder holder, int position) {
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

        String jam = DateFormat.format("HH:mm", kajianList.get(position).getTanggal()).toString();
        holder.tvTanggal.setText(getDayName(hari)+", "+tanggal);
        holder.tvJam.setText("Jam : "+jam);

        List<Waktu> waktuList = waktuHelper.getAllWaktu(kajian.getIdKajian());

        holder.lvMatikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Proses ...");
                progressDialog.show();
                long result = waktuHelper.hapusWaktu(kajian.getIdKajian());
                if (result > 0){
                    for (int i=0; i<waktuList.size(); i++){
                        new SetWaktu(context).stopAlarmManager(Integer.parseInt(waktuList.get(i).getIdTime()));
                    }
                    long resultReminder = reminderHelper.hapusReminder(idUser, kajian.getIdKajian());
                    if(resultReminder > 0) {
                        removeItem(position);
                        progressDialog.dismiss();
                        Toast.makeText(context, "Reminder kajian " + kajian.getJudul_kajian() + " telah dimatikan ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Glide.with(context).load(BuildConfig.BASE_URL_GAMBAR+"pamflet/"+kajian.getGambar()).into(holder.ivPamflet);

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

    public void removeItem(int position){
        kajianList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivPamflet;
        TextView tvkategori, tvJudulKajian, tvTanggal, tvJam, tvLokasi;
        LinearLayout lvMatikan;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPamflet = itemView.findViewById(R.id.iv_pamflet);
            tvJudulKajian = itemView.findViewById(R.id.tv_judul_kajian);
            tvkategori = itemView.findViewById(R.id.tv_kategori);
            tvJam = itemView.findViewById(R.id.tv_jam);
            tvLokasi = itemView.findViewById(R.id.tv_lokasi);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            lvMatikan = itemView.findViewById(R.id.lv_matikan);
        }
    }
}

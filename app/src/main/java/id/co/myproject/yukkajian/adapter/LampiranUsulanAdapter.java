package id.co.myproject.yukkajian.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import id.co.myproject.yukkajian.BuildConfig;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.local.LampiranHelper;
import id.co.myproject.yukkajian.model.Lampiran;

public class LampiranUsulanAdapter extends RecyclerView.Adapter<LampiranUsulanAdapter.ViewHolder> {

    private static final String TAG = "LampiranUsulanAdapter";

    Context context;
    List<Lampiran> lampiranList = new ArrayList<>();
    LampiranHelper lampiranHelper;
    int idUser;

    public LampiranUsulanAdapter(Context context, LampiranHelper lampiranHelper, int idUser) {
        this.context = context;
        this.lampiranHelper = lampiranHelper;
        this.idUser = idUser;
    }

    public void setKajianList(List<Lampiran> lampiranList){
        this.lampiranList.clear();
        this.lampiranList.addAll(lampiranList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LampiranUsulanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lampiran_usulan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LampiranUsulanAdapter.ViewHolder holder, int position) {
        Lampiran lampiran = lampiranList.get(position);
        byte[] decodedString = Base64.decode(lampiran.getGambar(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.ivLampiran.setImageBitmap(decodedByte);

        holder.tvHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long result = lampiranHelper.hapusLampiran(idUser, lampiran.getIdLampiran());
                if (result > 0){
                    Log.d(TAG, "onClick: Berhasil menghapus lampiran");
                    removeItem(position);
                }else {
                    Log.d(TAG, "onClick: Gagal menghapus lampiran");
                }
            }
        });

    }

    public void removeItem(int position){
        lampiranList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return lampiranList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivLampiran;
        TextView tvHapus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLampiran = itemView.findViewById(R.id.iv_lampiran);
            tvHapus = itemView.findViewById(R.id.tv_hapus);
        }
    }
}

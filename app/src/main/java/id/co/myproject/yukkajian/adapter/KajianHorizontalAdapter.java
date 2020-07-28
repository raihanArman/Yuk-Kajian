package id.co.myproject.yukkajian.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import id.co.myproject.yukkajian.BuildConfig;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.model.Kategori;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.view.detail.DetailActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KajianHorizontalAdapter extends RecyclerView.Adapter<KajianHorizontalAdapter.ViewHolder> {
    Context context;
    List<Kajian> kajianList = new ArrayList<>();
    ApiRequest apiRequest;

    public KajianHorizontalAdapter(Context context, ApiRequest apiRequest) {
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
    public KajianHorizontalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_kajian_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KajianHorizontalAdapter.ViewHolder holder, int position) {
        Kajian kajian = kajianList.get(position);
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivPamflet;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPamflet = itemView.findViewById(R.id.iv_pamflet);
        }
    }
}

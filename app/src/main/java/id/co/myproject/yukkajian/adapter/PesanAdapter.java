package id.co.myproject.yukkajian.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.model.Pesan;
import id.co.myproject.yukkajian.model.User;
import id.co.myproject.yukkajian.request.ApiRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesanAdapter extends RecyclerView.Adapter<PesanAdapter.ViewHolder> {

    List<Pesan> pesanList = new ArrayList<>();
    Context context;
    ApiRequest apiRequest;

    public PesanAdapter(Context context, ApiRequest apiRequest) {
        this.context = context;
        this.apiRequest = apiRequest;
    }

    public void setPesanList(List<Pesan> pesanList) {
        this.pesanList.clear();
        this.pesanList.addAll(pesanList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PesanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pesan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesanAdapter.ViewHolder holder, int position) {
        String tanggal = DateFormat.format("dd MMM yyyy HH:mm", pesanList.get(position).getTanggal()).toString();
        holder.tvTanggal.setText(tanggal);
        holder.tvIsi.setText(pesanList.get(position).getIsi());
        Call<User> callUser = apiRequest.userByIdRequest(Integer.parseInt(pesanList.get(position).getPengirim()));
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    User user = response.body();
                    holder.tvPengirim.setText("Dari : "+user.getNama()+" (Operator)");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pesanList.size();
    }


    public Pesan getItem(int position){
        return pesanList.get(position);
    }

    public void removeItem(int position){
        pesanList.remove(position);
        notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTanggal, tvIsi, tvPengirim;
        public RelativeLayout view_background;
        public LinearLayout view_foreground;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            tvIsi = itemView.findViewById(R.id.tv_isi);
            tvPengirim = itemView.findViewById(R.id.tv_pengirim);
            view_background =  itemView.findViewById(R.id.view_background);
            view_foreground =  itemView.findViewById(R.id.view_foreground);

        }
    }
}

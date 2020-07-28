package id.co.myproject.yukkajian.view.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.adapter.PesanAdapter;
import id.co.myproject.yukkajian.helper.RecyclerItemTouchHelper;
import id.co.myproject.yukkajian.helper.RecyclerItemTouchHelperListener;
import id.co.myproject.yukkajian.model.Pesan;
import id.co.myproject.yukkajian.model.Value;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import id.co.myproject.yukkajian.helper.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesanFragment extends Fragment implements RecyclerItemTouchHelperListener {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rvPesan;
    LinearLayout rootPesanFragment, lvKoneksi;
    ApiRequest apiRequest;
    PesanAdapter pesanAdapter;
    ImageView ivBack;
    LinearLayout lvNoPesan;
    int idUser;
    SharedPreferences sharedPreferences;

    public PesanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pesan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark) );


        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        lvNoPesan = getActivity().findViewById(R.id.lv_no_pesan);
        lvKoneksi = view.findViewById(R.id.lv_connection);
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
        sharedPreferences = getActivity().getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getInt(Utils.ID_USER_KEY, 0);
        rvPesan = view.findViewById(R.id.rv_pesan);
        ivBack = view.findViewById(R.id.iv_back);
        lvKoneksi = view.findViewById(R.id.lv_connection);
        rootPesanFragment = view.findViewById(R.id.root_pesan_fragment);

        pesanAdapter = new PesanAdapter(getActivity(), apiRequest);
        rvPesan.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPesan.setAdapter(pesanAdapter);
        rvPesan.setVisibility(View.INVISIBLE);

        ItemTouchHelper.SimpleCallback itemSimpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemSimpleCallback).attachToRecyclerView(rvPesan);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isConnectionInternet(getActivity())){
                    rootPesanFragment.setVisibility(View.VISIBLE);
                    lvKoneksi.setVisibility(View.GONE);
                    loadData();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    rootPesanFragment.setVisibility(View.INVISIBLE);
                    lvKoneksi.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Cek jaringan internet anda", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Utils.isConnectionInternet(getActivity())){
                    rootPesanFragment.setVisibility(View.VISIBLE);
                    lvKoneksi.setVisibility(View.GONE);
                    loadData();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    rootPesanFragment.setVisibility(View.INVISIBLE);
                    lvKoneksi.setVisibility(View.VISIBLE);
                    return;
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });



    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(false);
        Call<List<Pesan>> callPesan = apiRequest.pesanRequest(idUser);
        callPesan.enqueue(new Callback<List<Pesan>>() {
            @Override
            public void onResponse(Call<List<Pesan>> call, Response<List<Pesan>> response) {
                if (response.isSuccessful()){
                    List<Pesan> pesanList = response.body();
                    if (pesanList.size() > 0){
                        rvPesan.setVisibility(View.VISIBLE);
                        lvNoPesan.setVisibility(View.GONE);
                    }else {
                        rvPesan.setVisibility(View.INVISIBLE);
                        lvNoPesan.setVisibility(View.VISIBLE);
                    }
                    pesanAdapter.setPesanList(pesanList);
                }
            }

            @Override
            public void onFailure(Call<List<Pesan>> call, Throwable t) {
                Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof PesanAdapter.ViewHolder){
            final Pesan deleteItem = ((PesanAdapter)rvPesan.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();
            pesanAdapter.removeItem(deleteIndex);
            Call<Value> callHapusPesan = apiRequest.hapusPesanRequest(deleteItem.getIdPesan());
            callHapusPesan.enqueue(new Callback<Value>() {
                @Override
                public void onResponse(Call<Value> call, Response<Value> response) {
                    if (response.isSuccessful()){
                        if (response.body().getValue() == 1){
                            Snackbar snackbar = Snackbar.make(rootPesanFragment, response.body().getMessage(), Snackbar.LENGTH_LONG);
                            snackbar.show();
                            loadData();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Value> call, Throwable t) {
                    Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

package id.co.myproject.yukkajian.view.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.adapter.KajianAUsulandapter;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import id.co.myproject.yukkajian.helper.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListUsulanFragment extends Fragment {

    ProgressDialog progressDialog;
    LinearLayout lvContainerUsulkan, lvKoneksi, lvNoUsulkan;
    SwipeRefreshLayout swipeRefreshLayout;
    ApiRequest apiRequest;
    RecyclerView rvUsulan;
    SharedPreferences sharedPreferences;
    int idUser;
    KajianAUsulandapter kajianAUsulandapter;

    private static final String TAG = "ListUsulanFragment";

    public ListUsulanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_usulan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark) );

        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
        sharedPreferences = getActivity().getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getInt(Utils.ID_USER_KEY, 0);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Proses ...");
        lvContainerUsulkan = view.findViewById(R.id.lv_container_usulkan);
        lvNoUsulkan = view.findViewById(R.id.lv_no_usulkan);
        lvKoneksi = view.findViewById(R.id.lv_connection);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        rvUsulan = view.findViewById(R.id.rv_usulan);
        rvUsulan.setLayoutManager(new LinearLayoutManager(getActivity()));
        kajianAUsulandapter = new KajianAUsulandapter(getActivity(), apiRequest);
        rvUsulan.setAdapter(kajianAUsulandapter);
        rvUsulan.setVisibility(View.INVISIBLE);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isConnectionInternet(getActivity())){
                    lvContainerUsulkan.setVisibility(View.VISIBLE);
                    lvKoneksi.setVisibility(View.GONE);
                    loadData();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    lvContainerUsulkan.setVisibility(View.INVISIBLE);
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
                    lvContainerUsulkan.setVisibility(View.VISIBLE);
                    lvKoneksi.setVisibility(View.GONE);
                    loadData();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    lvContainerUsulkan.setVisibility(View.INVISIBLE);
                    lvKoneksi.setVisibility(View.VISIBLE);
                    return;
                }
            }
        });

        if (Utils.isConnectionInternet(getActivity())){
            lvContainerUsulkan.setVisibility(View.VISIBLE);
            lvKoneksi.setVisibility(View.GONE);
            loadData();
        }else {
            lvContainerUsulkan.setVisibility(View.INVISIBLE);
            lvKoneksi.setVisibility(View.VISIBLE);
            return;
        }

    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(false);
        progressDialog.show();
        Call<List<Kajian>> callKajianUsulan = apiRequest.allKajianUsulan(idUser);
        callKajianUsulan.enqueue(new Callback<List<Kajian>>() {
            @Override
            public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                if (response.isSuccessful()){
                    List<Kajian> kajianList = response.body();
                    Collections.sort(kajianList, new Comparator<Kajian>() {
                        @Override
                        public int compare(Kajian o1, Kajian o2) {
                            return o2.getTanggalUpload().compareTo(o1.getTanggalUpload());
                        }
                    });
                    if (kajianList.size() > 0){
                        rvUsulan.setVisibility(View.VISIBLE);
                        lvNoUsulkan.setVisibility(View.GONE);
                    }else {
                        rvUsulan.setVisibility(View.INVISIBLE);
                        lvNoUsulkan.setVisibility(View.VISIBLE);
                    }
                    kajianAUsulandapter.setKajianList(kajianList);
                    progressDialog.dismiss();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Cek kembali jaringan anda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Kajian>> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage());
            }
        });
    }
}

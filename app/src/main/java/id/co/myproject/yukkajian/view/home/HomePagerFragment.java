package id.co.myproject.yukkajian.view.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.adapter.KajianAdapter;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePagerFragment extends Fragment {

    RecyclerView rvKajianAll;
    ApiRequest apiRequest;
    String idKategori;
    KajianAdapter kajianAdapter;

    public HomePagerFragment(String idKategori) {
        // Required empty public constructor
        this.idKategori = idKategori;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);

        rvKajianAll = view.findViewById(R.id.rv_all_kajian);


        rvKajianAll.setLayoutManager(new LinearLayoutManager(getActivity()));
        kajianAdapter = new KajianAdapter(getActivity(), apiRequest);
        rvKajianAll.setAdapter(kajianAdapter);

        loadDataAll();

    }


    private void loadDataAll() {
        if (idKategori.equals("0")){
            Call<List<Kajian>> callKajianAll = apiRequest.allKajianRequest();
            callKajianAll.enqueue(new Callback<List<Kajian>>() {
                @Override
                public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                    if (response.isSuccessful()) {
                        List<Kajian> kajianList = response.body();
                        kajianAdapter.setKajianList(kajianList);
//                    Toast.makeText(getActivity(), ""+kajianList.size(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Kajian>> call, Throwable t) {

                    Log.e("ERROR", "onFailure: " + t.getMessage());
                }
            });

        }else {
            Call<List<Kajian>> callKajianAll = apiRequest.kajianByKategoriRequest(idKategori);
            callKajianAll.enqueue(new Callback<List<Kajian>>() {
                @Override
                public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                    if (response.isSuccessful()) {
                        List<Kajian> kajianList = response.body();
                        kajianAdapter.setKajianList(kajianList);
//                    Toast.makeText(getActivity(), ""+kajianList.size(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Kajian>> call, Throwable t) {

                    Log.e("ERROR", "onFailure: " + t.getMessage());
                }
            });
        }
    }
}

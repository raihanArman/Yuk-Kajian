package id.co.myproject.yukkajian.view.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.adapter.KajianAdapter;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import id.co.myproject.yukkajian.view.detail.LokasiActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class KajianHariIniFragment extends Fragment {

    ApiRequest apiRequest;
    KajianAdapter kajianAdapter;
    RecyclerView rvKajian;
    ImageView ivBack;
    LinearLayout lvLihatLokasi;
    public KajianHariIniFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kajian_hari_ini, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark) );


        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);

        rvKajian = view.findViewById(R.id.rv_kajian);
        lvLihatLokasi = view.findViewById(R.id.lv_lihat_lokasi);
        ivBack = view.findViewById(R.id.iv_back);
        kajianAdapter = new KajianAdapter(getActivity(), apiRequest);

        rvKajian.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvKajian.setAdapter(kajianAdapter);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        lvLihatLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), LokasiActivity.class);
                intent.putExtra("type_intent", LokasiActivity.INTENT_LOKASI_KAJIAN_HARI_INI);
                startActivity(intent);
            }
        });

        loadDataHariIni();

    }

    private void loadDataHariIni() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String time1 = " 00:00:00";
        String time2 = " 23:59:00";
        String tanggal1 = date + time1;
        String tanggal2 = date + time2;
        Call<List<Kajian>> callKajianByDay = apiRequest.kajianByDayRequest(tanggal1, tanggal2);
        callKajianByDay.enqueue(new Callback<List<Kajian>>() {
            @Override
            public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                List<Kajian> kajianList = response.body();
                kajianAdapter.setKajianList(kajianList);
            }

            @Override
            public void onFailure(Call<List<Kajian>> call, Throwable t) {

            }
        });
    }


}

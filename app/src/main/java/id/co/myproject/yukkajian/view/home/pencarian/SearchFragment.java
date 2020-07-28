package id.co.myproject.yukkajian.view.home.pencarian;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    ImageView iv_back;
    LinearLayout lv_empty;
    RecyclerView rv_cari;
    EditText et_cari;
    KajianAdapter kajianAdapter;
    TextView tvHasilPencarian;

    ApiRequest apiRequest;
    SharedPreferences sharedPreferences;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark) );

        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);

        iv_back = view.findViewById(R.id.iv_back);
        rv_cari = view.findViewById(R.id.rv_menu);
        et_cari = view.findViewById(R.id.et_cari);
        lv_empty = view.findViewById(R.id.lv_empty);
        tvHasilPencarian = view.findViewById(R.id.tv_hasil_pencarian);
        lv_empty.setVisibility(View.GONE);

        rv_cari.setLayoutManager(new LinearLayoutManager(getActivity()));
        kajianAdapter = new KajianAdapter(getActivity(), apiRequest);
        rv_cari.setAdapter(kajianAdapter);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });


        et_cari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                loadCariKajian(editable.toString());
            }
        });


    }

    private void loadCariKajian(String cari){
        Call<List<Kajian>> callCariKajian = apiRequest.cariKajianRequest(cari);
        callCariKajian.enqueue(new Callback<List<Kajian>>() {
            @Override
            public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                List<Kajian> kajianList = response.body();
                if (kajianList.size() > 0){
                    rv_cari.setVisibility(View.VISIBLE);
                    lv_empty.setVisibility(View.GONE);
                }else {
                    rv_cari.setVisibility(View.INVISIBLE);
                    tvHasilPencarian.setText("hasil pencarian '"+cari+"' tidak ditemukan");
                    lv_empty.setVisibility(View.VISIBLE);
                }
                kajianAdapter.setKajianList(kajianList);
            }

            @Override
            public void onFailure(Call<List<Kajian>> call, Throwable t) {

            }
        });
    }

    private void loadDataAll() {
        Call<List<Kajian>> callKajianAll = apiRequest.allKajianRequest();
        callKajianAll.enqueue(new Callback<List<Kajian>>() {
            @Override
            public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                if (response.isSuccessful()){
                    List<Kajian> kajianList = response.body();
                    kajianAdapter.setKajianList(kajianList);
//                    Toast.makeText(getActivity(), ""+kajianList.size(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Kajian>> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage());
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        loadDataAll();
    }
}

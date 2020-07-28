package id.co.myproject.yukkajian.view.home.pencarian;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.adapter.KajianAdapter;
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
public class FilterFragment extends Fragment {

    private static final String TAG = "FilterFragment";

    ArrayList<String> listIdKategori = new ArrayList<>();
    boolean load = false;
    ArrayList<String> listNamaKategori = new ArrayList<>();
    List<String> hariList = new ArrayList<>();
    List<Kajian> kajianList = new ArrayList<>();
    Spinner spJenisKajian, spHari;
    ImageView ivBack;
    RecyclerView rvFilter;
    LinearLayout lv_empty;
    ApiRequest apiRequest;
    int type_hari, type_kategori;
    String hari, kategori;

    KajianAdapter kajianAdapter;

    public FilterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark) );

        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);

        spJenisKajian = view.findViewById(R.id.sp_jenis_kajian);
        spHari = view.findViewById(R.id.sp_filter_hari);
        rvFilter = view.findViewById(R.id.rv_filter);
        ivBack = view.findViewById(R.id.iv_back);
        lv_empty = view.findViewById(R.id.lv_empty);

        kategori = getArguments().getString("kategori");
        hari = getArguments().getString("hari");
        listIdKategori = getArguments().getStringArrayList("id_kategori");
        listNamaKategori = getArguments().getStringArrayList("nama_kategori");

        loadDataKategori();
        loadHari();


        rvFilter.setLayoutManager(new LinearLayoutManager(getActivity()));
        kajianAdapter = new KajianAdapter(getActivity(), apiRequest);
        rvFilter.setAdapter(kajianAdapter);
        rvFilter.setVisibility(View.INVISIBLE);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        spJenisKajian.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type_kategori = position;
                loadDataFilter(type_hari, type_kategori);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spHari.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type_hari = position;
                loadDataFilter(type_hari, type_kategori);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void loadDataFilter(int type_hari, int type_kategori) {
        if (hariList.get(type_hari) != "Semua"){
            Calendar c2 =Calendar.getInstance();
            c2.set(Calendar.DAY_OF_WEEK, Utils.getDayNumber(hariList.get(type_hari)));
            c2.set(Calendar.HOUR_OF_DAY,0);
            c2.set(Calendar.MINUTE,0);
            c2.set(Calendar.SECOND,0);
//            Date dateSelected = c2.getTime();
//
//            Calendar now = Calendar.getInstance();
//            Date dateNow = now.getTime();
//            int hari = now.get(Calendar.DAY_OF_WEEK);
//            if (hariList.get(type_hari).equals(Utils.getDayName(hari))){
//                c2.add(Calendar.DATE, 0);
//            }else if (date.before(dateNow)){
//                c2.add(Calendar.DATE, 7);
//            }

            String minggu1 = DateFormat.format("yyyy-MM-dd", c2.getTime()).toString();
            c2.add(Calendar.DATE, 7);
            String minggu2 = DateFormat.format("yyyy-MM-dd", c2.getTime()).toString();
            c2.add(Calendar.DATE, 7);
            String minggu3 = DateFormat.format("yyyy-MM-dd", c2.getTime()).toString();

            String time1 = " 00:00:00";
            String time2 = " 23:59:00";


            if (!listIdKategori.get(type_kategori).equals("0")){
                setByHariKategori(minggu1, minggu2, minggu3, time1, time2, listIdKategori.get(type_kategori));
            }else {
                setByHari(minggu1, minggu2, minggu3, time1, time2);
            }
        }else {
            if (!listIdKategori.get(type_kategori).equals("0")){
                loadDataByKategori(listIdKategori.get(type_kategori));
            }else {
                loadDataAll();
            }
        }
    }

    private void loadDataByKategori(String idKategori){
        Call<List<Kajian>> callKajianByKategori = apiRequest.kajianByKategoriRequest(idKategori);
        callKajianByKategori.enqueue(new Callback<List<Kajian>>() {
            @Override
            public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                if (response.isSuccessful()){
                    setData(response.body());
                }
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
                    setData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Kajian>> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage());
            }
        });
    }

    private void setByHari(String minggu1, String minggu2, String minggu3, String time1, String time2) {
        kajianList.clear();
        Call<List<Kajian>> callKajianByDay = apiRequest.kajianByDayRequest(minggu1+time1, minggu1+time2);
        callKajianByDay.enqueue(new Callback<List<Kajian>>() {
            @Override
            public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                kajianList.addAll(response.body());
                Call<List<Kajian>> callKajianByDay = apiRequest.kajianByDayRequest(minggu2+time1, minggu2+time2);
                callKajianByDay.enqueue(new Callback<List<Kajian>>() {
                    @Override
                    public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                        kajianList.addAll(response.body());
                        Call<List<Kajian>> callKajianByDay = apiRequest.kajianByDayRequest(minggu3+time1, minggu3+time2);
                        callKajianByDay.enqueue(new Callback<List<Kajian>>() {
                            @Override
                            public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                                kajianList.addAll(response.body());
                                setData(kajianList);
                            }

                            @Override
                            public void onFailure(Call<List<Kajian>> call, Throwable t) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<Kajian>> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Kajian>> call, Throwable t) {

            }
        });
    }

    private void setByHariKategori(String minggu1, String minggu2, String minggu3, String time1, String time2, String kategori){
        kajianList.clear();
        Call<List<Kajian>> callKajian = apiRequest.kajianByDayAndKategoriRequest(minggu1+time1, minggu1+time2, kategori);
        callKajian.enqueue(new Callback<List<Kajian>>() {
            @Override
            public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                kajianList.addAll(response.body());
                Call<List<Kajian>> callKajianByDay = apiRequest.kajianByDayAndKategoriRequest(minggu2+time1, minggu2+time2, kategori);
                callKajianByDay.enqueue(new Callback<List<Kajian>>() {
                    @Override
                    public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                        kajianList.addAll(response.body());
                        Call<List<Kajian>> callKajianByDay = apiRequest.kajianByDayAndKategoriRequest(minggu3+time1, minggu3+time2, kategori);
                        callKajianByDay.enqueue(new Callback<List<Kajian>>() {
                            @Override
                            public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                                kajianList.addAll(response.body());
                                setData(kajianList);
                            }

                            @Override
                            public void onFailure(Call<List<Kajian>> call, Throwable t) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<Kajian>> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Kajian>> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage());
            }
        });
    }

    private void setData(List<Kajian> body) {
        List<Kajian> kajianList = body;
        if (kajianList.size() > 0){
            rvFilter.setVisibility(View.VISIBLE);
            lv_empty.setVisibility(View.GONE);
        }else {
            rvFilter.setVisibility(View.INVISIBLE);
            lv_empty.setVisibility(View.VISIBLE);
        }
        kajianAdapter.setKajianList(kajianList);
    }

    private void loadDataKategori() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, R.id.weekofday, listNamaKategori);
        spJenisKajian.setAdapter(arrayAdapter);

        int indexKategori = listNamaKategori.indexOf(kategori);
        spJenisKajian.setSelection(indexKategori);
        type_kategori = indexKategori;
    }


    private void loadHari(){
        hariList.add("Semua");
        hariList.add("Senin");
        hariList.add("Selasa");
        hariList.add("Rabu");
        hariList.add("Kamis");
        hariList.add("Jumat");
        hariList.add("Sabtu");
        hariList.add("Minggu");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, R.id.weekofday, hariList);
        spHari.setAdapter(arrayAdapter);

        int indexHari = hariList.indexOf(hari);
        spHari.setSelection(indexHari);
        type_hari = indexHari;
    }
}

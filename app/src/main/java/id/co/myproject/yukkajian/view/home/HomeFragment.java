package id.co.myproject.yukkajian.view.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import id.co.myproject.yukkajian.BuildConfig;
import id.co.myproject.yukkajian.adapter.ViewPagerAdapter;
import id.co.myproject.yukkajian.view.MainActivity;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.adapter.KajianAdapter;
import id.co.myproject.yukkajian.adapter.KajianHorizontalAdapter;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.model.Kategori;
import id.co.myproject.yukkajian.model.Pesan;
import id.co.myproject.yukkajian.model.User;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import id.co.myproject.yukkajian.helper.Utils;
import id.co.myproject.yukkajian.view.home.pencarian.FilterFragment;
import id.co.myproject.yukkajian.view.home.pencarian.SearchFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    ProgressDialog progressDialog;
    ArrayList<String> listIdKategori = new ArrayList<>();
    ArrayList<String> listNamaKategori = new ArrayList<>();
    RecyclerView rvKajianByDay;
    RelativeLayout rlNotif;
    TextView tvNotif;
    CircleImageView ivUser;
    ImageView ivBackground, ivTerapkan;
    ConstraintLayout viewNotif;
    ApiRequest apiRequest;
    KajianHorizontalAdapter kajianHorizontalAdapter;
    Spinner spJenisKajian, spHari;
    TextView tvCari, tvUseri, tvLihatKajianHariini, tvKajianHariIni;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout lvContainerHome, lvKoneksi;


    private TabLayout tabLayout;
    private ViewPager viewPager;

    int idUser;
    SharedPreferences sharedPreferences;
    String hari, kategori;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
        sharedPreferences = getActivity().getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getInt(Utils.ID_USER_KEY, 0);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Proses ...");
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        lvContainerHome = view.findViewById(R.id.lv_container_home);
        lvKoneksi = view.findViewById(R.id.lv_connection);
        rvKajianByDay = view.findViewById(R.id.rv_kajian_by_day);
        viewNotif = view.findViewById(R.id.view_notif);
        ivBackground = view.findViewById(R.id.iv_bg);
        ivTerapkan = view.findViewById(R.id.iv_terapkan);
        spJenisKajian = view.findViewById(R.id.sp_jenis_kajian);
        spHari = view.findViewById(R.id.sp_filter_hari);
        tvCari = view.findViewById(R.id.tv_cari);
        tvLihatKajianHariini = view.findViewById(R.id.tv_lihat_kajian_hariini);
        rlNotif = view.findViewById(R.id.rl_notif);
        tvNotif = view.findViewById(R.id.tv_notif);
        tvUseri = view.findViewById(R.id.tv_user);
        tvKajianHariIni = view.findViewById(R.id.tv_kajian_hari_ini);
        ivUser = view.findViewById(R.id.iv_user);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.vp_tab);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT );

        Glide.with(getActivity()).load(R.drawable.background_home_min).into(ivBackground);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvKajianByDay.setLayoutManager(linearLayoutManager);
        kajianHorizontalAdapter = new KajianHorizontalAdapter(getActivity(), apiRequest);
        rvKajianByDay.setAdapter(kajianHorizontalAdapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isConnectionInternet(getActivity())){
                    loadDataHariIni();
                    loadHari();
                    loadDataKategori();
                    loadJumlahPesan();
                    loadDataUser();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    progressDialog.dismiss();
                    lvContainerHome.setVisibility(View.INVISIBLE);
                    lvKoneksi.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Cek jaringan internet anda", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
                if (Utils.isConnectionInternet(getActivity())){
                    loadDataHariIni();
                    loadHari();
                    loadJumlahPesan();
                    loadDataUser();
                } else {
                    progressDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    lvContainerHome.setVisibility(View.INVISIBLE);
                    lvKoneksi.setVisibility(View.VISIBLE);
                    return;
                }
            }
        });

        if (Utils.isConnectionInternet(getActivity())){
            loadDataHariIni();
            loadHari();
            loadDataKategori();
            loadJumlahPesan();
            loadDataUser();
        }else {
            progressDialog.dismiss();
            lvContainerHome.setVisibility(View.INVISIBLE);
            lvKoneksi.setVisibility(View.VISIBLE);
            return;
        }

        viewNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)view.getContext()).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frame_home, new PesanFragment()).addToBackStack("null").commit();
            }
        });

        tvLihatKajianHariini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)view.getContext()).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frame_home, new KajianHariIniFragment()).addToBackStack("null").commit();
            }
        });

        spJenisKajian.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listNamaKategori.size() > 0) {
                    kategori = parent.getAdapter().getItem(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spHari.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hari = parent.getAdapter().getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ivTerapkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterFragment filterFragment = new FilterFragment();
                Bundle bundle = new Bundle();
                bundle.putString("kategori", kategori);
                bundle.putStringArrayList("id_kategori", listIdKategori);
                bundle.putStringArrayList("nama_kategori", listNamaKategori);
                bundle.putString("hari", hari);
                filterFragment.setArguments(bundle);
                ((MainActivity)view.getContext()).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frame_home, filterFragment).addToBackStack("null").commit();
            }
        });

        tvCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)view.getContext()).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frame_home, new SearchFragment()).addToBackStack("null").commit();
            }
        });

    }

    private void loadDataUser(){
        swipeRefreshLayout.setRefreshing(false);
        Call<User> callUser = apiRequest.userByIdRequest(idUser);
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    User user = response.body();
                    if (!user.getGambar().equals("") && user.getGambar() != null) {
                        Glide.with(getActivity()).load(BuildConfig.BASE_URL_GAMBAR + "profil/" + user.getGambar()).into(ivUser);
                    }else {
                        Glide.with(getActivity()).load(R.drawable.user_profil).into(ivUser);
                    }
                    tvUseri.setText(user.getNama());
                    progressDialog.dismiss();
                    lvContainerHome.setVisibility(View.VISIBLE);
                    lvKoneksi.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ERROR", "onFailure: "+t.getMessage());
            }
        });
    }

    private void loadJumlahPesan() {
        rlNotif.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        Call<Pesan> callJumlahPesan = apiRequest.jumlahPesanrequest(idUser);
        callJumlahPesan.enqueue(new Callback<Pesan>() {
            @Override
            public void onResponse(Call<Pesan> call, Response<Pesan> response) {
                if (response.isSuccessful()){
                    Pesan pesan = response.body();
                    if (pesan.getJumlahPesan().equals("0")){
                        rlNotif.setVisibility(View.GONE);
                    }else {
                        tvNotif.setText(pesan.getJumlahPesan());
                    }
                }
            }

            @Override
            public void onFailure(Call<Pesan> call, Throwable t) {
                Log.e("ERROR", "onFailure: "+t.getMessage());
            }
        });
    }

    private void loadDataHariIni() {
        swipeRefreshLayout.setRefreshing(false);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String time1 = " 00:00:00";
        String time2 = " 23:59:00";
        String tanggal1 = date + time1;
        String tanggal2 = date + time2;
        Call<List<Kajian>> callKajianByDay = apiRequest.kajianByDayRequest(tanggal1, tanggal2);
        callKajianByDay.enqueue(new Callback<List<Kajian>>() {
            @Override
            public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                if (response.isSuccessful()) {
                    List<Kajian> kajianList = new ArrayList<>();
                    if (response.body().size() > 0) {
                        kajianList.addAll(response.body());
                        tvKajianHariIni.setVisibility(View.VISIBLE);
                        tvLihatKajianHariini.setVisibility(View.VISIBLE);
                    }
                    kajianHorizontalAdapter.setKajianList(kajianList);
                }
            }

            @Override
            public void onFailure(Call<List<Kajian>> call, Throwable t) {

                Log.e("ERROR", "onFailure: "+t.getMessage());
            }
        });
    }

    private void loadDataKategori() {
        swipeRefreshLayout.setRefreshing(false);
        if (listNamaKategori.size() > 0 && listIdKategori.size() > 0){
            listIdKategori.clear();
            listNamaKategori.clear();
        }
        Call<List<Kategori>> callAllKategori = apiRequest.allKategoriRequest();
        callAllKategori.enqueue(new Callback<List<Kategori>>() {
            @Override
            public void onResponse(Call<List<Kategori>> call, Response<List<Kategori>> response) {
                if (response.isSuccessful()) {
                    List<Kategori> kategoriList = response.body();
                    listNamaKategori.add("Semua");
                    listIdKategori.add("0");
                    for (int i = 0; i < kategoriList.size(); i++) {
                        listNamaKategori.add(kategoriList.get(i).getNamaKategori());
                        listIdKategori.add(kategoriList.get(i).getIdKategori());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, R.id.weekofday, listNamaKategori);
                    spJenisKajian.setAdapter(arrayAdapter);

                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), getActivity(), response.body());
                    viewPager.setAdapter(viewPagerAdapter);
                    tabLayout.setupWithViewPager(viewPager);

                    tabLayout.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            // don't forget to add Tab first before measuring..
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            ((Activity) getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            int widthS = displayMetrics.widthPixels;
                            tabLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            int widthT = tabLayout.getMeasuredWidth();

                            if (widthS > widthT) {
                                tabLayout.setTabMode(TabLayout.MODE_FIXED);
                                tabLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                            }
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<List<Kategori>> call, Throwable t) {
                Log.e("ERROR", "onFailure: "+t.getMessage());
            }
        });
    }


    private void loadHari(){
        swipeRefreshLayout.setRefreshing(false);
        List<String> hariList = new ArrayList<>();
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
    }

}

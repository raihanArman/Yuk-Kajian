package id.co.myproject.yukkajian.view.usulan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.model.JenisKajian;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.model.Kategori;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import id.co.myproject.yukkajian.helper.ConvertBitmap;
import id.co.myproject.yukkajian.helper.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsulkanFragment extends Fragment implements ConvertBitmap {

    public static final int GALLERY_CODE = 123;
    private static final String TAG = "UsulkanFragment";
    public static boolean BACK_FROM_LAMPIRAN = false;

    boolean hasJam = false, hasTanggal = false, hasPamflet = false;
    Bitmap bitmap;
    int kategoriPosition, jenisKajianPosition, idUser;
    ProgressDialog progressDialog;
    String pamflet, jamResume, tanggalResume, tanggalInput;

    List<String> listIdKategori = new ArrayList<>();
    List<String> listIdJenisKajian = new ArrayList<>();

    LinearLayout btnLayoutAddImage, lvJam, lvTanggal;
    TextView tvGantiGambar, tvPamflet, tvPilihTanggal;
    ImageView ivKajian, ivJam, ivTanggal;
    Spinner spJenisKajian;
    Spinner spKategori;
    EditText etJudulKajian, etPemateri, etLokasi, etNoPemateri;
    TextView tvJam, tvTanggal;
    Button btnKirim;
    ScrollView svUsulkan;


    ApiRequest apiRequest;
    SharedPreferences sharedPreferences;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat simpleDateFormat;

    public UsulkanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_usulkan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FormUsulanActivity.prosesInputKajianDeskripsi = false;
        FormUsulanActivity.statusLampiran = false;

        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
        sharedPreferences = getActivity().getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        idUser = sharedPreferences.getInt(Utils.ID_USER_KEY, 0);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Proses ...");


        btnLayoutAddImage = view.findViewById(R.id.layout_btn_add_image);
        lvJam = view.findViewById(R.id.lv_jam);
        lvTanggal = view.findViewById(R.id.lv_tanggal);
        tvGantiGambar = view.findViewById(R.id.tv_ganti_gambar);
        ivKajian = view.findViewById(R.id.iv_kajian);
        ivJam = view.findViewById(R.id.iv_jam);
        ivTanggal = view.findViewById(R.id.iv_tanggal);
        spJenisKajian = view.findViewById(R.id.sp_jenis_kajian);
        spKategori = view.findViewById(R.id.sp_kategori);
        etJudulKajian = view.findViewById(R.id.et_judul_kajian);
        etNoPemateri = view.findViewById(R.id.et_no_telp_pemateri);
        etLokasi = view.findViewById(R.id.et_lokasi);
        etPemateri = view.findViewById(R.id.et_pemateri);
        tvJam = view.findViewById(R.id.tv_jam);
        tvTanggal = view.findViewById(R.id.tv_tanggal);
        btnKirim = view.findViewById(R.id.btn_kirim);
        svUsulkan = view.findViewById(R.id.sv_usulkan);
        tvPilihTanggal = view.findViewById(R.id.tv_pilih_tanggal);
        tvPamflet = view.findViewById(R.id.tv_pamflet);

        ((FormUsulanActivity)view.getContext()).getSupportActionBar().setTitle("Input Kajian");
        ((FormUsulanActivity)view.getContext()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((FormUsulanActivity)view.getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        loadDataKategori();
        loadDataJenisKajian();

        btnLayoutAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_CODE);
            }
        });

        tvGantiGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_CODE);
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnectionInternet(getActivity())) {
                    cekInput();
                }else {
                    Toast.makeText(getActivity(), "Tidak ada jaringan", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lvJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        lvTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        spKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kategoriPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spJenisKajian.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jenisKajianPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void cekInput(){
        if(hasPamflet){
            if (!TextUtils.isEmpty(etJudulKajian.getText().toString())){
                if (!TextUtils.isEmpty(etPemateri.getText().toString())){
                    if (hasJam){
                        if (hasTanggal){
                            if (!TextUtils.isEmpty(etLokasi.getText().toString())){
                                if (!TextUtils.isEmpty(etNoPemateri.getText().toString())){
                                    goToLampiran(pamflet);
                                }else {
                                    etLokasi.setError("No Pemateri tidak boleh kosong");
                                    Utils.scrollToView(svUsulkan, etNoPemateri);
                                }
                            }else {
                                etNoPemateri.setError("Lokasi tidak boleh kosong");
                                Utils.scrollToView(svUsulkan, etLokasi);
                            }
                        }else {
                            Toast.makeText(getActivity(), "Anda belum memilih tanggal", Toast.LENGTH_LONG).show();
                            lvTanggal.setBackgroundColor(getResources().getColor(R.color.red_main));
                            tvTanggal.setTextColor(getResources().getColor(android.R.color.white));
                            ivTanggal.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                            Utils.scrollToView(svUsulkan, tvPilihTanggal);
                        }
                    }else {
                        Toast.makeText(getActivity(), "Anda belum memilih jam", Toast.LENGTH_LONG).show();
                        lvJam.setBackgroundColor(getResources().getColor(R.color.red_main));
                        tvJam.setTextColor(getResources().getColor(android.R.color.white));
                        ivJam.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                        Utils.scrollToView(svUsulkan, tvPilihTanggal);
                    }
                }else {
                    etPemateri.setError("Nama Pemateri tidak boleh kosong");
                    Utils.scrollToView(svUsulkan, etPemateri);
                }
            }else {
                etJudulKajian.setError("Judul kajian tidak boleh error");
                Utils.scrollToView(svUsulkan, etJudulKajian);
            }
        }else {
            Toast.makeText(getActivity(), "Anda belum memasukkan pamflet", Toast.LENGTH_LONG).show();
            Utils.scrollToView(svUsulkan, tvPamflet);
        }
    }


    private void showDatePicker() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String hari = DateFormat.format("EEEE", newDate).toString();
                String tanggal = DateFormat.format("d MMMM yyyy", newDate).toString();
                tanggalInput = DateFormat.format("yyyy-MM-dd", newDate).toString();
                tvTanggal.setText(hari+", "+tanggal);
                tanggalResume = simpleDateFormat.format(newDate.getTime());
                lvTanggal.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                tvTanggal.setTextColor(getResources().getColor(android.R.color.black));
                ivTanggal.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
                hasTanggal = true;
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hourNormalisasi = "00", minuteNormalisasi = "00";
                if (hourOfDay < 10){
                    String value = String.valueOf(hourOfDay);
                    hourNormalisasi = "0"+value;
                }else {
                    hourNormalisasi = String.valueOf(hourOfDay);
                }
                if (minute < 10){
                    String value = String.valueOf(minute);
                    minuteNormalisasi = "0"+value;
                }else {
                    minuteNormalisasi = String.valueOf(minute);
                }
                tvJam.setText(hourNormalisasi+":"+minuteNormalisasi);
                lvJam.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                tvJam.setTextColor(getResources().getColor(android.R.color.black));
                ivJam.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
                jamResume = hourNormalisasi+":"+minuteNormalisasi;
                hasJam = true;
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));

        timePickerDialog.show();

    }


    private void loadDataKategori() {
        List<String> listNamaKategori = new ArrayList<>();
        Call<List<Kategori>> callAllKategori = apiRequest.allKategoriRequest();
        callAllKategori.enqueue(new Callback<List<Kategori>>() {
            @Override
            public void onResponse(Call<List<Kategori>> call, Response<List<Kategori>> response) {
                List<Kategori> kategoriList = response.body();
                for (int i=0; i<kategoriList.size(); i++){
                    listNamaKategori.add(kategoriList.get(i).getNamaKategori());
                    listIdKategori.add(kategoriList.get(i).getIdKategori());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, R.id.weekofday, listNamaKategori);
                spKategori.setAdapter(arrayAdapter);
            }

            @Override
            public void onFailure(Call<List<Kategori>> call, Throwable t) {
                Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataJenisKajian(){
        List<String> listNamaJenisKajian = new ArrayList<>();
        Call<List<JenisKajian>> callAllJenisKajian = apiRequest.allJenisKajianRequest();
        callAllJenisKajian.enqueue(new Callback<List<JenisKajian>>() {
            @Override
            public void onResponse(Call<List<JenisKajian>> call, Response<List<JenisKajian>> response) {
                List<JenisKajian> jenisKajianList = response.body();
                for (int i=0; i<jenisKajianList.size(); i++){
                    listNamaJenisKajian.add(jenisKajianList.get(i).getNamaJenisKajian());
                    listIdJenisKajian.add(jenisKajianList.get(i).getIdJenisKajian());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, R.id.weekofday, listNamaJenisKajian);
                spJenisKajian.setAdapter(arrayAdapter);
            }

            @Override
            public void onFailure(Call<List<JenisKajian>> call, Throwable t) {
                Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE){
            if (resultCode == RESULT_OK && data != null){
                hasPamflet = true;
                Uri imageUri = data.getData();
                ivKajian.setVisibility(View.VISIBLE);
                btnLayoutAddImage.setVisibility(View.INVISIBLE);
                tvGantiGambar.setVisibility(View.VISIBLE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new LoadBitmapConvertAsync(getActivity(), UsulkanFragment.this::bitmapToString).execute();
                ivKajian.setImageBitmap(bitmap);

            }
        }
    }

    @Override
    public void bitmapToString(String imgConvert) {
//        Toast.makeText(getActivity(), ""+imgConvert, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        pamflet = imgConvert;
        Log.d(TAG, "bitmapToString: "+imgConvert);
    }

    private void goToLampiran(String imgConvert) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Proses...");
        progressDialog.show();
        Kajian kajian = new Kajian();
        kajian.setIdJenisKajian(listIdJenisKajian.get(jenisKajianPosition));
        kajian.setIdKategori(listIdKategori.get(kategoriPosition));
        kajian.setJudul_kajian(etJudulKajian.getText().toString());
        kajian.setGambar(imgConvert);
        kajian.setPemateri(etPemateri.getText().toString());
        kajian.setLokasi(etLokasi.getText().toString());
        kajian.setNoTelpPemateri(etNoPemateri.getText().toString());
        String tanggal = tanggalInput+" "+tvJam.getText().toString()+":00";

        LampiranFragment lampiranFragment = new LampiranFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("kajian", kajian);
        bundle.putString("tanggal", tanggal);
        bundle.putInt("id_user", idUser);
        lampiranFragment.setArguments(bundle);

        progressDialog.dismiss();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_usulan, lampiranFragment)
                .addToBackStack("")
                .commit();
    }

    private class LoadBitmapConvertAsync extends AsyncTask<Void, Void, String>{

        private WeakReference<Context> weakContext;
        ConvertBitmap convertBitmap;

        public LoadBitmapConvertAsync(Context context, ConvertBitmap convertBitmap) {
            this.weakContext = new WeakReference<>(context);
            this.convertBitmap = convertBitmap;
        }

        @Override
        protected String doInBackground(Void... voids) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] imgByte = byteArrayOutputStream.toByteArray();
            String imageBitmap = Base64.encodeToString(imgByte, Base64.DEFAULT);
            return imageBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakContext.get();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            convertBitmap.bitmapToString(s);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BACK_FROM_LAMPIRAN) {
            ivKajian.setVisibility(View.VISIBLE);
            btnLayoutAddImage.setVisibility(View.INVISIBLE);
            tvGantiGambar.setVisibility(View.VISIBLE);
            ivKajian.setImageBitmap(bitmap);

            tvJam.setText(jamResume);
            lvJam.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            tvTanggal.setText(tanggalResume);
            lvTanggal.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }
}

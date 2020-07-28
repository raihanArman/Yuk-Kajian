package id.co.myproject.yukkajian.view.usulan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.adapter.LampiranUsulanAdapter;
import id.co.myproject.yukkajian.helper.Utils;
import id.co.myproject.yukkajian.local.LampiranHelper;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.model.Lampiran;
import id.co.myproject.yukkajian.model.Value;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import id.co.myproject.yukkajian.helper.ConvertBitmap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class LampiranFragment extends Fragment implements ConvertBitmap {


    private static final String TAG = "LampiranActivity";

    public static final int LAMPIRAN_1_CODE = 98;
    public static final int LAMPIRAN_2_CODE = 97;
    public static final int LAMPIRAN_3_CODE = 96;
    public static final int LAMPIRAN_4_CODE = 95;

    Bitmap bitmap;
    Map<String, String> lampiranMap = new HashMap<>();
    ApiRequest apiRequest;
    LampiranHelper lampiranHelper;
    LampiranUsulanAdapter lampiranUsulanAdapter;

    LinearLayout btnLayoutAddImage1, btnLayoutAddImage2, btnLayoutAddImage3, lvTambahSurat;
    TextView tvGantiGambar1,tvGantiGambar2,tvGantiGambar3,tvTitleSuratPemateri, tvTitleSuratLokasi, tvTitleSuratKeramaian, tvLink;
    ImageView ivLampiran1, ivLampiran2, ivLampiran3, ivLampiran4;
    EditText etLink;
    RecyclerView rvLampiranUsulan;
    ScrollView svLampiran;
    Button btnKirim;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    boolean hasSuratPemateri = false, hasSuratLokasi = false, hasSuratKeramaian = false;

    Kajian kajian;
    int idUser, lampiranKey;
    String tanggal;

    public LampiranFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lampiran, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
        lampiranHelper = LampiranHelper.getINSTANCE(getActivity());
        lampiranHelper.open();

        FormUsulanActivity.prosesInputKajianDeskripsi = true;
        FormUsulanActivity.statusLampiran = true;

        btnLayoutAddImage1 = view.findViewById(R.id.layout_btn_add_image_1);
        btnLayoutAddImage2 = view.findViewById(R.id.layout_btn_add_image_2);
        btnLayoutAddImage3 = view.findViewById(R.id.layout_btn_add_image_3);
        tvGantiGambar1 = view.findViewById(R.id.tv_ganti_gambar_1);
        tvGantiGambar2 = view.findViewById(R.id.tv_ganti_gambar_2);
        tvGantiGambar3 = view.findViewById(R.id.tv_ganti_gambar_3);
        tvLink = view.findViewById(R.id.tv_link);
        ivLampiran1 = view.findViewById(R.id.iv_lampiran1);
        ivLampiran2 = view.findViewById(R.id.iv_lampiran2);
        ivLampiran3 = view.findViewById(R.id.iv_lampiran3);
        svLampiran = view.findViewById(R.id.sv_lampiran);
        etLink = view.findViewById(R.id.et_link);
        tvTitleSuratKeramaian = view.findViewById(R.id.tv_title_surat_keramaian);
        tvTitleSuratLokasi = view.findViewById(R.id.tv_title_surat_lokasi);
        tvTitleSuratPemateri = view.findViewById(R.id.tv_title_surat_pemateri);
        btnKirim = view.findViewById(R.id.btn_kirim);
        rvLampiranUsulan = view.findViewById(R.id.rv_lampiran_usulan);
        lvTambahSurat = view.findViewById(R.id.lv_tambah_surat);
        toolbar =view. findViewById(R.id.toolbar);
        kajian = getArguments().getParcelable("kajian");
        idUser = getArguments().getInt("id_user", 0);
        tanggal = getArguments().getString("tanggal");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Proses ...");

        if (kajian.getIdJenisKajian().equals("1")){
            tvTitleSuratKeramaian.setText(tvTitleSuratKeramaian.getText()+" (*)");
            tvTitleSuratLokasi.setText(tvTitleSuratLokasi.getText()+" (*)");
        }else {
            etLink.setVisibility(View.GONE);
            tvLink.setVisibility(View.GONE);
            tvTitleSuratLokasi.setText(tvTitleSuratLokasi.getText()+" (*)");
        }

        Log.d(TAG, "onCreate: Gambar : "+kajian.getGambar());

        ((FormUsulanActivity)view.getContext()).getSupportActionBar().setTitle("Input Lampiran");
        ((FormUsulanActivity)view.getContext()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((FormUsulanActivity)view.getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvLampiranUsulan.setLayoutManager(linearLayoutManager);
        lampiranUsulanAdapter = new LampiranUsulanAdapter(getActivity(), lampiranHelper, idUser);
        rvLampiranUsulan.setAdapter(lampiranUsulanAdapter);


        btnLayoutAddImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                lampiranKey = 1;
                startActivityForResult(intent, LAMPIRAN_1_CODE);
            }
        });

        tvGantiGambar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                lampiranKey = 1;
                startActivityForResult(intent, LAMPIRAN_1_CODE);
            }
        });

        btnLayoutAddImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                lampiranKey = 2;
                startActivityForResult(intent, LAMPIRAN_2_CODE);
            }
        });

        tvGantiGambar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                lampiranKey = 2;
                startActivityForResult(intent, LAMPIRAN_2_CODE);
            }
        });

        btnLayoutAddImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                lampiranKey = 3;
                startActivityForResult(intent, LAMPIRAN_3_CODE);
            }
        });

        tvGantiGambar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                lampiranKey = 3;
                startActivityForResult(intent, LAMPIRAN_3_CODE);
            }
        });

        lvTambahSurat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                lampiranKey = 4;
                startActivityForResult(intent, LAMPIRAN_4_CODE);
            }
        });


        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnectionInternet(getActivity())) {
                    if (kajian.getIdJenisKajian().equals("1")){
                        if (hasSuratKeramaian){
                            if (hasSuratLokasi){
                                inputKajian();
                            }else {
                                Toast.makeText(getActivity(), "Anda belum memasukkan surat lokasi", Toast.LENGTH_LONG).show();
                                Utils.scrollToView(svLampiran, tvTitleSuratLokasi);
                            }
                        }else {
                            Toast.makeText(getActivity(), "Anda belum memasukkan surat keramaian", Toast.LENGTH_LONG).show();
                            Utils.scrollToView(svLampiran, tvTitleSuratKeramaian);
                        }
                    }else {
                        if (hasSuratLokasi){
                            inputKajian();
                        }else {
                            Toast.makeText(getActivity(), "Anda belum memasukkan surat lokasi", Toast.LENGTH_LONG).show();
                            Utils.scrollToView(svLampiran, tvTitleSuratLokasi);
                        }
                    }
                }else {
                    Toast.makeText(getActivity(), "Tidak ada jaringan", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void inputKajian() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Peringatan");
        builder.setMessage("Apakah anda yakin usulan kajian anda sudah tidak mau diubah ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Menginput...");
                progressDialog.show();
                if (kajian.getIdJenisKajian().equals("1")){
                    if (!TextUtils.isEmpty(etLink.getText())){
                        kajian.setLink(etLink.getText().toString());
                    }else {
                        kajian.setLink("");
                    }
                }else {
                    kajian.setLink("");
                }
                Call<Value> callInputKajian = apiRequest.inputKajianRequest(
                        idUser,
                        kajian.getIdJenisKajian(),
                        kajian.getIdKategori(),
                        kajian.getJudul_kajian(),
                        kajian.getPemateri(),
                        tanggal,
                        kajian.getGambar(),
                        kajian.getLokasi(),
                        kajian.getNoTelpPemateri(),
                        kajian.getLink()
                );
                callInputKajian.enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(Call<Value> call, Response<Value> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getValue() == 1){
                                int idUsulan = response.body().getIdUsulan();
                                for( Map.Entry<String, String> entry : lampiranMap.entrySet()){
                                    Log.d(TAG, "onResponse: Key : "+entry.getKey()+", Value : "+entry.getValue());
                                    Call<Value> callInputLampiran = apiRequest.inputLampiranRequest(
                                            idUsulan,
                                            entry.getKey(),
                                            entry.getValue()
                                    );
                                    callInputLampiran.enqueue(new Callback<Value>() {
                                        @Override
                                        public void onResponse(Call<Value> call, Response<Value> response) {
                                            Log.d(TAG, "onResponse: "+response.body().getMessage());
                                        }

                                        @Override
                                        public void onFailure(Call<Value> call, Throwable t) {
                                            Log.d(TAG, "onFailure: "+t.getMessage());
                                        }
                                    });
                                }
                                List<Lampiran> lampiranList = lampiranHelper.getAllLampiran(String.valueOf(idUser));
                                if (lampiranList.size() > 0){
                                    for (Lampiran lampiran : lampiranList){
                                        Call<Value> callInputLampiran = apiRequest.inputLampiranRequest(
                                                idUsulan,
                                                lampiran.getKeterangan(),
                                                lampiran.getGambar()
                                        );
                                        callInputLampiran.enqueue(new Callback<Value>() {
                                            @Override
                                            public void onResponse(Call<Value> call, Response<Value> response) {
                                                if (response.isSuccessful()) {
                                                    Log.d(TAG, "onResponse: " + response.body().getMessage());
                                                    long hapusLampiran = lampiranHelper.cleanLampiran(idUser);
                                                    if (hapusLampiran > 0) {
                                                        Log.d(TAG, "onResponse: berhasil hapus lampiran");
                                                    } else {
                                                        Log.d(TAG, "onResponse: gagal hapus lampiran");
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Value> call, Throwable t) {
                                                Log.d(TAG, "onFailure: "+t.getMessage());
                                            }
                                        });
                                    }
                                }
                            }
                            progressDialog.dismiss();
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        List<Lampiran> lampiranList = lampiranHelper.getAllLampiran(String.valueOf(idUser));
        if (lampiranList.size() > 0){
            lampiranList.clear();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.show();
        if (requestCode == LAMPIRAN_1_CODE){
            if (resultCode == RESULT_OK && data != null){
                Uri imageUri = data.getData();
                ivLampiran1.setVisibility(View.VISIBLE);
                btnLayoutAddImage1.setVisibility(View.INVISIBLE);
                tvGantiGambar1.setVisibility(View.VISIBLE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ivLampiran1.setImageBitmap(bitmap);
                new LoadBitmapConvertAsync(getActivity(), LampiranFragment.this::bitmapToString).execute();
                hasSuratPemateri = true;
            }
            progressDialog.dismiss();
        }else if (requestCode == LAMPIRAN_2_CODE){
            if (resultCode == RESULT_OK && data != null){
                Uri imageUri = data.getData();
                ivLampiran2.setVisibility(View.VISIBLE);
                btnLayoutAddImage2.setVisibility(View.INVISIBLE);
                tvGantiGambar2.setVisibility(View.VISIBLE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ivLampiran2.setImageBitmap(bitmap);
                new LoadBitmapConvertAsync(getActivity(), LampiranFragment.this::bitmapToString).execute();
                hasSuratLokasi = true;
            }
            progressDialog.dismiss();
        }else if (requestCode == LAMPIRAN_3_CODE){
            if (resultCode == RESULT_OK && data != null){
                Uri imageUri = data.getData();
                ivLampiran3.setVisibility(View.VISIBLE);
                btnLayoutAddImage3.setVisibility(View.INVISIBLE);
                tvGantiGambar3.setVisibility(View.VISIBLE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ivLampiran3.setImageBitmap(bitmap);
                new LoadBitmapConvertAsync(getActivity(), LampiranFragment.this::bitmapToString).execute();
                hasSuratKeramaian = true;
            }
            progressDialog.dismiss();
        }else if (requestCode == LAMPIRAN_4_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new LoadBitmapConvertAsync(getActivity(), LampiranFragment.this::bitmapToString).execute();

            }
            progressDialog.dismiss();
        }
    }

    @Override
    public void bitmapToString(String imgConvert) {
        progressDialog.dismiss();
        if (lampiranKey != 4) {
            if (lampiranMap.containsKey(getKeteranganLampiran(lampiranKey))){
                lampiranMap.remove(getKeteranganLampiran(lampiranKey));
            }
            lampiranMap.put(getKeteranganLampiran(lampiranKey), imgConvert);
        }else {
            Lampiran lampiran = new Lampiran();
            lampiran.setGambar(imgConvert);
            lampiran.setKeterangan(getKeteranganLampiran(lampiranKey));
            long result = lampiranHelper.addLampiran(String.valueOf(idUser), lampiran);
            if (result > 0){
                List<Lampiran> lampiranList = lampiranHelper.getAllLampiran(String.valueOf(idUser));
                Toast.makeText(getActivity(), "Lampiran list : "+lampiranList.size(), Toast.LENGTH_SHORT).show();
                lampiranUsulanAdapter.setKajianList(lampiranList);
            }else {
                Toast.makeText(getActivity(), "gagal menambah", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getKeteranganLampiran(int key){
        String keterangan = "";
        switch (key){
            case 1 :
                keterangan = "surat kesediaan pemateri";
                break;
            case 2 :
                keterangan = "surat peminjaman tempat";
                break;
            case 3 :
                keterangan = "surat izin keramaian";
                break;
            case 4 :
                keterangan = "surat kerjasama";
                break;
            default:
                return "salah keterangan";
        }

        return keterangan;
    }



    private class LoadBitmapConvertAsync extends AsyncTask<Void, Void, String> {

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


}

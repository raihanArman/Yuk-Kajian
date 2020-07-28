package id.co.myproject.yukkajian.view.login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import androidx.fragment.app.FragmentTransaction;
import id.co.myproject.yukkajian.view.MainActivity;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.helper.Utils;
import id.co.myproject.yukkajian.model.Value;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.co.myproject.yukkajian.helper.Utils.ID_USER_KEY;
import static id.co.myproject.yukkajian.helper.Utils.LOGIN_KEY;
import static id.co.myproject.yukkajian.helper.Utils.LOGIN_STATUS;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    EditText etNama, etEmail, etKonfirm;
    ScrollView svSignUp;
    TextInputEditText etPassword;
    private CardView isAtLeast8Parent, hasUppercaseParent, hasNumberParent;
    Button btnSignUp;
    TextView tv_login;
    private FrameLayout parentFrameLayout;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private ProgressDialog progressDialog;
    public static final String TAG = SignUpFragment.class.getSimpleName();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ApiRequest apiRequest;
    private boolean isAtLeast8 = false, hasUppercase = false, hasNumber = false, isRegistrationClickable = false;

    int idUser;


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNama = view.findViewById(R.id.et_nama);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etKonfirm = view.findViewById(R.id.et_confirm_password);
        btnSignUp = view.findViewById(R.id.btn_sign_up);
        tv_login = view.findViewById(R.id.tv_login);
        svSignUp = view.findViewById(R.id.sv_sign_up);
        isAtLeast8Parent = view.findViewById(R.id.p_item_1_icon_parent);
        hasUppercaseParent = view.findViewById(R.id.p_item_2_icon_parent);
        hasNumberParent = view.findViewById(R.id.p_item_3_icon_parent);
        parentFrameLayout = getActivity().findViewById(R.id.frame_login);

        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
        sharedPreferences = getActivity().getSharedPreferences(LOGIN_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Proses ...");

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registrationDataCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isConnectionInternet(getActivity())){
                    checkInput();
                }else {
                    Toast.makeText(getActivity(), "Tidak ada jaringan", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignInFragment());
            }
        });

    }

    private void checkInput() {
        if (!TextUtils.isEmpty(etEmail.getText())) {
            if (etEmail.getText().toString().matches(emailPattern)) {
                if (!TextUtils.isEmpty(etNama.getText())) {
                    if (!TextUtils.isEmpty(etPassword.getText())) {
                        if (!TextUtils.isEmpty(etKonfirm.getText())) {
                            if (etPassword.length() >= 8) {
                                if (isAtLeast8 && hasUppercase && hasNumber) {
                                    if (etPassword.getText().toString().equals(etKonfirm.getText().toString())){
                                        daftar();
                                    }else {
                                        etKonfirm.setError("Password tidak cocok");
                                        Utils.scrollToView(svSignUp, etKonfirm);
                                    }
                                } else {
                                    etPassword.setError("Password tidak sesuai");
                                    Utils.scrollToView(svSignUp, etPassword);
                                }
                            } else {
                                etPassword.setError("Password terlalu sedikit");
                                Utils.scrollToView(svSignUp, etPassword);
                            }
                        }else {
                            etKonfirm.setError("Konfirmasi password tidak boleh kosong");
                            Utils.scrollToView(svSignUp, etKonfirm);
                        }
                    }else {
                        etPassword.setError("Password tidak boleh kosong");
                        Utils.scrollToView(svSignUp, etPassword);
                    }
                } else {
                    etNama.setError("Nama tidak boleh kosong");
                    Utils.scrollToView(svSignUp, etNama);
                }
            }else {
                etEmail.setError("Email tidak sesuai format");
                Utils.scrollToView(svSignUp, etEmail);
            }
        }else {
            etEmail.setError("Email tidak boleh kosong");
            Utils.scrollToView(svSignUp, etEmail);
        }
    }

    @SuppressLint("ResourceType")
    private void registrationDataCheck() {
        String password = etPassword.getText().toString(), email = etEmail.getText().toString();

        if (password.length() >= 8) {
            isAtLeast8 = true;
            isAtLeast8Parent.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCheckOk)));
        } else {
            isAtLeast8 = false;
            isAtLeast8Parent.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCheckNo)));
        }
        if (password.matches("(.*[A-Z].*)")) {
            hasUppercase = true;
            hasUppercaseParent.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCheckOk)));
        } else {
            hasUppercase = false;
            hasUppercaseParent.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCheckNo)));
        }
        if (password.matches("(.*[0-9].*)")) {
            hasNumber = true;
            hasNumberParent.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCheckOk)));
        } else {
            hasNumber = false;
            hasNumberParent.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCheckNo)));
        }

    }


    private void daftar(){
        final String nama = etNama.getText().toString();
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        if (etEmail.getText().toString().matches(emailPattern)){
            if (etPassword.getText().toString().equals(etKonfirm.getText().toString())){
                progressDialog.show();
                Call<Value> callRegistrasiUser = apiRequest.registrasiUserRequest(
                        email,
                        password,
                        nama,
                        ""
                );
                callRegistrasiUser.enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(Call<Value> call, Response<Value> response) {
                        if (response.body().getValue() == 1){
                            idUser = response.body().getIdUser();
                            editor.putInt(ID_USER_KEY, idUser);
                            editor.putBoolean(LOGIN_STATUS, true);
                            editor.commit();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }else {
                            progressDialog.dismiss();
                            btnSignUp.setEnabled(true);
                            Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else {
            etEmail.setError("Email tidak cocok");
        }
    }


    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

}

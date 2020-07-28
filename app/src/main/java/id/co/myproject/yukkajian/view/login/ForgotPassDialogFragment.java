package id.co.myproject.yukkajian.view.login;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.helper.Utils;
import id.co.myproject.yukkajian.model.Value;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassDialogFragment extends DialogFragment {

    private CardView isAtLeast8Parent, hasUppercaseParent, hasNumberParent;
    private boolean isAtLeast8 = false, hasUppercase = false, hasNumber = false, isRegistrationClickable = false;

    EditText et_password, et_confirm_password;
    ApiRequest apiRequest;
    TextView tv_batal, tv_selesai;
    int idUser;

    public ForgotPassDialogFragment(int idUser) {
        // Required empty public constructor
        this.idUser = idUser;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_pass_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et_password = view.findViewById(R.id.et_password);
        et_confirm_password = view.findViewById(R.id.et_confirm_password);
        tv_selesai = view.findViewById(R.id.tv_selesai);
        tv_batal = view.findViewById(R.id.tv_batal);
        isAtLeast8Parent = view.findViewById(R.id.p_item_1_icon_parent);
        hasUppercaseParent = view.findViewById(R.id.p_item_2_icon_parent);
        hasNumberParent = view.findViewById(R.id.p_item_3_icon_parent);

        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);

        tv_batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
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

        tv_selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInput();
            }
        });
    }

    private void resetPassword(){
        Call<Value> lupaPasswordRequest = apiRequest.lupaPasswordRequest(idUser, et_password.getText().toString());
        lupaPasswordRequest.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (response.body().getValue() == 1) {
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_login, new SignInFragment());
                        fragmentTransaction.commit();
                        dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                Toast.makeText(getActivity(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkInput() {
        if (Utils.isConnectionInternet(getActivity())) {
            if (!TextUtils.isEmpty(et_password.getText().toString())) {
                if (!TextUtils.isEmpty(et_confirm_password.getText().toString())) {
                    if (et_password.getText().toString().equals(et_confirm_password.getText().toString())) {
                        if (et_password.getText().length() >= 8 && et_confirm_password.getText().length() >= 8) {
                            if (isAtLeast8 && hasUppercase && hasNumber) {
                                resetPassword();
                            } else {
                                et_password.setError("Password tidak sesuai");
                            }
                        } else {
                            et_password.setError("Password terlalu sedikit");
                        }
                    } else {
                        et_confirm_password.setError("Password tidak cocok");
                    }
                }else {
                    et_confirm_password.setError("Konfirmasi password tidak boleh kosong");
                }
            } else {
                et_password.setError("Password tidak boleh kosong");
            }
        }else {
            Toast.makeText(getActivity(), "Tidak ada jaringan", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("ResourceType")
    private void registrationDataCheck() {
        String password = et_password.getText().toString();

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

}

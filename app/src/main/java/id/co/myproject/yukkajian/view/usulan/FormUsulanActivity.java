package id.co.myproject.yukkajian.view.usulan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.helper.Utils;
import id.co.myproject.yukkajian.local.LampiranHelper;
import id.co.myproject.yukkajian.model.Lampiran;
import id.co.myproject.yukkajian.request.ApiRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.List;

public class FormUsulanActivity extends AppCompatActivity{

    public static boolean prosesInputKajianDeskripsi = false;
    public static boolean statusLampiran = false;
    LampiranHelper lampiranHelper;
    List<Lampiran> lampiranList;

    SharedPreferences sharedPreferences;
    int idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_usulan);

        sharedPreferences = getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getInt(Utils.ID_USER_KEY, 0);
        lampiranHelper = LampiranHelper.getINSTANCE(this);
        lampiranHelper.open();


        lampiranList = lampiranHelper.getAllLampiran(String.valueOf(idUser));
        if (lampiranList.size() > 0){
            lampiranHelper.cleanLampiran(idUser);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_usulan, new UsulkanFragment());
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            if (!prosesInputKajianDeskripsi){
                finish();
            }else {
                UsulkanFragment.BACK_FROM_LAMPIRAN = true;
                if (statusLampiran){
                    if (lampiranList.size() > 0){
                        lampiranHelper.cleanLampiran(idUser);
                    }
                }
                getSupportFragmentManager().popBackStackImmediate();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (statusLampiran){
            if (lampiranList.size() > 0){
                lampiranHelper.cleanLampiran(idUser);
            }
        }
    }
}

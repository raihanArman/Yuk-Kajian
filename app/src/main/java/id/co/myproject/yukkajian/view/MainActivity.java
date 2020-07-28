package id.co.myproject.yukkajian.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.local.ReminderHelper;
import id.co.myproject.yukkajian.model.Value;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import id.co.myproject.yukkajian.service.ReminderCekService;
import id.co.myproject.yukkajian.helper.Utils;
import id.co.myproject.yukkajian.view.home.HomeFragment;
import id.co.myproject.yukkajian.view.home.ListUsulanFragment;
import id.co.myproject.yukkajian.view.home.profil.ProfilFragment;
import id.co.myproject.yukkajian.view.home.ReminderFragment;
import id.co.myproject.yukkajian.view.usulan.FormUsulanActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.irfaan008.irbottomnavigation.SpaceItem;
import com.irfaan008.irbottomnavigation.SpaceNavigationView;
import com.irfaan008.irbottomnavigation.SpaceOnClickListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    
    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    ReminderHelper reminderHelper;
    SharedPreferences sharedPreferences;
    ApiRequest apiRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reminderHelper = ReminderHelper.getINSTANCE(this);
        reminderHelper.open();
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
        sharedPreferences = getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        int idUser = sharedPreferences.getInt(Utils.ID_USER_KEY, 0);

        frameLayout = findViewById(R.id.frame_home);
//        bottomNavigationView = findViewById(R.id.bottom_nav);

        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space_nav);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.nav_home));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.nav_usulan));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.nav_reminder));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.nav_user));


//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                if (item.getItemId() == R.id.home_nav){
//                    setFragment(new HomeFragment());
//                }else if (item.getItemId() == R.id.add_usulan_nav){
////                    setFragment(new UsulkanFragment());
//                    startActivity(new Intent(MainActivity.this, FormUsulanActivity.class));
//                }else if (item.getItemId() == R.id.profil_nav){
//                    setFragment(new ProfilFragment());
//                }else if (item.getItemId() == R.id.reminder_nav){
//                    setFragment(new ReminderFragment());
//                }else if (item.getItemId() == R.id.usulan_nav){
//                    setFragment(new ListUsulanFragment());
//                }
//
//                return true;
//            }
//        });

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Call<Value> callCekDataUser = apiRequest.cekDataRequest(idUser);
                callCekDataUser.enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(Call<Value> call, Response<Value> response) {
                        if(response.isSuccessful()){
                            if (response.body().getValue() == 1){
                                Intent intent = new Intent(MainActivity.this, FormUsulanActivity.class);
                                startActivity(intent);
                            }else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(response.body().getMessage());
                                builder.setMessage("Silahkan lengkapi terlebih dahulu profil anda");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+t.getMessage());
                    }
                });
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if (itemIndex == 0){
                    setFragment(new HomeFragment());
                }else if(itemIndex == 1) {
                    setFragment(new ListUsulanFragment());
                }else if(itemIndex == 2) {
                    setFragment(new ReminderFragment());
                }else if(itemIndex == 3) {
                    setFragment(new ProfilFragment());
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
            }
        });

        setFragment(new HomeFragment());


    }
    private void setFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(frameLayout.getId(), fragment);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(MainActivity.this, ReminderCekService.class));
    }


}

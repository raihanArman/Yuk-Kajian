package id.co.myproject.yukkajian.view;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import id.co.myproject.yukkajian.BuildConfig;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.model.Kajian;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thefuntasty.hauler.HaulerView;


public class KajianReminderScreenActivity extends AppCompatActivity {

    Kajian kajian;
    TextView tvJudulKajian, tvJam;
    CircleImageView ivKajian;
    ImageView ivBackground;
    Button btnMatikan;
    Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kajian_reminder_screen);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(10000);

        kajian = getIntent().getParcelableExtra("kajian");
        tvJudulKajian = findViewById(R.id.tv_judul_kajian);
        ivKajian = findViewById(R.id.iv_kajian);
        tvJam = findViewById(R.id.tv_jam);
        btnMatikan = findViewById(R.id.btn_matikan);
        ivBackground = findViewById(R.id.iv_background);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON ,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        tvJudulKajian.setText(kajian.getJudul_kajian());
        String jam = DateFormat.format("HH:mm", kajian.getTanggal()).toString();
        tvJam.setText(jam);
        Glide.with(this).load(BuildConfig.BASE_URL_GAMBAR+"pamflet/"+kajian.getGambar()).into(ivKajian);
        Glide.with(this).load(BuildConfig.BASE_URL_GAMBAR+"pamflet/"+kajian.getGambar()).into(ivBackground);


        btnMatikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        haulerView.setOnDragDismissedListener();

    }

    @Override
    protected void onPause() {
        super.onPause();
        vibrator.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vibrator.cancel();
    }
}

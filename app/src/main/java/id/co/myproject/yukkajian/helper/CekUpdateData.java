package id.co.myproject.yukkajian.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.receiver.TimeReceiver;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CekUpdateData extends View {
    Context context;
    private static final String TAG = "CekUpdateData";
    ApiRequest apiRequest;
    public CekUpdateData(Context context) {
        super(context);
        this.context = context;
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);

    }

    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            cekUpdate();
            mHandler.sendEmptyMessageDelayed(0, 1000);
            return false;
        }
    });

    private void cekUpdate() {
        Date date = Calendar.getInstance().getTime();
        String tanggal = DateFormat.format("yyyy-MM-dd HH:mm:ss", date).toString();
        Call<List<Kajian>> kajianCall = apiRequest.kajianUpdateRequest(tanggal);
        kajianCall.enqueue(new Callback<List<Kajian>>() {
            @Override
            public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                if (response.isSuccessful()){
                    if(response.body().size() > 0) {
                        Kajian kajian = response.body().get(0);
                        Intent intent = new Intent(context, TimeReceiver.class);
                        intent.putExtra("data_notif", kajian);
                        intent.setAction(Utils.UPDATE_DATA_RECEIVER);
                        intent.putExtra("wadaw", tanggal);
                        context.sendBroadcast(intent);
                    }
                }else {
                    Log.d(TAG, "onResponse: Gagal menampilkan data");
                }
            }

            @Override
            public void onFailure(Call<List<Kajian>> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });
    }

}

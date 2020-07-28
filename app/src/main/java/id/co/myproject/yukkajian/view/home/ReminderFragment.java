package id.co.myproject.yukkajian.view.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.adapter.ReminderAdapter;
import id.co.myproject.yukkajian.local.ReminderHelper;
import id.co.myproject.yukkajian.local.WaktuHelper;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import id.co.myproject.yukkajian.helper.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReminderFragment extends Fragment {

    RecyclerView rvReminder;
    ReminderAdapter reminderAdapter;
    ApiRequest apiRequest;
    ReminderHelper reminderHelper;
    WaktuHelper waktuHelper;
    SharedPreferences sharedPreferences;
    int idUser;
    LinearLayout lvNoReminder;

    public ReminderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark) );

        rvReminder = view.findViewById(R.id.rv_reminder);
        lvNoReminder = view.findViewById(R.id.lv_no_reminder);
        rvReminder.setVisibility(View.INVISIBLE);

        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
        sharedPreferences = getActivity().getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        reminderHelper = ReminderHelper.getINSTANCE(getActivity());
        waktuHelper = WaktuHelper.getINSTANCE(getActivity());
        reminderHelper.open();
        waktuHelper.open();
        idUser = sharedPreferences.getInt(Utils.ID_USER_KEY, 0);

        rvReminder.setLayoutManager(new LinearLayoutManager(getActivity()));
        reminderAdapter = new ReminderAdapter(getActivity(), apiRequest, waktuHelper, reminderHelper, idUser);
        rvReminder.setAdapter(reminderAdapter);

        loadData();

    }

    private void loadData() {
        List<Kajian> kajianList = reminderHelper.getAllKajianReminder(String.valueOf(idUser));
        if (kajianList.size() > 0){
            rvReminder.setVisibility(View.VISIBLE);
            lvNoReminder.setVisibility(View.GONE);
        }else {
            rvReminder.setVisibility(View.INVISIBLE);
            lvNoReminder.setVisibility(View.VISIBLE);
        }
        reminderAdapter.setKajianList(kajianList);
    }
}

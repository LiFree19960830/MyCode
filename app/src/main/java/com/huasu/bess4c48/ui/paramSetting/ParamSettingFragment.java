package com.huasu.bess4c48.ui.paramSetting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.adapter.BaseRecyclerViewHolder;
import com.huasu.bess4c48.adapter.SettingAdapter;
import com.huasu.bess4c48.bean.SettingItem;
import com.huasu.bess4c48.ui.paramSetting.bat.BatParamActivity;
import com.huasu.bess4c48.ui.paramSetting.function.FunctionParamActivity;
import com.huasu.bess4c48.ui.paramSetting.pcs.PcsParamActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 参数设置
 */
public class ParamSettingFragment extends Fragment {

    private static final String TAG = "ParamSettingFragment";
    private RecyclerView rvList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_param_setting, container, false);
        rvList = view.findViewById(R.id.rvList);
        SettingAdapter adapter = new SettingAdapter(init());
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, int position) {
                switch (position) {
                    case 0:
                        BatParamActivity.launchActivity();
                        break;
                    case 1:
                        FunctionParamActivity.launchActivity();
                        break;
                    case 2:
                        PcsParamActivity.launchActivity();
                        break;
                }
            }
        });
        return view;
    }


    private List<SettingItem> init() {
        List<SettingItem> list = new ArrayList<>();
        list.add(new SettingItem(R.mipmap.icon_bat_param, R.string.battery_parameters));
        list.add(new SettingItem(R.mipmap.icon_function_param, R.string.function_parameters));
        list.add(new SettingItem(R.mipmap.icon_pcs_param, R.string.pcs_parameters));
        return list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
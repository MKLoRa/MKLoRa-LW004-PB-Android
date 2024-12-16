package com.moko.lw004.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moko.ble.lib.task.OrderTask;
import com.moko.lw004.R;
import com.moko.lw004.activity.DeviceInfoActivity;
import com.moko.lw004.databinding.Lw004FragmentDeviceBinding;
import com.moko.lw004.dialog.BottomDialog;
import com.moko.support.lw004.LoRaLW004MokoSupport;
import com.moko.support.lw004.OrderTaskAssembler;

import java.util.ArrayList;

public class DeviceFragment extends Fragment {
    private static final String TAG = DeviceFragment.class.getSimpleName();
    private Lw004FragmentDeviceBinding mBind;

    private ArrayList<String> mTimeZones;
    private int mSelectedTimeZone;
    private ArrayList<String> mLowPowerPrompts;
    private int mSelectedLowPowerPrompt;
    private ArrayList<String> mVibrationIntensity;
    private int mSelectedVibrationIntensity;
    private boolean mLowPowerPayloadEnable;


    private DeviceInfoActivity activity;

    public DeviceFragment() {
    }


    public static DeviceFragment newInstance() {
        DeviceFragment fragment = new DeviceFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        mBind = Lw004FragmentDeviceBinding.inflate(inflater, container, false);
        activity = (DeviceInfoActivity) getActivity();
        mTimeZones = new ArrayList<>();
        for (int i = -24; i <= 28; i++) {
            if (i < 0) {
                if (i % 2 == 0) {
                    mTimeZones.add(String.format("UTC%d", i / 2));
                } else {
                    mTimeZones.add(i < -1 ? String.format("UTC%d:30", (i + 1) / 2) : "UTC-0:30");
                }
            } else if (i == 0) {
                mTimeZones.add("UTC");
            } else {
                if (i % 2 == 0) {
                    mTimeZones.add(String.format("UTC+%d", i / 2));
                } else {
                    mTimeZones.add(String.format("UTC+%d:30", (i - 1) / 2));
                }
            }
        }
        mLowPowerPrompts = new ArrayList<>();
        mLowPowerPrompts.add("10%");
        mLowPowerPrompts.add("20%");
        mLowPowerPrompts.add("30%");
        mLowPowerPrompts.add("40%");
        mLowPowerPrompts.add("50%");
        mLowPowerPrompts.add("60%");
        mVibrationIntensity = new ArrayList<>();
        mVibrationIntensity.add("No");
        mVibrationIntensity.add("Low");
        mVibrationIntensity.add("Medium");
        mVibrationIntensity.add("High");
        return mBind.getRoot();
    }

    public void setTimeZone(int timeZone) {
        mSelectedTimeZone = timeZone + 24;
        mBind.tvTimeZone.setText(mTimeZones.get(mSelectedTimeZone));
    }

    public void showTimeZoneDialog() {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mTimeZones, mSelectedTimeZone);
        dialog.setListener(value -> {
            mSelectedTimeZone = value;
            mBind.tvTimeZone.setText(mTimeZones.get(value));
            activity.showSyncingProgressDialog();
            ArrayList<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.setTimeZone(value - 24));
            orderTasks.add(OrderTaskAssembler.getTimeZone());
            LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        });
        dialog.show(activity.getSupportFragmentManager());
    }

    public void setLowPower(int lowPower) {
        mSelectedLowPowerPrompt = (lowPower / 10) - 1;
        mBind.tvLowPowerPrompt.setText(mLowPowerPrompts.get(mSelectedLowPowerPrompt));
        mBind.tvLowPowerPromptTips.setText(getString(R.string.low_power_prompt_tips_lw004, mLowPowerPrompts.get(mSelectedLowPowerPrompt)));
    }

    public void showLowPowerDialog() {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mLowPowerPrompts, mSelectedLowPowerPrompt);
        dialog.setListener(value -> {
            mSelectedLowPowerPrompt = value;
            mBind.tvLowPowerPrompt.setText(mLowPowerPrompts.get(value));
            mBind.tvLowPowerPromptTips.setText(getString(R.string.low_power_prompt_tips_lw004, mLowPowerPrompts.get(value)));
            activity.showSyncingProgressDialog();
            ArrayList<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.setLowPowerPercent((value + 1) * 10));
            orderTasks.add(OrderTaskAssembler.getLowPowerPercent());
            LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        });
        dialog.show(activity.getSupportFragmentManager());

    }

    public void changeLowPowerPayload() {
        mLowPowerPayloadEnable = !mLowPowerPayloadEnable;
        activity.showSyncingProgressDialog();
        ArrayList<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setLowPowerReportEnable(mLowPowerPayloadEnable ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.getLowPowerReportEnable());
        LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void setLowPowerPayload(int enable) {
        if (enable == 1) {
            mLowPowerPayloadEnable = true;
            mBind.ivLowPowerPayload.setImageResource(R.drawable.lw004_ic_checked);
        } else {
            mLowPowerPayloadEnable = false;
            mBind.ivLowPowerPayload.setImageResource(R.drawable.lw004_ic_unchecked);
        }
    }

    public void setVibrationIntensity(int intensity) {
        if (intensity == 0) {
            mSelectedVibrationIntensity = 0;
        } else if (intensity == 10) {
            mSelectedVibrationIntensity = 1;
        } else if (intensity == 50) {
            mSelectedVibrationIntensity = 2;
        } else if (intensity == 80) {
            mSelectedVibrationIntensity = 3;
        }
        mBind.tvVibrationIntensity.setText(mVibrationIntensity.get(mSelectedVibrationIntensity));
    }

    public void onVibrationIntensity() {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mVibrationIntensity, mSelectedVibrationIntensity);
        dialog.setListener(value -> {
            mSelectedVibrationIntensity = value;
            mBind.tvVibrationIntensity.setText(mVibrationIntensity.get(value));
            int intensity = 0;
            if (mSelectedVibrationIntensity == 1) {
                intensity = 10;
            } else if (mSelectedVibrationIntensity == 2) {
                intensity = 50;
            } else if (mSelectedVibrationIntensity == 3) {
                intensity = 80;
            }
            activity.showSyncingProgressDialog();
            ArrayList<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.setVibrationIntensity(intensity));
            orderTasks.add(OrderTaskAssembler.getVibrationIntensity());
            LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        });
        dialog.show(activity.getSupportFragmentManager());

    }
}

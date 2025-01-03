package com.moko.lw004.activity;


import android.os.Bundle;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lw004.R;
import com.moko.lw004.databinding.Lw004ActivityAlarmAlertSettingsBinding;
import com.moko.lw004.dialog.BottomDialog;
import com.moko.lw004.dialog.LoadingMessageDialog;
import com.moko.lw004.utils.ToastUtils;
import com.moko.support.lw004.LoRaLW004MokoSupport;
import com.moko.support.lw004.OrderTaskAssembler;
import com.moko.support.lw004.entity.OrderCHAR;
import com.moko.support.lw004.entity.ParamsKeyEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class AlarmAlertSettingsActivity extends BaseActivity {
    private Lw004ActivityAlarmAlertSettingsBinding mBind;
    private ArrayList<String> mValues;
    private int mSelected;
    private ArrayList<String> mTriggerModeValues;
    private int mTriggerModeSelected;
    private boolean savedParamsError;

    private boolean mAlertOnStartEnable;
    private boolean mAlertOnEndEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw004ActivityAlarmAlertSettingsBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        mValues = new ArrayList<>();
        mValues.add("BLE");
        mValues.add("GPS");
        mValues.add("BLE&GPS");
        mTriggerModeValues = new ArrayList<>();
        mTriggerModeValues.add("Single Click");
        mTriggerModeValues.add("Double Click");
        mTriggerModeValues.add("Long press 1s");
        mTriggerModeValues.add("Long press 2s");
        mTriggerModeValues.add("Long press 3s");
        mTriggerModeValues.add("Long press 4s");
        mTriggerModeValues.add("Long press 5s");
        EventBus.getDefault().register(this);
        showSyncingProgressDialog();
        mBind.tvTriggerMode.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getAlarmAlertTriggerMode());
            orderTasks.add(OrderTaskAssembler.getAlarmAlertPosStrategy());
            orderTasks.add(OrderTaskAssembler.getAlarmAlertStartEventNotifyEnable());
            orderTasks.add(OrderTaskAssembler.getAlarmAlertEndEventNotifyEnable());
            LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }, 500);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 400)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 400)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        final String action = event.getAction();
        if (!MokoConstants.ACTION_CURRENT_DATA.equals(action))
            EventBus.getDefault().cancelEventDelivery(event);
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderCHAR) {
                    case CHAR_PARAMS:
                        if (value.length >= 4) {
                            int header = value[0] & 0xFF;// 0xED
                            int flag = value[1] & 0xFF;// read or write
                            int cmd = value[2] & 0xFF;
                            if (header != 0xED)
                                return;
                            ParamsKeyEnum configKeyEnum = ParamsKeyEnum.fromParamKey(cmd);
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[3] & 0xFF;
                            if (flag == 0x01) {
                                // write
                                int result = value[4] & 0xFF;
                                switch (configKeyEnum) {
                                    case KEY_ALARM_ALERT_POS_STRATEGY:
                                    case KEY_ALARM_ALERT_START_EVENT_NOTIFY_ENABLE:
                                    case KEY_ALARM_ALERT_END_EVENT_NOTIFY_ENABLE:
                                    case KEY_ALARM_ALERT_TRIGGER_MODE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(AlarmAlertSettingsActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            ToastUtils.showToast(this, "Saved Successfully！");
                                        }
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read
                                switch (configKeyEnum) {
                                    case KEY_ALARM_ALERT_POS_STRATEGY:
                                        if (length > 0) {
                                            int strategy = value[4] & 0xFF;
                                            mSelected = strategy;
                                            mBind.tvAlertPosStrategy.setText(mValues.get(mSelected));
                                        }
                                        break;
                                    case KEY_ALARM_ALERT_START_EVENT_NOTIFY_ENABLE:
                                        if (length > 0) {
                                            mAlertOnStartEnable = value[4] == 1;
                                            mBind.ivAlertOnStart.setImageResource(mAlertOnStartEnable ? R.drawable.lw004_ic_checked : R.drawable.lw004_ic_unchecked);
                                        }
                                        break;
                                    case KEY_ALARM_ALERT_END_EVENT_NOTIFY_ENABLE:
                                        if (length > 0) {
                                            mAlertOnEndEnable = value[4] == 1;
                                            mBind.ivAlertOnEnd.setImageResource(mAlertOnEndEnable ? R.drawable.lw004_ic_checked : R.drawable.lw004_ic_unchecked);
                                        }
                                        break;
                                    case KEY_ALARM_ALERT_TRIGGER_MODE:
                                        if (length > 0) {
                                            mTriggerModeSelected = value[4] & 0xFF;
                                            mBind.tvTriggerMode.setText(mTriggerModeValues.get(mTriggerModeSelected));
                                        }
                                        break;
                                }
                            }
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }


    public void onBack(View view) {
        backHome();
    }

    @Override
    public void onBackPressed() {
        backHome();
    }

    private void backHome() {
        setResult(RESULT_OK);
        finish();
    }

    public void selectPosStrategy(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mSelected);
        dialog.setListener(value -> {
            mSelected = value;
            mBind.tvAlertPosStrategy.setText(mValues.get(value));
            savedParamsError = false;
            showSyncingProgressDialog();
            LoRaLW004MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setAlarmAlertPosStrategy(mSelected));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void selectTriggerMode(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mTriggerModeValues, mTriggerModeSelected);
        dialog.setListener(value -> {
            mTriggerModeSelected = value;
            mBind.tvTriggerMode.setText(mTriggerModeValues.get(value));
            savedParamsError = false;
            showSyncingProgressDialog();
            LoRaLW004MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setAlarmAlertTriggerMode(mTriggerModeSelected));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onChangeAlertOnStart(View view) {
        if (isWindowLocked())
            return;
        mAlertOnStartEnable = !mAlertOnStartEnable;
        savedParamsError = false;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setAlarmAlertStartEventNotifyEnable(mAlertOnStartEnable ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.getAlarmAlertStartEventNotifyEnable());
        LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void onChangeAlertOnEnd(View view) {
        if (isWindowLocked())
            return;
        mAlertOnEndEnable = !mAlertOnEndEnable;
        savedParamsError = false;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setAlarmAlertEndEventNotifyEnable(mAlertOnEndEnable ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.getAlarmAlertEndEventNotifyEnable());
        LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}

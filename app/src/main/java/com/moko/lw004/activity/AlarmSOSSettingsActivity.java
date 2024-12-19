package com.moko.lw004.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw004.databinding.Lw004ActivityAlarmSosSettingsBinding;
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
import java.util.Arrays;
import java.util.List;

public class AlarmSOSSettingsActivity extends BaseActivity {

    private Lw004ActivityAlarmSosSettingsBinding mBind;
    private ArrayList<String> mValues;
    private int mSelected;
    private ArrayList<String> mTriggerModeValues;
    private int mTriggerModeSelected;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw004ActivityAlarmSosSettingsBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        mValues = new ArrayList<>();
        mValues.add("BLE");
        mValues.add("GPS");
        mValues.add("BLE&GPS");
        mTriggerModeValues = new ArrayList<>();
        mTriggerModeValues.add("Double Click");
        mTriggerModeValues.add("Triple Click");
        mTriggerModeValues.add("Long press 1s");
        mTriggerModeValues.add("Long press 2s");
        mTriggerModeValues.add("Long press 3s");
        mTriggerModeValues.add("Long press 4s");
        mTriggerModeValues.add("Long press 5s");
        EventBus.getDefault().register(this);
        showSyncingProgressDialog();
        mBind.tvTriggerMode.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getAlarmSosTriggerMode());
            orderTasks.add(OrderTaskAssembler.getAlarmSosPosStrategy());
            orderTasks.add(OrderTaskAssembler.getAlarmSosReportInterval());
            orderTasks.add(OrderTaskAssembler.getAlarmSosStartEventNotifyEnable());
            orderTasks.add(OrderTaskAssembler.getAlarmSosEndEventNotifyEnable());
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
                                    case KEY_ALARM_SOS_REPORT_INTERVAL:
                                    case KEY_ALARM_SOS_POS_STRATEGY:
                                    case KEY_ALARM_SOS_START_EVENT_NOTIFY_ENABLE:
                                    case KEY_ALARM_SOS_END_EVENT_NOTIFY_ENABLE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_ALARM_SOS_TRIGGER_MODE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(AlarmSOSSettingsActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            ToastUtils.showToast(this, "Saved Successfully！");
                                        }
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read
                                switch (configKeyEnum) {
                                    case KEY_ALARM_SOS_REPORT_INTERVAL:
                                        if (length > 0) {
                                            int interval = MokoUtils.toInt(Arrays.copyOfRange(value, 4, 4 + length));
                                            mBind.etSosReportInterval.setText(String.valueOf(interval));
                                        }
                                        break;
                                    case KEY_ALARM_SOS_POS_STRATEGY:
                                        if (length > 0) {
                                            int strategy = value[4] & 0xFF;
                                            mSelected = strategy;
                                            mBind.tvSosPosStrategy.setText(mValues.get(mSelected));
                                        }
                                        break;
                                    case KEY_ALARM_SOS_START_EVENT_NOTIFY_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mBind.cbSosOnStart.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_ALARM_SOS_END_EVENT_NOTIFY_ENABLE:
                                        if (length > 0) {
                                            int enable = value[4] & 0xFF;
                                            mBind.cbSosOnEnd.setChecked(enable == 1);
                                        }
                                        break;
                                    case KEY_ALARM_SOS_TRIGGER_MODE:
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

    public void onSave(View view) {
        if (isWindowLocked())
            return;
        if (isValid()) {
            showSyncingProgressDialog();
            saveParams();
        } else {
            ToastUtils.showToast(this, "Para error!");
        }
    }

    private boolean isValid() {
        final String intervalStr = mBind.etSosReportInterval.getText().toString();
        if (TextUtils.isEmpty(intervalStr))
            return false;
        final int interval = Integer.parseInt(intervalStr);
        if (interval < 10 || interval > 600)
            return false;
        return true;

    }

    private void saveParams() {
        final String intervalStr = mBind.etSosReportInterval.getText().toString();
        final int interval = Integer.parseInt(intervalStr);
        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setAlarmSOSPosStrategy(mSelected));
        orderTasks.add(OrderTaskAssembler.setAlarmSOSReportInterval(interval));
        orderTasks.add(OrderTaskAssembler.setAlarmSOSStartEventNotifyEnable(mBind.cbSosOnStart.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setAlarmSOSEndEventNotifyEnable(mBind.cbSosOnEnd.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setAlarmSOSTriggerMode(mTriggerModeSelected));
        LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
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
            mBind.tvSosPosStrategy.setText(mValues.get(value));

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
        });
        dialog.show(getSupportFragmentManager());
    }
}

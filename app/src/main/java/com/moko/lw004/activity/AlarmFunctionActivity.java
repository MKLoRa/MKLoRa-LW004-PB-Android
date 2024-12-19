package com.moko.lw004.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lw004.databinding.Lw004ActivityAlarmFunctionBinding;
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

public class AlarmFunctionActivity extends BaseActivity {
    private Lw004ActivityAlarmFunctionBinding mBind;
    private boolean savedParamsError;

    private int mSelected;
    private ArrayList<String> mValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw004ActivityAlarmFunctionBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        mValues = new ArrayList<>();
        mValues.add("NO");
        mValues.add("Alert");
        mValues.add("SOS");
        showSyncingProgressDialog();
        mBind.tvAlarmType.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getAlarmType());
            orderTasks.add(OrderTaskAssembler.getAlarmExitPressDuration());
            LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }, 500);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
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
                                    case KEY_ALARM_TYPE:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_ALARM_EXIT_PRESS_DURATION:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(AlarmFunctionActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            ToastUtils.showToast(this, "Saved Successfully！");
                                        }
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read
                                switch (configKeyEnum) {
                                    case KEY_ALARM_TYPE:
                                        if (length > 0) {
                                            int type = value[4] & 0xFF;
                                            mSelected = type;
                                            mBind.tvAlarmType.setText(mValues.get(type));
                                        }
                                        break;
                                    case KEY_ALARM_EXIT_PRESS_DURATION:
                                        if (length > 0) {
                                            int duration = value[4] & 0xFF;
                                            mBind.etExitAlarmDuration.setText(String.valueOf(duration));
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

    public void onSave(View view) {
        final String durationStr = mBind.etExitAlarmDuration.getText().toString();
        if (TextUtils.isEmpty(durationStr)) {
            ToastUtils.showToast(this, "Para error!");
            return;
        }
        final int timeout = Integer.parseInt(durationStr);
        if (timeout < 5 || timeout > 15) {
            ToastUtils.showToast(this, "Para error!");
            return;
        }
        savedParamsError = false;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setAlarmType(mSelected));
        orderTasks.add(OrderTaskAssembler.setAlarmExitPressDuration(timeout));
        LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void onAlertSettings(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, AlarmAlertSettingsActivity.class));
    }

    public void onSOSSettings(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, AlarmSOSSettingsActivity.class));
    }

    public void selectAlarmType(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mSelected);
        dialog.setListener(value -> {
            mSelected = value;
            mBind.tvAlarmType.setText(mValues.get(value));
        });
        dialog.show(getSupportFragmentManager());
    }
}

package com.moko.lw004.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lw004.AppConstants;
import com.moko.lw004.R;
import com.moko.lw004.databinding.Lw004ActivityFilterRawDataSwitchBinding;
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

import androidx.annotation.Nullable;

public class FilterRawDataSwitchActivity extends BaseActivity {


    private Lw004ActivityFilterRawDataSwitchBinding mBind;
    private boolean savedParamsError;

    private boolean isBXPAccOpen;
    private boolean isBXPTHOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw004ActivityFilterRawDataSwitchBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        showSyncingProgressDialog();
        mBind.tvFilterByIbeacon.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getFilterRawData());
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
                                    case KEY_FILTER_BXP_ACC:
                                    case KEY_FILTER_BXP_TH:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(FilterRawDataSwitchActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            ToastUtils.showToast(this, "Saved Successfully！");
                                        }
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read
                                switch (configKeyEnum) {
                                    case KEY_FILTER_RAW_DATA:
                                        if (length == 9) {
                                            mBind.tvFilterByOther.setText(value[4] == 1 ? "ON" : "OFF");
                                            mBind.tvFilterByIbeacon.setText(value[5] == 1 ? "ON" : "OFF");
                                            mBind.tvFilterByUid.setText(value[6] == 1 ? "ON" : "OFF");
                                            mBind.tvFilterByUrl.setText(value[7] == 1 ? "ON" : "OFF");
                                            mBind.tvFilterByTlm.setText(value[8] == 1 ? "ON" : "OFF");
                                            mBind.ivFilterByBxpAcc.setImageResource(value[9] == 1 ? R.drawable.lw004_ic_checked : R.drawable.lw004_ic_unchecked);
                                            mBind.ivFilterByBxpTh.setImageResource(value[10] == 1 ? R.drawable.lw004_ic_checked : R.drawable.lw004_ic_unchecked);
                                            mBind.tvFilterByMkibeacon.setText(value[11] == 1 ? "ON" : "OFF");
                                            mBind.tvFilterByMkibeaconAcc.setText(value[12] == 1 ? "ON" : "OFF");
                                            isBXPAccOpen = value[9] == 1;
                                            isBXPTHOpen = value[10] == 1;
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

    private LoadingMessageDialog mLoadingMessageDialog;

    public void showSyncingProgressDialog() {
        mLoadingMessageDialog = new LoadingMessageDialog();
        mLoadingMessageDialog.setMessage("Syncing..");
        mLoadingMessageDialog.show(getSupportFragmentManager());

    }

    public void dismissSyncProgressDialog() {
        if (mLoadingMessageDialog != null)
            mLoadingMessageDialog.dismissAllowingStateLoss();
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

    public void onFilterByBXPAcc(View view) {
        if (isWindowLocked())
            return;
        isBXPAccOpen = !isBXPAccOpen;
        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setFilterBXPAccEnable(isBXPAccOpen ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.getFilterRawData());
        LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void onFilterByBXPTH(View view) {
        if (isWindowLocked())
            return;
        isBXPTHOpen = !isBXPTHOpen;
        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setFilterBXPTHEnable(isBXPTHOpen ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.getFilterRawData());
        LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void onFilterByIBeacon(View view) {
        if (isWindowLocked())
            return;
        Intent i = new Intent(this, FilterIBeaconActivity.class);
        startActivityForResult(i, AppConstants.REQUEST_CODE_FILTER_RAW_DATA);
    }

    public void onFilterByUid(View view) {
        if (isWindowLocked())
            return;
        Intent i = new Intent(this, FilterUIDActivity.class);
        startActivityForResult(i, AppConstants.REQUEST_CODE_FILTER_RAW_DATA);
    }

    public void onFilterByUrl(View view) {
        if (isWindowLocked())
            return;
        Intent i = new Intent(this, FilterUrlActivity.class);
        startActivityForResult(i, AppConstants.REQUEST_CODE_FILTER_RAW_DATA);
    }

    public void onFilterByTlm(View view) {
        if (isWindowLocked())
            return;
        Intent i = new Intent(this, FilterTLMActivity.class);
        startActivityForResult(i, AppConstants.REQUEST_CODE_FILTER_RAW_DATA);
    }

    public void onFilterByMKiBeacon(View view) {
        if (isWindowLocked())
            return;
        Intent i = new Intent(this, FilterMKIBeaconActivity.class);
        startActivityForResult(i, AppConstants.REQUEST_CODE_FILTER_RAW_DATA);
    }

    public void onFilterByMKiBeaconAcc(View view) {
        if (isWindowLocked())
            return;
        Intent i = new Intent(this, FilterMKIBeaconAccActivity.class);
        startActivityForResult(i, AppConstants.REQUEST_CODE_FILTER_RAW_DATA);
    }

    public void onFilterByOther(View view) {
        if (isWindowLocked())
            return;
        Intent i = new Intent(this, FilterOtherActivity.class);
        startActivityForResult(i, AppConstants.REQUEST_CODE_FILTER_RAW_DATA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_FILTER_RAW_DATA) {
            showSyncingProgressDialog();
            mBind.tvFilterByIbeacon.postDelayed(() -> {
                List<OrderTask> orderTasks = new ArrayList<>();
                orderTasks.add(OrderTaskAssembler.getFilterRawData());
                LoRaLW004MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
            }, 1000);
        }
    }
}

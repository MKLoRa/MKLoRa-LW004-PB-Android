package com.moko.lw004.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.moko.lw004.R;
import com.moko.lw004.entity.LogData;

public class LogDataListAdapter extends BaseQuickAdapter<LogData, BaseViewHolder> {

    public LogDataListAdapter() {
        super(R.layout.lw004_item_log_data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LogData item) {
        helper.setText(R.id.tv_time, item.name);
        helper.setImageResource(R.id.iv_checked, item.isSelected ? R.drawable.lw004_ic_selected : R.drawable.lw004_ic_unselected);
    }
}

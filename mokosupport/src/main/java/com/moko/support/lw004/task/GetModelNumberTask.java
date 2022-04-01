package com.moko.support.lw004.task;

import com.moko.ble.lib.task.OrderTask;
import com.moko.support.lw004.entity.OrderCHAR;

public class GetModelNumberTask extends OrderTask {

    public byte[] data;

    public GetModelNumberTask() {
        super(OrderCHAR.CHAR_MODEL_NUMBER, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}

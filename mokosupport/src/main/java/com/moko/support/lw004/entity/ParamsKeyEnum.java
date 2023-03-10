package com.moko.support.lw004.entity;


import java.io.Serializable;

public enum ParamsKeyEnum implements Serializable {

    KEY_CLOSE(0x02),
    KEY_RESTART(0x03),
    KEY_RESET(0x04),
    KEY_VOLTAGE(0x05),
    KEY_BUTTON_CLOSE_ENABLE(0x06),
    // 低电百分比
    KEY_LOW_POWER_PERCENT(0x07),
    // 低电状态数据上报开关
    KEY_LOW_POWER_REPORT_ENABLE(0x08),
    // ble
    KEY_ADV_NAME(0x09),
    KEY_ADV_INTERVAL(0x0A),
    KEY_ADV_TX_POWER(0x0B),
    KEY_ADV_TIMEOUT(0x0C),
    KEY_PASSWORD_VERIFY_ENABLE(0x0D),
    KEY_PASSWORD(0x0E),
    KEY_TIME_ZONE(0x0F),
    KEY_TIME_UTC(0x10),
    // ACC
    KEY_ACC_WAKEUP_THRESHOLD(0x11),
    KEY_ACC_WAKEUP_DURATION(0x12),
    KEY_ACC_MOTION_THRESHOLD(0x13),
    KEY_ACC_MOTION_DURATION(0x14),
    KEY_ACC_MOTION_END_TIMEOUT(0x15),

    KEY_START_ADV(0x16),
    KEY_MAC(0x17),
    KEY_PCBA_STATUS(0x1B),
    KEY_SELFTEST_STATUS(0x1C),

    // 关机信息上报开关
    KEY_SHUTDOWN_PAYLOAD_ENABLE(0x20),
    // 工作模式选择
    KEY_DEVICE_MODE(0x21),
    // 设备心跳间隔
    KEY_HEARTBEAT_INTERVAL(0x22),
    // 定期模式上报间隔
    KEY_PERIODIC_MODE_REPORT_INTERVAL(0x23),
    // 定期模式定位策略
    KEY_PERIODIC_MODE_POS_STRATEGY(0x24),
    // 定时模式时间点
    KEY_TIME_MODE_REPORT_TIME_POINT(0x25),
    // 定时模式定位策略
    KEY_TIME_MODE_POS_STRATEGY(0x26),
    // 运动模式事件
    KEY_MOTION_MODE_EVENT(0x27),
    // 运动开始定位策略
    KEY_MOTION_MODE_START_POS_STRATEGY(0x28),
    // 运动开始定位上报次数
    KEY_MOTION_MODE_START_NUMBER(0x29),
    // 运动中定位策略
    KEY_MOTION_MODE_TRIP_POS_STRATEGY(0x2A),
    // 运动中定位间隔
    KEY_MOTION_MODE_TRIP_REPORT_INTERVAL(0x2B),
    // 运动结束定位策略
    KEY_MOTION_MODE_END_POS_STRATEGY(0x2C),
    // 运动结束定位次数
    KEY_MOTION_MODE_END_NUMBER(0x2D),
    // 运动结束定位间隔
    KEY_MOTION_MODE_END_REPORT_INTERVAL(0x2E),


    // 下行请求定位策略
    KEY_DOWN_LINK_POS_STRATEGY(0x30),
    // ManDown功能开关
    KEY_MAN_DOWN_DETECTION_ENABLE(0x31),
    // ManDown定位策略
    KEY_MAN_DOWN_POS_STRATEGY(0x32),
    // ManDown检测超时时间
    KEY_MAN_DOWN_DETECTION_TIMEOUT(0x33),
    // ManDown定位数据上报间隔
    KEY_MAN_DOWN_REPORT_INTERVAL(0x34),
    // ManDown开始事件通知开关
    KEY_MAN_DOWN_START_EVENT_NOTIFY_ENABLE(0x35),
    // ManDown结束事件通知开关
    KEY_MAN_DOWN_END_EVENT_NOTIFY_ENABLE(0x36),
    // 马达震动强度
    KEY_VIBRATION_INTENSITY(0x37),
    // 报警类型
    KEY_ALARM_TYPE(0x38),
    // 退出报警操作
    KEY_ALARM_EXIT_PRESS_DURATION(0x39),
    // SOS开始事件通知
    KEY_ALARM_SOS_START_EVENT_NOTIFY_ENABLE(0x3A),
    // SOS结束事件通知
    KEY_ALARM_SOS_END_EVENT_NOTIFY_ENABLE(0x3B),
    // SOS定位策略
    KEY_ALARM_SOS_POS_STRATEGY(0x3C),
    // SOS定位数据上报间隔
    KEY_ALARM_SOS_REPORT_INTERVAL(0x3D),
    // SOS触发方式
    KEY_ALARM_SOS_TRIGGER_MODE(0x3E),
    // Alert开始事件通知
    KEY_ALARM_ALERT_START_EVENT_NOTIFY_ENABLE(0x3F),
    // Alert结束事件通知
    KEY_ALARM_ALERT_END_EVENT_NOTIFY_ENABLE(0x40),
    // Alert定位策略
    KEY_ALARM_ALERT_POS_STRATEGY(0x41),
    // Alert触发方式
    KEY_ALARM_ALERT_TRIGGER_MODE(0x42),

    // GPS定位超时
    KEY_GPS_POS_TIMEOUT(0x50),
    // GPS位置精度因子
    KEY_GPS_PDOP_LIMIT(0x51),
    // 蓝牙定位超时时间
    KEY_BLE_POS_TIMEOUT(0x52),
    // 蓝牙定位成功MAC数量
    KEY_BLE_POS_MAC_NUMBER(0x53),
    // 过滤
    KEY_FILTER_RSSI(0x54),
    KEY_FILTER_RELATIONSHIP(0x55),
    KEY_FILTER_MAC_PRECISE(0x56),
    KEY_FILTER_MAC_REVERSE(0x57),
    KEY_FILTER_MAC_RULES(0x58),
    KEY_FILTER_NAME_PRECISE(0x59),
    KEY_FILTER_NAME_REVERSE(0x5A),
    KEY_FILTER_NAME_RULES(0x5B),
    KEY_FILTER_RAW_DATA(0x5C),
    KEY_FILTER_IBEACON_ENABLE(0x5D),
    KEY_FILTER_IBEACON_MAJOR_RANGE(0x5E),
    KEY_FILTER_IBEACON_MINOR_RANGE(0x5F),
    KEY_FILTER_IBEACON_UUID(0x60),
    KEY_FILTER_MKIBEACON_ENABLE(0x61),
    KEY_FILTER_MKIBEACON_MAJOR_RANGE(0x62),
    KEY_FILTER_MKIBEACON_MINOR_RANGE(0x63),
    KEY_FILTER_MKIBEACON_UUID(0x64),
    KEY_FILTER_MKIBEACON_ACC_ENABLE(0x65),
    KEY_FILTER_MKIBEACON_ACC_MAJOR_RANGE(0x66),
    KEY_FILTER_MKIBEACON_ACC_MINOR_RANGE(0x67),
    KEY_FILTER_MKIBEACON_ACC_UUID(0x68),
    KEY_FILTER_EDDYSTONE_UID_ENABLE(0x69),
    KEY_FILTER_EDDYSTONE_UID_NAMESPACE(0x6A),
    KEY_FILTER_EDDYSTONE_UID_INSTANCE(0x6B),
    KEY_FILTER_EDDYSTONE_URL_ENABLE(0x6C),
    KEY_FILTER_EDDYSTONE_URL(0x6D),
    KEY_FILTER_EDDYSTONE_TLM_ENABLE(0x6E),
    KEY_FILTER_EDDYSTONE_TLM_VERSION(0x6F),
    KEY_FILTER_BXP_ACC(0x70),
    KEY_FILTER_BXP_TH(0x71),
    KEY_FILTER_OTHER_ENABLE(0x72),
    KEY_FILTER_OTHER_RELATIONSHIP(0x73),
    KEY_FILTER_OTHER_RULES(0x74),


    // lora
    KEY_LORA_REGION(0x80),
    KEY_LORA_MODE(0x81),
    KEY_NETWORK_STATUS(0x82),
    KEY_LORA_DEV_EUI(0x83),
    KEY_LORA_APP_EUI(0x84),
    KEY_LORA_APP_KEY(0x85),
    KEY_LORA_DEV_ADDR(0x86),
    KEY_LORA_APP_SKEY(0x87),
    KEY_LORA_NWK_SKEY(0x88),
    KEY_LORA_MESSAGE_TYPE(0x89),
    KEY_LORA_CH(0x8A),
    KEY_LORA_DR(0x8B),
    KEY_LORA_UPLINK_STRATEGY(0x8C),
    KEY_LORA_DUTYCYCLE(0x8D),
    KEY_LORA_TIME_SYNC_INTERVAL(0x8E),
    KEY_LORA_NETWORK_CHECK_INTERVAL(0x8F),
    KEY_LORA_MAX_RETRANSMISSION_TIMES(0x90),
    KEY_LORA_ADR_ACK_LIMIT(0x91),
    KEY_LORA_ADR_ACK_DELAY(0x92),
    ;

    private int paramsKey;

    ParamsKeyEnum(int paramsKey) {
        this.paramsKey = paramsKey;
    }


    public int getParamsKey() {
        return paramsKey;
    }

    public static ParamsKeyEnum fromParamKey(int paramsKey) {
        for (ParamsKeyEnum paramsKeyEnum : ParamsKeyEnum.values()) {
            if (paramsKeyEnum.getParamsKey() == paramsKey) {
                return paramsKeyEnum;
            }
        }
        return null;
    }
}

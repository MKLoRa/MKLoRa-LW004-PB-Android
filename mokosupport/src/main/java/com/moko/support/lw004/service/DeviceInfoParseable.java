package com.moko.support.lw004.service;

import com.moko.support.lw004.entity.DeviceInfo;

public interface DeviceInfoParseable<T> {
    T parseDeviceInfo(DeviceInfo deviceInfo);
}

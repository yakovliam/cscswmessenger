package com.yakovliam.cscswmessenger.service.machine;

import com.welie.blessed.BluetoothGattCharacteristic;

public interface DataMachine {

    void start();

    void onReceiveData(BluetoothGattCharacteristic characteristic, byte[] data);
}

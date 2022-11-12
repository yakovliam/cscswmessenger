package com.yakovliam.cscswmessenger.machine;

import com.welie.blessed.BluetoothGattCharacteristic;

public class EmptyDataMachine implements DataMachine {
    @Override
    public void start() {
        // no-op
    }

    @Override
    public void onReceiveData(BluetoothGattCharacteristic characteristic, byte[] data) {
        // no-op
    }
}

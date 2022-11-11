package com.yakovliam.cscswmessenger.filter.filters.data;

import com.welie.blessed.BluetoothGattCharacteristic;
import com.welie.blessed.BluetoothPeripheral;

public class BlessedPeripheralConnectionDataReceivedPredicateActionData {

    /**
     * Peripheral
     */
    private final BluetoothPeripheral peripheral;

    /**
     * Gatt characteristic
     */
    private final BluetoothGattCharacteristic bluetoothGattCharacteristic;

    /**
     * Predicate action data constructor
     *
     * @param peripheral                  peripheral
     * @param bluetoothGattCharacteristic bluetooth gatt characteristic
     */
    public BlessedPeripheralConnectionDataReceivedPredicateActionData(BluetoothPeripheral peripheral, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        this.peripheral = peripheral;
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
    }

    /**
     * Returns the peripheral
     *
     * @return peripheral
     */
    public BluetoothPeripheral peripheral() {
        return peripheral;
    }

    /**
     * Returns the gatt characteristic
     *
     * @return gatt characteristic
     */
    public BluetoothGattCharacteristic bluetoothGattCharacteristic() {
        return bluetoothGattCharacteristic;
    }
}

package com.yakovliam.cscswmessenger.service;

import com.welie.blessed.*;
import org.jetbrains.annotations.NotNull;

public abstract class BlessedBLEService implements Service {

    /**
     * When a peripheral is discovered
     *
     * @param peripheral peripheral
     * @param scanResult scan result
     */
    public abstract void onDiscoveredPeripheral(@NotNull BluetoothPeripheral peripheral, @NotNull ScanResult scanResult);

    /**
     * When a peripheral is connected
     *
     * @param peripheral peripheral
     */
    public abstract void onConnectedToPeripheral(BluetoothPeripheral peripheral);

    /**
     * When a peripheral is disconnected
     *
     * @param peripheral peripheral
     */
    public abstract void onDisconnectedFromPeripheral(BluetoothPeripheral peripheral);

    /**
     * When new data is received
     *
     * @param peripheral     peripheral
     * @param value          value
     * @param characteristic characteristic that was updated / data was received from
     * @param status         status
     */
    public abstract void onDataReceived(@NotNull BluetoothPeripheral peripheral, byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull BluetoothCommandStatus status);

    /**
     * Writes data to a target characteristic
     *
     * @param characteristic target characteristic
     * @param data           data
     */
    public abstract void write(BluetoothGattCharacteristic characteristic, byte[] data);

    /**
     * When a service is discovered
     *
     * @param peripheral peripheral
     * @param service    service
     */
    public abstract void onDiscoveredService(BluetoothPeripheral peripheral, BluetoothGattService service);

    /**
     * When a characteristic is discovered
     *
     * @param peripheral     peripheral
     * @param service        service
     * @param characteristic characteristic
     */
    public abstract void onDiscoveredCharacteristic(BluetoothPeripheral peripheral, BluetoothGattService service, BluetoothGattCharacteristic characteristic);

    /**
     * Returns the BCM
     *
     * @return BCM
     */
    public abstract BluetoothCentralManager bluetoothCentralManager();

    /**
     * Returns the target characteristic
     *
     * @return target characteristic
     */
    public abstract BluetoothGattCharacteristic targetCharacteristic();
}

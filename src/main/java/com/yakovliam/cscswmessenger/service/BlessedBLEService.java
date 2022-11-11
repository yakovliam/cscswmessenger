package com.yakovliam.cscswmessenger.service;

import com.welie.blessed.*;
import org.jetbrains.annotations.NotNull;

public abstract class BlessedBLEService implements Service {

    /**
     * When the target peripheral is connected
     *
     * @param peripheral peripheral
     */
    public abstract void onConnectedToTargetPeripheral(BluetoothPeripheral peripheral);

    /**
     * When the target peripheral is disconnected
     *
     * @param peripheral peripheral
     */
    public abstract void onDisconnectedFromTargetPeripheral(BluetoothPeripheral peripheral);

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
     * Returns the BCM
     *
     * @return BCM
     */
    public abstract BluetoothCentralManager getBluetoothCentralManager();

    /**
     * Writes data to a predetermined target characteristic
     *
     * @param data data
     */
    public abstract void write(byte[] data);

    /**
     * When the target service is discovered
     *
     * @param service service
     */
    public abstract void onDiscoveredTargetService(BluetoothGattService service);

    /**
     * When the target characteristic is discovered
     *
     * @param bluetoothGattCharacteristic characteristic
     */
    public abstract void onDiscoveredTargetCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic);
}

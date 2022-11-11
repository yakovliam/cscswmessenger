package com.yakovliam.cscswmessenger.provider.peripheral;

import com.welie.blessed.*;
import com.yakovliam.cscswmessenger.CSCSWMessengerBootstrapper;
import com.yakovliam.cscswmessenger.service.BlessedBLEService;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class BCMPeripheralConnectionCallbackImplementation extends BluetoothPeripheralCallback {

    /**
     * The blessed BLE service
     */
    private final BlessedBLEService blessedBLEService;

    /**
     * Bluetooth Central Manager peripheral connection callback implementation constructor
     * * @param blessedBLEService blessed BLE service
     */
    public BCMPeripheralConnectionCallbackImplementation(BlessedBLEService blessedBLEService) {
        this.blessedBLEService = blessedBLEService;
    }

    @Override
    public void onServicesDiscovered(@NotNull BluetoothPeripheral peripheral, @NotNull List<BluetoothGattService> services) {
        // loop through the services and call the callback for each
        services.forEach(service -> {
            // call the callback for the service
            this.blessedBLEService.onDiscoveredService(peripheral, service);
            // note: discovering of characteristics are handled in the callback itself
        });
    }

    @Override
    public void onNotificationStateUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull BluetoothGattCharacteristic characteristic, @NotNull BluetoothCommandStatus status) {
        super.onNotificationStateUpdate(peripheral, characteristic, status);
    }

    @Override
    public void onCharacteristicUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull BluetoothCommandStatus status) {
        CSCSWMessengerBootstrapper.LOGGER.info("Characteristic update was called, data received: {}", Arrays.toString(value));
        // call the callback that data was received
        this.blessedBLEService.onDataReceived(peripheral, value, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(@NotNull BluetoothPeripheral peripheral, byte @NotNull [] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull BluetoothCommandStatus status) {
        super.onCharacteristicWrite(peripheral, value, characteristic, status);
    }

    @Override
    public void onDescriptorRead(@NotNull BluetoothPeripheral peripheral, byte @NotNull [] value, @NotNull BluetoothGattDescriptor descriptor, @NotNull BluetoothCommandStatus status) {
        super.onDescriptorRead(peripheral, value, descriptor, status);
    }

    @Override
    public void onDescriptorWrite(@NotNull BluetoothPeripheral peripheral, byte @NotNull [] value, @NotNull BluetoothGattDescriptor descriptor, @NotNull BluetoothCommandStatus status) {
        super.onDescriptorWrite(peripheral, value, descriptor, status);
    }

    @Override
    public void onBondingStarted(@NotNull BluetoothPeripheral peripheral) {
        super.onBondingStarted(peripheral);
    }

    @Override
    public void onBondingSucceeded(@NotNull BluetoothPeripheral peripheral) {
        super.onBondingSucceeded(peripheral);
    }

    @Override
    public void onBondingFailed(@NotNull BluetoothPeripheral peripheral) {
        super.onBondingFailed(peripheral);
    }

    @Override
    public void onBondLost(@NotNull BluetoothPeripheral peripheral) {
        super.onBondLost(peripheral);
    }

    @Override
    public void onReadRemoteRssi(@NotNull BluetoothPeripheral peripheral, int rssi, @NotNull BluetoothCommandStatus status) {
        super.onReadRemoteRssi(peripheral, rssi, status);
    }
}

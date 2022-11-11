package com.yakovliam.cscswmessenger.provider.peripheral;

import com.welie.blessed.*;
import com.yakovliam.cscswmessenger.CSCSWMessengerBootstrapper;
import com.yakovliam.cscswmessenger.provider.context.BLEPeripheralConnectionProvisionContext;
import com.yakovliam.cscswmessenger.service.BlessedBLEService;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BCMPeripheralConnectionCallbackImplementation extends BluetoothPeripheralCallback {

    /**
     * Provision context
     */
    private final BLEPeripheralConnectionProvisionContext provisionContext;

    /**
     * The blessed BLE service
     */
    private final BlessedBLEService blessedBLEService;

    /**
     * Bluetooth Central Manager peripheral connection callback implementation constructor
     *
     * @param provisionContext  provision context
     * @param blessedBLEService blessed BLE service
     */
    public BCMPeripheralConnectionCallbackImplementation(BLEPeripheralConnectionProvisionContext provisionContext, BlessedBLEService blessedBLEService) {
        this.provisionContext = provisionContext;
        this.blessedBLEService = blessedBLEService;
    }

    @Override
    public void onServicesDiscovered(@NotNull BluetoothPeripheral peripheral, @NotNull List<BluetoothGattService> services) {
        Optional<BluetoothGattService> optionalBluetoothGattService = services.stream().filter(this.provisionContext::serviceDiscoveredPassesTargetTest).findFirst();
        if (optionalBluetoothGattService.isEmpty()) {
            return;
        }
        BluetoothGattService targetService = optionalBluetoothGattService.get();

        // target is found, so call the callback
        this.blessedBLEService.onDiscoveredTargetService(targetService);
        // run the characteristic provision context filter on the characteristics of that service
        // TODO implement this

        // on the characteristic that match the provision context filter, notify when an update occurs
    }

    @Override
    public void onNotificationStateUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull BluetoothGattCharacteristic characteristic, @NotNull BluetoothCommandStatus status) {
        super.onNotificationStateUpdate(peripheral, characteristic, status);
    }

    @Override
    public void onCharacteristicUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull BluetoothCommandStatus status) {
        CSCSWMessengerBootstrapper.LOGGER.info("Characteristic update was called, data received: {}", Arrays.toString(value));
        // if the received data meets the provision context
        if (!this.provisionContext.connectionDataPassesTargetTest(peripheral, characteristic)) {
            return;
        }
        // call the callback that data was received
        this.blessedBLEService.onDataReceived(peripheral, value, characteristic, status);

    }

    @Override
    public void onCharacteristicWrite(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull BluetoothCommandStatus status) {
        super.onCharacteristicWrite(peripheral, value, characteristic, status);
    }

    @Override
    public void onDescriptorRead(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattDescriptor descriptor, @NotNull BluetoothCommandStatus status) {
        super.onDescriptorRead(peripheral, value, descriptor, status);
    }

    @Override
    public void onDescriptorWrite(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattDescriptor descriptor, @NotNull BluetoothCommandStatus status) {
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

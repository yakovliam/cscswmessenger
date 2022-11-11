package com.yakovliam.cscswmessenger.provider;

import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothCommandStatus;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.ScanResult;
import com.yakovliam.cscswmessenger.CSCSWMessengerBootstrapper;
import com.yakovliam.cscswmessenger.provider.context.BLEPeripheralConnectionProvisionContext;
import com.yakovliam.cscswmessenger.provider.context.BLEPeripheralTargetProvisionContext;
import com.yakovliam.cscswmessenger.provider.peripheral.BluetoothCentralManagerPeripheralConnectionCallbackProvider;
import com.yakovliam.cscswmessenger.service.BlessedBLEService;
import com.yakovliam.cscswmessenger.utils.PeripheralConstants;
import org.jetbrains.annotations.NotNull;

public class BCMCallbackImplementation extends BluetoothCentralManagerCallback {

    /**
     * Provision context
     */
    private final BLEPeripheralTargetProvisionContext provisionContext;

    /**
     * The blessed BLE service
     */
    private final BlessedBLEService blessedBLEService;

    /**
     * Peripheral connection callback provider
     */
    private final BluetoothCentralManagerPeripheralConnectionCallbackProvider peripheralConnectionCallbackProvider;

    /**
     * Bluetooth Central Manager callback implementation constructor
     *
     * @param provisionContext  provision context
     * @param blessedBLEService blessed BLE service
     */
    public BCMCallbackImplementation(BLEPeripheralTargetProvisionContext provisionContext, BlessedBLEService blessedBLEService) {
        this.provisionContext = provisionContext;
        this.blessedBLEService = blessedBLEService;
        this.peripheralConnectionCallbackProvider = new BluetoothCentralManagerPeripheralConnectionCallbackProvider(new BLEPeripheralConnectionProvisionContext(PeripheralConstants.TARGET_CHARACTERISTIC_UUID), blessedBLEService);
    }

    @Override
    public void onConnectedPeripheral(@NotNull BluetoothPeripheral peripheral) {
        // call the callback inside the service
        this.blessedBLEService.onConnectedToTargetPeripheral(peripheral);
    }

    @Override
    public void onConnectionFailed(@NotNull BluetoothPeripheral peripheral, @NotNull BluetoothCommandStatus status) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void onDisconnectedPeripheral(@NotNull BluetoothPeripheral peripheral, @NotNull BluetoothCommandStatus status) {
        super.onDisconnectedPeripheral(peripheral, status);
        // call the disconnection callback
        this.blessedBLEService.onDisconnectedFromTargetPeripheral(peripheral);
    }

    @Override
    public void onDiscoveredPeripheral(@NotNull BluetoothPeripheral peripheral, @NotNull ScanResult scanResult) {
        CSCSWMessengerBootstrapper.LOGGER.info("Discovered peripheral with name {}, addr {}", peripheral.getName(), peripheral.getAddress());
        // if the discovered peripheral isn't the target
        if (!this.provisionContext.peripheralPassesTargetTest(peripheral)) {
            return;
        }
        CSCSWMessengerBootstrapper.LOGGER.info("Target peripheral discovered!");
        // connect to the target peripheral
        this.blessedBLEService.getBluetoothCentralManager().connectPeripheral(peripheral, this.peripheralConnectionCallbackProvider.provide());
    }

    @Override
    public void onScanStarted() {
        super.onScanStarted();
    }

    @Override
    public void onScanStopped() {
        super.onScanStopped();
    }

    @Override
    public void onScanFailed(int errorCode) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public @NotNull String onPinRequest(@NotNull BluetoothPeripheral peripheral) {
        throw new RuntimeException("Not implemented");
    }
}

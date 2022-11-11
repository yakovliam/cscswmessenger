package com.yakovliam.cscswmessenger.provider;

import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothCommandStatus;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.ScanResult;
import com.yakovliam.cscswmessenger.CSCSWMessengerBootstrapper;
import com.yakovliam.cscswmessenger.service.BlessedBLEService;
import org.jetbrains.annotations.NotNull;

public class BCMCallbackImplementation extends BluetoothCentralManagerCallback {

    /**
     * The blessed BLE service
     */
    private final BlessedBLEService blessedBLEService;

    /**
     * Bluetooth Central Manager callback implementation constructor
     *
     * @param blessedBLEService blessed BLE service
     */
    public BCMCallbackImplementation(BlessedBLEService blessedBLEService) {
        this.blessedBLEService = blessedBLEService;
    }

    @Override
    public void onConnectedPeripheral(@NotNull BluetoothPeripheral peripheral) {
        CSCSWMessengerBootstrapper.LOGGER.info("Connected to the peripheral with name {}, addr {}", peripheral.getName(), peripheral.getAddress());
        // call the callback inside the service
        this.blessedBLEService.onConnectedToPeripheral(peripheral);
    }

    @Override
    public void onConnectionFailed(@NotNull BluetoothPeripheral peripheral, @NotNull BluetoothCommandStatus status) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void onDisconnectedPeripheral(@NotNull BluetoothPeripheral peripheral, @NotNull BluetoothCommandStatus status) {
        CSCSWMessengerBootstrapper.LOGGER.info("Disconnected from peripheral with name {}", peripheral.getName());
        // call the disconnection callback
        this.blessedBLEService.onDisconnectedFromPeripheral(peripheral);
    }

    @Override
    public void onDiscoveredPeripheral(@NotNull BluetoothPeripheral peripheral, @NotNull ScanResult scanResult) {
        CSCSWMessengerBootstrapper.LOGGER.info("Discovered peripheral with name {}, addr {}", peripheral.getName(), peripheral.getAddress());
        this.blessedBLEService.onDiscoveredPeripheral(peripheral, scanResult);
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

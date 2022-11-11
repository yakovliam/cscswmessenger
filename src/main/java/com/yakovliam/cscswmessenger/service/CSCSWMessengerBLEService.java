package com.yakovliam.cscswmessenger.service;

import com.welie.blessed.*;
import com.yakovliam.cscswmessenger.provider.BluetoothCentralManagerCallbackProvider;
import com.yakovliam.cscswmessenger.provider.context.BLEPeripheralTargetProvisionContext;
import com.yakovliam.cscswmessenger.service.machine.CSCSWMessengerDataMachine;
import com.yakovliam.cscswmessenger.service.machine.DataMachine;
import com.yakovliam.cscswmessenger.service.machine.EmptyDataMachine;
import com.yakovliam.cscswmessenger.utils.PeripheralConstants;
import org.jetbrains.annotations.NotNull;

public class CSCSWMessengerBLEService extends BlessedBLEService {

    private static final String TARGET_ENDING_DIGITS = "018";

    /**
     * The data machine that receives data and sends data
     * This machine is also responsible for keeping state
     */
    private DataMachine dataMachine;

    /**
     * The target peripheral
     */
    private BluetoothPeripheral targetPeripheral;

    /**
     * Target characteristic
     */
    private BluetoothGattCharacteristic targetCharacteristic;

    /**
     * Blessed Bluetooth central manager
     */
    private final BluetoothCentralManager bluetoothCentralManager;

    public CSCSWMessengerBLEService() {
        this.dataMachine = new EmptyDataMachine();
        // create a bluetooth central manager callback provider
        BluetoothCentralManagerCallbackProvider bluetoothCentralManagerCallbackProvider = new BluetoothCentralManagerCallbackProvider(new BLEPeripheralTargetProvisionContext(TARGET_ENDING_DIGITS), this);
        // initialize the bluetooth central manager with the callback provider
        this.bluetoothCentralManager = new BluetoothCentralManager(bluetoothCentralManagerCallbackProvider.provide());
    }

    /**
     * Starts scanning for peripherals
     */
    public void startScanningForPeripherals() {
        this.bluetoothCentralManager.scanForPeripherals();
    }

    /**
     * Stops scanning for peripherals
     */
    public void stopScanningForPeripherals() {
        this.bluetoothCentralManager.stopScan();
    }

    @Override
    public void onConnectedToTargetPeripheral(BluetoothPeripheral peripheral) {
        // stop scanning for peripherals, we found the target one
        this.stopScanningForPeripherals();
        // set the target peripheral
        this.targetPeripheral = peripheral;
        // initialize a new data machine
        this.dataMachine = new CSCSWMessengerDataMachine(this);
        // start the data machine
        this.dataMachine.start();
    }

    @Override
    public void onDisconnectedFromTargetPeripheral(BluetoothPeripheral peripheral) {
    }

    @Override
    public void onDataReceived(@NotNull BluetoothPeripheral peripheral, byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull BluetoothCommandStatus status) {
        this.dataMachine.onReceiveData(characteristic, value);
    }

    @Override
    public BluetoothCentralManager getBluetoothCentralManager() {
        return this.bluetoothCentralManager;
    }

    @Override
    public void write(byte[] data) {
        this.targetPeripheral.writeCharacteristic(this.targetCharacteristic, data, BluetoothGattCharacteristic.WriteType.WITH_RESPONSE);
    }

    @Override
    public void onDiscoveredTargetService(BluetoothGattService service) {
    }

    @Override
    public void onDiscoveredTargetCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        this.targetCharacteristic = bluetoothGattCharacteristic;
    }
}

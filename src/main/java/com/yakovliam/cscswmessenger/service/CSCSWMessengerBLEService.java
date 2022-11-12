package com.yakovliam.cscswmessenger.service;

import com.welie.blessed.*;
import com.yakovliam.cscswmessenger.CSCSWMessengerBootstrapper;
import com.yakovliam.cscswmessenger.filter.filters.characteristic.BlessedCharacteristicDiscoveredTargetCharacteristicFilter;
import com.yakovliam.cscswmessenger.filter.filters.data.BlessedConnectionDataReceivedTargetCharacteristicFilter;
import com.yakovliam.cscswmessenger.filter.filters.data.BlessedPeripheralConnectionDataReceivedPredicateActionData;
import com.yakovliam.cscswmessenger.filter.filters.peripheral.BlessedPeripheralNameFilter;
import com.yakovliam.cscswmessenger.filter.filters.service.BlessedServiceDiscoveredTargetServiceFilter;
import com.yakovliam.cscswmessenger.provider.BluetoothCentralManagerCallbackProvider;
import com.yakovliam.cscswmessenger.provider.peripheral.BluetoothCentralManagerPeripheralConnectionCallbackProvider;
import com.yakovliam.cscswmessenger.machine.CSCSWMessengerDataMachine;
import com.yakovliam.cscswmessenger.machine.DataMachine;
import com.yakovliam.cscswmessenger.machine.EmptyDataMachine;
import com.yakovliam.cscswmessenger.utils.PeripheralConstants;
import org.jetbrains.annotations.NotNull;

public class CSCSWMessengerBLEService extends BlessedBLEService {

    private static final BlessedPeripheralNameFilter TARGET_PERIPHERAL_FILTER = new BlessedPeripheralNameFilter((name) -> name.endsWith(PeripheralConstants.TARGET_PERIPHERAL_ENDING_DIGITS));

    public static final BlessedConnectionDataReceivedTargetCharacteristicFilter DATA_RECEIVED_TARGET_CHARACTERISTIC_FILTER = new BlessedConnectionDataReceivedTargetCharacteristicFilter((characteristic) -> characteristic.getUuid().equals(PeripheralConstants.TARGET_CHARACTERISTIC_UUID));

    public static final BlessedServiceDiscoveredTargetServiceFilter SERVICE_DISCOVERED_TARGET_SERVICE_FILTER = new BlessedServiceDiscoveredTargetServiceFilter((service -> service.getUuid().equals(PeripheralConstants.TARGET_SERVICE_UUID)));

    public static final BlessedCharacteristicDiscoveredTargetCharacteristicFilter CHARACTERISTIC_DISCOVERED_TARGET_CHARACTERISTIC_FILTER = new BlessedCharacteristicDiscoveredTargetCharacteristicFilter((characteristic -> characteristic.getUuid().equals(PeripheralConstants.TARGET_CHARACTERISTIC_UUID)));

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

    /**
     * Bluetooth central manager peripheral connection callback provider
     */
    private final BluetoothCentralManagerPeripheralConnectionCallbackProvider bluetoothCentralManagerPeripheralConnectionCallbackProvider;

    public CSCSWMessengerBLEService() {
        this.dataMachine = new EmptyDataMachine();

        // create bluetooth central manager callback provider
        BluetoothCentralManagerCallbackProvider bluetoothCentralManagerCallbackProvider = new BluetoothCentralManagerCallbackProvider(this);
        this.bluetoothCentralManagerPeripheralConnectionCallbackProvider = new BluetoothCentralManagerPeripheralConnectionCallbackProvider(this);
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
    public void onDiscoveredPeripheral(@NotNull BluetoothPeripheral peripheral, @NotNull ScanResult scanResult) {
        // filter the peripheral to see if it's the target
        if (!TARGET_PERIPHERAL_FILTER.passes(peripheral)) {
            return;
        }

        CSCSWMessengerBootstrapper.LOGGER.info("Found target peripheral, name: {}. Attempting to connect.", peripheral.getName());

        // stop scanning
        this.stopScanningForPeripherals();

        // initialize a new data machine
        this.dataMachine = new CSCSWMessengerDataMachine(this);

        // the target peripheral has been discovered ...
        // connect to the target peripheral
        this.bluetoothCentralManager.connectPeripheral(peripheral, this.bluetoothCentralManagerPeripheralConnectionCallbackProvider.provide());
    }

    @Override
    public void onConnectedToPeripheral(BluetoothPeripheral peripheral) {
        // set the target peripheral
        this.targetPeripheral = peripheral;
    }

    @Override
    public void onDisconnectedFromPeripheral(BluetoothPeripheral peripheral) {
        this.targetPeripheral = null;
    }

    @Override
    public void onDataReceived(@NotNull BluetoothPeripheral peripheral, byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull BluetoothCommandStatus status) {
        CSCSWMessengerBootstrapper.LOGGER.info("Testing received data characteristic against target characteristic");
        // yes, we're already filtering the characteristic before, but it's safe to do it again when we receive data
        // even though we should only be receiving data from the target characteristic

        // create the predicate action data to test against the filter
        BlessedPeripheralConnectionDataReceivedPredicateActionData actionData = new BlessedPeripheralConnectionDataReceivedPredicateActionData(peripheral, characteristic);
        // filter the data to see if it's from the target characteristic
        if (!DATA_RECEIVED_TARGET_CHARACTERISTIC_FILTER.passes(actionData)) {
            CSCSWMessengerBootstrapper.LOGGER.warn("Data received from non-target characteristic");
            return;
        }

        CSCSWMessengerBootstrapper.LOGGER.info("Processing received data inside the data machine");

        // the data is from the target characteristic, so we can pass it to the data machine
        this.dataMachine.onReceiveData(characteristic, value);
    }

    @Override
    public BluetoothCentralManager bluetoothCentralManager() {
        return this.bluetoothCentralManager;
    }

    @Override
    public BluetoothGattCharacteristic targetCharacteristic() {
        return this.targetCharacteristic;
    }

    @Override
    public boolean write(BluetoothGattCharacteristic characteristic, byte[] data) {
        return this.targetPeripheral.writeCharacteristic(this.targetCharacteristic, data, BluetoothGattCharacteristic.WriteType.WITH_RESPONSE);
    }

    @Override
    public void onDiscoveredService(BluetoothPeripheral peripheral, BluetoothGattService service) {
        // filter to find the target service
        if (!SERVICE_DISCOVERED_TARGET_SERVICE_FILTER.passes(service)) {
            return;
        }

        CSCSWMessengerBootstrapper.LOGGER.info("Found target service of {}, discovering characteristics", PeripheralConstants.TARGET_SERVICE_UUID.toString());

        // the target service has been discovered, so we can loop through the characteristics and find the target characteristic
        // loop through the characteristics and call the callback for each
        service.getCharacteristics().forEach(characteristic -> {
            this.onDiscoveredCharacteristic(peripheral, service, characteristic);
        });
    }

    @Override
    public void onDiscoveredCharacteristic(BluetoothPeripheral peripheral, BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        CSCSWMessengerBootstrapper.LOGGER.info("Found characteristic of {}", characteristic.getUuid());
        // filter to find the target characteristic
        if (!CHARACTERISTIC_DISCOVERED_TARGET_CHARACTERISTIC_FILTER.passes(characteristic)) {
            return;
        }

        CSCSWMessengerBootstrapper.LOGGER.info("Found target characteristic of {}", PeripheralConstants.TARGET_CHARACTERISTIC_UUID.toString());

        // the target characteristic has been discovered, so we can set it
        this.targetCharacteristic = characteristic;
        // enable notifications for the target characteristic
        this.targetPeripheral.setNotify(this.targetCharacteristic, true);

        // the target characteristic has been discovered, so we can start the data machine
        this.dataMachine.start();
    }
}

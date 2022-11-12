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

import java.util.UUID;

public class CSCSWMessengerBLEService extends BlessedBLEService {

    private UUID targetServiceUUID;

    private UUID targetCharWriteUUID;

    private UUID targetCharNotifyUUID;

    public static final BlessedPeripheralNameFilter TARGET_PERIPHERAL_FILTER = new BlessedPeripheralNameFilter((name) -> name.endsWith(PeripheralConstants.TARGET_PERIPHERAL_ENDING_DIGITS));

    public static final BlessedPeripheralNameFilter TARGET_PERIPHERAL_TYPE_2_FILTER = new BlessedPeripheralNameFilter((name) -> name.startsWith(PeripheralConstants.TARGET_PERIPHERAL_TYPE_2_STARTING_DIGITS));

    private BlessedConnectionDataReceivedTargetCharacteristicFilter connectionDataReceivedTargetCharacteristicFilter;

    private BlessedServiceDiscoveredTargetServiceFilter serviceDiscoveredTargetServiceFilter;

    private BlessedCharacteristicDiscoveredTargetCharacteristicFilter characteristicDiscoveredTargetCharacteristicFilter;

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

        // determine the machine type based on the peripheral name
        if (TARGET_PERIPHERAL_TYPE_2_FILTER.passes(peripheral)) {
            CSCSWMessengerBootstrapper.LOGGER.info("Target peripheral is type 2, using type 2 machine");
            this.targetCharWriteUUID = PeripheralConstants.TYPE_2_CHAR_WRITE_UUID;
            this.targetCharNotifyUUID = PeripheralConstants.TYPE_2_CHAR_NOTIFY_UUID;
            this.targetServiceUUID = PeripheralConstants.TYPE_2_SERVICE_UUID;
        } else {
            CSCSWMessengerBootstrapper.LOGGER.info("Target peripheral is type 1 (ME51), using type 1 machine");
            this.targetCharWriteUUID = PeripheralConstants.ME51_CHAR_WRITE_UUID;
            this.targetCharNotifyUUID = PeripheralConstants.ME51_CHAR_NOTIFY_UUID;
            this.targetServiceUUID = PeripheralConstants.ME51_SERVICE_UUID;
        }

        // set the filters to use the target service and characteristic (+ notify) uuids
        this.connectionDataReceivedTargetCharacteristicFilter = new BlessedConnectionDataReceivedTargetCharacteristicFilter((characteristic) -> characteristic.getUuid().equals(this.targetCharNotifyUUID));
        this.serviceDiscoveredTargetServiceFilter = new BlessedServiceDiscoveredTargetServiceFilter((service -> service.getUuid().equals(this.targetServiceUUID)));
        this.characteristicDiscoveredTargetCharacteristicFilter = new BlessedCharacteristicDiscoveredTargetCharacteristicFilter((characteristic -> characteristic.getUuid().equals(this.targetCharWriteUUID)));

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
        if (!connectionDataReceivedTargetCharacteristicFilter.passes(actionData)) {
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
        if (!serviceDiscoveredTargetServiceFilter.passes(service)) {
            return;
        }

        CSCSWMessengerBootstrapper.LOGGER.info("Found target service of {}, discovering characteristics", this.targetServiceUUID.toString());

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
        if (!characteristicDiscoveredTargetCharacteristicFilter.passes(characteristic)) {
            return;
        }

        CSCSWMessengerBootstrapper.LOGGER.info("Found target characteristic of {}", this.targetCharWriteUUID.toString());

        // the target characteristic has been discovered, so we can set it
        this.targetCharacteristic = characteristic;
        // enable notifications for the target characteristic
        this.targetPeripheral.setNotify(this.targetServiceUUID, this.targetCharNotifyUUID, true);

        // the target characteristic has been discovered, so we can start the data machine
        this.dataMachine.start();
    }
}

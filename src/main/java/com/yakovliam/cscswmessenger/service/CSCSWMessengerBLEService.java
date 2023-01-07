package com.yakovliam.cscswmessenger.service;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCommandStatus;
import com.welie.blessed.BluetoothGattCharacteristic;
import com.welie.blessed.BluetoothGattService;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.ScanResult;
import com.yakovliam.cscswmessenger.CSCSWMessengerBootstrapper;
import com.yakovliam.cscswmessenger.filter.filters.characteristic.BlessedCharacteristicDiscoveredTargetCharacteristicFilter;
import com.yakovliam.cscswmessenger.filter.filters.data.BlessedConnectionDataReceivedTargetCharacteristicFilter;
import com.yakovliam.cscswmessenger.filter.filters.data.BlessedPeripheralConnectionDataReceivedPredicateActionData;
import com.yakovliam.cscswmessenger.filter.filters.peripheral.BlessedPeripheralNameFilter;
import com.yakovliam.cscswmessenger.filter.filters.service.BlessedServiceDiscoveredTargetServiceFilter;
import com.yakovliam.cscswmessenger.machine.CSCSWMessengerDataMachine;
import com.yakovliam.cscswmessenger.machine.DataMachine;
import com.yakovliam.cscswmessenger.provider.BluetoothCentralManagerCallbackProvider;
import com.yakovliam.cscswmessenger.provider.peripheral.BluetoothCentralManagerPeripheralConnectionCallbackProvider;
import com.yakovliam.cscswmessenger.service.model.MappedPeripheralWrapper;
import com.yakovliam.cscswmessenger.utils.PeripheralConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class CSCSWMessengerBLEService extends BlessedBLEService {

  public static final List<String> TARGET_PERIPHERAL_ENDING_DIGITS =
      List.of("001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011", "012",
          "013", "014", "015", "016", "017", "018", "019", "020", "021", "022", "023", "024");

  public static final BlessedPeripheralNameFilter TARGET_PERIPHERAL_FILTER =
      new BlessedPeripheralNameFilter(
          (name) -> TARGET_PERIPHERAL_ENDING_DIGITS.stream().anyMatch(name::endsWith));

  public static final BlessedPeripheralNameFilter TARGET_PERIPHERAL_TYPE_2_FILTER =
      new BlessedPeripheralNameFilter(
          (name) -> name.startsWith(PeripheralConstants.TARGET_PERIPHERAL_TYPE_2_STARTING_DIGITS));

  /**
   * The mapped peripheral wrappers
   */
  private final List<MappedPeripheralWrapper> mappedPeripheralWrappers = new ArrayList<>();

  /**
   * Blessed Bluetooth central manager
   */
  private final BluetoothCentralManager bluetoothCentralManager;

  /**
   * Bluetooth central manager peripheral connection callback provider
   */
  private final BluetoothCentralManagerPeripheralConnectionCallbackProvider
      bluetoothCentralManagerPeripheralConnectionCallbackProvider;

  public CSCSWMessengerBLEService() {
    // create bluetooth central manager callback provider
    BluetoothCentralManagerCallbackProvider bluetoothCentralManagerCallbackProvider =
        new BluetoothCentralManagerCallbackProvider(this);
    this.bluetoothCentralManagerPeripheralConnectionCallbackProvider =
        new BluetoothCentralManagerPeripheralConnectionCallbackProvider(this);
    // initialize the bluetooth central manager with the callback provider
    this.bluetoothCentralManager =
        new BluetoothCentralManager(bluetoothCentralManagerCallbackProvider.provide());
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
  public void onDiscoveredPeripheral(@NotNull BluetoothPeripheral peripheral,
                                     @NotNull ScanResult scanResult) {
    // filter the peripheral to see if it's the target
    if (!TARGET_PERIPHERAL_FILTER.passes(peripheral)) {
      return;
    }

    // if the peripheral is already connected, skip
    if (this.mappedPeripheralWrappers.stream().anyMatch(
        mappedPeripheralWrapper -> mappedPeripheralWrapper.getPeripheral().equals(peripheral))) {
      return;
    }

    CSCSWMessengerBootstrapper.LOGGER.info(
        "Found target peripheral, name: {}. Attempting to connect.", peripheral.getName());

    UUID targetCharWriteUUID;
    UUID targetCharNotifyUUID;
    UUID targetServiceUUID;

    // determine the machine type based on the peripheral name
    if (TARGET_PERIPHERAL_TYPE_2_FILTER.passes(peripheral)) {
      CSCSWMessengerBootstrapper.LOGGER.info("Target peripheral is type 2, using type 2 machine");
      targetCharWriteUUID = PeripheralConstants.TYPE_2_CHAR_WRITE_UUID;
      targetCharNotifyUUID = PeripheralConstants.TYPE_2_CHAR_NOTIFY_UUID;
      targetServiceUUID = PeripheralConstants.TYPE_2_SERVICE_UUID;
    } else {
      CSCSWMessengerBootstrapper.LOGGER.info(
          "Target peripheral is type 1 (ME51), using type 1 machine");
      targetCharWriteUUID = PeripheralConstants.ME51_CHAR_WRITE_UUID;
      targetCharNotifyUUID = PeripheralConstants.ME51_CHAR_NOTIFY_UUID;
      targetServiceUUID = PeripheralConstants.ME51_SERVICE_UUID;
    }

    // create a new mapped peripheral wrapper object
    MappedPeripheralWrapper mappedPeripheral =
        new MappedPeripheralWrapper(peripheral, targetServiceUUID, targetCharWriteUUID,
            targetCharNotifyUUID);

    // set the new data machine
    mappedPeripheral.setDataMachine(new CSCSWMessengerDataMachine(this, mappedPeripheral));

    // set the filters to use the target service and characteristic (+ notify) uuids
    BlessedConnectionDataReceivedTargetCharacteristicFilter
        connectionDataReceivedTargetCharacteristicFilter =
        new BlessedConnectionDataReceivedTargetCharacteristicFilter(
            (characteristic) -> characteristic.getUuid().equals(targetCharNotifyUUID));
    BlessedServiceDiscoveredTargetServiceFilter serviceDiscoveredTargetServiceFilter =
        new BlessedServiceDiscoveredTargetServiceFilter(
            (service -> service.getUuid().equals(targetServiceUUID)));
    BlessedCharacteristicDiscoveredTargetCharacteristicFilter
        characteristicDiscoveredTargetCharacteristicFilter =
        new BlessedCharacteristicDiscoveredTargetCharacteristicFilter(
            (characteristic -> characteristic.getUuid().equals(targetCharWriteUUID)));

    mappedPeripheral.setConnectionDataReceivedTargetCharacteristicFilter(
        connectionDataReceivedTargetCharacteristicFilter);
    mappedPeripheral.setServiceDiscoveredTargetServiceFilter(serviceDiscoveredTargetServiceFilter);
    mappedPeripheral.setCharacteristicDiscoveredTargetCharacteristicFilter(
        characteristicDiscoveredTargetCharacteristicFilter);

    // add the mapped peripheral to the list
    this.mappedPeripheralWrappers.add(mappedPeripheral);

    // the target peripheral has been discovered ...
    // connect to the target peripheral
    this.bluetoothCentralManager.connectPeripheral(peripheral,
        this.bluetoothCentralManagerPeripheralConnectionCallbackProvider.provide());
  }

  @Override
  public void onConnectedToPeripheral(BluetoothPeripheral peripheral) {
    // no-op
  }

  @Override
  public void onDisconnectedFromPeripheral(BluetoothPeripheral peripheral) {
    // remove the mapped peripheral
    this.mappedPeripheralWrappers.removeIf(
        mappedPeripheralWrapper -> mappedPeripheralWrapper.getPeripheral().equals(peripheral));
  }

  @Override
  public void onDataReceived(@NotNull BluetoothPeripheral peripheral, byte[] value,
                             @NotNull BluetoothGattCharacteristic characteristic,
                             @NotNull BluetoothCommandStatus status) {
    CSCSWMessengerBootstrapper.LOGGER.info(
        "Testing received data characteristic against target characteristic");
    // yes, we're already filtering the characteristic before, but it's safe to do it again when we receive data
    // even though we should only be receiving data from the target characteristic

    // create the predicate action data to test against the filter
    BlessedPeripheralConnectionDataReceivedPredicateActionData actionData =
        new BlessedPeripheralConnectionDataReceivedPredicateActionData(peripheral, characteristic);

    // get the mapped peripheral wrapper
    MappedPeripheralWrapper mappedPeripheralWrapper = this.mappedPeripheralWrappers.stream()
        .filter(mappedPeripheral -> mappedPeripheral.getPeripheral().equals(peripheral)).findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Received data from peripheral that is not mapped to a peripheral wrapper"));

    BlessedConnectionDataReceivedTargetCharacteristicFilter filter =
        mappedPeripheralWrapper.getConnectionDataReceivedTargetCharacteristicFilter().orElseThrow(
            () -> new IllegalStateException(
                "Received data from peripheral that is not mapped to a peripheral wrapper"));

    // filter the data to see if it's from the target characteristic
    if (!filter.passes(actionData)) {
      CSCSWMessengerBootstrapper.LOGGER.warn("Data received from non-target characteristic");
      return;
    }

    CSCSWMessengerBootstrapper.LOGGER.info("Processing received data inside the data machine");

    // get the data machine
    DataMachine dataMachine = mappedPeripheralWrapper.getDataMachine().orElseThrow(
        () -> new IllegalStateException(
            "Received data from peripheral that is not mapped to a peripheral wrapper"));

    // the data is from the target characteristic, so we can pass it to the data machine
    dataMachine.onReceiveData(characteristic, value);
  }

  @Override
  public BluetoothCentralManager bluetoothCentralManager() {
    return this.bluetoothCentralManager;
  }


  @Override
  public boolean write(MappedPeripheralWrapper mappedPeripheralWrapper, byte[] data) {
    BluetoothGattCharacteristic characteristic =
        mappedPeripheralWrapper.getTargetWriteCharacteristic()
            .orElseThrow(() -> new IllegalStateException("Target characteristic is not set"));

    return mappedPeripheralWrapper.getPeripheral().writeCharacteristic(characteristic, data,
        BluetoothGattCharacteristic.WriteType.WITH_RESPONSE);
  }

  @Override
  public void onDiscoveredService(BluetoothPeripheral peripheral, BluetoothGattService service) {
    // get the mapped peripheral wrapper
    MappedPeripheralWrapper mappedPeripheralWrapper = this.mappedPeripheralWrappers.stream()
        .filter(mappedPeripheral -> mappedPeripheral.getPeripheral().equals(peripheral)).findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Received data from peripheral that is not mapped to a peripheral wrapper"));

    // get the service filter
    BlessedServiceDiscoveredTargetServiceFilter filter =
        mappedPeripheralWrapper.getServiceDiscoveredTargetServiceFilter().orElseThrow(
            () -> new IllegalStateException(
                "Received data from peripheral that is not mapped to a peripheral wrapper"));

    // filter to find the target service
    if (!filter.passes(service)) {
      return;
    }

    CSCSWMessengerBootstrapper.LOGGER.info(
        "Found target service of {}, discovering characteristics",
        mappedPeripheralWrapper.getTargetServiceUUID().toString());

    // the target service has been discovered, so we can loop through the characteristics and find the target characteristic
    // loop through the characteristics and call the callback for each
    service.getCharacteristics().forEach(characteristic -> {
      this.onDiscoveredCharacteristic(peripheral, service, characteristic);
    });
  }

  @Override
  public void onDiscoveredCharacteristic(BluetoothPeripheral peripheral,
                                         BluetoothGattService service,
                                         BluetoothGattCharacteristic characteristic) {
    CSCSWMessengerBootstrapper.LOGGER.info("Found characteristic of {}", characteristic.getUuid());

    // get the mapped peripheral wrapper
    MappedPeripheralWrapper mappedPeripheralWrapper = this.mappedPeripheralWrappers.stream()
        .filter(mappedPeripheral -> mappedPeripheral.getPeripheral().equals(peripheral)).findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Received data from peripheral that is not mapped to a peripheral wrapper"));

    // get the characteristic filter
    BlessedCharacteristicDiscoveredTargetCharacteristicFilter filter =
        mappedPeripheralWrapper.getCharacteristicDiscoveredTargetCharacteristicFilter().orElseThrow(
            () -> new IllegalStateException(
                "Received data from peripheral that is not mapped to a peripheral wrapper"));

    // filter to find the target characteristic
    if (!filter.passes(characteristic)) {
      return;
    }

    CSCSWMessengerBootstrapper.LOGGER.info("Found target characteristic of {}",
        mappedPeripheralWrapper.getTargetCharWriteUUID().toString());

    // enable notifications for the target characteristic
    mappedPeripheralWrapper.getPeripheral()
        .setNotify(mappedPeripheralWrapper.getTargetServiceUUID(),
            mappedPeripheralWrapper.getTargetCharNotifyUUID(), true);

    // set the target characteristic
    mappedPeripheralWrapper.setTargetWriteCharacteristic(characteristic);

    // get the data machine
    DataMachine dataMachine = mappedPeripheralWrapper.getDataMachine().orElseThrow(
        () -> new IllegalStateException(
            "Received data from peripheral that is not mapped to a peripheral wrapper"));

    CSCSWMessengerBootstrapper.LOGGER.info("\n\n\n\nStarting data machine\n\n\n\n");

    // the target characteristic has been discovered, so we can start the data machine
    dataMachine.start();
  }
}

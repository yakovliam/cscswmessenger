package com.yakovliam.cscswmessenger.service.model;

import com.welie.blessed.BluetoothGattCharacteristic;
import com.welie.blessed.BluetoothPeripheral;
import com.yakovliam.cscswmessenger.filter.filters.characteristic.BlessedCharacteristicDiscoveredTargetCharacteristicFilter;
import com.yakovliam.cscswmessenger.filter.filters.data.BlessedConnectionDataReceivedTargetCharacteristicFilter;
import com.yakovliam.cscswmessenger.filter.filters.service.BlessedServiceDiscoveredTargetServiceFilter;
import com.yakovliam.cscswmessenger.machine.DataMachine;
import java.util.Optional;
import java.util.UUID;

public class MappedPeripheralWrapper {

  /**
   * The peripheral
   */
  private final BluetoothPeripheral peripheral;

  /**
   * The service uuid
   */
  private final UUID targetServiceUUID;

  /**
   * The characteristic uuid
   */
  private final UUID targetCharWriteUUID;

  /**
   * The characteristic uuid
   */
  private final UUID targetCharNotifyUUID;

  /**
   * The data machine
   */
  private DataMachine dataMachine;

  /**
   * Target write characteristic
   */
  private BluetoothGattCharacteristic targetWriteCharacteristic;

  private BlessedConnectionDataReceivedTargetCharacteristicFilter
      connectionDataReceivedTargetCharacteristicFilter;

  private BlessedServiceDiscoveredTargetServiceFilter serviceDiscoveredTargetServiceFilter;

  private BlessedCharacteristicDiscoveredTargetCharacteristicFilter
      characteristicDiscoveredTargetCharacteristicFilter;

  /**
   * MappedPeripheralWrapper constructor
   *
   * @param peripheral                the peripheral
   * @param targetServiceUUID         the target service uuid
   * @param targetCharWriteUUID       the target char write uuid
   * @param targetCharNotifyUUID      the target char notify uuid
   * @param dataMachine               the data machine
   * @param targetWriteCharacteristic the target write characteristic
   */
  public MappedPeripheralWrapper(BluetoothPeripheral peripheral, UUID targetServiceUUID,
                                 UUID targetCharWriteUUID, UUID targetCharNotifyUUID,
                                 DataMachine dataMachine,
                                 BluetoothGattCharacteristic targetWriteCharacteristic) {
    this.peripheral = peripheral;
    this.targetServiceUUID = targetServiceUUID;
    this.targetCharWriteUUID = targetCharWriteUUID;
    this.targetCharNotifyUUID = targetCharNotifyUUID;
    this.dataMachine = dataMachine;
    this.targetWriteCharacteristic = targetWriteCharacteristic;
  }

  /**
   * MappedPeripheralWrapper constructor
   *
   * @param peripheral           the peripheral
   * @param targetServiceUUID    the target service uuid
   * @param targetCharWriteUUID  the target char write uuid
   * @param targetCharNotifyUUID the target char notify uuid
   */
  public MappedPeripheralWrapper(BluetoothPeripheral peripheral, UUID targetServiceUUID,
                                 UUID targetCharWriteUUID, UUID targetCharNotifyUUID) {
    this.peripheral = peripheral;
    this.targetServiceUUID = targetServiceUUID;
    this.targetCharWriteUUID = targetCharWriteUUID;
    this.targetCharNotifyUUID = targetCharNotifyUUID;
    this.dataMachine = null;
    this.targetWriteCharacteristic = null;
    this.connectionDataReceivedTargetCharacteristicFilter = null;
    this.serviceDiscoveredTargetServiceFilter = null;
    this.characteristicDiscoveredTargetCharacteristicFilter = null;
  }


  /**
   * Gets the peripheral
   *
   * @return the peripheral
   */
  public BluetoothPeripheral getPeripheral() {
    return peripheral;
  }

  /**
   * Gets the target service uuid
   *
   * @return the target service uuid
   */
  public UUID getTargetServiceUUID() {
    return targetServiceUUID;
  }

  /**
   * Gets the target char write uuid
   *
   * @return the target char write uuid
   */
  public UUID getTargetCharWriteUUID() {
    return targetCharWriteUUID;
  }

  /**
   * Gets the target char notify uuid
   *
   * @return the target char notify uuid
   */
  public UUID getTargetCharNotifyUUID() {
    return targetCharNotifyUUID;
  }

  /**
   * Gets the data machine
   *
   * @return the data machine
   */
  public Optional<DataMachine> getDataMachine() {
    return Optional.ofNullable(dataMachine);
  }

  /**
   * Sets the data machine
   *
   * @param dataMachine the data machine
   */
  public void setDataMachine(DataMachine dataMachine) {
    this.dataMachine = dataMachine;
  }

  /**
   * Gets the target notify characteristic
   *
   * @return the target notify characteristic
   */
  public Optional<BluetoothGattCharacteristic> getTargetWriteCharacteristic() {
    return Optional.ofNullable(targetWriteCharacteristic);
  }

  /**
   * Sets the target notify characteristic
   *
   * @param targetWriteCharacteristic the target notify characteristic
   */
  public void setTargetWriteCharacteristic(BluetoothGattCharacteristic targetWriteCharacteristic) {
    this.targetWriteCharacteristic = targetWriteCharacteristic;
  }

  /**
   * Gets the connection data received target characteristic filter
   *
   * @return the connection data received target characteristic filter
   */
  public Optional<BlessedConnectionDataReceivedTargetCharacteristicFilter> getConnectionDataReceivedTargetCharacteristicFilter() {
    return Optional.ofNullable(connectionDataReceivedTargetCharacteristicFilter);
  }

  /**
   * Sets the connection data received target characteristic filter
   *
   * @param connectionDataReceivedTargetCharacteristicFilter the connection data received target characteristic filter
   */
  public void setConnectionDataReceivedTargetCharacteristicFilter(
      BlessedConnectionDataReceivedTargetCharacteristicFilter connectionDataReceivedTargetCharacteristicFilter) {
    this.connectionDataReceivedTargetCharacteristicFilter =
        connectionDataReceivedTargetCharacteristicFilter;
  }

  /**
   * Gets the service discovered target service filter
   *
   * @return the service discovered target service filter
   */
  public Optional<BlessedServiceDiscoveredTargetServiceFilter> getServiceDiscoveredTargetServiceFilter() {
    return Optional.ofNullable(serviceDiscoveredTargetServiceFilter);
  }

  /**
   * Sets the service discovered target service filter
   *
   * @param serviceDiscoveredTargetServiceFilter the service discovered target service filter
   */
  public void setServiceDiscoveredTargetServiceFilter(
      BlessedServiceDiscoveredTargetServiceFilter serviceDiscoveredTargetServiceFilter) {
    this.serviceDiscoveredTargetServiceFilter = serviceDiscoveredTargetServiceFilter;
  }

  /**
   * Gets the characteristic discovered target characteristic filter
   *
   * @return the characteristic discovered target characteristic filter
   */
  public Optional<BlessedCharacteristicDiscoveredTargetCharacteristicFilter> getCharacteristicDiscoveredTargetCharacteristicFilter() {
    return Optional.ofNullable(characteristicDiscoveredTargetCharacteristicFilter);
  }

  /**
   * Sets the characteristic discovered target characteristic filter
   *
   * @param characteristicDiscoveredTargetCharacteristicFilter the characteristic discovered target characteristic filter
   */
  public void setCharacteristicDiscoveredTargetCharacteristicFilter(
      BlessedCharacteristicDiscoveredTargetCharacteristicFilter characteristicDiscoveredTargetCharacteristicFilter) {
    this.characteristicDiscoveredTargetCharacteristicFilter =
        characteristicDiscoveredTargetCharacteristicFilter;
  }
}

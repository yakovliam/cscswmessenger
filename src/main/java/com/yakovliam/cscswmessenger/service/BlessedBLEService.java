package com.yakovliam.cscswmessenger.service;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCommandStatus;
import com.welie.blessed.BluetoothGattCharacteristic;
import com.welie.blessed.BluetoothGattService;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.ScanResult;
import com.yakovliam.cscswmessenger.service.model.MappedPeripheralWrapper;
import org.jetbrains.annotations.NotNull;

public abstract class BlessedBLEService implements Service {

  /**
   * When a peripheral is discovered
   *
   * @param peripheral peripheral
   * @param scanResult scan result
   */
  public abstract void onDiscoveredPeripheral(@NotNull BluetoothPeripheral peripheral,
                                              @NotNull ScanResult scanResult);

  /**
   * When a peripheral is connected
   *
   * @param peripheral peripheral
   */
  public abstract void onConnectedToPeripheral(BluetoothPeripheral peripheral);

  /**
   * When a peripheral is disconnected
   *
   * @param peripheral peripheral
   */
  public abstract void onDisconnectedFromPeripheral(BluetoothPeripheral peripheral);

  /**
   * When new data is received
   *
   * @param peripheral     peripheral
   * @param value          value
   * @param characteristic characteristic that was updated / data was received from
   * @param status         status
   */
  public abstract void onDataReceived(@NotNull BluetoothPeripheral peripheral, byte[] value,
                                      @NotNull BluetoothGattCharacteristic characteristic,
                                      @NotNull BluetoothCommandStatus status);

  /**
   * When a service is discovered
   *
   * @param peripheral peripheral
   * @param service    service
   */
  public abstract void onDiscoveredService(BluetoothPeripheral peripheral,
                                           BluetoothGattService service);

  /**
   * When a characteristic is discovered
   *
   * @param peripheral     peripheral
   * @param service        service
   * @param characteristic characteristic
   */
  public abstract void onDiscoveredCharacteristic(BluetoothPeripheral peripheral,
                                                  BluetoothGattService service,
                                                  BluetoothGattCharacteristic characteristic);

  /**
   * Returns the BCM
   *
   * @return BCM
   */
  public abstract BluetoothCentralManager bluetoothCentralManager();

  /**
   * Writes data to a target characteristic
   *
   * @param mappedPeripheralWrapper the mapped peripheral wrapper
   * @param data                    data
   * @return true if successful
   */
  public abstract boolean write(MappedPeripheralWrapper mappedPeripheralWrapper, byte[] data);
}

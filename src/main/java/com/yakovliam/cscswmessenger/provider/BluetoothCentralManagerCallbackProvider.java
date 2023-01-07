package com.yakovliam.cscswmessenger.provider;

import com.welie.blessed.BluetoothCentralManagerCallback;
import com.yakovliam.cscswmessenger.service.BlessedBLEService;

public class BluetoothCentralManagerCallbackProvider
    implements Provider<BluetoothCentralManagerCallback> {

  /**
   * Bluetooth central manager callback
   */
  private final BluetoothCentralManagerCallback bluetoothCentralManagerCallback;

  /**
   * Bluetooth central manager callback provider constructor
   *
   * @param blessedBLEService the blessed ble service
   */
  public BluetoothCentralManagerCallbackProvider(BlessedBLEService blessedBLEService) {
    this.bluetoothCentralManagerCallback = new BCMCallbackImplementation(blessedBLEService);
  }

  @Override
  public BluetoothCentralManagerCallback provide() {
    return this.bluetoothCentralManagerCallback;
  }
}

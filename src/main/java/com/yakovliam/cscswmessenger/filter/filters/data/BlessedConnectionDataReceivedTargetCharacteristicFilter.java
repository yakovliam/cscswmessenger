package com.yakovliam.cscswmessenger.filter.filters.data;

import com.welie.blessed.BluetoothGattCharacteristic;
import java.util.function.Predicate;

public class BlessedConnectionDataReceivedTargetCharacteristicFilter
    extends BlessedConnectionDataReceivedFilter {

  /**
   * Peripheral filter constructor
   *
   * @param filterAction filter action
   */
  public BlessedConnectionDataReceivedTargetCharacteristicFilter(
      Predicate<BluetoothGattCharacteristic> filterAction) {
    super((data) -> filterAction.test(data.bluetoothGattCharacteristic()));
  }
}

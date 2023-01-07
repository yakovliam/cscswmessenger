package com.yakovliam.cscswmessenger.filter.filters.peripheral;

import com.welie.blessed.BluetoothPeripheral;
import com.yakovliam.cscswmessenger.filter.BlessedObjectFilter;
import java.util.function.Predicate;

public abstract class BlessedPeripheralFilter extends BlessedObjectFilter<BluetoothPeripheral> {

  /**
   * Peripheral filter constructor
   *
   * @param filterAction filter action
   */
  protected BlessedPeripheralFilter(Predicate<BluetoothPeripheral> filterAction) {
    super(filterAction);
  }
}

package com.yakovliam.cscswmessenger.filter.filters.service;

import com.welie.blessed.BluetoothGattService;
import com.yakovliam.cscswmessenger.filter.BlessedObjectFilter;
import java.util.function.Predicate;

public abstract class BlessedServiceDiscoveredFilter
    extends BlessedObjectFilter<BluetoothGattService> {

  /**
   * Service filter constructor
   *
   * @param filterAction filter action
   */
  protected BlessedServiceDiscoveredFilter(Predicate<BluetoothGattService> filterAction) {
    super(filterAction);
  }
}

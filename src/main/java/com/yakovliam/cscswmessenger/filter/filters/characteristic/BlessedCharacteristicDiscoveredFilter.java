package com.yakovliam.cscswmessenger.filter.filters.characteristic;

import com.welie.blessed.BluetoothGattCharacteristic;
import com.welie.blessed.BluetoothGattService;
import com.yakovliam.cscswmessenger.filter.BlessedObjectFilter;

import java.util.function.Predicate;

public abstract class BlessedCharacteristicDiscoveredFilter extends BlessedObjectFilter<BluetoothGattCharacteristic> {

    /**
     * Characteristic filter constructor
     *
     * @param filterAction filter action
     */
    protected BlessedCharacteristicDiscoveredFilter(Predicate<BluetoothGattCharacteristic> filterAction) {
        super(filterAction);
    }
}

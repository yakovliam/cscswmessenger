package com.yakovliam.cscswmessenger.filter.filters.characteristic;

import com.welie.blessed.BluetoothGattCharacteristic;
import com.welie.blessed.BluetoothGattService;

import java.util.function.Predicate;

public class BlessedCharacteristicDiscoveredTargetCharacteristicFilter extends BlessedCharacteristicDiscoveredFilter {

    /**
     * Characteristic filter constructor
     *
     * @param filterAction filter action
     */
    public BlessedCharacteristicDiscoveredTargetCharacteristicFilter(Predicate<BluetoothGattCharacteristic> filterAction) {
        super(filterAction);
    }
}

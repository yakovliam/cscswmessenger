package com.yakovliam.cscswmessenger.filter.filters.service;

import com.welie.blessed.BluetoothGattService;

import java.util.function.Predicate;

public class BlessedServiceDiscoveredTargetServiceFilter extends BlessedServiceDiscoveredFilter {

    /**
     * Service filter constructor
     *
     * @param filterAction filter action
     */
    public BlessedServiceDiscoveredTargetServiceFilter(Predicate<BluetoothGattService> filterAction) {
        super(filterAction);
    }
}

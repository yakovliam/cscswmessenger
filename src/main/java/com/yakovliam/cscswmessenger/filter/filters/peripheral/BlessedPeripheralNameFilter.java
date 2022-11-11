package com.yakovliam.cscswmessenger.filter.filters.peripheral;

import java.util.function.Predicate;

public class BlessedPeripheralNameFilter extends BlessedPeripheralFilter {

    /**
     * Peripheral filter constructor
     *
     * @param filterAction filter action
     */
    public BlessedPeripheralNameFilter(Predicate<String> filterAction) {
        super((peripheral) -> filterAction.test(peripheral.getName()));
    }
}

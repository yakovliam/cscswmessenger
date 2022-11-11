package com.yakovliam.cscswmessenger.provider.context;

import com.welie.blessed.BluetoothPeripheral;
import com.yakovliam.cscswmessenger.filter.filters.peripheral.BlessedPeripheralFilter;
import com.yakovliam.cscswmessenger.filter.filters.peripheral.BlessedPeripheralNameFilter;

import java.util.List;

public final class BLEPeripheralTargetProvisionContext implements ProvisionContext {

    /**
     * Peripheral filter list
     */
    private final List<BlessedPeripheralFilter> peripheralFilterList;

    /**
     * BLE peripheral target provision context
     */
    public BLEPeripheralTargetProvisionContext(String targetPeripheralNameEndingDigits) {
        this.peripheralFilterList = List.of(new BlessedPeripheralNameFilter((name) -> name.endsWith(targetPeripheralNameEndingDigits)));
    }

    /**
     * If the peripheral is the target peripheral
     *
     * @param peripheral peripheral
     * @return if the peripheral passes
     */
    public boolean peripheralPassesTargetTest(BluetoothPeripheral peripheral) {
        return peripheralFilterList.stream().allMatch(filter -> filter.passes(peripheral));
    }
}
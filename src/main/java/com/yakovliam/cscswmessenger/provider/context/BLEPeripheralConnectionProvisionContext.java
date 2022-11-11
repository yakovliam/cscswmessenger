package com.yakovliam.cscswmessenger.provider.context;

import com.welie.blessed.BluetoothGattCharacteristic;
import com.welie.blessed.BluetoothGattService;
import com.welie.blessed.BluetoothPeripheral;
import com.yakovliam.cscswmessenger.filter.filters.data.BlessedConnectionDataReceivedFilter;
import com.yakovliam.cscswmessenger.filter.filters.data.BlessedConnectionDataReceivedTargetCharacteristicFilter;
import com.yakovliam.cscswmessenger.filter.filters.data.BlessedPeripheralConnectionDataReceivedPredicateActionData;
import com.yakovliam.cscswmessenger.filter.filters.service.BlessedServiceDiscoveredFilter;
import com.yakovliam.cscswmessenger.filter.filters.service.BlessedServiceDiscoveredTargetServiceFilter;
import com.yakovliam.cscswmessenger.utils.PeripheralConstants;

import java.util.List;
import java.util.UUID;

public class BLEPeripheralConnectionProvisionContext implements ProvisionContext {

    /**
     * Peripheral filter list
     */
    private final List<BlessedConnectionDataReceivedFilter> peripheralFilterList;

    /**
     * Services discovered filter list
     */
    private final List<BlessedServiceDiscoveredFilter> serviceDiscoveredFilterList;

    /**
     * BLE peripheral connection target provision context
     *
     * @param targetCharacteristicUUID target characteristic uuid
     */
    public BLEPeripheralConnectionProvisionContext(UUID targetCharacteristicUUID) {
        this.peripheralFilterList = List.of(new BlessedConnectionDataReceivedTargetCharacteristicFilter((characteristic) -> characteristic.getUuid().equals(targetCharacteristicUUID)));
        this.serviceDiscoveredFilterList = List.of(new BlessedServiceDiscoveredTargetServiceFilter((service -> service.getUuid().equals(PeripheralConstants.TARGET_SERVICE_UUID))));
    }

    /**
     * If the peripheral is the target peripheral
     *
     * @param peripheral                  peripheral
     * @param bluetoothGattCharacteristic characteristic
     * @return if the data passes
     */
    public boolean connectionDataPassesTargetTest(BluetoothPeripheral peripheral, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        BlessedPeripheralConnectionDataReceivedPredicateActionData actionData = new BlessedPeripheralConnectionDataReceivedPredicateActionData(peripheral, bluetoothGattCharacteristic);
        return peripheralFilterList.stream().allMatch(filter -> filter.passes(actionData));
    }

    /**
     * If the service discovered is the target one
     *
     * @param bluetoothGattService bluetooth gatt service
     * @return if the service passes
     */
    public boolean serviceDiscoveredPassesTargetTest(BluetoothGattService bluetoothGattService) {
        return serviceDiscoveredFilterList.stream().allMatch(filter -> filter.passes(bluetoothGattService));
    }
}
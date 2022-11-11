package com.yakovliam.cscswmessenger.provider.peripheral;

import com.yakovliam.cscswmessenger.provider.Provider;
import com.yakovliam.cscswmessenger.provider.context.BLEPeripheralConnectionProvisionContext;
import com.yakovliam.cscswmessenger.service.BlessedBLEService;

public class BluetoothCentralManagerPeripheralConnectionCallbackProvider implements Provider<BCMPeripheralConnectionCallbackImplementation> {

    /**
     * Bluetooth central manager peripheral connected callback
     */
    private final BCMPeripheralConnectionCallbackImplementation bcmPeripheralConnectionCallbackImplementation;

    /**
     * Bluetooth central manager callback provider constructor
     *
     * @param context           the context required for providing the provided object
     * @param blessedBLEService the blessed ble service
     */
    public BluetoothCentralManagerPeripheralConnectionCallbackProvider(BLEPeripheralConnectionProvisionContext context, BlessedBLEService blessedBLEService) {
        this.bcmPeripheralConnectionCallbackImplementation = new BCMPeripheralConnectionCallbackImplementation(context, blessedBLEService);
    }

    @Override
    public BCMPeripheralConnectionCallbackImplementation provide() {
        return this.bcmPeripheralConnectionCallbackImplementation;
    }
}

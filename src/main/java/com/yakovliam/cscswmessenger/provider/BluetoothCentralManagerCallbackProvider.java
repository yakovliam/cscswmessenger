package com.yakovliam.cscswmessenger.provider;

import com.welie.blessed.BluetoothCentralManagerCallback;
import com.yakovliam.cscswmessenger.provider.context.BLEPeripheralTargetProvisionContext;
import com.yakovliam.cscswmessenger.service.BlessedBLEService;

public class BluetoothCentralManagerCallbackProvider implements Provider<BluetoothCentralManagerCallback> {

    /**
     * Bluetooth central manager callback
     */
    private final BluetoothCentralManagerCallback bluetoothCentralManagerCallback;

    /**
     * Bluetooth central manager callback provider constructor
     *
     * @param context           the context required for providing the provided object
     * @param blessedBLEService the blessed ble service
     */
    public BluetoothCentralManagerCallbackProvider(BLEPeripheralTargetProvisionContext context, BlessedBLEService blessedBLEService) {
        this.bluetoothCentralManagerCallback = new BCMCallbackImplementation(context, blessedBLEService);
    }

    @Override
    public BluetoothCentralManagerCallback provide() {
        return this.bluetoothCentralManagerCallback;
    }
}

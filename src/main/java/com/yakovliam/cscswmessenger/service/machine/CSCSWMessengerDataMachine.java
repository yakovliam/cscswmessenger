package com.yakovliam.cscswmessenger.service.machine;

import com.welie.blessed.BluetoothGattCharacteristic;
import com.yakovliam.cscswmessenger.CSCSWMessengerBootstrapper;
import com.yakovliam.cscswmessenger.service.BlessedBLEService;

public class CSCSWMessengerDataMachine implements DataMachine {

    /**
     * Blessed ble service
     */
    private final BlessedBLEService bleService;

    /**
     * Data machine constructor
     *
     * @param blessedBLEService blessed ble service
     */
    public CSCSWMessengerDataMachine(BlessedBLEService blessedBLEService) {
        this.bleService = blessedBLEService;
    }

    /**
     * When data is received from the remote server
     *
     * @param characteristic characteristic
     * @param data           data
     */
    public void onReceiveData(BluetoothGattCharacteristic characteristic, byte[] data) {
        CSCSWMessengerBootstrapper.LOGGER.info("Received data from the remote machine!");

        // construct the data into a packet using a buffer
    }

    /**
     * Starts the data machine process
     */
    public void start() {
        CSCSWMessengerBootstrapper.LOGGER.info("Starting the data machine process!");
        // "send VI"
        // this doesn't actually do anything, it's just a test
        this.bleService.write(this.bleService.targetCharacteristic(), new byte[]{0});
    }

    /**
     * Receives the vendor id packet
     *
     * @param data data
     */
    private void receiveVendorIdPacket(byte[] data) {

    }
}

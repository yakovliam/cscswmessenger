package com.yakovliam.cscswmessenger.machine;

import com.csc.cpmobile.utils.Utils;
import com.welie.blessed.BluetoothGattCharacteristic;
import com.yakovliam.cscswmessenger.CSCSWMessengerBootstrapper;
import com.yakovliam.cscswmessenger.machine.service.CSCSWTokenBroker;
import com.yakovliam.cscswmessenger.machine.store.CSCSWProcessDataStore;
import com.yakovliam.cscswmessenger.machine.utils.CSCSWUtils;
import com.yakovliam.cscswmessenger.service.BlessedBLEService;

import static com.yakovliam.cscswmessenger.machine.utils.CSCSWConstants.CSCSW_VENDOR_ID;

public class CSCSWMessengerDataMachine implements DataMachine {

    /**
     * Blessed ble service
     */
    private final BlessedBLEService bleService;

    /**
     * The current process step
     */
    private CSCSWProcessStep currentProcessStep;

    /**
     * CSCSW token broker
     */
    private final CSCSWTokenBroker tokenBroker;

    /**
     * The data store for the process
     */
    private final CSCSWProcessDataStore dataStore;

    /**
     * Response buffer
     */
    private final StringBuffer responseBuffer = new StringBuffer();

    /**
     * The CSCSW token
     * <p>
     * Grabbed from the web api
     */
    private byte[] token;

    /**
     * Data machine constructor
     *
     * @param blessedBLEService blessed ble service
     */
    public CSCSWMessengerDataMachine(BlessedBLEService blessedBLEService) {
        this.bleService = blessedBLEService;
        this.currentProcessStep = CSCSWProcessStep.START;

        this.tokenBroker = new CSCSWTokenBroker();

        this.dataStore = new CSCSWProcessDataStore();
    }

    /**
     * When data is received from the remote server
     *
     * @param characteristic characteristic
     * @param data           data
     */
    public void onReceiveData(BluetoothGattCharacteristic characteristic, byte[] data) {
        CSCSWMessengerBootstrapper.LOGGER.info("Received data from the remote machine!");

        // add the received data (as hex string) to the response buffer
        responseBuffer.append(CSCSWUtils.convertBytesToHexString(data));

        // construct a full byte array packet from the buffer
        byte[] packet = Utils.hexStringToByteArray(this.responseBuffer.toString().replace(" ", ""));

        // check to see if the full packet is valid before processing
        boolean isValidPacket = packet.length - 5 == Utils.getLengthFromToken(packet);

        if (!isValidPacket) {
            // since the packet is not valid, we can't process it
            // therefore, we return and wait for more data to be received
            // to construct a valid packet
            return;
        }

        // clear the response buffer
        this.responseBuffer.setLength(0);

        // process the packet
        switch (currentProcessStep) {
            case VENDOR_ID -> this.receiveVendorIdPacket(packet);
            case GET_PRICE -> this.receiveGetPricePacket(packet);
        }
    }

    /**
     * Sends chunks of data to the remote server
     *
     * @param data data
     */
    private void sendChunksToRemoteMachine(byte[][] data) {
        boolean success = false;

        // while not success, keep send chunks to remote machine until success
        while (!success) {
            // send chunks
            for (byte[] chunk : data) {
                success = bleService.write(this.bleService.targetCharacteristic(), chunk);
            }
        }
    }

    /**
     * Starts the data machine process
     */
    public void start() {
        CSCSWMessengerBootstrapper.LOGGER.info("Starting the data machine process!");

        // use the token grabber to get a new token
        this.token = CSCSWUtils.convertByteArrayObjectsToPrimitives(this.tokenBroker.provideBrokered());

        // create the vendor id packet (asks the CSCSW machine to confirm the vendor id)
        byte[] packet = new byte[CSCSW_VENDOR_ID.length() + 4];
        byte[] vendorIdBytes = CSCSW_VENDOR_ID.getBytes();
        byte[] ttiBytes = "TTI".getBytes();
        System.arraycopy(vendorIdBytes, 0, packet, 0, vendorIdBytes.length);
        System.arraycopy(ttiBytes, 0, packet, vendorIdBytes.length, ttiBytes.length);
        packet[vendorIdBytes.length + ttiBytes.length] = 1;

        // set the current process step to the next, which is the vendor id one
        this.currentProcessStep = CSCSWProcessStep.VENDOR_ID;

        // format the packet and split it into 20 byte chunks
        byte[][] chunksToSend = CSCSWUtils.splitBytesIntoChunks(CSCSWUtils.formatPacket(packet, "VI"), 20);
        this.sendChunksToRemoteMachine(chunksToSend);
    }

    /**
     * Receives the vendor id packet
     *
     * @param data data
     */
    private void receiveVendorIdPacket(byte[] data) {
        // construct a packet to get ask the CSCSW machine for price data
        byte[] getPriceData = new byte[4];

        for (int i = 0; i < 4; ++i) {
            if (this.token == null) {
                getPriceData[i] = 0;
            } else {
                getPriceData[i] = this.token[i + 6];
            }
        }
        if (data.length >= 9) {
            // set the machine type
            System.arraycopy(data, 9, this.dataStore.machineType(), 0, 1);
        }

        // set the current process step to the next, which is the get price one
        this.currentProcessStep = CSCSWProcessStep.GET_PRICE;

        // format the packet and split it into 20 byte chunks
        byte[][] chunksToSend = CSCSWUtils.splitBytesIntoChunks(CSCSWUtils.formatPacket(getPriceData, "GP"), 20);
        this.sendChunksToRemoteMachine(chunksToSend);
    }

    /**
     * Receives the get price packet
     *
     * @param data data
     */
    private void receiveGetPricePacket(byte[] data) {
    }
}

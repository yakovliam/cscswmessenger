package com.yakovliam.cscswmessenger.machine;

import static com.yakovliam.cscswmessenger.machine.utils.CSCSWConstants.CSCSW_VENDOR_ID;

import com.welie.blessed.BluetoothGattCharacteristic;
import com.yakovliam.cscswmessenger.CSCSWMessengerBootstrapper;
import com.yakovliam.cscswmessenger.machine.service.CSCSWTokenBroker;
import com.yakovliam.cscswmessenger.machine.store.CSCSWProcessDataStore;
import com.yakovliam.cscswmessenger.machine.utils.CSCSWUtils;
import com.yakovliam.cscswmessenger.service.BlessedBLEService;
import com.yakovliam.cscswmessenger.service.model.MappedPeripheralWrapper;
import java.nio.charset.Charset;

public class CSCSWMessengerDataMachine implements DataMachine {

  /**
   * Blessed ble service
   */
  private final BlessedBLEService bleService;

  /**
   * Mapper peripheral wrapper
   */
  private final MappedPeripheralWrapper mappedPeripheralWrapper;

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
   * @param blessedBLEService       blessed ble service
   * @param mappedPeripheralWrapper mapped peripheral wrapper
   */

  public CSCSWMessengerDataMachine(BlessedBLEService blessedBLEService,
                                   MappedPeripheralWrapper mappedPeripheralWrapper) {
    this.bleService = blessedBLEService;
    this.mappedPeripheralWrapper = mappedPeripheralWrapper;
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
    CSCSWMessengerBootstrapper.LOGGER.info(
        "Received data inside data machine (hex): " + CSCSWUtils.convertByteArrayToHexString(data));
    // add the received data (as hex string) to the response buffer
    responseBuffer.append(CSCSWUtils.convertBytesToHexString(data));

    // construct a full byte array packet from the buffer
    byte[] packet =
        CSCSWUtils.convertHexStringToByteArray(this.responseBuffer.toString().replace(" ", ""));

    // check to see if the full packet is valid before processing
    boolean isValidPacket = packet.length - 5 == CSCSWUtils.getCompletePacketLengthFromData(packet);

    if (!isValidPacket) {
      // since the packet is not valid, we can't process it
      // therefore, we return and wait for more data to be received
      // to construct a valid packet
      CSCSWMessengerBootstrapper.LOGGER.info("Received invalid packet, waiting for more data...");
      return;
    }

    // clear the response buffer
    this.responseBuffer.setLength(0);

    // print the packet data to the log
    StringBuilder packetData = new StringBuilder();
    packetData.append("\n----- PACKET DATA -----\n");
    packetData.append("Hex: ").append(CSCSWUtils.convertBytesToHexString(packet)).append("\n");
    packetData.append("ASCII: ").append(new String(packet, Charset.defaultCharset())).append("\n");
    packetData.append("----- END PACKET DATA -----\n");
    CSCSWMessengerBootstrapper.LOGGER.info("Received valid packet, data: {}", packetData);

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

    // send chunks
    for (byte[] chunk : data) {
      boolean success = false;
      while (!success) {
        success = bleService.write(mappedPeripheralWrapper, chunk);
        CSCSWMessengerBootstrapper.LOGGER.info(
            "Sent chunk to remote machine (ascii): " + new String(chunk, Charset.defaultCharset()));
        try {
          Thread.sleep(100L);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
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
    byte[][] chunksToSend =
        CSCSWUtils.splitBytesIntoChunks(CSCSWUtils.formatPacket(packet, "VI"), 20);
    this.sendChunksToRemoteMachine(chunksToSend);
  }

  /**
   * Receives the vendor id packet
   *
   * @param data data
   */
  private void receiveVendorIdPacket(byte[] data) {
    CSCSWMessengerBootstrapper.LOGGER.info("Received vendor id packet, processing...");
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
    byte[][] chunksToSend =
        CSCSWUtils.splitBytesIntoChunks(CSCSWUtils.formatPacket(getPriceData, "GP"), 20);
    this.sendChunksToRemoteMachine(chunksToSend);
  }

  /**
   * Receives the get price packet
   *
   * @param data data
   */
  private void receiveGetPricePacket(byte[] data) {
    CSCSWMessengerBootstrapper.LOGGER.info("Received get price packet, processing...");

    // a byte array with the status of the machine
    byte[] statusArray = new byte[1]; // 'var28'

    // copy the "vend price" data to the data store
    System.arraycopy(data, 6, this.dataStore.vendPrice(), 0, 2);
    System.arraycopy(data, 8, statusArray, 0, 1);

    // and integer (1 or 0) representing whether a retry is needed to fetch the price
    int retryPriceRequest = statusArray[0] & 2;

    // set the pulse money
    this.dataStore.setPulseMoney(CSCSWUtils.getPriceFromPacket(data));

    // convert the status array to a binary string
    String statusBinaryString = Integer.toBinaryString(
        Integer.parseInt(CSCSWUtils.convertByteArrayToHexString(statusArray), 16));
    StringBuilder currentStatus = new StringBuilder();

    // insert leading zeros to the binary string
    for (int i = 0; i < 8 - statusBinaryString.length(); ++i) {
      currentStatus.insert(0, "0");
    }

    currentStatus.append(statusBinaryString);

    // set the machine status variables
    this.dataStore.setStartButtonIsEnabled("1".equals(currentStatus.substring(5, 6)));
    this.dataStore.setStartButtonIsPressed("1".equals(currentStatus.substring(4, 5)));
    this.dataStore.setCanDoTopOff("1".equals(currentStatus.substring(3, 4)));
    this.dataStore.setCanDoSuperCycle("1".equals(currentStatus.substring(2, 3)));

    // not sure where this is used... going to leave it blank right now
//        if (this.dataStore.canDoTopOff() || this.dataStore.canDoSuperCycle()) {
//            System.arraycopy(data, 9, var5, 0, 2);
//            System.arraycopy(data, 11, var6, 0, 1);
//        }

    // ask the data to start / extend
    byte[] seData = new byte[this.dataStore.vendPrice().length + this.token.length];
    System.arraycopy(this.dataStore.vendPrice(), 0, seData, 0, this.dataStore.vendPrice().length);
    System.arraycopy(this.token, 0, seData, this.dataStore.vendPrice().length, this.token.length);

    // set the current process step to the next, which is the start / extend one
    this.currentProcessStep = CSCSWProcessStep.START_CYCLE_EXTEND;

    // format the packet and split it into 20 byte chunks
    byte[][] chunksToSend =
        CSCSWUtils.splitBytesIntoChunks(CSCSWUtils.formatPacket(seData, "SE"), 20);
    this.sendChunksToRemoteMachine(chunksToSend);
  }
}

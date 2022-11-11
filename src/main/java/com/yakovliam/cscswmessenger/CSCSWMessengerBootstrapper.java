package com.yakovliam.cscswmessenger;

import com.yakovliam.cscswmessenger.service.CSCSWMessengerBLEService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSCSWMessengerBootstrapper {

    public static final Logger LOGGER = LoggerFactory.getLogger(CSCSWMessengerBootstrapper.class);

    /**
     * Main entry point of the CSCSWMessenger project
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // initialize the bootstrapper instance
        new CSCSWMessengerBootstrapper();
    }

    /**
     * CSCSWMessengerBootstrapper constructor
     * The kind-of entry point of the program
     */
    public CSCSWMessengerBootstrapper() {
        CSCSWMessengerBLEService bleService = new CSCSWMessengerBLEService();
        bleService.startScanningForPeripherals();
    }
}
package com.yakovliam.cscswmessenger.utils;

import java.util.UUID;

public class PeripheralConstants {

    public static final String TARGET_PERIPHERAL_ENDING_DIGITS = "017";
    public static final UUID TYPE_2_VENDOR_SPECIFIC_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static final UUID TYPE_1_VENDOR_SPECIFIC_SERVICE = UUID.fromString("49535343-fe7d-4ae5-8fa9-9fafd205e455");

    public static final UUID TYPE_1_VENDOR_SPECIFIC_CHARACTERISTIC = UUID.fromString("49535343-8841-43F4-A8D4-ECBE34729BB3");
    public static final UUID TYPE_2_VENDOR_SPECIFIC_CHARACTERISTIC = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    public static final UUID TARGET_SERVICE_UUID = TYPE_2_VENDOR_SPECIFIC_SERVICE;
    public static final UUID TARGET_CHARACTERISTIC_UUID = TYPE_2_VENDOR_SPECIFIC_CHARACTERISTIC;

}

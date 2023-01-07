package com.yakovliam.cscswmessenger.utils;

import java.util.UUID;

public class PeripheralConstants {

  public static final String TARGET_PERIPHERAL_ENDING_DIGITS = "002";
  public static final String TARGET_PERIPHERAL_TYPE_2_STARTING_DIGITS = "20COL";

  public static final UUID TYPE_2_SERVICE_UUID =
      UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
  public static final UUID TYPE_2_CHAR_WRITE_UUID =
      UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
  public static final UUID TYPE_2_CHAR_NOTIFY_UUID =
      UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

  public static final UUID ME51_CHAR_NOTIFY_UUID =
      UUID.fromString("49535343-1E4D-4BD9-BA61-23C647249616");
  public static final UUID ME51_CHAR_WRITE_UUID =
      UUID.fromString("49535343-8841-43F4-A8D4-ECBE34729BB3");
  public static final UUID ME51_SERVICE_UUID =
      UUID.fromString("49535343-fe7d-4ae5-8fa9-9fafd205e455");
}

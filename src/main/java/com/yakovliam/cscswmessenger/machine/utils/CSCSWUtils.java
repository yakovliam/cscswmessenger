package com.yakovliam.cscswmessenger.machine.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.stream.IntStream;

public class CSCSWUtils {

    /**
     * Calculates the longitude redundancy check for the given data
     *
     * @param data data
     * @return lrc
     */
    public static byte calculateLRC(byte[] data) {
        byte lrc = 0;
        for (byte datum : data) {
            lrc ^= datum;
        }

        return lrc;
    }

    /**
     * Verifies a packet
     * <p>
     * Adapted from a CSCSW algorithm
     *
     * @param data data
     * @return verified packet
     */
    public static byte[] verifyPacket(byte[] data) {
        int lenMinusEight = data.length - 8;
        byte[] output = new byte[lenMinusEight];
        System.arraycopy(data, 8, output, 0, lenMinusEight);
        return output;
    }

    /**
     * Formats a packet for sending to a CSCSW machine
     * <p>
     * Adapted from a CSCSW algorithm
     *
     * @param data       data
     * @param packetType the type of packet to send, known by the CSCSW machine
     * @return formatted packet
     */
    public static byte[] formatPacket(byte[] data, String packetType) {
        int var2 = 7;
        int var3 = 0;
        int var4;
        if (data != null) {
            var4 = data.length;
            var2 = 7 + data.length;
        } else {
            var4 = 0;
        }

        byte[] var5 = new byte[var2];
        byte[] var6 = new byte[var4 + 4];
        var5[0] = 2;
        int var7 = var4 + 2;
        byte var8 = (byte) (('\uff00' & var7) >> 8);
        var5[1] = var8;
        var6[0] = var8;
        var8 = (byte) (var7 & 255);
        var5[2] = var8;
        var6[1] = var8;
        byte[] var9 = packetType.getBytes();
        var5[3] = var9[0];
        var6[2] = var9[0];
        var5[4] = var9[1];

        for (var6[3] = var9[1]; var3 < var4; ++var3) {
            var8 = data[var3];
            var5[var3 + 5] = var8;
            var6[var3 + 4] = var8;
        }

        var5[var2 - 2] = calculateLRC(var6);
        var5[var2 - 1] = 3;
        return var5;
    }

    /**
     * Splits a packet into equally sized chunks of a given size
     * <p>
     * Adapted from a CSCSW algorithm
     *
     * @param data      data
     * @param chunkSize chunk size
     * @return chunks
     */
    public static byte[][] splitBytesIntoChunks(byte[] data, int chunkSize) {
        int var2 = data.length % chunkSize;
        byte[][] outputChunks = new byte[(int) Math.ceil((double) (data.length / chunkSize)) + 1][chunkSize];
        int var4 = 0;

        int var6;
        for (int var5 = var4; var4 < outputChunks.length; var5 = var6) {
            var6 = var5 + chunkSize;
            outputChunks[var4] = Arrays.copyOfRange(data, var5, var6);
            ++var4;
        }

        data = new byte[var2];
        System.arraycopy(outputChunks[outputChunks.length - 1], 0, data, 0, var2);
        outputChunks[outputChunks.length - 1] = data;
        return outputChunks;
    }


    /**
     * Converts a byte array to a hex string
     *
     * @param data data
     * @return hex string
     */
    public static String convertBytesToHexString(byte[] data) {
        StringBuilder buffer = new StringBuilder();

        for (byte datum : data) {
            buffer.append(String.format("%02x ", datum));
        }

        return buffer.toString().replace(" ", "");
    }

    /**
     * Gets the completed packet length from data received from a CSCSW machine
     *
     * @param constructedPacket constructed packet
     * @return length
     */
    public static int getCompletePacketLengthFromData(byte[] constructedPacket) {
        byte secondByte = constructedPacket[1];
        return constructedPacket[2] & 255 | secondByte << 8;
    }


    /**
     * Converts a hex string to a byte array
     * <p>
     * Adapted from <a href="https://stackoverflow.com/a/140861"/>a stackoverflow post</a>,
     * originally adapted from a CSCSW algorithm
     *
     * @param hexString hex string
     * @return byte array
     */
    public static byte[] convertHexStringToByteArray(String hexString) {
        return HexFormat.of().parseHex(hexString);
//        if ("".equals(hexString)) {
//            return null;
//        } else {
//            int var1 = hexString.length();
//            byte[] var2 = new byte[var1 / 2];
//
//            for (int var3 = 0; var3 < var1; var3 += 2) {
//                var2[var3 / 2] = (byte) ((Character.digit(hexString.charAt(var3), 16) << 4) + Character.digit(hexString.charAt(var3 + 1), 16));
//            }
//
//            return var2;
//        }
    }

    /**
     * Converts a byte array to a hex string
     *
     * @param data data
     * @return hex string
     */
    public static String convertByteArrayToHexString(byte[] data) {
        return HexFormat.of().withUpperCase().formatHex(data);
    }

    /**
     * Converts a byte array of primitives to a byte array of objects
     *
     * @param bytes bytes
     * @return byte array of objects
     */
    public static Byte[] convertByteArrayPrimitiveToObjects(byte[] bytes) {
        return IntStream.range(0, bytes.length).mapToObj(i -> bytes[i]).toArray(Byte[]::new);
    }

    /**
     * Converts a byte array of objects to a byte array of primitives
     *
     * @param oBytes byte array of objects
     * @return byte array of primitives
     */
    public static byte[] convertByteArrayObjectsToPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];
        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }
        return bytes;
    }

    /**
     * Returns the price of vending/pulsing (unknown) from a packet
     *
     * @param packet packet
     * @return price
     */
    public static String getPriceFromPacket(byte[] packet) {
        if (packet == null) {
            return "0";
        } else {
            byte[] var1 = new byte[2];

            for (int var2 = 0; var2 < 2; ++var2) {
                var1[var2] = packet[var2 + 6];
            }

            return String.valueOf(ByteBuffer.wrap(var1).getShort());
        }
    }
}

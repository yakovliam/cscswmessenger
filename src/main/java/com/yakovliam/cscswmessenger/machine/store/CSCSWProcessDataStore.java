package com.yakovliam.cscswmessenger.machine.store;

public class CSCSWProcessDataStore {

    /**
     * The type of CSCSW machine that is being communicated with
     * The length is going to be 1 byte, always
     * <p>
     * According to discovered information:
     * Type "1": KioSoft Ultra LX Pro
     * Type "2": CleanReader Connect (Card Reader) or CleanReader Solo Connect (App Only)
     */
    private byte[] machineType = new byte[1];

    public byte[] machineType() {
        return machineType;
    }

    public void setMachineType(byte[] machineType) {
        this.machineType = machineType;
    }
}

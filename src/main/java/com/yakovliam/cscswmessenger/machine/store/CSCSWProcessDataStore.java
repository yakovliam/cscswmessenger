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

    /**
     * The price it costs (in cents) to use the machine
     * This price can either be a top off price or the full load price, depending on the data received from the machine
     * <p>
     * Two bytes in length... for $1.50, the data  will be 0x00 0x96
     * For $1.25, the data will be 0x00 0x7D
     * The 0x7D will show up as a right bracket in ascii... look out for that!
     */
    private byte[] vendPrice = new byte[2];

    /**
     * The money required to do a "pulse"
     * The definition of pulse is unknown, and it's something referred to in related CSCSW code
     */
    private String pulseMoney;

    /**
     * If the start button is enabled
     */
    private boolean startButtonIsEnabled = false;

    /**
     * If the start button is pressed
     */
    private boolean isStartButtonIsPressed = false;

    /**
     * If the machine can do a top off
     */
    private boolean canDoTopOff = false;

    /**
     * If the machine can do a super cycle
     */
    private boolean canDoSuperCycle = false;


    public byte[] machineType() {
        return machineType;
    }

    public byte[] vendPrice() {
        return vendPrice;
    }

    public void setPulseMoney(String pulseMoney) {
        this.pulseMoney = pulseMoney;
    }

    public String pulseMoney() {
        return pulseMoney;
    }

    public boolean startButtonIsEnabled() {
        return startButtonIsEnabled;
    }

    public void setStartButtonIsEnabled(boolean startButtonIsEnabled) {
        this.startButtonIsEnabled = startButtonIsEnabled;
    }

    public boolean isStartButtonIsPressed() {
        return isStartButtonIsPressed;
    }

    public void setStartButtonIsPressed(boolean startButtonIsPressed) {
        isStartButtonIsPressed = startButtonIsPressed;
    }

    public boolean canDoTopOff() {
        return canDoTopOff;
    }

    public void setCanDoTopOff(boolean canDoTopOff) {
        this.canDoTopOff = canDoTopOff;
    }

    public boolean canDoSuperCycle() {
        return canDoSuperCycle;
    }

    public void setCanDoSuperCycle(boolean canDoSuperCycle) {
        this.canDoSuperCycle = canDoSuperCycle;
    }
}

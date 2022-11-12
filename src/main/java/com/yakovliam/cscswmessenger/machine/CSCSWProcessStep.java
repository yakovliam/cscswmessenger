package com.yakovliam.cscswmessenger.machine;

public enum CSCSWProcessStep {
    START(0), VENDOR_ID(1), GET_PRICE(2), START_CYCLE_EXTEND(3), END_CYCLE(4), GET_FIRMWARES_VERSION(5), GET_INFORMATION_REMANIN_TIME(6), GET_INFORMATION_MACHINE_TYPE(7), GET_INFORMATION_MACHINE_STATUS(8);

    /**
     * The CSCSW determined process identifier
     */
    private final int processId;

    /**
     * CSCSW process
     *
     * @param processId process id
     */
    CSCSWProcessStep(int processId) {
        this.processId = processId;
    }

    /**
     * Returns the process id
     *
     * @return process id
     */
    public int processId() {
        return processId;
    }
}

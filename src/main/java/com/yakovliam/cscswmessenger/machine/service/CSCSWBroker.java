package com.yakovliam.cscswmessenger.machine.service;

public interface CSCSWBroker<T> {

    /**
     * Provides an object brokered from a CSCSW service
     *
     * @return object
     */
    T provideBrokered();
}

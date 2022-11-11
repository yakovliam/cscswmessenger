package com.yakovliam.cscswmessenger.provider;

public interface Provider<T> {

    /**
     * Provides the generic
     *
     * @return generic
     */
    T provide();
}

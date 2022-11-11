package com.yakovliam.cscswmessenger.filter;

import java.util.function.Predicate;

public abstract class BlessedObjectFilter<T> {

    /**
     * The filter action
     */
    private final Predicate<T> filterAction;

    /**
     * Blessed object filter constructor
     *
     * @param filterAction filter action
     */
    protected BlessedObjectFilter(Predicate<T> filterAction) {
        this.filterAction = filterAction;
    }

    /**
     * Filter a given object
     *
     * @param given given
     * @return if the given passes the filter
     */
    public boolean passes(T given) {
        return this.filterAction.test(given);
    }
}

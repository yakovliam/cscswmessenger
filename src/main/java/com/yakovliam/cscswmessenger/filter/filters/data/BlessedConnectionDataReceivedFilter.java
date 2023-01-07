package com.yakovliam.cscswmessenger.filter.filters.data;

import com.yakovliam.cscswmessenger.filter.BlessedObjectFilter;
import java.util.function.Predicate;

public abstract class BlessedConnectionDataReceivedFilter
    extends BlessedObjectFilter<BlessedPeripheralConnectionDataReceivedPredicateActionData> {

  /**
   * Connection data received filter constructor
   *
   * @param filterAction filter action
   */
  protected BlessedConnectionDataReceivedFilter(
      Predicate<BlessedPeripheralConnectionDataReceivedPredicateActionData> filterAction) {
    super(filterAction);
  }
}


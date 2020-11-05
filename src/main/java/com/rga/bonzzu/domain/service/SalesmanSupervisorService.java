package com.rga.bonzzu.domain.service;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.rangeClosed;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SalesmanSupervisorService {

  int xInitAxis;
  int xEndAxis;
  int yInitAxis;
  int yEndAxis;

  public SalesmanSupervisorService(int xInitAxis, int xEndAxis, int yInitAxis, int yEndAxis) {
    this.xInitAxis = xInitAxis;
    this.xEndAxis = xEndAxis;
    this.yInitAxis = yInitAxis;
    this.yEndAxis = yEndAxis;
  }

  public long getAmountOfHousesAdvertisedTheGivenTimes(int advertisedTimes, SalesmanRange... ranges)
      throws Exception {
    if (ranges.length < advertisedTimes) {
      throw new Exception("InvalidAmountOfAdvertisedHousesByAmountOfSalesman");
    }
    if (stream(ranges).filter(r -> !isValidRange(r)).findAny().isPresent()) {
      throw new Exception("InvalidRange");
    }
    Map<String, AtomicInteger> rangeCounter = new HashMap<String, AtomicInteger>();

    stream(ranges).forEach(r -> addCounts(r, rangeCounter));

    return rangeCounter
        .keySet()
        .stream()
        .filter(k -> rangeCounter.get(k).get() >= advertisedTimes)
        .count();
  }

  private void addCounts(SalesmanRange range, Map<String, AtomicInteger> rangeCounter) {
    getMapPoints(range).forEach(point -> addCounterPoints(rangeCounter, point));
  }

  private void addCounterPoints(Map<String, AtomicInteger> rangeCounter, String point) {
    if (rangeCounter.containsKey(point)) {
      rangeCounter.get(point).incrementAndGet();
    } else {
      rangeCounter.put(point, new AtomicInteger(1));
    }
  }

  private Set<String> getMapPoints(SalesmanRange range) {
    return rangeClosed(range.getxInitAxis(), range.getxEndAxis())
        .mapToObj(x -> getOneDimensionRange(x, range.getyInitAxis(), range.getyEndAxis()))
        .flatMap(Set::stream)
        .collect(toSet());
  }

  private Set<String> getOneDimensionRange(int xValue, int yInit, int yEnd) {
    return rangeClosed(yInit, yEnd).mapToObj(y -> new String(xValue + "," + y)).collect(toSet());
  }

  private boolean isValidRange(SalesmanRange range) {
    boolean isInRange =
        range.getxEndAxis() <= this.xEndAxis
            && range.getyEndAxis() <= this.yEndAxis
            && range.getxInitAxis() >= this.xInitAxis
            && range.getyInitAxis() >= this.yInitAxis;
    boolean isValidRange =
        range.getxInitAxis() < range.getxEndAxis() && range.getyInitAxis() < range.getyEndAxis();
    return isInRange && isValidRange;
  }

  public static class SalesmanRange {
    int xInitAxis;
    int xEndAxis;
    int yInitAxis;
    int yEndAxis;

    public SalesmanRange(int xInitAxis, int yInitAxis, int xEndAxis, int yEndAxis) {
      this.xInitAxis = xInitAxis;
      this.xEndAxis = xEndAxis;
      this.yInitAxis = yInitAxis;
      this.yEndAxis = yEndAxis;
    }

    public static SalesmanRange range(int xInitAxis, int yInitAxis, int xEndAxis, int yEndAxis) {
      return new SalesmanRange(xInitAxis, yInitAxis, xEndAxis, yEndAxis);
    }

    public int getxInitAxis() {
      return xInitAxis;
    }

    public int getxEndAxis() {
      return xEndAxis;
    }

    public int getyInitAxis() {
      return yInitAxis;
    }

    public int getyEndAxis() {
      return yEndAxis;
    }
  }
}

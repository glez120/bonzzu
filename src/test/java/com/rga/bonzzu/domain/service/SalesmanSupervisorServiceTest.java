package com.rga.bonzzu.domain.service;

import static com.rga.bonzzu.domain.service.SalesmanSupervisorService.SalesmanRange.range;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.Test;

import com.rga.bonzzu.domain.service.SalesmanSupervisorService.SalesmanRange;

public class SalesmanSupervisorServiceTest {

  private SalesmanSupervisorService service = new SalesmanSupervisorService(0, 109, 0, 109);

  @Test
  public void shouldShowCoincidencesForAdvertisedOnce() throws Exception {

    long timesAdvertised =
        service.getAmountOfHousesAdvertisedTheGivenTimes(1, range(0, 0, 4, 4), range(1, 1, 2, 5));

    assertEquals(27, timesAdvertised);
  }

  @Test
  public void shouldShowCoincidencesForAdvertisedMultipleTimes() throws Exception {

    long timesAdvertised =
        service.getAmountOfHousesAdvertisedTheGivenTimes(2, range(0, 0, 4, 4), range(1, 1, 2, 5));

    assertEquals(8, timesAdvertised);
  }

  @Test
  public void shouldShowConcurrencyForWiderRanges() throws Exception {

    long timesAdvertised =
        service.getAmountOfHousesAdvertisedTheGivenTimes(
            3, range(0, 5, 100, 109), range(55, 55, 80, 90), range(4, 8, 66, 75));

    assertEquals(252, timesAdvertised);
  }

  public void shouldPerformForBigLoad() throws Exception {
    List<SalesmanRange> list =
        IntStream.range(0, 1000).mapToObj(i -> generateRandomRange()).collect(toList());
    long initTime = currentTimeMillis();

    service.getAmountOfHousesAdvertisedTheGivenTimes(5, list.toArray(new SalesmanRange[100]));

    assertTrue((currentTimeMillis() - initTime) < 2000);
  }

  @Test
  public void shouldFailOnInvalidRange() throws Exception {
    Exception ex = null;

    try {
      service.getAmountOfHousesAdvertisedTheGivenTimes(1, range(100, 200, 200, 300));
    } catch (Exception e) {
      ex = e;
    }

    assertNotNull(ex);
    assertEquals("InvalidRange", ex.getMessage());
  }

  @Test
  public void shouldFailOnInvalidExpectedAmount() throws Exception {
    Exception ex = null;

    try {
      service.getAmountOfHousesAdvertisedTheGivenTimes(5, range(100, 200, 200, 300));
    } catch (Exception e) {
      ex = e;
    }

    assertNotNull(ex);
    assertEquals("InvalidAmountOfAdvertisedHousesByAmountOfSalesman", ex.getMessage());
  }

  private SalesmanRange generateRandomRange() {
    return range(getRandom(0, 25), getRandom(0, 25), getRandom(90, 109), getRandom(90, 109));
  }

  private Integer getRandom(int min, int max) {
    Random random = new Random();
    return random.ints(min, max).findFirst().getAsInt();
  }
}

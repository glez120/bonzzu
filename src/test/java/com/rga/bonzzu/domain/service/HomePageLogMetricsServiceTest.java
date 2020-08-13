package com.rga.bonzzu.domain.service;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.rga.bonzzu.domain.service.HomePageLogMetricsService.HomePageLog;

public class HomePageLogMetricsServiceTest {

  private HomePageLogMetricsService service;

  @Before
  public void init() throws Exception {
    InputStream resource = getClass().getResourceAsStream("/productHomePageLog.txt");
    service = new HomePageLogMetricsService(resource);
  }

  @Test
  public void shouldFetchUsersByProductVisits() throws Exception {

    List<String> users = service.getUserVisitsByProduct("p1");

    assertEquals(3, users.size());
    assertTrue(users.containsAll(asList("u1", "u10", "u20")));
  }

  @Test
  public void shouldGetVisitsOnGivenDateRange() throws Exception {

    List<HomePageLog> visits = service.getVisitsByTimeRange("2019-01-01 00", "2019-01-03 00");

    assertEquals(5, visits.size());
  }

  @Test
  public void q1Evidence1() throws Exception {
    assertEquals(3, service.getUserVisitsByProduct("p1").size());
    assertEquals(1, service.getUserVisitsByProduct("p3").size());
    assertEquals(2, service.getUserVisitsByProduct("p12").size());
  }
}

package com.rga.bonzzu.domain.service;

import static java.lang.System.getProperty;
import static java.lang.Thread.sleep;
import static java.util.Arrays.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import org.junit.Test;

public class CurrencyConversionServiceTest {

  private static final String BASE_DIR = "/currencyDataSource/";
  private static final CurrencyConversionService SERVICE;

  // For testing reasons the time is reduced from minutes to seconds
  static {
    SERVICE = new CurrencyConversionService(5000, BASE_DIR, 1000);
  }

  @Test
  public void shouldGenerateCurrencyConversionData() throws Exception {

    Map<String, String> conversionMap = SERVICE.getCurrentCurrencyConversion();

    assertNotNull(conversionMap);
    assertEquals(3, conversionMap.size());
  }

  @Test
  public void shouldGenerateNewDataEveryFiveSeconds() throws Exception {
    Map<String, String> baseData = SERVICE.getCurrentCurrencyConversion();
    sleep(5500);

    Map<String, String> newData = SERVICE.getCurrentCurrencyConversion();

    assertFalse(baseData.get("CAD_USD").equals(newData.get("CAD_USD")));
    assertFalse(baseData.get("FR_USD").equals(newData.get("FR_USD")));
    assertFalse(baseData.get("MXN_USD").equals(newData.get("MXN_USD")));
  }

  @Test
  public void shouldGenerateConversionLogFiles() throws Exception {
    sleep(21000);

    File[] files = new File(getProperty("user.dir") + BASE_DIR).listFiles();

    assertTrue(files.length > 4);
    assertTrue(stream(files).filter(f -> f.getName().equals("buildId.txt")).findAny().isPresent());
  }
}

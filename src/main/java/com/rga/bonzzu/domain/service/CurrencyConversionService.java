package com.rga.bonzzu.domain.service;

import static java.lang.Math.random;
import static java.lang.System.getProperty;
import static java.lang.Thread.sleep;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.readAllLines;
import static java.util.Arrays.stream;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CurrencyConversionService {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final String BUILD_ID_FILE_NAME = "buildId.txt";
  private static final String FILE_FORMAT = ".txt";
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss");
  private static final String[] SUPPORTED_CURRENCIES = {"CAD_USD", "FR_USD", "MXN_USD"};
  private static final String BREAK_LINE = "\n";
  private Map<String, String> currentCurrencyConversion;

  public CurrencyConversionService(
      long currencyReloadTime, String currencyFilesBasePath, long currencyMapRefreshTime) {
    System.out.println("==> CurrencyConversionService <==");
    try {
      CurrencyConversionBuilder builder = new CurrencyConversionBuilder(currencyFilesBasePath);
      new Timer().scheduleAtFixedRate(builder, 0, currencyReloadTime);

      CurrencyConversionLoader loader = new CurrencyConversionLoader(this, currencyFilesBasePath);
      new Timer().scheduleAtFixedRate(loader, 100, currencyMapRefreshTime);

      serviceWarmUp();
    } catch (InterruptedException e) {
      System.out.println("==> ERROR WHILE WARMING UP THE SERVICE <==");
      e.printStackTrace();
    }
  }

  void updateCurrencyConversion(Map<String, String> currentCurrencyConversion) {
    this.currentCurrencyConversion = currentCurrencyConversion;
  }

  public Map<String, String> getCurrentCurrencyConversion() {
    return this.currentCurrencyConversion;
  }

  private void serviceWarmUp() throws InterruptedException {
    sleep(500);
  }

  public static class CurrencyConversionLoader extends TimerTask {

    private CurrencyConversionService service;
    private String currencyFilesBasePath;
    private String currentBuildId = "";

    public CurrencyConversionLoader(
        CurrencyConversionService service, String currencyFilesBasePath) {
      this.service = service;
      this.currencyFilesBasePath = currencyFilesBasePath;
    }

    @Override
    public void run() {
      try {
        CurrencyConversionBuildId buildId = getCurrentBuildId();
        boolean isCurrencyUpdated = !currentBuildId.equals(buildId.getBuildId());
        if (isCurrencyUpdated) {
          currentBuildId = buildId.getBuildId();
          updateCurrencyConversion(buildId);
        }
      } catch (IOException e) {
        System.out.println("===> ERROR <===");
        System.out.println();
        e.printStackTrace();
      }
    }

    private void updateCurrencyConversion(CurrencyConversionBuildId buildId) throws IOException {
      String path = getProperty("user.dir") + currencyFilesBasePath + buildId.getFileName();
      List<String> lines = readAllLines(Paths.get(path));
      Map<String, String> map =
          lines.stream().map(l -> l.split(" - ")).collect(toMap(a -> a[0], a -> a[1]));
      service.updateCurrencyConversion(map);
    }

    private CurrencyConversionBuildId getCurrentBuildId() throws IOException {
      String path = getProperty("user.dir") + currencyFilesBasePath + BUILD_ID_FILE_NAME;
      String file = new String(readAllBytes(Paths.get(path)));
      return GSON.fromJson(file, CurrencyConversionBuildId.class);
    }
  }

  public static class CurrencyConversionBuilder extends TimerTask {

    private String currencyFilesBasePath;

    public CurrencyConversionBuilder(String currencyFilesBasePath) {
      this.currencyFilesBasePath = currencyFilesBasePath;
    }

    @Override
    public void run() {
      try {
        buildCurrencyConversionRecord();
      } catch (Exception e) {
        System.out.println("===> ERROR <===");
        System.out.println();
        e.printStackTrace();
      }
    }

    private void buildCurrencyConversionRecord() throws IOException {
      Map<String, Double> map = stream(SUPPORTED_CURRENCIES).collect(toMap(k -> k, k -> random()));
      String fileName = buildFileName();
      buildRecordFile(map, fileName);
      createBuildId(fileName);
    }

    private void createBuildId(String fileName) throws IOException {
      String filePath = buildFilePath(BUILD_ID_FILE_NAME);
      writeFile(filePath, GSON.toJson(new CurrencyConversionBuildId(fileName)));
    }

    private void buildRecordFile(Map<String, Double> map, String fileName) throws IOException {
      String filePath = buildFilePath(fileName);
      String data = map.entrySet().stream().map(e -> mapper(e)).collect(joining(BREAK_LINE));
      writeFile(filePath, data);
    }

    private void writeFile(String filePath, String fileContent) throws IOException {
      BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
      writer.write(fileContent);
      writer.close();
    }

    private String buildFileName() {
      return DATE_FORMAT.format(new Date()) + FILE_FORMAT;
    }

    private String buildFilePath(String fileName) {
      return getProperty("user.dir") + currencyFilesBasePath + fileName;
    }

    private static String mapper(Entry<String, Double> entry) {
      return entry.getKey() + " - " + entry.getValue().toString().substring(0, 4);
    }
  }

  public static class CurrencyConversionBuildId {

    private static final String CURRENCY_CONVERSION_BUILD_ID_VERSION = "v1";

    private String buildId = randomUUID().toString();
    private String fileName;
    private String version = CURRENCY_CONVERSION_BUILD_ID_VERSION;

    public CurrencyConversionBuildId(String fileName) {
      this.fileName = fileName;
    }

    public String getBuildId() {
      return buildId;
    }

    public String getFileName() {
      return fileName;
    }

    public String getVersion() {
      return version;
    }
  }
}

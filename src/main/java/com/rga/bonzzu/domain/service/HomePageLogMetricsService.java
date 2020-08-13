package com.rga.bonzzu.domain.service;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class HomePageLogMetricsService {

  private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  private static final SimpleDateFormat DATE_RANGE_FORMATTER =
      new SimpleDateFormat("yyyy-MM-dd HH");
  private List<String> fileContent;
  private List<HomePageLog> homePageLog;
  private static final String CONTENT_SEPARATOR = "\\t";

  public HomePageLogMetricsService(InputStream file) throws Exception {
    fileContent = getFileContent(file);
    validateFileInput();
    this.homePageLog = buildHomePageLog();
  }

  public List<String> getUserVisitsByProduct(String productId) {
    Predicate<HomePageLog> filter = log -> log.getProductId().equals(productId);
    Function<HomePageLog, String> mapper = log -> log.getUserId();
    return homePageLog.stream().filter(filter).map(mapper).distinct().collect(toList());
  }

  public List<HomePageLog> getVisitsByTimeRange(String initialDate, String endDate)
      throws ParseException {
    Date iDate = DATE_RANGE_FORMATTER.parse(initialDate);
    Date eDate = DATE_RANGE_FORMATTER.parse(endDate);
    Predicate<HomePageLog> filter = l -> isDateInRange(l.getTimestamp(), iDate, eDate);
    return homePageLog.stream().filter(filter).collect(toList());
  }

  private boolean isDateInRange(Date date, Date initialRange, Date endRange) {
    return isAfterThan(date, initialRange) && isBeforeThan(date, endRange);
  }

  private boolean isAfterThan(Date baseDate, Date dateToCompare) {
    return baseDate.compareTo(dateToCompare) > 0;
  }

  private boolean isBeforeThan(Date baseDate, Date dateToCompare) {
    return baseDate.compareTo(dateToCompare) < 0;
  }

  private void validateFileInput() throws Exception {
    if (fileContent.stream().filter(l -> !isValidLine(l)).findAny().isPresent()) {
      throw new Exception("Invalid file format");
    }
  }

  private boolean isValidLine(String line) {
    String[] lineContent = line.split(CONTENT_SEPARATOR);
    boolean validLine = lineContent.length == 3;
    return validLine && isValidDateFormat(lineContent[2]);
  }

  private boolean isValidDateFormat(String date) {
    try {
      DATE_FORMATTER.parse(date);
    } catch (ParseException e) {
      return false;
    }
    return true;
  }

  private List<String> getFileContent(InputStream file) {
    return new BufferedReader(new InputStreamReader(file)).lines().collect(toList());
  }

  private List<HomePageLog> buildHomePageLog() {
    return fileContent.stream().map(this::mapFileLineToHomePageLog).collect(toList());
  }

  private HomePageLog mapFileLineToHomePageLog(String line) {
    String[] lineContent = line.split(CONTENT_SEPARATOR);
    return new HomePageLog(lineContent[0], lineContent[1], lineContent[2]);
  }

  public static class HomePageLog {
    private String productId;
    private String userId;
    private Date timestamp;

    public HomePageLog(String productId, String userId, String timestamp) {

      try {
        this.productId = productId;
        this.userId = userId;
        this.timestamp = DATE_FORMATTER.parse(timestamp);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }

    public String getProductId() {
      return productId;
    }

    public String getUserId() {
      return userId;
    }

    public Date getTimestamp() {
      return timestamp;
    }

    @Override
    public String toString() {
      return String.format(
          "HomePageLog [productId: %s, userId: %s, timestamp: %s]", productId, userId, timestamp);
    }
  }
}

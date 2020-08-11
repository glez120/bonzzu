package com.rga.bonzzu.domain.service;

import static java.lang.String.valueOf;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import java.util.List;

import com.rga.bonzzu.domain.exception.InvalidColumnsForMessageExeception;

public class MessageEncriptor {

  public static String decrypt(String encryptedMessage, int columns)
      throws InvalidColumnsForMessageExeception {
    validateInputs(encryptedMessage, columns);
    List<String> messageColumns = splitMessage(encryptedMessage, columns);
    return buildMessage(messageColumns, columns);
  }

  private static List<String> splitMessage(String message, int columns) {
    int rowCount = message.length() / columns;
    return range(0, rowCount).mapToObj(i -> getRow(i, message, columns)).collect(toList());
  }

  private static String getRow(int index, String message, int columns) {
    String substring = substring(index, columns, message);
    return isEven(index) ? substring : flipString(substring);
  }

  private static String substring(int index, int columns, String message) {
    int bIndex = (index) * columns;
    return message.substring(bIndex, (bIndex + columns));
  }

  private static String flipString(String string) {
    return new StringBuilder(string).reverse().toString();
  }

  private static String buildMessage(List<String> formattedMessage, int columns) {
    return range(0, columns).mapToObj(i -> getCol(formattedMessage, i)).collect(joining());
  }

  private static String getCol(List<String> formattedMessage, int index) {
    return formattedMessage.stream().map(s -> valueOf(s.charAt(index))).collect(joining());
  }

  private static boolean isEven(int number) {
    return (number % 2) == 0;
  }

  private static void validateInputs(String encryptedMessage, int columns)
      throws InvalidColumnsForMessageExeception {
    if ((encryptedMessage.length() % columns) > 0) {
      throw new InvalidColumnsForMessageExeception();
    }
  }
}

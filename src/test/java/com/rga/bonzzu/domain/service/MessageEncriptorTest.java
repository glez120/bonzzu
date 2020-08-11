package com.rga.bonzzu.domain.service;

import static com.rga.bonzzu.domain.service.MessageEncriptor.decrypt;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.rga.bonzzu.domain.exception.InvalidColumnsForMessageExeception;

public class MessageEncriptorTest {

  @Test
  public void shouldDecryptMessage() throws Exception {

    String message = decrypt("toioynnkpheleaigshareconhtomesnlewx", 5);

    assertEquals("theresnoplacelikehomeonasnowynightx", message);
  }

  @Test
  public void shouldDecryptSmallMessage() throws Exception {

    String message = decrypt("ttyohhieneesiaabss", 3);

    assertEquals("thisistheeasyoneab", message);
  }

  @Test(expected = InvalidColumnsForMessageExeception.class)
  public void shouldFailDecryptingMessageWithInvalidColumns() throws Exception {

    decrypt("ttyohhieneesiaabss", 5);
  }
}

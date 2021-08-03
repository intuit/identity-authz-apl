package com.intuit.apl.engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 * Collection of utility methods.
 * 
 * @author bdutt
 * 
 */
public class Util {

  private Util() {
    // Utility class
  }

  private static Logger logger = LoggerFactory.getLogger(Util.class);

  /**
   * Compute percentile.
   * 
   * @param times list of time measurements
   * @param percent percentile
   * @return percentile value
   */
  public static double tp(List<Double> times, int percent) {
    float percentF = (float) percent / 100;
    int index = (int) (percentF * times.size() - 1);
    if (index < 0) {
      index = 0;
    }
    Collections.sort(times);
    return times.get(index);
  }

  /**
   * Read a file from classpath.
   * 
   * @param resourceFilePath file path as a resource in classpath
   * @return contents of the file
   */
  public static Optional<String> readFileFromResource(String resourceFilePath) {
    String content = null;

    try {
      File file = ResourceUtils.getFile("classpath:" + resourceFilePath);
      content = new String(Files.readAllBytes(file.toPath()));
    } catch (IOException e) {
      logger.error("Failed to read the file from resource folder. Error = {}", e);
    }

    return Optional.ofNullable(content);
  }
}

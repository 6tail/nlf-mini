package com.nlf.mini.logger;

import com.nlf.mini.logger.impl.CommonsLoggingLogger;
import com.nlf.mini.logger.impl.DefaultLogger;
import com.nlf.mini.logger.impl.Slf4jLogger;

public class LoggerFactory {

  protected static LoggerType loggerType = LoggerType.DEFAULT;

  private LoggerFactory() {
  }

  static {
    try {
      Class.forName("org.slf4j.LoggerFactory");
      loggerType = LoggerType.SLF4J;
    } catch (Exception ignore) {
    }
    try {
      Class.forName("org.apache.commons.logging.LogFactory");
      loggerType = LoggerType.COMMONS_LOGGING;
    } catch (Exception ignore) {
    }
  }

  public static ILogger getLogger(Class<?> clazz) {
    ILogger logger;
    String name = clazz.getName();
    switch (loggerType) {
      case SLF4J:
        logger = new Slf4jLogger(name);
        break;
      case COMMONS_LOGGING:
        logger = new CommonsLoggingLogger(name);
        break;
      default:
        logger = new DefaultLogger(name);
    }
    return logger;
  }
}

package com.nlf.mini.logger;

import com.nlf.mini.App;
import com.nlf.mini.logger.impl.CommonsLoggingLogger;
import com.nlf.mini.logger.impl.DefaultLogger;
import com.nlf.mini.logger.impl.NoLogger;
import com.nlf.mini.logger.impl.Slf4jLogger;

/**
 * 日志工厂
 *
 * @author 6tail
 */
public class LoggerFactory {

  protected static String[] active = App.getPropertyString("nlf.logger.active", "").replace("\"", "").replace("'", "").split(",");
  protected static LoggerType loggerType;

  private LoggerFactory() {
  }

  static {
    for (String type : active) {
      type = type.trim();
      if (type.equalsIgnoreCase(LoggerType.DEFAULT.name())) {
        loggerType = LoggerType.DEFAULT;
        break;
      } else if (type.equalsIgnoreCase(LoggerType.SLF4J.name())) {
        try {
          Class.forName("org.slf4j.LoggerFactory");
          loggerType = LoggerType.SLF4J;
          break;
        } catch (Exception ignore) {
        }
      } else if (type.equalsIgnoreCase(LoggerType.COMMONS_LOGGING.name())) {
        try {
          Class.forName("org.apache.commons.logging.LogFactory");
          loggerType = LoggerType.COMMONS_LOGGING;
          break;
        } catch (Exception ignore) {
        }
      }
    }
  }

  public static ILogger getLogger(Class<?> clazz) {
    ILogger logger;
    String name = clazz.getName();
    if (null != loggerType) {
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
    } else {
      logger = new NoLogger(name);
    }
    return logger;
  }
}

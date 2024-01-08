package com.nlf.mini.logger.impl;

import com.nlf.mini.logger.AbstractLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * commons-logging日志
 *
 * @author 6tail
 */
public class CommonsLoggingLogger extends AbstractLogger {

  public CommonsLoggingLogger(String name) {
    super(name);
    try {
      Class<?> clazz = Class.forName("org.apache.commons.logging.LogFactory");
      Method m = clazz.getMethod("getLog", String.class);
      logger = m.invoke(null, name);
    } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void debug(String s) {
    if (enable && null != logger) {
      try {
        Method m = logger.getClass().getMethod("debug", Object.class);
        m.invoke(logger, s);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void info(String s) {
    if (enable && null != logger) {
      try {
        Method m = logger.getClass().getMethod("info", Object.class);
        m.invoke(logger, s);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void warn(String s) {
    if (enable && null != logger) {
      try {
        Method m = logger.getClass().getMethod("warn", Object.class);
        m.invoke(logger, s);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
  }

}

package com.nlf.mini.logger.impl;

import com.nlf.mini.logger.AbstractLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * slf4j日志
 *
 * @author 6tail
 */
public class Slf4jLogger extends AbstractLogger {

  public Slf4jLogger(String name) {
    super(name);
    try {
      Class<?> clazz = Class.forName("org.slf4j.LoggerFactory");
      Method m = clazz.getMethod("getLogger", String.class);
      logger = m.invoke(null, name);
    } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}

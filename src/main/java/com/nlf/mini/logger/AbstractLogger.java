package com.nlf.mini.logger;

import com.nlf.mini.App;
import com.nlf.mini.core.IProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 抽象日志
 *
 * @author 6tail
 */
public abstract class AbstractLogger implements ILogger {
  protected String name;
  protected boolean enable;
  protected Object logger;
  protected IProxy proxy;

  protected AbstractLogger(String name) {
    this.name = name;
    enable = App.getPropertyBoolean("nlf.logger.enable", false);
    proxy = App.getProxy();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void debug(String s) {
    if (enable && null != logger) {
      try {
        Method m = logger.getClass().getMethod("debug", String.class);
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
        Method m = logger.getClass().getMethod("info", String.class);
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
        Method m = logger.getClass().getMethod("warn", String.class);
        m.invoke(logger, s);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
  }

}

package com.nlf.mini.logger.impl;

import com.nlf.mini.App;
import com.nlf.mini.logger.AbstractLogger;
import com.nlf.mini.util.DateUtil;


/**
 * 默认日志
 *
 * @author 6tail
 */
public class DefaultLogger extends AbstractLogger {

  public static final String KLASS = DefaultLogger.class.getName();

  public DefaultLogger(String name) {
    super(name);
  }

  private StackTraceElement findStackTrace() {
    int index = 0;
    StackTraceElement[] sts = Thread.currentThread().getStackTrace();
    for (StackTraceElement st : sts) {
      if (KLASS.equals(st.getClassName())) {
        index += 2;
        break;
      } else {
        index++;
      }
    }
    return sts[index];
  }

  @Override
  public void debug(String s) {
    if (enable) {
      System.out.println(App.getProperty("nlf.log.default.debug", DateUtil.ymdhms(DateUtil.now()), name, findStackTrace(), s));
    }
  }

  @Override
  public void info(String s) {
    if (enable) {
      System.out.println(App.getProperty("nlf.log.default.info", DateUtil.ymdhms(DateUtil.now()), name, findStackTrace(), s));
    }
  }

  @Override
  public void warn(String s) {
    if (enable) {
      System.out.println(App.getProperty("nlf.log.default.warn", DateUtil.ymdhms(DateUtil.now()), name, findStackTrace(), s));
    }
  }
}

package com.nlf.mini.logger.impl;

import com.nlf.mini.logger.AbstractLogger;


/**
 * 无日志
 *
 * @author 6tail
 */
public class NoLogger extends AbstractLogger {

  public NoLogger(String name) {
    super(name);
  }

  @Override
  public void debug(String s) {
  }

  @Override
  public void info(String s) {
  }

  @Override
  public void warn(String s) {
  }
}

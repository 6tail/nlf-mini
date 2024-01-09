package com.nlf.mini.logger;

/**
 * 日志接口
 *
 * @author 6tail
 */
public interface ILogger {
  String getName();
  void debug(String s);
  void info(String s);
  void warn(String s);
}

package com.nlf.mini.logger;

public interface ILogger {
  String getName();
  void debug(String s);
  void info(String s);
  void warn(String s);
}

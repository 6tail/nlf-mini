package com.nlf.mini.core;

/**
 * 扫描器
 *
 * @author 6tail
 */
public interface IScanner {
  /**
   * 开始扫描
   *
   * @return 扫描器
   */
  IScanner start();

  /**
   * 设置调用者路径
   *
   * @param path 路径
   * @return 扫描器
   */
  IScanner setCaller(String path);
}
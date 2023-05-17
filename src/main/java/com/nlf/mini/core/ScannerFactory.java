package com.nlf.mini.core;

import com.nlf.mini.core.impl.DefaultScanner;

/**
 * 扫描器工厂
 * 
 * @author 6tail
 *
 */
public class ScannerFactory {
  /** 是否已经触发过扫描 */
  private static boolean scanned;
  /** 扫描器 */
  private static IScanner scanner = new DefaultScanner();

  /**
   * 自定义扫描器，必须在触发扫描之前设置才有效
   * 
   * @param scanner 扫描器
   */
  public static void setScanner(IScanner scanner){
    if(scanned){
      return;
    }
    ScannerFactory.scanner = scanner;
  }

  /**
   * 获取扫描器
   * @return 扫描器
   */
  public static IScanner getScanner(){
    return scanner;
  }

  /**
   * 开始扫描
   * 
   */
  public static void startScan(){
    if(scanned){
      return;
    }
    scanned = true;
    scanner.start();
  }
}
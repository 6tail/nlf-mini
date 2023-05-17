package com.nlf.mini.core;

/**
 * 扫描接口抽象
 *
 * @author 6tail
 */
public abstract class AbstractScanner implements IScanner {
  /**
   * 调用者路径
   */
  protected String caller;

  public IScanner setCaller(String caller) {
    this.caller = caller;
    return this;
  }

}
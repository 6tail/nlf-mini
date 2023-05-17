package com.nlf.mini.resource.i18n;

import com.nlf.mini.resource.Resource;

/**
 * 资源：i18n
 *
 * @author 6tail
 */
public class I18nResource extends Resource {
  /**
   * 名称
   */
  private String name;

  public I18nResource() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return super.toString() + " name=" + name;
  }
}
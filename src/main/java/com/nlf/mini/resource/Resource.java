package com.nlf.mini.resource;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 资源
 *
 * @author 6tail
 */
public class Resource {
  /**
   * 是否位于jar中
   */
  protected boolean inJar;
  /**
   * 所在路径
   */
  protected String root;
  /**
   * 文件名
   */
  protected String fileName;

  public boolean isInJar() {
    return inJar;
  }

  public void setInJar(boolean inJar) {
    this.inJar = inJar;
  }

  public String getRoot() {
    return root;
  }

  public void setRoot(String root) {
    this.root = root;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * 获取文件最后修改时间
   *
   * @return 文件最后修改时间
   * @throws IOException IOException
   */
  public long lastModified() throws IOException {
    if (inJar) {
      ZipFile zip = new ZipFile(root);
      ZipEntry en = zip.getEntry(fileName);
      return en.getTime();
    } else {
      return new File(root + File.separator + fileName).lastModified();
    }
  }

  @Override
  public String toString() {
    return "inJar=" + inJar + " root=" + root + " fileName=" + fileName;
  }
}
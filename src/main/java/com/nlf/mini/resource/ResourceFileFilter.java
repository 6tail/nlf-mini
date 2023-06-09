package com.nlf.mini.resource;

import java.io.File;
import java.io.FileFilter;

/**
 * resource文件过滤器
 * 
 * @author 6tail
 * 
 */
public class ResourceFileFilter implements FileFilter{

  public boolean accept(File f){
    if(f.isDirectory()){
      return true;
    }
    String name = f.getName();
    return name.endsWith(".properties") || name.endsWith(".class");
  }
}
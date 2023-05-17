package com.nlf.mini.core.impl;

import com.nlf.mini.App;
import com.nlf.mini.core.AbstractScanner;
import com.nlf.mini.core.IScanner;
import com.nlf.mini.exception.NlfException;
import com.nlf.mini.plugin.IPlugin;
import com.nlf.mini.resource.ResourceFileFilter;
import com.nlf.mini.resource.i18n.I18nResource;
import com.nlf.mini.resource.i18n.comparator.I18nComparator;
import com.nlf.mini.util.FileUtil;
import com.nlf.mini.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 默认扫描器
 *
 * @author 6tail
 */
public class DefaultScanner extends AbstractScanner {
  public static final String CHARSET = "utf-8";
  public static final String SUF_JAR = ".jar";
  public static final String SUF_PPT = ".properties";
  public static final String SUF_CLASS = ".class";
  private static final List<String> ignoreClasses = new ArrayList<String>() {
    {
      add(DefaultScanner.class.getName());
      add(IPlugin.class.getName());
      add(App.class.getName());
    }
  };
  private static final String PLUGIN_INTERFACE_NAME = IPlugin.class.getName().replace(".", "/");
  private static final String PLUGIN_APPLY_NAME = "onApply";
  /**
   * resource文件过滤器
   */
  protected ResourceFileFilter resourceFilter = new ResourceFileFilter();
  /**
   * i18n比较器
   */
  protected I18nComparator i18nComparator = new I18nComparator();

  /**
   * jar路径
   */
  protected Set<String> jars = new HashSet<>();
  /**
   * 字节码路径
   */
  protected Set<String> classes = new HashSet<>();

  /**
   * 筛选路径
   *
   * @param paths 路径
   * @return 筛选后的路径
   */
  protected Set<String> filterPath(String... paths) {
    Set<String> l = new HashSet<>();
    for (String p : paths) {
      if (null == p) {
        continue;
      }
      p = p.trim();
      if (p.length() < 1) {
        continue;
      }
      File f = new File(p);
      if (!f.exists()) {
        continue;
      }
      if (f.isDirectory()) {
        String path = f.getAbsolutePath();
        if (path.endsWith(File.separator + ".")) {
          path = path.substring(0, path.lastIndexOf(File.separator));
        }
        l.add(path);
      } else if (f.getName().endsWith(SUF_JAR)) {
        l.add(f.getAbsolutePath());
      }
    }
    return l;
  }

  /**
   * 寻找classpath们
   *
   * @return classpath们
   */
  protected Set<String> findFromClassPath() {
    return filterPath(System.getProperty("java.class.path").split(File.pathSeparator));
  }

  /**
   * 寻找NLF框架调用者所在路径
   *
   * @throws ClassNotFoundException       ClassNotFoundException
   * @throws UnsupportedEncodingException UnsupportedEncodingException
   */
  protected void findCallerPath() throws ClassNotFoundException, UnsupportedEncodingException {
    if (null == App.caller) {
      if (null == caller) {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        //调用框架的类
        String callerClassName = null;
        Class<?> callerClass = null;

        for (int i = sts.length - 1; i > -1; i--) {
          StackTraceElement t = sts[i];
          String className = t.getClassName();
          if (className.startsWith(App.PACKAGE)) {
            callerClass = Class.forName(callerClassName);
            if (null != callerClass.getClassLoader()) {
              break;
            }
          }
          callerClassName = className;
        }
        if (null != callerClass) {
          String callerPath = callerClass.getProtectionDomain().getCodeSource().getLocation().getPath();
          App.caller = new File(URLDecoder.decode(callerPath, CHARSET)).getAbsolutePath();
        }
      } else {
        App.caller = caller;
      }
    }
  }

  protected void findFramePath() throws UnsupportedEncodingException {
    if (null == App.frame) {
      App.frame = new File(URLDecoder.decode(IScanner.class.getProtectionDomain().getCodeSource().getLocation().getPath(), CHARSET)).getAbsolutePath();
    }
  }

  /**
   * 寻找框架调用者(如果是jar)引用的Class-Path
   *
   * @return classpath们
   * @throws IOException IO异常
   */
  protected Set<String> findFromCallerClassPath() throws IOException {
    Set<String> classPaths = new HashSet<>();
    String callerPath = App.caller;
    if (callerPath.endsWith(SUF_JAR)) {
      JarFile jar = null;
      try {
        jar = new JarFile(callerPath);
        Manifest mf = jar.getManifest();
        if (null != mf) {
          Attributes attrs = mf.getMainAttributes();
          String classPath = attrs.getValue("Class-Path");
          if (null != classPath) {
            String[] cps = classPath.split(" ");
            classPaths.addAll(filterPath(cps));
          }
        }
      } finally {
        IOUtil.closeQuietly(jar);
      }
    }
    return classPaths;
  }

  public IScanner start() {
    try {
      findCallerPath();
      findAppRoot();
      findFramePath();
      System.out.println("App.caller         = " + App.caller);
      System.out.println("App.root           = " + App.root);
      System.out.println("App.frame          = " + App.frame);
      Set<String> paths = new HashSet<>();
      paths.add(App.caller);
      paths.add(App.root);
      paths.add(App.frame);
      paths.addAll(findFromClassPath());
      paths.addAll(findFromCallerClassPath());

      for (String p : paths) {
        if (p.endsWith(SUF_JAR)) {
          jars.add(p);
        } else {
          classes.add(p);
        }
      }
      scan();
      buildI18n();
    } catch (Exception e) {
      throw new NlfException(e);
    }
    return this;
  }

  protected void buildI18n() {
    if (App.I18N.size() > 1) {
      List<String> l = new ArrayList<>(App.I18N);
      l.sort(i18nComparator);
      App.I18N.clear();
      App.I18N.addAll(l);
    }
  }

  protected void scanClasses(File file, String root) {
    if (file.isDirectory()) {
      File[] fs = file.listFiles(resourceFilter);
      if (null != fs) {
        for (File f : fs) {
          scanClasses(f, root);
        }
      }
      return;
    }
    String fileName = file.getAbsolutePath().replace(root, "");
    if (fileName.startsWith(File.separator)) {
      fileName = fileName.substring(File.separator.length());
    }
    if (fileName.endsWith(SUF_PPT)) {
      String name = fileName.substring(0, fileName.lastIndexOf(".")).replace(File.separator, ".");
      for (Locale locale : Locale.getAvailableLocales()) {
        String tag = "_" + locale.getLanguage();
        if (name.endsWith(tag)) {
          name = name.substring(0, name.lastIndexOf(tag));
          break;
        }
      }
      I18nResource ir = new I18nResource();
      ir.setRoot(root);
      ir.setInJar(false);
      ir.setName(name);
      ir.setFileName(fileName);
      App.I18N_RESOURCE.add(ir);
      App.I18N.add(name);
    } else if (fileName.endsWith(SUF_CLASS) && !fileName.contains("$")) {
      String s = null;
      try {
        s = FileUtil.readAsText(file);
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (null != s && s.contains(PLUGIN_INTERFACE_NAME) && s.contains(PLUGIN_APPLY_NAME)) {
        String name = fileName.substring(0, fileName.lastIndexOf(".")).replace(File.separator, ".");
        if (!ignoreClasses.contains(name)) {
          App.plugins.add(name);
        }
      }
    }
  }

  protected void scanJar(File jarFile) throws IOException {
    String root = jarFile.getAbsolutePath();
    System.out.println("[v] " + root);
    ZipFile zip = null;
    try {
      zip = new ZipFile(jarFile);
      Enumeration<?> entries = zip.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        String fileName = entry.getName();
        if (fileName.endsWith(SUF_PPT)) {
          String name = fileName.substring(0, fileName.lastIndexOf(".")).replace("/", ".");
          for (Locale locale : Locale.getAvailableLocales()) {
            String tag = "_" + locale;
            if (name.endsWith(tag)) {
              name = name.substring(0, name.lastIndexOf(tag));
              break;
            }
          }
          I18nResource ir = new I18nResource();
          ir.setRoot(root);
          ir.setInJar(true);
          ir.setName(name);
          ir.setFileName(fileName);
          App.I18N_RESOURCE.add(ir);
          App.I18N.add(name);
        } else if (fileName.endsWith(SUF_CLASS) && !fileName.contains("$")) {
          String s = null;
          try {
            s = FileUtil.readAsText(zip.getInputStream(entry), CHARSET);
          } catch (IOException e) {
            e.printStackTrace();
          }
          if (null != s && s.contains(PLUGIN_INTERFACE_NAME) && s.contains(PLUGIN_APPLY_NAME)) {
            String name = fileName.substring(0, fileName.lastIndexOf(".")).replace(File.separator, ".");
            if (!ignoreClasses.contains(name)) {
              App.plugins.add(name);
            }
          }
        }
      }
    } finally {
      IOUtil.closeQuietly(zip);
    }
  }

  protected void scan() throws IOException {
    App.I18N_RESOURCE.clear();
    for (String p : classes) {
      System.out.println("[v] " + p);
      App.DIRECTORIES.add(p);
      scanClasses(new File(p), p);
    }
    for (String p : jars) {
      scanJar(new File(p));
    }
  }

  /**
   * 寻找应用根目录
   */
  protected void findAppRoot() {
    if (null == App.root) {
      String callerPath = App.caller;
      App.root = callerPath.endsWith(SUF_JAR) ? new File(callerPath).getParentFile().getAbsolutePath() : callerPath;
    }
  }
}

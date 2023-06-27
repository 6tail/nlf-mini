package com.nlf.mini;

import com.nlf.mini.core.IProxy;
import com.nlf.mini.core.ScannerFactory;
import com.nlf.mini.core.impl.DefaultProxy;
import com.nlf.mini.dao.paging.impl.DefaultPagingRender;
import com.nlf.mini.dao.setting.impl.PropertiesDbSettingManager;
import com.nlf.mini.extend.dao.sql.dbType.common.*;
import com.nlf.mini.extend.dao.sql.dbType.mysql.MysqlDao;
import com.nlf.mini.extend.dao.sql.dbType.mysql.MysqlSelecter;
import com.nlf.mini.extend.dao.sql.dbType.mysql.MysqlTemplate;
import com.nlf.mini.extend.dao.sql.dbType.oracle.OracleDao;
import com.nlf.mini.extend.dao.sql.dbType.oracle.OracleSelecter;
import com.nlf.mini.extend.dao.sql.dbType.oracle.OracleTemplate;
import com.nlf.mini.extend.dao.sql.dbType.sqlserver.SqlserverDao;
import com.nlf.mini.extend.dao.sql.dbType.sqlserver.SqlserverSelecter;
import com.nlf.mini.extend.dao.sql.dbType.sqlserver.SqlserverTemplate;
import com.nlf.mini.extend.dao.sql.type.jdbc.JdbcConnectionProvider;
import com.nlf.mini.extend.dao.sql.type.jdbc.JdbcSettingProvider;
import com.nlf.mini.extend.serialize.obj.impl.DefaultObjParser;
import com.nlf.mini.extend.serialize.obj.impl.DefaultObjWrapper;
import com.nlf.mini.extend.serialize.xml.impl.DefaultXmlParser;
import com.nlf.mini.extend.serialize.xml.impl.DefaultXmlWrapper;
import com.nlf.mini.plugin.IPlugin;
import com.nlf.mini.resource.i18n.I18nResource;
import com.nlf.mini.resource.klass.comparator.ClassComparator;
import com.nlf.mini.serialize.json.impl.DefaultJsonParser;
import com.nlf.mini.serialize.json.impl.DefaultJsonWrapper;

import java.text.MessageFormat;
import java.util.*;

/**
 * 应用信息
 *
 * @author 6tail
 */
public class App {
  public static Locale locale = Locale.getDefault();
  /**
   * 应用根目录
   */
  public static String root;
  /**
   * 调用者所在路径，可能是目录，也可能是jar文件
   */
  public static String caller;
  /**
   * 框架所在路径，可能是目录，也可能是jar文件
   */
  public static String frame;

  /**
   * 环境配置
   */
  public static String profile;

  private static final String OBJECT_CLASS_NAME = Object.class.getName();

  /**
   * 代理
   */
  private static volatile IProxy proxy;

  /**
   * 扫描到的目录
   */
  public static final Set<String> DIRECTORIES = new LinkedHashSet<>();
  /**
   * i18n文件名缓存
   */
  public static final Set<String> I18N = new LinkedHashSet<>();
  /**
   * 框架包名
   */
  public static final String PACKAGE = App.class.getPackage().getName();
  /**
   * 所有扫描到的i18n缓存
   */
  public static final List<I18nResource> I18N_RESOURCE = new ArrayList<>();
  /**
   * 类比较器
   */
  protected static ClassComparator classComparator = new ClassComparator();

  /**
   * 接口实现类列表缓存
   */
  public static final Map<String, List<String>> INTERFACE_IMPLEMENTS = new HashMap<>();

  public static final Set<String> plugins = new HashSet<>();

  static {
    ScannerFactory.startScan();
    addImplement(DefaultProxy.class);
    addImplement(DefaultJsonParser.class);
    addImplement(DefaultJsonWrapper.class);
    addImplement(DefaultXmlParser.class);
    addImplement(DefaultXmlWrapper.class);
    addImplement(DefaultObjParser.class);
    addImplement(DefaultObjWrapper.class);
    addImplement(DefaultPagingRender.class);
    addImplement(PropertiesDbSettingManager.class);
    addImplement(JdbcSettingProvider.class);
    addImplement(JdbcConnectionProvider.class);
    addImplement(MysqlDao.class);
    addImplement(OracleDao.class);
    addImplement(SqlserverDao.class);
    addImplement(MysqlSelecter.class);
    addImplement(OracleSelecter.class);
    addImplement(SqlserverSelecter.class);
    addImplement(ASqlSelecter.class);
    addImplement(ASqlInserter.class);
    addImplement(ASqlUpdater.class);
    addImplement(ASqlDeleter.class);
    addImplement(ASqlJoiner.class);
    addImplement(MysqlTemplate.class);
    addImplement(OracleTemplate.class);
    addImplement(SqlserverTemplate.class);
    addImplement(ASqlTemplate.class);
    applyPlugins();
  }

  protected App() {
  }

  protected static void applyPlugins() {
    for (String c : plugins) {
      IPlugin p = null;
      try {
        p = proxy.newInstance(c);
      } catch (Exception ignore) {
      }
      if (null != p ) {
        p.onApply();
      }
    }
  }

  protected static Set<String> getInterfaces(Class<?> klass) {
    Set<String> interfaces = new HashSet<>();
    for (Class<?> i : klass.getInterfaces()) {
      interfaces.add(i.getName());
      interfaces.addAll(getInterfaces(i));
    }
    Class<?> superClass = klass.getSuperclass();
    if (null != superClass && !OBJECT_CLASS_NAME.equals(superClass.getName())) {
      interfaces.addAll(getInterfaces(superClass));
    }
    return interfaces;
  }

  public static void addImplement(Class<?> klass) {
    String className = klass.getName();
    String proxyName = IProxy.class.getName();
    for (String interfaceName : getInterfaces(klass)) {
      List<String> impls = INTERFACE_IMPLEMENTS.computeIfAbsent(interfaceName, k -> new ArrayList<>());
      if (!impls.contains(className)) {
        impls.add(className);
      }
      impls.sort(classComparator);
      if (proxyName.equals(interfaceName)) {
        proxy = new DefaultProxy().newInstance(proxyName);
      }
    }
  }

  /**
   * 获取properties值，默认使用请求客户端的locale，如果未设置则使用默认locale
   *
   * @param key    key
   * @param params 传入参数
   * @return 值
   */
  public static String getProperty(String key, Object... params) {
    return getProperty(locale, key, params);
  }

  /**
   * 获取properties值
   *
   * @param locale locale
   * @param key    key
   * @param params 传入参数
   * @return 值
   */
  public static String getProperty(Locale locale, String key, Object... params) {
    String value = null;
    String baseName = null;
    for (String i18n : I18N) {
      try {
        value = ResourceBundle.getBundle(i18n, locale).getString(key);
        baseName = i18n;
        break;
      } catch (Exception ignored) {
      }
    }
    if (null != profile && null != baseName) {
      try {
        value = ResourceBundle.getBundle(baseName + "-" + profile, locale).getString(key);
      } catch (Exception ignored) {
      }
    }
    return null == value ? null : params.length > 0 ? MessageFormat.format(value, params) : value;
  }

  /**
   * 获取properties的short值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public static short getPropertyShort(String key, short defaultValue) {
    try {
      return Short.parseShort(getProperty(key));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取properties的int值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public static int getPropertyInt(String key, int defaultValue) {
    try {
      return Integer.parseInt(getProperty(key));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取properties的long值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public static long getPropertyLong(String key, long defaultValue) {
    try {
      return Long.parseLong(getProperty(key));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取properties的float值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public static float getPropertyFloat(String key, float defaultValue) {
    try {
      return Float.parseFloat(getProperty(key));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取properties的double值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public static double getPropertyDouble(String key, double defaultValue) {
    try {
      return Double.parseDouble(getProperty(key));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取properties的boolean值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public static boolean getPropertyBoolean(String key, boolean defaultValue) {
    try {
      String value = getProperty(key);
      return null == value ? defaultValue : Boolean.parseBoolean(value);
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取properties的字符串值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public static String getPropertyString(String key, String defaultValue) {
    try {
      String ret = getProperty(key);
      return null == ret ? defaultValue : ret;
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取properties的字符串值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param locale       locale
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public static String getPropertyString(Locale locale, String key, String defaultValue) {
    try {
      String ret = getProperty(locale, key);
      return null == ret ? defaultValue : ret;
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取代理接口
   *
   * @return 代理接口
   */
  public static IProxy getProxy() {
    return proxy;
  }

  /**
   * 获取类或接口的实现类列表的交集（如果是接口，则它的所有实现类参与交集；如果不是接口，它自身参与交集）。
   *
   * @param interfaceOrClassNames 完整类或接口名
   * @return 实现类列表
   */
  public static List<String> getImplements(String... interfaceOrClassNames) {
    List<String> l = null;
    for (String name : interfaceOrClassNames) {
      List<String> sub = new ArrayList<>();
      List<String> impls = INTERFACE_IMPLEMENTS.get(name);
      if (null != impls) {
        sub.addAll(impls);
      } else {
        sub.add(name);
      }
      if (null == l) {
        l = sub;
      } else {
        l.retainAll(sub);
      }
    }
    return l;
  }

  /**
   * 获取类或接口的实现类列表的交集（如果是接口，则它的所有实现类参与交集；如果不是接口，它自身参与交集）。
   *
   * @param interfaceOrClasses 类或接口们
   * @return 实现类列表
   */
  public static List<String> getImplements(Class<?>... interfaceOrClasses) {
    List<String> l = new ArrayList<>();
    for (Class<?> c : interfaceOrClasses) {
      l.add(c.getName());
    }
    String[] arr = new String[l.size()];
    l.toArray(arr);
    return getImplements(arr);
  }

  /**
   * 从类或接口的实现类列表的交集（如果是接口，则它的所有实现类参与交集；如果不是接口，它自身参与交集）中取得一个默认实现类，默认实现类的挑选规则由扫描器指定。
   *
   * @param interfaceOrClassNames 完整类或接口名
   * @return 默认实现类
   */
  public static String getImplement(String... interfaceOrClassNames) {
    List<String> impls = getImplements(interfaceOrClassNames);
    return impls.size() < 1 ? null : impls.get(0);
  }

  /**
   * 从类或接口的实现类列表的交集（如果是接口，则它的所有实现类参与交集；如果不是接口，它自身参与交集）中取得一个默认实现类，默认实现类的挑选规则由扫描器指定。
   *
   * @param interfaceOrClasses 类或接口们
   * @return 默认实现类
   */
  public static String getImplement(Class<?>... interfaceOrClasses) {
    List<String> impls = getImplements(interfaceOrClasses);
    return impls.size() < 1 ? null : impls.get(0);
  }
}

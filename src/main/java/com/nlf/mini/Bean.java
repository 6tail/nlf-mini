package com.nlf.mini;

import com.nlf.mini.core.AbstractBean;
import com.nlf.mini.util.StringUtil;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 通用对象封装，支持链式调用
 *
 * @author 6tail
 */
public class Bean extends AbstractBean implements Map<String, Object>, Cloneable {
  private static final long serialVersionUID = 1;
  /**
   * 键值对
   */
  protected Map<String, Object> values = new LinkedHashMap<>();

  public Bean() {
  }

  public Bean(String key, Object value) {
    values.put(key, value);
  }

  /**
   * 是否存在指定键
   *
   * @param key 键
   * @return true/false 存在/不存在
   */
  public boolean containsKey(String key) {
    return values.containsKey(key);
  }

  /**
   * 获取值
   *
   * @param key 键
   * @return 值
   */
  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    return (T) values.get(key);
  }

  /**
   * 获取Object值，如果为null,返回默认值
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  @SuppressWarnings("unchecked")
  public <E> E get(String key, E defaultValue) {
    Object o = values.get(key);
    return null == o ? defaultValue : (E) o;
  }

  /**
   * 转换为指定类的实例
   *
   * @param klass 指定类
   * @return 实例
   */
  @SuppressWarnings("unchecked")
  public <E> E toObject(Class<E> klass) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
    if (Bean.class.equals(klass) || Map.class.equals(klass)) {
      return (E) this;
    }
    E o = App.getProxy().newInstance(klass.getName());
    BeanInfo info = Introspector.getBeanInfo(o.getClass(), Object.class);
    PropertyDescriptor[] props = info.getPropertyDescriptors();
    for (PropertyDescriptor p : props) {
      Method setterMethod = p.getWriteMethod();
      if (null == setterMethod) {
        continue;
      }
      Class<?> propType = p.getPropertyType();
      Type paramType = setterMethod.getGenericParameterTypes()[0];
      String name = p.getName();
      for (String key : keySet()) {
        if (!key.equalsIgnoreCase(name)) {
          continue;
        }
        setterMethod.invoke(o, convert(get(key), propType, paramType));
        break;
      }
    }
    return o;
  }

  /**
   * 设置值
   *
   * @param key   键
   * @param value 值
   * @return 自己
   */
  public Bean set(String key, Object value) {
    values.put(key, value);
    return this;
  }

  /**
   * 当条件满足时设置值
   *
   * @param key       键
   * @param value     值
   * @param condition 条件
   * @return 自己
   */
  public Bean setIf(String key, Object value, boolean condition) {
    if (condition) {
      values.put(key, value);
    }
    return this;
  }

  /**
   * 移除指定键值
   *
   * @param key 键
   * @return 自己
   */
  public Bean remove(String key) {
    values.remove(key);
    return this;
  }

  /**
   * 获取键的集合
   *
   * @return 键集合
   */
  public Set<String> keySet() {
    return values.keySet();
  }

  @Override
  public String toString() {
    return values.toString();
  }

  @Override
  public Bean clone() {
    Bean o = new Bean();
    for (Entry<String, Object> entry : values.entrySet()) {
      Object v = entry.getValue();
      if (null == v) {
        o.set(entry.getKey(), null);
      } else if (v instanceof Bean) {
        o.set(entry.getKey(), ((Bean)v).clone());
      } else {
        o.set(entry.getKey(), v);
      }
    }
    return o;
  }

  /**
   * 获取值
   * <p>
   * 通过路径获取值，例如：{a:{b:{c:1}}}，使用a.b.c可得到1；{a:[{b:1}]}，使用a[0].b可得到1
   *
   * @param path 键的路径，例如：a.b.c或a.b[0].c
   * @return 值
   */
  @SuppressWarnings("unchecked")
  public <T> T select(String path) {
    if (null == path) {
      return null;
    }
    List<String> paths = StringUtil.list(path, "\\.");
    int depth = paths.size();
    if (depth < 1) {
      return null;
    }
    String leaf = paths.remove(depth - 1);
    Bean node = this;
    for (String p : paths) {
      if (p.contains("[") && p.contains("]")) {
        String key = StringUtil.left(p, "[");
        int index = Integer.parseInt(StringUtil.between(p, "[", "]"));
        List<Bean> nodes = node.getList(key);
        node = nodes.get(index);
      } else {
        List<Bean> nodes = node.getList(p);
        node = nodes.get(0);
      }
    }
    if (leaf.contains("[") && leaf.contains("]")) {
      String key = StringUtil.left(leaf, "[");
      int index = Integer.parseInt(StringUtil.between(leaf, "[", "]"));
      return (T) (node.getList(key).get(index));
    } else {
      return node.get(leaf);
    }
  }

  /**
   * 获取Bean值，一般用于链式调用，可能返回null
   *
   * @param key 键
   * @return Bean，可能为null
   */
  public Bean getBean(String key) {
    return get(key);
  }

  /**
   * 获取Bean值
   *
   * @param path 键的路径，例如：a.b.c或a.b[0].c
   * @return Bean，可能为null
   */
  public Bean selectBean(String path) {
    return select(path);
  }

  /**
   * 获取Bean值，一般用于链式调用，如果获取失败，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return Bean
   */
  public Bean getBean(String key, Bean defaultValue) {
    Bean v = null;
    try {
      v = get(key);
    } catch (Exception ignored) {
    }
    return null == v ? defaultValue : v;
  }

  /**
   * 获取Bean值，如果获取失败，返回默认值，不抛出异常
   *
   * @param path         键的路径，例如：a.b.c或a.b[0].c
   * @param defaultValue 默认值
   * @return Bean
   */
  public Bean selectBean(String path, Bean defaultValue) {
    Bean v = null;
    try {
      v = selectBean(path);
    } catch (Exception ignored) {
    }
    return null == v ? defaultValue : v;
  }

  protected byte[] convertBytes(Object v, byte[] defaultValue) {
    try {
      if (v instanceof byte[]) {
        return (byte[]) v;
      } else if (v instanceof String) {
        return (byte[]) convertBytes(v);
      }
    } catch (Exception ignore) {
    }
    return defaultValue;
  }

  /**
   * 获取byte[]值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public byte[] getBytes(String key, byte[] defaultValue) {
    return convertBytes(values.get(key), defaultValue);
  }

  /**
   * 根据路径获取short值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param path         键的路径，例如：a.b.c或a.b[0].c
   * @param defaultValue 默认值
   * @return 值
   */
  public byte[] selectBytes(String path, byte[] defaultValue) {
    return convertBytes(select(path), defaultValue);
  }

  /**
   * 获取short值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public short getShort(String key, short defaultValue) {
    try {
      return Short.parseShort(String.valueOf(values.get(key)));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 根据路径获取short值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param path         键的路径，例如：a.b.c或a.b[0].c
   * @param defaultValue 默认值
   * @return 值
   */
  public short selectShort(String path, short defaultValue) {
    try {
      return Short.parseShort(String.valueOf(select(path)));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取int值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public int getInt(String key, int defaultValue) {
    try {
      return Integer.parseInt(String.valueOf(values.get(key)));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 根据路径获取int值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param path         键的路径，例如：a.b.c或a.b[0].c
   * @param defaultValue 默认值
   * @return 值
   */
  public int selectInt(String path, int defaultValue) {
    try {
      return Integer.parseInt(String.valueOf(select(path)));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取long值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public long getLong(String key, long defaultValue) {
    try {
      return Long.parseLong(String.valueOf(values.get(key)));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 根据路径获取long值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param path         键的路径，例如：a.b.c或a.b[0].c
   * @param defaultValue 默认值
   * @return 值
   */
  public long selectLong(String path, long defaultValue) {
    try {
      return Long.parseLong(String.valueOf(select(path)));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取double值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public double getDouble(String key, double defaultValue) {
    try {
      return Double.parseDouble(String.valueOf(values.get(key)));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 根据路径获取double值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param path         键的路径，例如：a.b.c或a.b[0].c
   * @param defaultValue 默认值
   * @return 值
   */
  public double selectDouble(String path, double defaultValue) {
    try {
      return Double.parseDouble(String.valueOf(select(path)));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取float值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public float getFloat(String key, float defaultValue) {
    try {
      return Float.parseFloat(String.valueOf(values.get(key)));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 根据路径获取float值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param path         键的路径，例如：a.b.c或a.b[0].c
   * @param defaultValue 默认值
   * @return 值
   */
  public float selectFloat(String path, float defaultValue) {
    try {
      return Float.parseFloat(String.valueOf(select(path)));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取boolean值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public boolean getBoolean(String key, boolean defaultValue) {
    try {
      return Boolean.parseBoolean(String.valueOf(values.get(key)));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 根据路径获取boolean值，如果获取不到或出错，返回默认值，不抛出异常
   *
   * @param path         键的路径，例如：a.b.c或a.b[0].c
   * @param defaultValue 默认值
   * @return 值
   */
  public boolean selectBoolean(String path, boolean defaultValue) {
    try {
      return Boolean.parseBoolean(String.valueOf(select(path)));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 获取String值，如果为null,返回null
   *
   * @param key 键
   * @return 值
   */
  public String getString(String key) {
    return getString(key, null);
  }

  /**
   * 根据路径获取String值，如果为null,返回null
   *
   * @param path 键的路径，例如：a.b.c或a.b[0].c
   * @return 值
   */
  public String selectString(String path) {
    return selectString(path, null);
  }

  /**
   * 获取String值，如果为null,返回默认值
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public String getString(String key, String defaultValue) {
    Object o = values.get(key);
    return null == o ? defaultValue : o.toString();
  }

  /**
   * 获取String值，如果为null,返回默认值
   *
   * @param path         键的路径，例如：a.b.c或a.b[0].c
   * @param defaultValue 默认值
   * @return 值
   */
  public String selectString(String path, String defaultValue) {
    Object o = select(path);
    return null == o ? defaultValue : o.toString();
  }

  /**
   * 强制获取List，即使是非Collection，也会强制返回只有1个元素的List。如果不存在该键，返回0个元素的List。
   *
   * @param key 键
   * @return List
   */
  @SuppressWarnings("unchecked")
  public <T> List<T> getList(String key) {
    List<T> l = new ArrayList<>();
    Object o = values.get(key);
    if (null == o) {
      return l;
    }
    if (o instanceof Collection) {
      l.addAll((Collection<T>) o);
    } else {
      l.add((T) o);
    }
    return l;
  }

  /**
   * 强制获取List，即使是非Collection，也会强制返回只有1个元素的List。如果不存在该键，返回0个元素的List。
   *
   * @param path 键的路径，例如：a.b.c或a.b[0].c
   * @return List
   */
  @SuppressWarnings("unchecked")
  public <T> List<T> selectList(String path) {
    List<T> l = new ArrayList<>();
    Object o = select(path);
    if (null == o) {
      return l;
    }
    if (o instanceof Collection) {
      l.addAll((Collection<T>) o);
    } else {
      l.add((T) o);
    }
    return l;
  }

  @Deprecated
  public boolean containsKey(Object key) {
    return containsKey(key + "");
  }

  public boolean containsValue(Object value) {
    return values.containsValue(value);
  }

  public Set<Entry<String, Object>> entrySet() {
    return values.entrySet();
  }

  @Deprecated
  public Object get(Object key) {
    return values.get(key);
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  @Deprecated
  public Object put(String key, Object value) {
    return values.put(key, value);
  }

  public void putAll(Map<? extends String, ?> map) {
    values.putAll(map);
  }

  @Deprecated
  public Object remove(Object key) {
    return values.remove(key);
  }

  public int size() {
    return values.size();
  }

  public Collection<Object> values() {
    return values.values();
  }

  public void clear() {
    values.clear();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Bean)) {
      return false;
    }
    return Objects.equals(values, ((Bean) o).values);
  }

  @Override
  public int hashCode() {
    return values.hashCode();
  }

}

package com.nlf.mini.serialize.json.impl;

import com.nlf.mini.dao.paging.IPageable;
import com.nlf.mini.serialize.AbstractWrapper;
import com.nlf.mini.util.Base64Util;
import com.nlf.mini.util.DateUtil;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 默认json包装器
 *
 * @author 6tail
 */
public class DefaultJsonWrapper extends AbstractWrapper {
  protected String quote = "\"";

  protected String wrapNumber(Object o) {
    return o + "";
  }

  protected String wrapBool(Object o) {
    return o + "";
  }

  protected String wrapString(Object o) {
    String s = o + "";
    s = s.replace("\\", "\\\\");
    s = s.replace("\b", "\\b");
    s = s.replace("\t", "\\t");
    s = s.replace("\n", "\\n");
    s = s.replace("\f", "\\f");
    s = s.replace("\r", "\\r");
    s = s.replace("\"", "\\\"");
    return quote + s + quote;
  }

  protected String wrapDate(Object o) {
    return wrapString(DateUtil.ymdhms((Date) o));
  }

  protected String wrapByteArray(Object o) {
    byte[] d = (byte[]) o;
    return wrapString(Base64Util.encode(d));
  }

  protected String buildArrayString(StringBuilder s) {
    if (s.length() > 0) {
      s.deleteCharAt(0);
    }
    s.insert(0, "[");
    s.append("]");
    return s.toString();
  }

  protected String wrapShortArray(Object o) {
    short[] l = (short[]) o;
    StringBuilder s = new StringBuilder();
    for (short n : l) {
      s.append(",");
      s.append(n);
    }
    return buildArrayString(s);
  }

  protected String wrapIntArray(Object o) {
    int[] l = (int[]) o;
    StringBuilder s = new StringBuilder();
    for (int n : l) {
      s.append(",");
      s.append(n);
    }
    return buildArrayString(s);
  }

  protected String wrapLongArray(Object o) {
    long[] l = (long[]) o;
    StringBuilder s = new StringBuilder();
    for (long n : l) {
      s.append(",");
      s.append(n);
    }
    return buildArrayString(s);
  }

  protected String wrapFloatArray(Object o) {
    float[] l = (float[]) o;
    StringBuilder s = new StringBuilder();
    for (float n : l) {
      s.append(",");
      s.append(n);
    }
    return buildArrayString(s);
  }

  protected String wrapDoubleArray(Object o) {
    double[] l = (double[]) o;
    StringBuilder s = new StringBuilder();
    for (double n : l) {
      s.append(",");
      s.append(n);
    }
    return buildArrayString(s);
  }

  protected String wrapArray(Object o) {
    if (o instanceof byte[]) {
      return wrapByteArray(o);
    } else if (o instanceof short[]) {
      return wrapShortArray(o);
    } else if (o instanceof int[]) {
      return wrapIntArray(o);
    } else if (o instanceof long[]) {
      return wrapLongArray(o);
    } else if (o instanceof float[]) {
      return wrapFloatArray(o);
    } else if (o instanceof double[]) {
      return wrapDoubleArray(o);
    }
    Object[] l = (Object[]) o;
    return wrapCollection(Arrays.asList(l));
  }

  protected String wrapCollection(Object o) {
    Collection<?> l = (Collection<?>) o;
    StringBuilder s = new StringBuilder();
    for (Object obj : l) {
      s.append(",");
      s.append(wrap(obj));
    }
    return buildArrayString(s);
  }

  protected String wrapEnumeration(Object o) {
    Enumeration<?> l = (Enumeration<?>) o;
    StringBuilder s = new StringBuilder();
    while (l.hasMoreElements()) {
      s.append(",");
      s.append(wrap(l.nextElement()));
    }
    return buildArrayString(s);
  }

  protected String wrapMap(Object o) {
    Map<?, ?> m = (Map<?, ?>) o;
    StringBuilder s = new StringBuilder();
    s.append("{");
    Iterator<?> it = m.keySet().iterator();
    while (it.hasNext()) {
      Object key = it.next();
      s.append(wrapString(key + ""));
      s.append(":");
      s.append(wrap(m.get(key)));
      if (it.hasNext()) {
        s.append(",");
      }
    }
    s.append("}");
    return s.toString();
  }

  protected String wrapObject(Object o) {
    try {
      StringBuilder s = new StringBuilder();
      s.append("{");
      BeanInfo info = Introspector.getBeanInfo(o.getClass(), Object.class);
      PropertyDescriptor[] props = info.getPropertyDescriptors();
      for (int i = 0, j = props.length; i < j; i++) {
        if (i > 0) {
          s.append(",");
        }
        PropertyDescriptor desc = props[i];
        s.append(wrapString(desc.getName()));
        s.append(":");
        Method method = desc.getReadMethod();
        if (null == method) {
          s.append(wrap(null));
        } else {
          s.append(wrap(method.invoke(o)));
        }
      }
      s.append("}");
      return s.toString();
    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
      throw new RuntimeException(e);
    }
  }

  public String wrap(Object o) {
    StringBuilder s = new StringBuilder();
    if (null == o) {
      s.append("null");
    } else if (o instanceof Number) {
      s.append(wrapNumber(o));
    } else if (o instanceof Boolean) {
      s.append(wrapBool(o));
    } else if (o instanceof Character || o instanceof String) {
      s.append(wrapString(o));
    } else if (o instanceof Date) {
      s.append(wrapDate(o));
    } else if (o.getClass().isArray()) {
      s.append(wrapArray(o));
    } else if (o instanceof IPageable) {
      s.append(wrapObject(o));
    } else if (o instanceof Collection) {
      s.append(wrapCollection(o));
    } else if (o instanceof Map) {
      s.append(wrapMap(o));
    } else if (o instanceof Enumeration) {
      s.append(wrapEnumeration(o));
    } else if (o instanceof Enum) {
      s.append(wrapString(o));
    } else {
      s.append(wrapObject(o));
    }
    return s.toString();
  }

  public boolean support(String format) {
    return "json".equalsIgnoreCase(format);
  }
}

package com.nlf.mini.extend.serialize.xml.impl;

import com.nlf.mini.exception.NlfException;
import com.nlf.mini.extend.serialize.xml.exception.XmlFormatException;
import com.nlf.mini.serialize.AbstractParser;
import com.nlf.mini.serialize.node.INode;
import com.nlf.mini.serialize.node.impl.NodeList;
import com.nlf.mini.serialize.node.impl.NodeMap;
import com.nlf.mini.serialize.node.impl.NodeString;
import com.nlf.mini.util.StringUtil;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认xml解析器
 *
 * @author 6tail
 */
public class DefaultXmlParser extends AbstractParser {
  private static final int MIN_STACK_SIZE = 2;
  /**
   * CDATA起始符
   */
  public static final String CDATA_PREFIX = "![CDATA[";
  /**
   * CDATA结束符
   */
  public static final String CDATA_SUFFIX = "]]";
  /**
   * 注释结束符
   */
  public static final String ANNO_SUFFIX = "--";
  /**
   * 注释起始符
   */
  public static final String ANNO_PREFIX = "!-";
  private static final String EQ_QUOTE_DOUBLE = "=\"";

  /**
   * 当前字符
   */
  private int c;
  /**
   * 待解析的字符串
   */
  private String os;
  /**
   * 字符读取器
   */
  private Reader reader;
  /**
   * 节点缓存栈
   */
  private final List<INode> stack = new ArrayList<>();

  public boolean support(String format) {
    return "xml".equalsIgnoreCase(format);
  }

  @Override
  public INode parseAll(String s) {
    os = s;
    if (null == s) {
      return null;
    }
    s = s.trim();
    s = s.substring(s.indexOf("<"));
    reader = new StringReader(s);
    while (-1 != c) {
      parseNode();
    }
    return stack.get(0);
  }

  protected void parseNode() {
    skip();
    if (c == '<') {
      parseXmlNode();
    } else {
      String s = readUntil('<');
      INode p = stack.get(stack.size() - 1);
      try {
        ((NodeString) p).setValue(s.replace("&lt;", "<").replace("&gt", ">"));
      } catch (Exception e) {
        throw new XmlFormatException(s);
      }
    }
  }

  private void parseEndTag(String tag) {
    int stackSize = stack.size();
    if (stackSize < MIN_STACK_SIZE) {
      return;
    }
    // 最后一个节点
    INode el = stack.remove(stackSize - 1);
    stackSize--;
    INode p = stack.get(stackSize - 1);
    switch (p.getType()) {
      case LIST:
        ((NodeList) p).add(el);
        break;
      case MAP:
        NodeMap map = ((NodeMap) p);
        INode xe = map.get(tag);
        if (null != xe) {
          if (xe instanceof NodeList) {
            NodeList list = (NodeList) xe;
            list.add(el);
          } else {
            NodeList list = new NodeList();
            list.setName(tag);
            list.add(xe);
            list.add(el);
            map.set(tag, list);
          }
        } else {
          map.set(tag, el);
        }
        break;
      case STRING:
        NodeMap m = new NodeMap();
        m.setName(p.getName());
        m.setAttributes(p.getAttributes());
        m.set(tag, el);
        stack.set(stackSize - 1, m);
        break;
      default:
    }
  }

  private void parseXmlNode() {
    next();
    switch (c) {
      case '?':
        readUntil('>');
        next();
        break;
      case '!':
        String s = readUntil('>').trim();
        String us = s.toUpperCase();
        StringBuilder value = new StringBuilder();
        // 处理CDATA
        if (us.startsWith(CDATA_PREFIX)) {
          while (!us.endsWith(CDATA_SUFFIX)) {
            next();
            if (-1 == c) {
              throw new XmlFormatException(os);
            }
            us = readUntil('>');
            value.append(">");
            value.append(us);
            us = us.toUpperCase();
          }
          s = value.substring(CDATA_PREFIX.length());
          s = s.substring(0, s.length() - CDATA_SUFFIX.length());
          int stackSize = stack.size();
          if (stackSize > 0) {
            INode p = stack.get(stackSize - 1);
            ((NodeString) p).setValue(s);
          }
        } else if (us.startsWith(ANNO_PREFIX)) {
          // 忽略注释
          while (!us.endsWith(ANNO_SUFFIX)) {
            next();
            if (-1 == c) {
              throw new XmlFormatException(os);
            }
            us = readUntil('>');
            us = us.toUpperCase();
          }
        }
        next();
        break;
      case '/':
        next();
        String tag = readUntil('>');
        next();
        parseEndTag(tag);
        break;
      default:
        String startTag = readUntil('>');
        next();
        boolean isClosed = false;
        startTag = startTag.trim();
        if (startTag.endsWith("/")) {
          isClosed = true;
          startTag = startTag.substring(0, startTag.length() - 1);
        }
        String nodeName = startTag;
        Map<String, String> attrs = new HashMap<>(16);
        if (startTag.contains(" ")) {
          nodeName = StringUtil.between(startTag, "", " ");
          String attr = StringUtil.right(startTag, " ");
          while (attr.contains(EQ_QUOTE_DOUBLE)) {
            String attrName = StringUtil.between(attr, "", EQ_QUOTE_DOUBLE).trim();
            attr = StringUtil.right(attr, EQ_QUOTE_DOUBLE);
            String attrValue = StringUtil.left(attr, "\"").trim();
            attr = StringUtil.right(attr, "\"");
            attrs.put(attrName, attrValue);
          }
        }
        NodeString xs = new NodeString(null);
        xs.setName(nodeName);
        xs.setAttributes(attrs);
        stack.add(xs);
        if (isClosed) {
          parseEndTag(nodeName);
        }
    }
  }

  /**
   * 一直读取，直到遇到指定字符，不包括指定字符
   *
   * @param endTag 指定字符
   * @return 读取到的字符串
   */
  private String readUntil(int endTag) {
    StringBuilder s = new StringBuilder();
    while (-1 != c && endTag != c) {
      s.append((char) c);
      next();
    }
    return s.toString();
  }

  /**
   * 读取下一个字符
   */
  protected void next() {
    try {
      c = reader.read();
    } catch (IOException e) {
      throw new NlfException(e);
    }
  }

  /**
   * 跳过无意义字符和注释
   */
  protected void skip() {
    if (-1 == c) {
      return;
    }
    // 忽略0到32之间的
    if (0 <= c && ' ' >= c) {
      next();
      skip();
    }
    // 忽略DEL及回车换行
    if (127 == c || '\r' == c || '\n' == c) {
      next();
      skip();
    }
  }

}

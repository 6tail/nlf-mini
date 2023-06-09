package com.nlf.mini.serialize.json;

import com.nlf.mini.serialize.ConvertFactory;

/**
 * json序列化工具
 *
 * @author 6tail
 *
 */
public class JSON{
  /**
   * 将指定对象转换为json字符串
   *
   * @param o 对象
   * @return json字符串
   */
  public static String fromObject(Object o){
    return ConvertFactory.getWrapper("json").wrap(o);
  }

  /**
   * 将字符串转换为Bean或者List<Bean>
   *
   * @param s 字符串
   * @return Bean或者List<Bean>
   */
  public static <T>T toBean(String s){
    return ConvertFactory.getParser("json").parse(s);
  }
}

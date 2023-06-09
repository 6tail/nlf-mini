package com.nlf.mini.serialize.node;

import java.io.Serializable;
import java.util.Map;

/**
 * 节点接口
 *
 * @author 6tail
 */
public interface INode extends Serializable {
  /**
   * 类型
   *
   * @return 类型
   */
  NodeType getType();

  /**
   * 获取节点名称
   *
   * @return 节点名称
   */
  String getName();

  /**
   * 获取节点属性们
   *
   * @return 属性们
   */
  Map<String, String> getAttributes();
}

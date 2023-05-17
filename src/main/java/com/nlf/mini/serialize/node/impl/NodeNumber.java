package com.nlf.mini.serialize.node.impl;

import com.nlf.mini.serialize.node.AbstractNode;
import com.nlf.mini.serialize.node.NodeType;

/**
 * 数字类型节点
 *
 * @author 6tail
 */
public class NodeNumber extends AbstractNode {
  private static final long serialVersionUID = 1;
  private final Number n;

  public NodeNumber(Number n) {
    this.n = n;
  }

  public NodeType getType() {
    return NodeType.NUMBER;
  }

  @Override
  public String toString() {
    return n + "";
  }

  public Number getValue() {
    return n;
  }
}

package com.nlf.mini.serialize.node.impl;

import com.nlf.mini.serialize.node.AbstractNode;
import com.nlf.mini.serialize.node.INode;
import com.nlf.mini.serialize.node.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * List类型节点
 *
 * @author 6tail
 */
public class NodeList extends AbstractNode {
  private static final long serialVersionUID = 1;
  private final List<INode> l = new ArrayList<>();

  public int size() {
    return l.size();
  }

  public INode get(int index) {
    return l.get(index);
  }

  public NodeType getType() {
    return NodeType.LIST;
  }

  public void add(INode o) {
    l.add(o);
  }

  public List<INode> getValue() {
    return l;
  }

  @Override
  public String toString() {
    return l.toString();
  }
}

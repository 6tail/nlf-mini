package com.nlf.mini.serialize.node.impl;

import com.nlf.mini.serialize.node.AbstractNode;
import com.nlf.mini.serialize.node.INode;
import com.nlf.mini.serialize.node.NodeType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Map对象类型节点
 *
 * @author 6tail
 */
public class NodeMap extends AbstractNode {
  private static final long serialVersionUID = 1;
  private final Map<String, INode> o = new LinkedHashMap<>();

  public INode get(String key) {
    return o.get(key);
  }

  public void set(String key, INode value) {
    o.put(key, value);
  }

  public Set<String> keySet() {
    return o.keySet();
  }

  public NodeType getType() {
    return NodeType.MAP;
  }

  public Map<String, INode> getValue(){
    return o;
  }

  @Override
  public String toString() {
    return o.toString();
  }
}

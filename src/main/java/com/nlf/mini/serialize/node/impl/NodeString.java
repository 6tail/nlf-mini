package com.nlf.mini.serialize.node.impl;

import com.nlf.mini.serialize.node.AbstractNode;
import com.nlf.mini.serialize.node.NodeType;

/**
 * 字符串类型节点
 *
 * @author 6tail
 */
public class NodeString extends AbstractNode {
  private static final long serialVersionUID = 1;
  private String o;

  public NodeString(String o){
    setValue(o);
  }

  public void setValue(String o){
    this.o = o;
  }

  public String getValue(){
    return o;
  }

  @Override
  public String toString(){
    return o;
  }

  public NodeType getType(){
    return NodeType.STRING;
  }
}

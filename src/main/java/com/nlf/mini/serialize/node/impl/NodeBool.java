package com.nlf.mini.serialize.node.impl;

import com.nlf.mini.serialize.node.AbstractNode;
import com.nlf.mini.serialize.node.NodeType;

/**
 * 布尔类型节点
 *
 * @author 6tail
 *
 */
public class NodeBool extends AbstractNode {
  private static final long serialVersionUID = 1;
  private final boolean o;

  public NodeBool(boolean o){
    this.o = o;
  }

  @Override
  public String toString(){
    return o?"true":"false";
  }

  public NodeType getType(){
    return NodeType.BOOL;
  }

  public boolean value(){
    return o;
  }
}

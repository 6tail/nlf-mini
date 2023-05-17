package com.nlf.mini.extend.dao.sql;

import com.nlf.mini.dao.executer.IDaoExecuter;

import java.util.List;

/**
 * SQL执行器
 * 
 * @author 6tail
 *
 */
public interface ISqlExecuter extends IDaoExecuter {
  /**
   * 获取最近一次操作生成的SQL语句
   * 
   * @return SQL语句
   */
  String getSql();
  
  /**
   * 获取最近一次操作的参数列表
   * @return 参数列表
   */
  List<Object> getParams();
}
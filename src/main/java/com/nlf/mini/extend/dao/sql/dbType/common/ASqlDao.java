package com.nlf.mini.extend.dao.sql.dbType.common;

import com.nlf.mini.extend.dao.sql.AbstractSqlDao;

/**
 * SqlDao的默认实现
 * @author 6tail
 *
 */
public class ASqlDao extends AbstractSqlDao {
  public boolean support(String dbType){
    return true;
  }
}
package com.nlf.mini.extend.dao.sql.dbType.mysql;

import com.nlf.mini.extend.dao.sql.dbType.common.ASqlDao;

/**
 * Mysql Dao
 *
 * @author 6tail
 */
public class MysqlDao extends ASqlDao {
  @Override
  public boolean support(String dbType){
    return "mysql".equalsIgnoreCase(dbType);
  }
}
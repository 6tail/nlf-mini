package com.nlf.mini.extend.dao.sql.dbType.sqlserver;

import com.nlf.mini.Bean;
import com.nlf.mini.dao.paging.PageData;
import com.nlf.mini.extend.dao.sql.dbType.common.ASqlSelecter;
import com.nlf.mini.logger.ILogger;
import com.nlf.mini.logger.LoggerFactory;

import java.util.List;

/**
 * SQL查询器的sqlserver实现
 *
 * @author 6tail
 */
public class SqlserverSelecter extends ASqlSelecter {

  private static final ILogger logger = LoggerFactory.getLogger(SqlserverSelecter.class);

  @Override
  public boolean support(String dbType) {
    return "sqlserver".equalsIgnoreCase(dbType);
  }

  @Override
  public List<Bean> top(int count) {
    params.clear();
    sql = buildSql();
    sql = "SELECT TOP " + count + sql.replaceFirst("SELECT", "");
    logger.debug(buildLog());
    return queryList();
  }

  @Override
  public PageData page(int pageNumber, int pageSize) {
    PageData d = new PageData();
    d.setPageSize(pageSize);
    d.setPageNumber(pageNumber);
    d.setRecordCount(count());
    if (d.getPageNumber() > d.getPageCount()) {
      return d;
    }
    params.clear();
    sql = buildSql();
    sql = "SELECT TOP " + (d.getPageNumber() * d.getPageSize()) + sql.replaceFirst("SELECT", "");
    logger.debug(buildLog());
    List<Bean> l = queryList((d.getPageNumber() - 1) * d.getPageSize());
    d.setData(l);
    return d;
  }
}

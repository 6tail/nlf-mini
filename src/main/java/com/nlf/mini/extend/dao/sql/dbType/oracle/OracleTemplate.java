package com.nlf.mini.extend.dao.sql.dbType.oracle;

import com.nlf.mini.Bean;
import com.nlf.mini.dao.paging.PageData;
import com.nlf.mini.extend.dao.sql.dbType.common.ASqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * SQL模板的oracle实现
 *
 * @author 6tail
 */
public class OracleTemplate extends ASqlTemplate {

  private static final Logger logger = LoggerFactory.getLogger(OracleTemplate.class);

  @Override
  public boolean support(String dbType) {
    return "oracle".equalsIgnoreCase(dbType);
  }

  @Override
  public List<Bean> top(int count) {
    params.clear();
    String sql = buildSql();
    sql = "SELECT * FROM (" + sql + ") WHERE ROWNUM <= " + count;
    sql = buildParams(sql, param);
    this.sql = sql;
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
    String sql = buildSql();
    sql = "SELECT * FROM (SELECT NLFTABLE_.*,ROWNUM RN_ FROM (" + sql + ") NLFTABLE_ WHERE ROWNUM <= " + (d.getPageNumber() * d.getPageSize()) + ") WHERE RN_ > " + ((d.getPageNumber() - 1) * d.getPageSize());
    sql = buildParams(sql, param);
    this.sql = sql;
    logger.debug(buildLog());
    List<Bean> l = queryList();
    d.setData(l);
    return d;
  }
}
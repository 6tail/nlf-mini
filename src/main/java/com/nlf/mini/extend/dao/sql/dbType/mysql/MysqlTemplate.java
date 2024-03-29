package com.nlf.mini.extend.dao.sql.dbType.mysql;

import com.nlf.mini.Bean;
import com.nlf.mini.dao.paging.PageData;
import com.nlf.mini.extend.dao.sql.dbType.common.ASqlTemplate;
import com.nlf.mini.logger.ILogger;
import com.nlf.mini.logger.LoggerFactory;

import java.util.List;

/**
 * SQL模板的mysql实现
 * @author 6tail
 *
 */
public class MysqlTemplate extends ASqlTemplate{

  private static final ILogger logger = LoggerFactory.getLogger(MysqlTemplate.class);

  @Override
  public boolean support(String dbType){
    return "mysql".equalsIgnoreCase(dbType);
  }

  @Override
  public List<Bean> top(int count){
    params.clear();
    String sql = buildSql();
    sql = sql+" LIMIT 0,"+count;
    sql = buildParams(sql,param);
    this.sql = sql;
    logger.debug(buildLog());
    return queryList();
  }

  @Override
  public PageData page(int pageNumber, int pageSize){
    PageData d = new PageData();
    d.setPageSize(pageSize);
    d.setPageNumber(pageNumber);
    d.setRecordCount(count());
    if(d.getPageNumber()>d.getPageCount()){
      return d;
    }
    params.clear();
    String sql = buildSql();
    sql = "SELECT * FROM ("+sql+") NLFTABLE_ LIMIT "+((d.getPageNumber()-1)*d.getPageSize())+","+d.getPageSize();
    sql = buildParams(sql,param);
    this.sql = sql;
    logger.debug(buildLog());
    List<Bean> l = queryList();
    d.setData(l);
    return d;
  }
}
package com.nlf.mini.extend.dao.sql.dbType.oracle;

import com.nlf.mini.Bean;
import com.nlf.mini.dao.paging.PageData;
import com.nlf.mini.extend.dao.sql.dbType.common.ASqlSelecter;
import com.nlf.mini.logger.ILogger;
import com.nlf.mini.logger.LoggerFactory;

import java.util.List;

/**
 * SQL查询器的oracle实现
 * @author 6tail
 *
 */
public class OracleSelecter extends ASqlSelecter{

  private static final ILogger logger = LoggerFactory.getLogger(OracleSelecter.class);

  @Override
  public boolean support(String dbType){
    return "oracle".equalsIgnoreCase(dbType);
  }

  @Override
  public List<Bean> top(int count){
    params.clear();
    sql = buildSql();
    sql = "SELECT * FROM ("+sql+") WHERE ROWNUM <= "+count;
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
    sql = buildSql();
    sql = "SELECT * FROM (SELECT NLFTABLE_.*,ROWNUM RN_ FROM ("+sql+") NLFTABLE_ WHERE ROWNUM <= "+(d.getPageNumber()*d.getPageSize())+") WHERE RN_ > "+((d.getPageNumber()-1)*d.getPageSize());
    logger.debug(buildLog());
    List<Bean> l = queryList();
    d.setData(l);
    return d;
  }
}
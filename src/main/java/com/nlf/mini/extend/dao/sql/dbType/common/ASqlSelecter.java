package com.nlf.mini.extend.dao.sql.dbType.common;

import com.nlf.mini.App;
import com.nlf.mini.Bean;
import com.nlf.mini.dao.exception.DaoException;
import com.nlf.mini.dao.paging.PageData;
import com.nlf.mini.extend.dao.sql.*;
import com.nlf.mini.logger.ILogger;
import com.nlf.mini.logger.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * SQL查询器的默认实现
 *
 * @author 6tail
 *
 */
public class ASqlSelecter extends AbstractSqlExecuter implements ISqlSelecter {

  private static final ILogger logger = LoggerFactory.getLogger(ASqlSelecter.class);

  protected ISqlJoiner joiner;

  public ISqlJoiner getJoiner(){
    if(null==joiner){
      joiner = App.getProxy().newInstance(ISqlJoiner.class.getName());
      joiner.setSelecter(this);
    }
    return joiner;
  }

  public ISqlSelecter table(String tables){
    this.tables.add(tables);
    return this;
  }

  public ISqlSelecter tableIf(String tables,boolean condition){
    if(condition){
      table(tables);
    }
    return this;
  }

  public ISqlSelecter column(String columns){
    Condition cond = new Condition();
    cond.setColumn(columns);
    this.columns.add(cond);
    return this;
  }

  public ISqlSelecter columnIf(String columns,boolean condition){
    if(condition){
      column(columns);
    }
    return this;
  }

  @Override
  public ISqlSelecter where(String sql){
    super.where(sql);
    return this;
  }

  @Override
  public ISqlSelecter where(String columnOrSql, Object valueOrBean){
    super.where(columnOrSql,valueOrBean);
    return this;
  }

  @Override
  public ISqlSelecter whereIf(String sql, boolean condition){
    if(condition){
      where(sql);
    }
    return this;
  }

  @Override
  public ISqlSelecter whereIf(String columnOrSql, Object valueOrBean, boolean condition){
    if(condition){
      where(columnOrSql,valueOrBean);
    }
    return this;
  }

  @Override
  public ISqlSelecter whereIn(String column, Object... values){
    super.whereIn(column,values);
    return this;
  }

  @Override
  public ISqlSelecter whereNotIn(String column, Object... values){
    super.whereNotIn(column,values);
    return this;
  }

  @Override
  public ISqlSelecter whereNotEqual(String column, Object value){
    super.whereNotEqual(column,value);
    return this;
  }

  public ISqlSelecter having(String sql){
    havings.add(buildPureSqlCondition(sql));
    return this;
  }

  public ISqlSelecter having(String columnOrSql,Object valueOrBean){
    Condition cond = new Condition();
    cond.setColumn(columnOrSql);
    if(null==valueOrBean){
      cond.setStart(" IS");
      cond.setPlaceholder(" NULL");
      cond.setType(ConditionType.pure_sql);
    }else{
      if(columnOrSql.contains(NAMED_PLACEHOLDER_PREFIX)&&valueOrBean instanceof Bean){
        cond.setStart("");
        cond.setPlaceholder("");
        cond.setEnd("");
        cond.setValue(valueOrBean);
        cond.setType(ConditionType.multi_params);
      }else{
        cond.setValue(valueOrBean);
      }
    }
    havings.add(cond);
    return this;
  }

  public ISqlSelecter havingIf(String sql,boolean condition){
    if(condition){
      having(sql);
    }
    return this;
  }

  public ISqlSelecter havingIf(String columnOrSql,Object valueOrBean,boolean condition){
    if(condition){
      having(columnOrSql,valueOrBean);
    }
    return this;
  }

  public ISqlSelecter groupBy(String columns){
    groupBys.add(columns);
    return this;
  }

  public ISqlSelecter groupByIf(String columns,boolean condition){
    if(condition){
      groupBy(columns);
    }
    return this;
  }

  protected ISqlSelecter sorts(String columns,String order){
    String right = columns;
    int index = right.indexOf(",");
    StringBuilder col = new StringBuilder();
    int bracketOpen = 0;
    int bracketClose = 0;
    while(index>-1){
      String left = right.substring(0,index);
      bracketOpen += left.length()-left.replace("(","").length();
      bracketClose += left.length()-left.replace(")","").length();
      col.append(left);
      if(bracketOpen>bracketClose){
        col.append(",");
      }else{
        if(col.length()>0){
          sorts.add(col+" "+order);
        }
        col.setLength(0);
        bracketOpen = 0;
        bracketClose = 0;
      }
      right = right.substring(index+1);
      index = right.indexOf(",");
    }
    if(right.length()>0){
      col.append(right);
      sorts.add(col+" "+order);
    }
    return this;
  }

  public ISqlSelecter asc(String columns){
    return sorts(columns,"ASC");
  }

  public ISqlSelecter ascIf(String columns,boolean condition){
    if(condition){
      asc(columns);
    }
    return this;
  }

  public ISqlSelecter desc(String columns){
    return sorts(columns,"DESC");
  }

  public ISqlSelecter descIf(String columns,boolean condition){
    if(condition){
      desc(columns);
    }
    return this;
  }

  @Override
  public String buildSql(){
    return buildSql(false);
  }

  public String buildSql(boolean count){
    StringBuilder s = new StringBuilder();
    s.append("SELECT ");
    if(count){
      s.append("COUNT(1) NLFCOUNT_");
    }else {
      if(columns.isEmpty()){
        s.append("*");
      }else{
        for(int i=0,j=columns.size();i<j;i++){
          if(i>0){
            s.append(",");
          }
          s.append(columns.get(i).getColumn());
        }
      }
    }
    s.append(" FROM ");
    s.append(buildTables());
    s.append(buildSqlWhere());
    for(int i = 0,j = groupBys.size();i<j;i++){
      s.append(" ");
      s.append(i<1?"GROUP BY ":",");
      s.append(groupBys.get(i));
    }
    for(int i = 0,l = havings.size();i<l;i++){
      s.append(" ");
      s.append(i<1?"HAVING":"AND");
      s.append(" ");
      s.append(buildSqlParams(havings.get(i)));
    }
    if(!count){
      for (int i = 0, j = sorts.size(); i < j; i++) {
        s.append(" ");
        s.append(i < 1 ? "ORDER BY " : ",");
        s.append(sorts.get(i));
      }
    }
    return s.toString();
  }

  public List<Bean> query(){
    params.clear();
    sql = buildSql();
    logger.debug(buildLog());
    return queryList();
  }

  public List<Bean> top(int count){
    throw new DaoException(App.getProperty("nlf.exception.dao.operation_not_support"));
  }

  public Bean topOne(){
    List<Bean> l = top(1);
    if(l.size()<1){
      throw new DaoException(App.getProperty("nlf.exception.dao.select.one.not_found"));
    }
    return l.get(0);
  }

  public Bean one(){
    List<Bean> l = query();
    if(l.size()<1){
      throw new DaoException(App.getProperty("nlf.exception.dao.select.one.not_found"));
    }
    return l.get(0);
  }

  public int count(){
    params.clear();
    sql = buildSql(true);
    logger.debug(buildLog());
    List<Bean> l = queryList();
    if(l.size()<1){
      throw new DaoException(App.getProperty("nlf.exception.dao.select.one.not_found"));
    }
    Bean o = l.get(0);
    return o.getInt("NLFCOUNT_",0);
  }

  public PageData page(int pageNumber,int pageSize){
    throw new DaoException(App.getProperty("nlf.exception.dao.operation_not_support"));
  }

  public Iterator<Bean> iterator(){
    params.clear();
    sql = buildSql();
    logger.debug(buildLog());
    return queryIterator();
  }
}

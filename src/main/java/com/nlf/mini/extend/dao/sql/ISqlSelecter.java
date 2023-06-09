package com.nlf.mini.extend.dao.sql;

import com.nlf.mini.Bean;
import com.nlf.mini.dao.exception.DaoException;
import com.nlf.mini.dao.paging.PageData;

import java.util.Iterator;
import java.util.List;

/**
 * SQL查询器
 *
 * @author 6tail
 *
 */
public interface ISqlSelecter extends ISqlExecuter{

  /**
   * 获取SQL JOIN封装器
   * @return JOIN封装器
   */
  ISqlJoiner getJoiner();

  /**
   * 指定表
   *
   * @param tables 表名
   * @return SQL查询器
   */
  ISqlSelecter table(String tables);

  /**
   * 当满足条件时指定表
   *
   * @param tables 表名
   * @param condition 条件是否满足
   * @return SQL查询器
   */
  ISqlSelecter tableIf(String tables,boolean condition);

  /**
   * 指定列
   *
   * @param columns 列名，多列以逗号间隔
   * @return SQL查询器
   */
  ISqlSelecter column(String columns);

  /**
   * 当满足条件时指定列
   *
   * @param columns 列名，多列以逗号间隔
   * @param condition 条件是否满足
   * @return SQL查询器
   */
  ISqlSelecter columnIf(String columns,boolean condition);

  /**
   * 纯SQL语句的where
   *
   * @param sql SQL语句
   * @return SQL查询器
   */
  ISqlSelecter where(String sql);

  /**
   * 带参数的where
   *
   * @param columnOrSql 列名或SQL语句，SQL语句使用冒号加参数名绑定参数，如(age>:age or name=:name)中:age将绑定到bean中key为age的值，:name将绑定到bean中key为name的值
   * @param valueOrBean 参数值或Bean，Bean用于给多个参数赋值
   * @return SQL查询器
   */
  ISqlSelecter where(String columnOrSql,Object valueOrBean);

  /**
   * 当满足条件时执行where
   *
   * @param sql SQL语句
   * @param condition 条件是否满足
   * @return SQL查询器
   */
  ISqlSelecter whereIf(String sql,boolean condition);

  /**
   * 当满足条件时执行where
   *
   * @param columnOrSql 列名或SQL语句，SQL语句使用冒号加参数名绑定参数，如(age>:age or name=:name)中:age将绑定到bean中key为age的值，:name将绑定到bean中key为name的值
   * @param valueOrBean 参数值或Bean，Bean用于给多个参数赋值
   * @param condition 条件是否满足
   * @return SQL查询器
   */
  ISqlSelecter whereIf(String columnOrSql,Object valueOrBean,boolean condition);

  /**
   * where in
   *
   * @param column 列名
   * @param values 参数值
   * @return SQL查询器
   */
  ISqlSelecter whereIn(String column,Object... values);

  /**
   * where not in
   *
   * @param column 列名
   * @param values 参数值
   * @return SQL查询器
   */
  ISqlSelecter whereNotIn(String column,Object... values);

  /**
   * where !=
   *
   * @param column 列名
   * @param value 参数值
   * @return SQL查询器
   */
  ISqlSelecter whereNotEqual(String column,Object value);

  /**
   * group by
   *
   * @param columns 列名，多列以逗号间隔
   * @return SQL查询器
   */
  ISqlSelecter groupBy(String columns);

  /**
   * 当满足条件时执行group by
   *
   * @param columns 列名，多列以逗号间隔
   * @param condition 条件是否满足
   * @return SQL查询器
   */
  ISqlSelecter groupByIf(String columns,boolean condition);

  /**
   * 纯SQL语句的having
   *
   * @param sql SQL语句
   * @return SQL查询器
   */
  ISqlSelecter having(String sql);

  /**
   * 带参数的having
   *
   * @param columnOrSql 列名或SQL语句，SQL语句使用冒号加参数名绑定参数，如(age>:age or name=:name)中:age将绑定到bean中key为age的值，:name将绑定到bean中key为name的值
   * @param valueOrBean 参数值或Bean，Bean用于给多个参数赋值
   * @return SQL查询器
   */
  ISqlSelecter having(String columnOrSql,Object valueOrBean);

  /**
   * 当满足条件时执行having
   *
   * @param sql SQL语句
   * @param condition 条件是否满足
   * @return SQL查询器
   */
  ISqlSelecter havingIf(String sql,boolean condition);

  /**
   * 当满足条件时执行having
   *
   * @param columnOrSql 列名或SQL语句，SQL语句使用冒号加参数名绑定参数，如(age>:age or name=:name)中:age将绑定到bean中key为age的值，:name将绑定到bean中key为name的值
   * @param valueOrBean 参数值或Bean，Bean用于给多个参数赋值
   * @param condition 条件是否满足
   * @return SQL查询器
   */
  ISqlSelecter havingIf(String columnOrSql,Object valueOrBean,boolean condition);

  /**
   * 升序排序
   *
   * @param columns 列名，多列以逗号间隔
   * @return SQL查询器
   */
  ISqlSelecter asc(String columns);

  /**
   * 当满足条件时执行升序排序
   *
   * @param columns 列名，多列以逗号间隔
   * @param condition 条件是否满足
   * @return SQL查询器
   */
  ISqlSelecter ascIf(String columns,boolean condition);

  /**
   * 降序排序
   *
   * @param columns 列名，多列以逗号间隔
   * @return SQL查询器
   */
  ISqlSelecter desc(String columns);

  /**
   * 当满足条件时执行降序排序
   *
   * @param columns 列名，多列以逗号间隔
   * @param condition 条件是否满足
   * @return SQL查询器
   */
  ISqlSelecter descIf(String columns,boolean condition);

  /**
   * 查询
   *
   * @return Bean列表
   */
  List<Bean> query();

  /**
   * 获取前几条记录
   *
   * @param count 记录数量
   * @return Bean列表
   */
  List<Bean> top(int count);

  /**
   * 获取第一条记录，如果没有匹配记录，抛出异常
   *
   * @return Bean
   */
  Bean topOne();

  /**
   * 获取一条记录，如果未获取到，抛出异常，如果获取到多条记录，返回第一条
   *
   * @return Bean
   * @throws DaoException 数据异常
   */
  Bean one();

  /**
   * 获取记录数
   *
   * @return 记录数量
   */
  int count();

  /**
   * 获取分页数据
   *
   * @param pageNumber 页码，从1开始
   * @param pageSize 每页记录数
   * @return 分页
   */
  PageData page(int pageNumber, int pageSize);

  /**
   * 迭代结果集
   *
   * @return 结果集迭代器
   */
  Iterator<Bean> iterator();

}

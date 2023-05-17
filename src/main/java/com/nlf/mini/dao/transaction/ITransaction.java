package com.nlf.mini.dao.transaction;

import com.nlf.mini.dao.connection.IConnection;

/**
 * 事务接口
 *
 * @author 6tail
 *
 */
public interface ITransaction{
  /**
   * 提交事务
   */
  void commit();

  /**
   * 回滚事务
   */
  void rollback();

  /**
   * 获取DB连接
   *
   * @return DB连接
   */
  IConnection getConnection();

  /**
   * 开始批处理
   */
  void startBatch();

  /**
   * 取消批处理
   */
  void cancelBatch();

  /**
   * 执行批处理
   * @return 更新记录数
   */
  int[] executeBatch();
}

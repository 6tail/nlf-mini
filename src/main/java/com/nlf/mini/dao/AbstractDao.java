package com.nlf.mini.dao;

import com.nlf.mini.App;
import com.nlf.mini.dao.connection.ConnectionFactory;
import com.nlf.mini.dao.connection.IConnection;
import com.nlf.mini.dao.exception.DaoException;
import com.nlf.mini.dao.executer.AbstractDaoExecuter;
import com.nlf.mini.dao.executer.IDaoExecuter;

import java.util.HashMap;
import java.util.Map;

/**
 * 抽象Dao，所有Dao实现都应该继承俺
 *
 * @author 6tail
 */
public abstract class AbstractDao implements IDao {
  /**
   * 执行器缓存，{dbType:{executerInterface:executerImpl}
   */
  protected static final Map<String, Map<String, String>> EXECUTERS = new HashMap<>();
  /**
   * DB别名
   */
  protected String alias;

  protected IConnection connection;

  public String getAlias() {
    return alias;
  }

  public void init(String alias) {
    this.alias = alias;
    connection = ConnectionFactory.getConnection(alias);
  }

  public DaoType getType() {
    return DaoType.sql;
  }

  /**
   * 供子类获取DB连接接口
   *
   * @return DB连接接口
   */
  protected IConnection getConnection() {
    return connection;
  }

  protected IDaoExecuter getImpl(String dbType, String executerInterface) {
    Map<String, String> impls = EXECUTERS.computeIfAbsent(dbType, k -> new HashMap<>(2));
    if (!impls.containsKey(executerInterface)) {
      java.util.List<String> l = App.getImplements(executerInterface);
      for (String klass : l) {
        IDaoExecuter executer = App.getProxy().newInstance(klass);
        if (executer.support(dbType)) {
          impls.put(executerInterface, klass);
          return executer;
        }
      }
      impls.put(executerInterface, null);
    } else {
      String impl = impls.get(executerInterface);
      if (null != impl) {
        return App.getProxy().newInstance(impl);
      }
    }
    throw new DaoException(App.getProperty("nlf.exception.dao.executer.not_found", dbType, executerInterface));
  }

  protected IDaoExecuter getExecuter(String executerInterface) {
    IConnection connection = getConnection();
    String dbType = connection.getDbSetting().getDbType();
    AbstractDaoExecuter executer = (AbstractDaoExecuter) getImpl(dbType, executerInterface);
    executer.setConnection(connection);
    return executer;
  }

  @Override
  public void close() {
    getConnection().close();
  }
}
package com.nlf.mini.extend.dao.sql;

import com.nlf.mini.dao.AbstractDao;
import com.nlf.mini.dao.connection.IConnection;
import com.nlf.mini.dao.exception.DaoException;
import com.nlf.mini.dao.transaction.ITransaction;

import java.sql.SQLException;

/**
 * 抽象SqlDao
 * @author 6tail
 *
 */
public abstract class AbstractSqlDao extends AbstractDao implements ISqlDao{

  public ISqlDeleter getDeleter(){
    return (ISqlDeleter)getExecuter(ISqlDeleter.class.getName());
  }
  
  public ISqlUpdater getUpdater(){
    return (ISqlUpdater)getExecuter(ISqlUpdater.class.getName());
  }
  public ISqlSelecter getSelecter(){
    return (ISqlSelecter)getExecuter(ISqlSelecter.class.getName());
  }
  
  public ISqlInserter getInserter(){
    return (ISqlInserter)getExecuter(ISqlInserter.class.getName());
  }

  public ISqlTemplate getTemplate(){
    return (ISqlTemplate)getExecuter(ISqlTemplate.class.getName());
  }
  
  public ITransaction beginTransaction(){
    IConnection connection = getConnection();
    try{
      ((SqlConnection)connection).getConnection().setAutoCommit(false);
    }catch(SQLException e){
      throw new DaoException(e);
    }
    SqlTransaction t = new SqlTransaction();
    t.setConnection(connection);
    return t;
  }
}
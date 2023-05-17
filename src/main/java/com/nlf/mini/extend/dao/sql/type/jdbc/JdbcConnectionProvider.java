package com.nlf.mini.extend.dao.sql.type.jdbc;

import com.nlf.mini.dao.connection.AbstractConnectionProvider;
import com.nlf.mini.dao.connection.IConnection;
import com.nlf.mini.dao.exception.DaoException;
import com.nlf.mini.extend.dao.sql.SqlConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC连接提供器
 *
 * @author 6tail
 */
public class JdbcConnectionProvider extends AbstractConnectionProvider {

  public IConnection getConnection() {
    JdbcSetting setting = (JdbcSetting) this.setting;
    Connection conn;
    try {
      conn = DriverManager.getConnection(setting.getUrl(), setting.getUser(), setting.getPassword());
    } catch (SQLException e) {
      throw new DaoException(e);
    }
    SqlConnection sc = new SqlConnection(conn);
    sc.setDbSetting(setting);
    return sc;
  }

  public boolean support(String type) {
    return "jdbc".equalsIgnoreCase(type);
  }

}
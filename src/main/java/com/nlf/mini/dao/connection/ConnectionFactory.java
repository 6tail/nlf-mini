package com.nlf.mini.dao.connection;

import com.nlf.mini.App;
import com.nlf.mini.dao.exception.DaoException;
import com.nlf.mini.dao.setting.DbSettingFactory;
import com.nlf.mini.dao.setting.IDbSetting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 连接工厂
 *
 * @author 6tail
 */
public class ConnectionFactory {

  /**
   * 连接提供器缓存
   */
  protected static final Map<String, IConnectionProvider> POOL = new HashMap<>();

  protected ConnectionFactory() {
  }

  /**
   * 根据别名获取连接
   *
   * @param alias 别名
   * @return 连接
   */
  public static IConnection getConnection(String alias) {
    IDbSetting setting = DbSettingFactory.getSetting(alias);
    String type = setting.getType();
    if (!POOL.containsKey(alias)) {
      List<String> impls = App.getImplements(IConnectionProvider.class);
      for (String impl : impls) {
        IConnectionProvider p = App.getProxy().newInstance(impl);
        if (p.support(type)) {
          POOL.put(alias, p);
          p.setDbSetting(setting);
          return p.getConnection();
        }
      }
      POOL.put(alias, null);
    } else {
      IConnectionProvider p = POOL.get(alias);
      if (null != p) {
        return p.getConnection();
      }
    }
    throw new DaoException(App.getProperty("nlf.exception.dao.connection.provider.not_found", type));
  }

  /**
   * 获取默认连接
   *
   * @return 默认连接
   */
  public static IConnection getConnection() {
    return getConnection(DbSettingFactory.getDefaultSetting().getAlias());
  }
}

package com.nlf.mini.dao.connection;

import com.nlf.mini.App;
import com.nlf.mini.dao.exception.DaoException;
import com.nlf.mini.dao.setting.IDbSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 连接提供器父类
 *
 * @author 6tail
 */
public abstract class AbstractConnectionProvider implements IConnectionProvider {

  private static final Logger logger = LoggerFactory.getLogger(AbstractConnectionProvider.class);

  /**
   * 已注册驱动
   */
  protected static final Set<String> REGISTED_DRIVERS = new java.util.HashSet<>();
  /**
   * 连接设置
   */
  protected IDbSetting setting;

  public void setDbSetting(IDbSetting setting) {
    this.setting = setting;
    String driver = setting.getDriver();
    if (null == driver) {
      return;
    }
    synchronized (this) {
      if (REGISTED_DRIVERS.contains(driver)) {
        return;
      }
      try {
        Class.forName(driver);
        REGISTED_DRIVERS.add(driver);
      } catch (ClassNotFoundException e) {
        throw new DaoException(App.getProperty("nlf.exception.dao.driver.not_found", driver), e);
      }
      logger.debug(App.getProperty("nlf.dao.driver.registed", driver));
    }
  }

}
package com.nlf.mini.dao.exception;

import com.nlf.mini.App;
import com.nlf.mini.exception.NlfException;

/**
 * 数据访问异常
 * 
 * @author 6tail
 */
public class DaoException extends NlfException {
  private static final long serialVersionUID = 1;
  private static final String MESSAGE = "nlf.exception.dao";

  public DaoException(){
    super(App.getProperty(MESSAGE));
  }

  public DaoException(String message){
    super(message);
  }

  public DaoException(Throwable cause){
    this(App.getProperty(MESSAGE),cause);
  }

  public DaoException(String message,Throwable cause){
    super(message,cause);
  }
}
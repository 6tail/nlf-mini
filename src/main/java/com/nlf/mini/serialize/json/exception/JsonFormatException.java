package com.nlf.mini.serialize.json.exception;

import com.nlf.mini.App;
import com.nlf.mini.exception.NlfException;

/**
 * 不支持的JSON格式
 *
 * @author 6tail
 *
 */
public class JsonFormatException extends NlfException {
  private static final long serialVersionUID = 1;
  private static final String MESSAGE = "nlf.serialize.json.format";

  public JsonFormatException(){
    super(App.getProperty(MESSAGE));
  }

  public JsonFormatException(String message){
    super(App.getProperty(MESSAGE,message));
  }

  public JsonFormatException(Throwable cause){
    this(App.getProperty(MESSAGE),cause);
  }

  public JsonFormatException(String message,Throwable cause){
    super(App.getProperty(MESSAGE,message),cause);
  }
}
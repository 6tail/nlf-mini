package com.nlf.mini.extend.serialize.obj.impl;

import com.nlf.mini.serialize.IParser;
import com.nlf.mini.serialize.json.JSON;
import com.nlf.mini.util.Base64Util;
import com.nlf.mini.util.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.ZipInputStream;

/**
 * 默认obj解析器
 *
 * @author 6tail
 */
public class DefaultObjParser implements IParser {
  public boolean support(String format) {
    return "obj".equalsIgnoreCase(format);
  }

  @SuppressWarnings("unchecked")
  public <T> T parse(String s) {
    Object obj;
    ByteArrayOutputStream bObj = null;
    ByteArrayInputStream bZip = null;
    ObjectInputStream iObj = null;
    ZipInputStream iZip = null;
    try {
      bObj = new ByteArrayOutputStream();
      bZip = new ByteArrayInputStream(Base64Util.decode(s));
      iZip = new ZipInputStream(bZip);
      iZip.getNextEntry();
      byte[] buffer = new byte[IOUtil.BUFFER_SIZE];
      int offset = -1;
      while ((offset = iZip.read(buffer)) != -1) {
        bObj.write(buffer, 0, offset);
      }
      iObj = new ObjectInputStream(new ByteArrayInputStream(bObj.toByteArray()));
      obj = iObj.readObject();
    } catch (ClassNotFoundException | IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtil.closeQuietly(iZip);
      IOUtil.closeQuietly(bZip);
      IOUtil.closeQuietly(iObj);
      IOUtil.closeQuietly(bObj);
    }
    return (T) (JSON.toBean(JSON.fromObject(obj)));
  }
}
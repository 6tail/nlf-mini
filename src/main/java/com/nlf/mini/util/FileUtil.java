package com.nlf.mini.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件工具
 *
 * @author 6tail
 */
public class FileUtil {
  /**
   * 文件BOM头
   */
  public static Map<String, byte[]> BOM = new HashMap<>();

  static {
    BOM.put("utf-8", new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
  }

  /**
   * 以文本形式读取文件内容，自动识别文件编码
   *
   * @param file 文件
   * @return 文本内容
   * @throws IOException IO异常
   */
  public static String readAsText(File file) throws IOException {
    String sutf8 = readAsText(file, "utf-8");
    String sgbk = readAsText(file, "gbk");
    int lsutf8 = sutf8.length();
    int lsgbk = sgbk.length();
    return lsutf8 < lsgbk ? sutf8 : sgbk;
  }

  /**
   * 以指定编码读取文件内容
   *
   * @param file   文件
   * @param encode 编码
   * @return 文本内容
   * @throws IOException IO异常
   */
  protected static String readAsText(File file, String encode) throws IOException {
    return readAsText(new FileInputStream(file), encode);
  }

  /**
   * 以指定编码读取文件内容
   *
   * @param in     输入流
   * @param encode 编码
   * @return 文本内容
   * @throws IOException IO异常
   */
  public static String readAsText(InputStream in, String encode) throws IOException {
    StringBuilder s = new StringBuilder();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(in, encode));
      String line;
      while (null != (line = br.readLine())) {
        s.append(line);
        s.append("\r\n");
      }
    } finally {
      IOUtil.closeQuietly(br);
    }
    String rs = s.toString();
    byte[] bomBytes = BOM.get(encode.toLowerCase());
    if (null != bomBytes) {
      String bom = new String(bomBytes, encode);
      if (rs.startsWith(bom)) {
        rs = rs.substring(bom.length());
      }
    }
    return rs;
  }

  /**
   * 写到文件
   *
   * @param inputStream 输入流
   * @param file        目标文件
   * @throws IOException IO异常
   */
  public static void write(InputStream inputStream, File file) throws IOException {
    BufferedOutputStream bos = null;
    try {
      bos = new BufferedOutputStream(new FileOutputStream(file));
      byte[] buffer = new byte[IOUtil.BUFFER_SIZE];
      int l;
      while ((l = inputStream.read(buffer)) != -1) {
        bos.write(buffer, 0, l);
      }
      bos.flush();
    } finally {
      IOUtil.closeQuietly(bos);
    }
  }
}
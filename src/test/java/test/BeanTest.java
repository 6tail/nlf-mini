package test;

import com.nlf.mini.Bean;
import com.nlf.mini.serialize.json.JSON;
import org.junit.Assert;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

public class BeanTest {
  @Test
  public void test() {
    Bean o = new Bean("bytes", new byte[]{1, 2, 3});
    Assert.assertEquals("{\"bytes\":\"AQID\"}", JSON.fromObject(o));
  }

  @Test
  public void test1() {
    String s = "{\"bytes\": \"AQID\"}";
    Bean o = JSON.toBean(s);
    Assert.assertEquals("AQID", o.getString("bytes"));
    Assert.assertArrayEquals(new byte[]{1, 2, 3}, o.getBytes("bytes", null));
  }

  @Test
  public void test2() {
    A a = new A();
    a.setBytes(new byte[]{1, 2, 3});
    Assert.assertEquals("{\"bytes\":\"AQID\"}", JSON.fromObject(a));
  }

  @Test
  public void test3() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
    String s = "{\"bytes\": \"AQID\"}";
    Bean o = JSON.toBean(s);
    A a = o.toObject(A.class);
    Assert.assertArrayEquals(new byte[]{1, 2, 3}, a.getBytes());
  }

  @Test
  public void test4() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
    String s = "{\"bytes\": \"AQID\"}";
    Bean o = JSON.toBean(s);
    B b = o.toObject(B.class);
    Assert.assertEquals("AQID", b.getBytes());
  }

  public static class A {
    private byte[] bytes;

    public void setBytes(byte[] bytes) {
      this.bytes = bytes;
    }

    public byte[] getBytes() {
      return bytes;
    }
  }

  public static class B {
    private String bytes;

    public void setBytes(String bytes) {
      this.bytes = bytes;
    }

    public String getBytes() {
      return bytes;
    }
  }
}

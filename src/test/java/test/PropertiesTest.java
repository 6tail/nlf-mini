package test;

import com.nlf.mini.App;
import org.junit.Assert;
import org.junit.Test;

public class PropertiesTest {
  @Test
  public void test() {
    Assert.assertEquals("dev", App.getProperty("hello"));
  }

  @Test
  public void test1() {
    App.profile = null;
    Assert.assertEquals("default", App.getProperty("hello"));
    App.profile = "dev";
  }

  @Test
  public void test2() {
    App.profile = "prod";
    Assert.assertEquals("prod", App.getProperty("hello"));
    App.profile = "dev";
  }

  @Test
  public void test3() {
    Assert.assertEquals("hello world", App.getProperty("say"));
  }
}

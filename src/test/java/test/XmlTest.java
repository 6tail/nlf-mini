package test;

import com.nlf.mini.Bean;
import com.nlf.mini.extend.serialize.xml.XML;
import org.junit.Assert;
import org.junit.Test;

public class XmlTest {
  @Test
  public void test() {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>hello</b></a>";
    Bean a = XML.toBean(xml);
    Assert.assertEquals("hello", a.getString("b"));
  }

}

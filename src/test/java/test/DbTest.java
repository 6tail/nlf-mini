package test;

import com.nlf.mini.dao.paging.PageData;
import com.nlf.mini.extend.dao.sql.ISqlDao;
import com.nlf.mini.extend.dao.sql.SqlDaoFactory;
import org.junit.Assert;
import org.junit.Test;

public class DbTest {
  @Test
  public void test() {
    ISqlDao dao = SqlDaoFactory.getDao();

    int count = dao.getSelecter().table("user").count();
    Assert.assertTrue(count > 0);

    PageData pd = dao.getSelecter().table("user").page(1, 2);
    System.out.println(pd);

    dao.close();
  }
}

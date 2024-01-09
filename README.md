# nlf-mini [![License](https://img.shields.io/badge/license-MIT-4EB1BA.svg?style=flat-square)](https://github.com/6tail/nlf-mini/blob/master/LICENSE)

nlf-mini是 [nlf2](https://github.com/6tail/nlf2-maven) 的简化版，主要优化了扫描机制，优化了日志配置，移除了web相关的支持，移除了非关系型数据库的支持，使用properties配置替代了默认的db文件配置，应用了新的插件机制。

### Maven

```xml
<dependency>
  <groupId>cn.6tail</groupId>
  <artifactId>nlf-mini</artifactId>
  <version>1.0.6</version>
</dependency>
```

### 下载jar

[Download](https://github.com/6tail/nlf-mini/releases)

## 示例

    import com.nlf.mini.extend.dao.sql.ISqlDao;
    import com.nlf.mini.extend.dao.sql.SqlDaoFactory;
     
    public class Demo {
      public static void main(String[] args){
        ISqlDao dao = SqlDaoFactory.getDao();
        int count = dao.getSelecter().table("user").count();
        System.out.println(count);
        dao.close();
      }
    }

## 其他

### 框架日志配置

框架日志激活方式如下，最左优先级最高。按如下配置时，当框架发现支持slf4j时，将自动使用slf4j输出框架相关日志：

    nlf.logger.active = slf4j, commons_logging, default

其中，default指默认控制台输出框架日志。

默认配置为不输出框架日志，即：

    nlf.logger.active = 

## 插件

1. [nlf-mini-plugin-druid](https://github.com/6tail/nlf-mini-plugin-druid) druid连接池插件
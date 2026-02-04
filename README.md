本工程为使用 TDD 的方式从构建一个仿知乎的论坛项目（详见[build-zhihu-with-springboot-and-tdd-manual](https://github.com/qianhuihuiji/build-zhihu-with-springboot-and-tdd-manual)）的起步工程，不包含具体逻辑，仅添加了项目中使用到的 Maven 依赖和项目常用的公共类等。

开发环境为了保持数据库结构的一致性，我们引入了`flyway`插件来对数据库进行版本化的管理。并且，我们内置了一份`user`数据表的建表语句。


本工程为使用 TDD 的方式从构建一个仿知乎的论坛项目（详见[build-zhihu-with-springboot-and-tdd-manual](https://github.com/qianhuihuiji/build-zhihu-with-springboot-and-tdd-manual)）的起步工程，不包含具体逻辑，仅添加了项目中使用到的 Maven 依赖和项目常用的公共类等。

开发环境为了保持数据库结构的一致性，我们引入了`flyway`插件来对数据库进行版本化的管理。并且，我们内置了一份`user`数据表的建表语句。

> 注：Flyway是一个简单开源数据库版本控制器（约定大于配置），主要提供migrate、clean、info、validate、baseline、repair等命令。它支持SQL（PL/SQL、T-SQL）方式和Java方式，支持命令行客户端等，还提供一系列的插件支持（Maven、Gradle、SBT、ANT等）。

此外，我们使用了MyBatis作为ORM框架，并且配置了MyBatis Generator，用以生成数据库映射实体类。使用时只需要先修改generatorConfig.xml配置文件中需要生成的表名，再调用 Generator#main()即可。


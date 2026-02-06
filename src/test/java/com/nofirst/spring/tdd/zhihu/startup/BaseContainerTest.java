package com.nofirst.spring.tdd.zhihu.startup;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.TestcontainersConfiguration;

@SpringBootTest(classes = SpringTddZhihuStartupApplication.class)
public abstract class BaseContainerTest {

    // 声明容器静态变量，在静态代码块中完成实例化
    public static MySQLContainer<?> mysqlContainer;

    // 静态代码块：类加载时执行，保证配置在容器实例化前生效
    static {
        // 开启框架级复用总开关（与外部全局配置双重保障，防止外部配置缺失）
        TestcontainersConfiguration.getInstance().updateUserConfig("testcontainers.reuse.enable", "true");

        // 这里的 mysql:8.0 镜像最好先本地下载，不然启动测试会先尝试下载，导致首次测试时间会变得非常长
        // 实例化MySQL容器，仅保留官方核心复用配置.withReuse(true)，无任何自定义标签
        mysqlContainer = new MySQLContainer<>("mysql:8.0")
                .withDatabaseName("zhihu")
                .withUsername("root")
                .withPassword("root")
                .withReuse(true);
    }

    @BeforeAll
    public static void start() {
        mysqlContainer.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        // 手动启动
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);

    }


}

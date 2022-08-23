package com.user.usercenter;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@SpringBootTest
@RunWith(SpringRunner.class)
class UserCenterApplicationTests {


    @Test
    void contextLoads() {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/user_center?serverTimezone=GMT%2B8", "root", "pwb2001")
                .globalConfig(builder -> {
                    builder.author("ice") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:\\JavaUser\\UserCenterModule\\user-center"+"//src/main//java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.user") // 设置父包名
                            .moduleName("usercenter") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "D:\\JavaUser\\UserCenterModule\\user-center"+"//src/main//java//com//user//usercenter//mapper//xml")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("chat_record","user_friend","user_friend_req") // 设置需要生成的表名
                            .addTablePrefix(); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }

}

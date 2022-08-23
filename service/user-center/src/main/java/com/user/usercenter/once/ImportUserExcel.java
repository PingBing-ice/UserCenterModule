package com.user.usercenter.once;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ice
 * @date 2022/7/13 12:13
 *  导入数据
 */

public class ImportUserExcel extends UserListener {
    public static void simpleRead() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "C:\\Users\\BING\\Desktop\\test.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取100条数据 然后返回过来 直接调用使用数据就行
        List<DemoUser> userList = EasyExcel.read(fileName, DemoUser.class, new UserListener()).sheet().doReadSync();
        System.out.println("总数"+userList.size());
        Map<String, List<DemoUser>> collect = userList.parallelStream().filter(demoUser -> StringUtils.isNotEmpty(demoUser.getUsername())).collect(Collectors.groupingBy(DemoUser::getUsername));
        System.out.println("不重复的昵称数"+collect.keySet().size());
    }

    public static void main(String[] args) {
        simpleRead();
    }
}

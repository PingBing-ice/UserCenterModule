package com.user.model.utils;

import org.springframework.util.StringUtils;



import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class IpUtilSealUp {

    private static final List<String> ipList = new CopyOnWriteArrayList<>();

    public static void addIpList(String ip) {
        ipList.add(ip);
    }

    public static boolean selectByIp(String ips) {
        if (!StringUtils.hasText(ips)) {
            return false;
        }
        if (ipList.size() <= 0) {
            return false;
        }
        for (String ip : ipList) {
            return ip.equals(ips);
        }
        return false;
    }

}
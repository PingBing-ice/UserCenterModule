package com.user.util.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 算法工具类
 * @author ice
 * @date 2022/9/19 17:08
 */
public class AlgorithmUtils {

    /**
     * 编辑距离
     * 地址 =>  https://blog.csdn.net/DBC_121/article/details/104198838
     * @param word1 1
     * @param word2 2
     * @return 返回的 最小  编辑距离
     */
    public static int  minDistance(List<String> word1, List<String> word2){
        int n = word1.size();
        int m = word2.size();

        if (n * m == 0) {
            return n + m;
        }

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++){
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++){
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++){
            for (int j = 1; j < m + 1; j++){
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int left_down = d[i - 1][j - 1];
                if (!word1.get(i - 1).equals(word2.get(j - 1))) {
                    left_down += 1;
                }
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }

    /**
     * 测试类
     * @param args
     */
    public static void main(String[] args) {
        List<String> a = Arrays.asList("java","男","王者荣耀");
        List<String> b = Arrays.asList("java","c++","python");
        List<String> c = Arrays.asList("女","python");
        List<String> d = Arrays.asList("男","王者荣耀");
        System.out.println(minDistance(a, b));
        System.out.println(minDistance(a, c));
        System.out.println(minDistance(a, d));

    }
}

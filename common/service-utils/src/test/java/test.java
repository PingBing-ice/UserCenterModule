import java.io.Serializable;

/**
 * @author ice
 * @date 2022/8/23 16:20
 */

public class test implements Serializable {



    private static final long serialVersionUID = 4399252747120150045L;



    public static void main(String[] args) {

        System.out.println(3/2);
        String mmmm = new String("mmmm");
        StringBuffer stringBuffer = new StringBuffer("mmmmmms");
        stringBuffer.reverse();
        System.out.println(stringBuffer);
    }

    private static void print(int i) {
        for (int j = 31; j >=0; j--) {
            System.out.print((i&(1<<j)) == 0?"0":"1");
        }
    }
}

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Hashtable;

/**
 * @author ice
 * @date 2022/8/23 16:20
 */

public class test implements Serializable {



    private static final long serialVersionUID = 4399252747120150045L;



    public static void main(String[] args) {
        Usb usb = new UsbTaoFactory();
        InvocationHandler header = new MyShellHeader(usb);
        Usb proxy = (Usb) Proxy.newProxyInstance(usb.getClass().getClassLoader(), usb.getClass().getInterfaces(), header);
        float shell = proxy.shell(1);
        System.out.println("通过动态代理.代理对象"+shell);
        Hashtable<String, String> map = new Hashtable<>();
        map.put("s", "a");
    }

    private static void print(int i) {
        for (int j = 31; j >=0; j--) {
            System.out.print((i&(1<<j)) == 0?"0":"1");
        }
    }
}

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author ice
 * @date 2022/8/30 12:24
 */

public class MyShellHeader implements InvocationHandler {
    private Object obj =null;
    // 动态传入对象
    public MyShellHeader(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ArrayList<String> list = new ArrayList<>();

        Object res;
        res = method.invoke(obj, args);
        // 增强方法
        if (res != null) {
            float price = (float) res;
            price = price + 25;
            res = price;
        }
        System.out.println("淘宝优惠券");
        return res;
    }
}

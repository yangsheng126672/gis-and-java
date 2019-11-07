import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: test
 * @Author: liaosijun
 * @Time: 2019/6/20 10:55
 */
public class Test {

//	public static void main(String[] args) {
//		String a = null;
//		String b = "";
//		String c = "  ";
//		System.out.println(StringUtils.isEmpty(a));
//		System.out.println(StringUtils.isEmpty(b));
//		System.out.println(StringUtils.isEmpty(c));
//		List list = new ArrayList();
//		list.add(1);
//		list.add(2);
//		list.add(3);
//		list.add(4);
////		List arrayList = list.subList(0,2);
////		arrayList.clear();
//		list.forEach(ac -> {
//			System.out.println(ac);
//		});
//	}

	public static int getFactorial(int n) {
		if (n >= 0) {
			if (n == 0) {
				System.out.println(n + "!=1");
				return 1;
			} else {
				System.out.println(n);
				int temp = n * getFactorial(n - 1);
				System.out.println(n + "!=" + temp);
				return 1;
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		getFactorial(4);
	}

}
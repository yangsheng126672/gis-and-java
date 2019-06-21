import org.springframework.util.StringUtils;

/**
 * @Description: test
 * @Author: liaosijun
 * @Time: 2019/6/20 10:55
 */
public class Test {

	public static void main(String[] args) {
		String a = null;
		String b = "";
		String c = "  ";
		System.out.println(StringUtils.isEmpty(a));
		System.out.println(StringUtils.isEmpty(b));
		System.out.println(StringUtils.isEmpty(c));
	}
}
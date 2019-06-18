package mybatisGen;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:用mybatis generator 表转实体
 * @Author: liaosijun
 * @Time: 2019/6/14 14:14
 */
public class TableToEntityTest {

	public static void main(String[] args) {
		try {
			List<String> warnings = new ArrayList<>();
			boolean overwrite = true;
			String genCfg = "/generatorConfig.xml";
			File configFile = new File(TableToEntityTest.class.getResource(genCfg).getFile());
			ConfigurationParser cp = new ConfigurationParser(warnings);
			Configuration config = cp.parseConfiguration(configFile);
			DefaultShellCallback callback = new DefaultShellCallback(overwrite);
			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
			myBatisGenerator.generate(null);
			System.out.println("完成");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
package com.jdrx.gis.config;

import com.jdrx.platform.commons.rest.config.Swagger2Config;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * swagger配置
 * @date 2019-09-23
 */
public class MySwaggersConfig extends Swagger2Config {

    @Override
    public Docket createRestApi() {
        ParameterBuilder db = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        db.name("deptPath").description("机构路径").modelRef(new ModelRef("String")).parameterType("header").required(true).build();
        pars.add(db.build());
        return super.createRestApi().globalOperationParameters(pars);
    }

}

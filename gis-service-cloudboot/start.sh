#!/bin/sh
# 样例
source /etc/profile
cd /opt/springcloud/zhsw-logger


nohup java -Dspring.profiles.active=test  -Dspring.application.name=zhsw-logger-app -jar jdrx-zhsw-logger-cloudboot-1.0.0.jar 2>&1 console.log &

# 模板如下
#source /etc/profile
#cd /opt/springcloud/目录
#nohup java -Dspring.profiles.active=test  -Dspring.application.name=【application.properties中配置项spring.application.name的值】 -jar boot标准jar包.jar 2>&1 >console.log &

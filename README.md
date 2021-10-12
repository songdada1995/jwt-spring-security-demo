# jwt-spring-security-demo
jwt-spring-security-demo

来源：https://github.com/szerhusenBC/jwt-spring-security-demo

本demo是在此案例基础上改造，持久层框架改为mybatis
SQL表结构文件也做了些许更改

项目启动后，浏览器输入http://localhost:8080/index.html

##################
注意：
1.此demo在前端保存token并未设置过期时间，但生成的jwt的token是存在过期时间的。若运行后未退出登录，下次再运行项目，打开页面可能出现401，可在前端js方法里面把token先清除再去获取。
2.导入import.sql文件，生成数据库，修改application.properties里面的数据库用户密码。

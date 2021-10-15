该模块为dubbo应用的公共服务 我的所有dubbo项目都会有这个公共服务

技术栈 dubbo2.7.1+zookeeper3.4.13(注册中心)+mybatis+jdbc+shardingjdbc 

包含 链路追踪,多注册中心配置,分库分表,xss攻击,swagger,主从数据源,P6Datasourse,logback日志

子服务依赖 对应commons服务即可(commons-core核心模块子服务都需要依赖 另外持久层依赖commons-db,web层依赖commons-web)

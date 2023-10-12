# Docker 方式部署

## 一.创建自定义`Docker`网桥

```bash
docker network create cloud-storage-disk
```

## 二.搭建基础环境

### 1.创建 `Redis` 容器

#### 1.1.拉取 `Redis` 镜像

```bash
docker pull redis:7.0.0
```

#### 1.2.创建容器

```bash
docker run -p 6379:6379 --name redis --network cloud-storage-disk --restart=always -d redis:7.0.0 --requirepass "123456"
# -p 设置映射端口号；“:“ 左侧为宿主机端口号，”:“ 右侧为容器内端口号
# --name 设置容器名称
# --network 设置容器连接的 docker 网络，此为刚刚创建的桥接网络
# -d 设置容器为后台运行
# --requirepass 设置 redis 密码
```

### 2.创建 `MySQL` 容器

#### 2.1.拉取 `MySQL` 镜像

```bash
docker pull mysql:8.0.29
```

#### 2.2.创建容器

```bash
docker run -d -p 3306:3306 --privileged=true --network cloud-storage-disk --restart=always -v /opt/docker/mysql:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 --name mysql mysql:8.0.29 --character-set-server=utf8mb4 --collation-server=utf8mb4_general_ci
# -d 设置容器为后台运行
# -p 设置映射端口号
# --network 设置容器连接的网络
# -v 挂载的目录；":" 左侧为宿主机目录，右侧为容器内目录
# -e 设置参数
# --name 设置容器名称
```

### 3.创建配置文件

#### 3.1.创建配置文件

```bash
/opt/webapps/cloud-storage-disk/properties/application.yml
# 云存储后端服务会读取 application.yml 配置文件
```

#### 3.2.编写配置文件

```yml
# application name
spring:
  application:
    name: cloud-storage-disk
  # active profile
  profiles:
    active: prod
  # database driver
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    # datasource name
    name: defaultDataSource
    # database url
    url: "jdbc:mysql://mysql:3306/cloud-storage-disk?useSSL=false&\
      useUnicode=true&\
      characterEncoding=UTF-8&\
      allowMultiQueries=true&\
      allowPublicKeyRetrieval=true&\
      serverTimezone"
    # username and password of database
    username: root # {your_mysql_username}
    password: 123456 # {your_mysql_password}
    # database connection pool config
    hikari:
      connection-timeout: 30000
      minimum-idle: 5
      maximum-pool-size: 15
      auto-commit: true
      idle-timeout: 30000
      pool-name: DateSourceHikariCP
      max-lifetime: 120000
  # unified time zone
  jackson:
    time-zone: GMT+8
  # static resources location
  web:
    resources:
      static-locations: classpath:/resources/,classpath:/static/,classpath:/templates/
  # max size of upload file and request
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB
  # redis config
  redis:
    host: redis
    password: 123456 # {your_redis_password}
    port: 6379
    database: 1
    timeout: 5000
    lettuce:
      shutdown-timeout: 5000
      pool:
        max-active: 8
        max-idle: 8
        max-wait: 30
        min-idle: 0
# application web connection port
server:
  port: 8100
```

### 4.拉取创建云存储后端容器

#### 4.1.拉取服务

```bash
docker pull yangcloud/cloud-storage-disk:${version}
```

#### 4.2.创建服务容器

```bash
docker run --name cloud-storage-disk --network cloud-storage-disk --restart=always -p 80:80 -v /opt/webapps/cloud-storage-disk/properties:/opt/webapps/cloud-storage-disk/properties -v /opt/webapps/cloud-storage-disk/storage:/opt/webapps/cloud-storage-disk/storage -itd yangcloud/cloud-storage-disk:${version}
```

#### 5.开放的目录

```bash
# 服务配置文件所在目录
/opt/webapps/cloud-storage-disk/properties
# 服务本地存储目录
/opt/webapps/cloud-storage-disk/storage
```

## 三.访问网站

```
http://{ip}/
```

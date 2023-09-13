---
outline: deep
---

# Docker 方式部署

## 搭建基础环境

### 创建自定义桥接网络

```
docker network create cloud-storage-disk
```

### 创建 `Redis` 容器

#### 拉取 `Redis` 镜像

```
docker pull redis:7.0.0
```

#### 创建容器

```
docker run -p 6379:6379 --name redis --network cloud-storage-disk -d redis:7.0.0 --requirepass "123456"
# -p 设置映射端口号；“:“ 左侧为宿主机端口号，”:“ 右侧为容器内端口号
# --name 设置容器名称
# --network 设置容器连接的 docker 网络，此为刚刚创建的桥接网络
# -d 设置容器为后台运行
# --requirepass 设置 redis 密码
```

### 创建 `MySQL` 容器

#### 拉取 `MySQL` 镜像

```
docker pull mysql:8.0.29
```

#### 创建容器

```
docker run -d -p 3306:3306 --privileged=true --network cloud-storage-disk -v /opt/docker/mysql:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 --name mysql mysql:8.0.29 --character-set-server=utf8mb4 --collation-server=utf8mb4_general_ci
# -d 设置容器为后台运行
# -p 设置映射端口号
# --network 设置容器连接的网络
# -v 挂载的目录；":" 左侧为宿主机目录，右侧为容器内目录
# -e 设置参数
# --name 设置容器名称
```

### 创建配置文件

#### 创建配置文件

```
/opt/webapps/cloud-storage-disk/properties/application.yml
# 云存储后端服务会读取 application.yml 配置文件
```

#### 编写配置文件

```
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
    username: root
    password: 123456
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
    password: 123456
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

### 拉取创建云存储后端容器

#### 拉取后端镜像

```
docker pull yangcloud/cloud-storage-disk-api:0.1
```

#### 创建后端容器

```
docker run --name cloud-storage-disk-api --network cloud-storage-disk -p 8100:8100 -v /opt/webapps/cloud-storage-disk/properties:/opt/webapps/cloud-storage-disk/properties -itd yangcloud/cloud-storage-disk-api:0.1
# 后端服务 Jar 包启动的端口号为 8100，开放的端口也为 8100
```

#### 开放的目录

```shell
# 后端服务配置文件所在目录
/opt/webapps/cloud-storage-disk/properties
# 后端服务本地存储目录
/opt/webapps/storage/cloud-storage-disk
```

### 拉取创建云存储前端容器

#### 拉取前端镜像

```
docker pull yangcloud/cloud-storage-disk-web:0.1
```

#### 创建前端容器

```
docker run --name cloud-storage-disk-web --network cloud-storage-disk -p 80:80 -itd yangcloud/cloud-storage-disk-web:0.1
# 前端容器可挂载容器内目录：/usr/local/nginx/conf/vhost
# 此目录为容器内 nginx 配置文件所在目录
# 此目录内文件名需以 .conf 为后缀，如 test.conf
# 容器内默认配置文件 pan.conf 内容：
server {
    listen       80;
    server_name  localhost;

    location / {
        root /usr/local/nginx/html;
        index index.html;
    }

    location /pan {
        proxy_set_header HOST $host;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header REMOTE-HOST $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_pass http://cloud-storage-disk-api:8100;
    }
}
```

#### 开放的目录

```shell
# nginx 配置文件所在目录
/usr/local/nginx/conf/vhost
```

### 访问网站

```
http://{ip}/
```

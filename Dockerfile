FROM centos:7

MAINTAINER zhuboyang <zhuboyang1996@foxmail.com>

WORKDIR /opt/webapps/cloud-storage-disk/

COPY ./cloud-storage-disk-api/web/target/cloud-storage-disk-api.jar /opt/webapps/cloud-storage-disk/cloud-storage-disk-api.jar
COPY ./cloud-storage-disk-web/dist /opt/webapps/cloud-storage-disk/html

# 替换 yum 源为阿里源
RUN echo "185.242.235.248 storage.yangcloud.online" >> /etc/hosts; \
    cd /etc/yum.repo.d/; \
    mv CentOS-Base.repo CentOS-Base.repo.bak; \
    curl -o CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo; \
    yum clean all; \
    yum makecache; \
    # 安装各依赖
    yum install -y epel-release gcc gcc-c++ make cmake automake autoconf openssl-devel pcre-devel perl-devel unzip; \
    # 加载 ffmpeg 的源
    rpm --import http://li.nux.ro/download/nux/RPM-GPG-KEY-nux.ro; \
    rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-5.el7.nux.noarch.rpm; \
    # 安装 ffmpeg
    yum install -y ffmpeg ffmpeg-devel; \
    # 下载并配置 JDK1.8
    curl -o jdk-8u261-linux-x64.tar.gz http://storage.yangcloud.online/pan/jdk-8u261-linux-x64.tar.gz; \
    tar -zxf jdk-8u261-linux-x64.tar.gz; \
    mv -f jdk1.8.0_261/ /usr/local/jdk1.8; \
    rm -rf jdk-8u261-linux-x64.tar.gz; \
    # 拉取后端配置文件
    curl -o properties.zip http://storage.yangcloud.online/pan/properties.zip; \
    unzip properties.zip; \
    rm -rf properties.zip; \
    # 拉取并安装 NodeJS
    curl -o node-v16.14.2-linux-x64.tar.gz http://storage.yangcloud.online/pan/node-v16.14.2-linux-x64.tar.gz; \
    tar -xvf node-v16.14.2-linux-x64.tar.gz; \
    mv node-v16.14.2-linux-x64 /usr/local/node16.14.2; \
    ln -s /usr/local/node16.14.2/bin/node /usr/local/bin/; \
    ln -s /usr/local/node16.14.2/bin/npm /usr/local/bin/; \
    rm -rf node-v16.14.2-linux-x64.tar.gz; \
    # 拉取 Nginx 安装包，并配置、清理
    curl -o nginx-1.20.2.tar.gz http://storage.yangcloud.online/pan/nginx-1.20.2.tar.gz; \
    tar -xvf nginx-1.20.2.tar.gz; \
    cd nginx-1.20.2; \
    ./configure --prefix=/usr/local/nginx --with-http_ssl_module --with-http_realip_module --with-http_gunzip_module --with-http_gzip_static_module; \
    make && make install; \
    ln -s /usr/local/nginx/sbin/nginx /usr/local/bin/; \
    cd ..; \
    rm -rf nginx*; \
    # 进入 Nginx 的目录，开始配置配置文件
    cd /usr/local/nginx; \
    rm -rf html; \
    mv /opt/webapps/cloud-storage-disk/html /usr/local/nginx/html; \
    # 清理 Dockerfile
    rm -rf Dockerfile

ENV JAVA_HOME /usr/local/jdk1.8
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH $PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$FFMPEG_HOME/bin

VOLUME ["/opt/webapps/cloud-storage-disk/properties", "/opt/webapps/cloud-storage-disk/storage"]

EXPOSE 80

ENTRYPOINT ["java", "-jar", "-Xmx512m", "-Xms512m", "cloud-storage-disk-api.jar", "--spring.config.location=properties/application.yml", "&"]



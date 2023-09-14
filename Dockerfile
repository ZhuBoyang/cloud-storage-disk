FROM centos:7

MAINTAINER zhuboyang <zhuboyang1996@foxmail.com>

RUN yum install -y vim wget gcc gcc-c++ cmake make nasm yasm unzip; \
    wget http://storage.yangcloud.online/pan/jdk-8u261-linux-x64.tar.gz; \
    tar -zxvf jdk-8u261-linux-x64.tar.gz; \
    mv -f jdk1.8.0_261 /usr/local/jdk1.8; \
    wget http://storage.yangcloud.online/pan/ffmpeg-6.0.tar.xz; \
    tar -xvf ffmpeg-6.0.tar.xz; \
    cd ffmpeg-6.0; \
    ./configure --prefix=/usr/local/ffmpeg; \
    make && make install; \
    rm -rf jdk-8u261-linux-x64.tar.gz ffmpeg-6.0.tar.xz ffmpeg-6.0 Dockerfile

ENV JAVA_HOME=/usr/local/jdk1.8 \
    CLASSPATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar \
    FFMPEG_HOME=/usr/local/ffmpeg \
    PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$FFMPEG_HOME/bin

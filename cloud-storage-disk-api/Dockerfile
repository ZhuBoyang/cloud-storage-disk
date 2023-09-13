FROM centos:7

MAINTAINER zhuboyang <zhuboyang1996@foxmail.com>

WORKDIR /opt/webapps/cloud-storage-disk/

COPY ./web/target/cloud-storage-disk-api.jar /opt/webapps/cloud-storage-disk/cloud-storage-disk-api.jar

RUN yum install -y vim wget gcc gcc-c++ cmake make nasm yasm unzip; \
    wget http://storage.yangcloud.online/pan/jdk-8u261-linux-x64.tar.gz; \
    tar -zxvf jdk-8u261-linux-x64.tar.gz; \
    mv -f jdk1.8.0_261 /usr/local/jdk1.8; \
    wget http://storage.yangcloud.online/pan/properties.zip; \
    unzip properties.zip; \
    wget http://storage.yangcloud.online/pan/ffmpeg-6.0.tar.xz; \
    tar -xvf ffmpeg-6.0.tar.xz; \
    cd ffmpeg-6.0; \
    ./configure --prefix=/usr/local/ffmpeg; \
    make && make install; \
    rm -rf *.tar.gz *.tar.xz ffmpeg-6.0 properties Dockerfile

ENV JAVA_HOME /usr/local/jdk1.8
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV FFMPEG_HOME /usr/local/ffmpeg
ENV PATH $PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$FFMPEG_HOME/bin

VOLUME ["/opt/webapps/cloud-storage-disk/properties", "/opt/webapps/storage/cloud-storage-disk"]

EXPOSE 8100

CMD ["java", "-jar", "-Xmx512m", "-Xms512m", "cloud-storage-disk-api.jar", "--spring.config.location=properties/application.yml", "&"]
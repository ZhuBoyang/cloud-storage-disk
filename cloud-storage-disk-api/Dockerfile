FROM centos:7

MAINTAINER zhuboyang <zhuboyang1996@foxmail.com>

WORKDIR /opt/webapps/cloud-storage-disk/

RUN yum install -y wget epel-release; \
    rpm --import http://li.nux.ro/download/nux/RPM-GPG-KEY-nux.ro; \
    rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-5.el7.nux.noarch.rpm; \
    yum install -y ffmpeg ffmpeg-devel; \
    wget http://storage.yangcloud.online/pan/jdk-8u261-linux-x64.tar.gz; \
    tar -zxf jdk-8u261-linux-x64.tar.gz; \
    mv -f jdk1.8.0_261/ /usr/local/jdk1.8; \
    rm -rf jdk-8u261-linux-x64.tar.gz; \
    wget http://storage.yangcloud.online/pan/properties.zip; \
    unzip properties.zip; \
    rm -rf properties.zip Dockerfile 

ENV JAVA_HOME /usr/local/jdk1.8
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH $PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$FFMPEG_HOME/bin

VOLUME ["/opt/webapps/cloud-storage-disk/properties", "/opt/webapps/storage/cloud-storage-disk"]

EXPOSE 8100

COPY ./web/target/cloud-storage-disk-api.jar /opt/webapps/cloud-storage-disk/cloud-storage-disk-api.jar

CMD ["java", "-jar", "-Xmx512m", "-Xms512m", "cloud-storage-disk-api.jar", "--spring.config.location=properties/application.yml", "&"]
FROM centos:7

MAINTAINER zhuboyang <zhuboyang1996@foxmail.com>

WORKDIR /opt/webapps/cloud-storage-disk/

RUN yum install -y epel-release unzip; \
    rpm --import http://li.nux.ro/download/nux/RPM-GPG-KEY-nux.ro; \
    rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-5.el7.nux.noarch.rpm; \
    yum install -y ffmpeg ffmpeg-devel; \
    cd /root/; \
    curl -o jdk-8u261-linux-x64.tar.gz http://storage.yangcloud.online/pan/jdk-8u261-linux-x64.tar.gz; \
    tar -zxf jdk-8u261-linux-x64.tar.gz; \
    mv -f jdk1.8.0_261/ /usr/local/jdk1.8; \
    curl -o properties.zip http://storage.yangcloud.online/pan/properties.zip; \
    unzip properties.zip; \
    curl -o openoffice4.tar.gz http://storage.yangcloud.online/pan/openoffice4.tar.gz; \
    tar -zxf openoffice4.tar.gz; \
    cd /root/en-US/RPMS; \
    yum localistall -y *.rpm; \
    cd /root/en-US/RPMS/desktop-integration; \
    yum localistall -y openoffice4.1.14-redhat-menus-4.1.14-9811.noarch.rpm; \
    yum install -y libXext.x86_64 freetype; \
    yum groupinstall -y "X Window System"

ENV JAVA_HOME=/usr/local/jdk1.8 \
    CLASSPATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar \
    PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$FFMPEG_HOME/bin

VOLUME ["/opt/webapps/cloud-storage-disk/properties", "/opt/webapps/storage/cloud-storage-disk"]

EXPOSE 8100

COPY ./web/target/cloud-storage-disk-api.jar /opt/webapps/cloud-storage-disk/cloud-storage-disk-api.jar

CMD ["java", "-jar", "-Xmx512m", "-Xms512m", "cloud-storage-disk-api.jar", "--spring.config.location=properties/application.yml", "&"]
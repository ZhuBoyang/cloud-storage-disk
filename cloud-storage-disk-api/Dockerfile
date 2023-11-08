FROM centos:7

MAINTAINER zhuboyang <zhuboyang1996@foxmail.com>

WORKDIR /opt/webapps/cloud-storage-disk/

COPY ./web/target/cloud-storage-disk-api.jar /opt/webapps/cloud-storage-disk/cloud-storage-disk-api.jar

# install ffmpeg
RUN yum install -y epel-release unzip; \
    rpm --import http://li.nux.ro/download/nux/RPM-GPG-KEY-nux.ro; \
    rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-5.el7.nux.noarch.rpm; \
    yum install -y ffmpeg ffmpeg-devel; \
    cd /root/; \
    # install and config JDK 1.8
    curl -o jdk-8u261-linux-x64.tar.gz http://storage.yangcloud.online/pan/jdk-8u261-linux-x64.tar.gz; \
    tar -zxf jdk-8u261-linux-x64.tar.gz; \
    mv -f jdk1.8.0_261/ /usr/local/jdk1.8; \
    # download system property
    curl -o properties.zip http://storage.yangcloud.online/pan/properties.zip; \
    unzip properties.zip; \
    # download and install openoffice4
    cd /root/; \
    curl -o openoffice4.tar.gz http://storage.yangcloud.online/pan/openoffice4.tar.gz; \
    tar -zxf openoffice4.tar.gz; \
    cd /root/en-US/RPMS; \
    yum localinstall -y *.rpm; \
    cd /root/en-US/RPMS/desktop-integration; \
    yum localinstall -y openoffice4.1.14-redhat-menus-4.1.14-9811.noarch.rpm; \
    yum install -y libXext.x86_64 freetype; \
    yum groupinstall -y "X Window System"; \
    # download, install and config chinese fonts \
    cd /usr/share/fonts; \
    curl -o fonts.zip http://storage.yangcloud.online/pan/font.zip; \
    unzip font.zip; \
    mv font chinese; \
    yum install -y mkfontscale; \
    mkfontscale; \
    mkfontdir;

ENV JAVA_HOME /usr/local/jdk1.8
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV OPENOFFICE_HOME /opt/openoffice4/program
ENV PATH $PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$FFMPEG_HOME/bin:$OPENOFFICE_HOME
ENV LANG en_US.UTF-8
ENV LC_ALL en_US.UTF-8
ENV LC_CTYPE en_US.UTF-8

VOLUME ["/opt/webapps/cloud-storage-disk/properties", "/opt/webapps/storage/cloud-storage-disk"]

EXPOSE 8100

CMD ["java", "-jar", "-Xmx512m", "-Xms512m", "cloud-storage-disk-api.jar", "--spring.config.location=properties/application.yml", "&"]
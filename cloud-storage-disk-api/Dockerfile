FROM basic-service

MAINTAINER zhuboyang <zhuboyang1996@foxmail.com>

WORKDIR /opt/webapps/cloud-storage-disk/

COPY ./web/target/cloud-storage-disk-api.jar /opt/webapps/cloud-storage-disk/cloud-storage-disk-api.jar

RUN wget http://storage.yangcloud.online/pan/properties.zip; \
    unzip properties.zip; \
    rm -rf properties.zip Dockerfile

VOLUME ["/opt/webapps/cloud-storage-disk/properties", "/opt/webapps/storage/cloud-storage-disk"]

EXPOSE 8100

CMD ["java", "-jar", "-Xmx512m", "-Xms512m", "cloud-storage-disk-api.jar", "--spring.config.location=properties/application.yml", "&"]
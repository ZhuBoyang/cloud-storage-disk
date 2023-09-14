FROM basic-service

MAINTAINER zhuboyang <zhuboyang1996@foxmail.com>

WORKDIR /opt/webapps/cloud-storage-disk/

COPY ./dist /opt/webapps/cloud-storage-disk/html

RUN yum install -y vim wget gcc gcc-c++ make libtool zlib-devel pcre-devel openssl openssl-devel; \
    wget http://storage.yangcloud.online/pan/node-v16.14.2-linux-x64.tar.gz; \
    tar -zxvf node-v16.14.2-linux-x64.tar.gz; \
    mv node-v16.14.2-linux-x64/ /usr/local/node-v16.14.2; \
    ln -s /usr/local/node-v16.14.2/bin/node /usr/local/bin/node; \
    ln -s /usr/local/node-v16.14.2/bin/npm /usr/local/bin/npm; \
    wget http://storage.yangcloud.online/pan/nginx-1.20.2.tar.gz; \
    tar -zxvf nginx-1.20.2.tar.gz; \
    cd nginx-1.20.2; \
    ./configure --prefix=/usr/local/nginx --with-http_ssl_module --with-http_gzip_static_module --with-stream_ssl_module; \
    make && make install; \
    cd ..; \
    mv -f html/* /usr/local/nginx/html/; \
    rm -rf *.tar.gz Dockerfile html/ nginx-1.20.2/; \
    cd /usr/local/nginx/conf; \
    rm -rf nginx.conf; \
    wget http://storage.yangcloud.online/pan/nginx.conf; \
    mkdir vhost && cd vhost; \
    wget http://storage.yangcloud.online/pan/pan.conf

ENV NGINX_HOME /usr/local/nginx
ENV PATH $PATH:$NGINX_HOME/sbin

VOLUME ["/usr/local/nginx/conf/vhost"]

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
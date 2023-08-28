# 煮雨云存储

## 项目介绍

一款开源的云存储（网盘）系统，支持个人存储或多人存储。

## 功能列表

① 注册（使用邮箱进行注册）

② 登录

③ 上传文件

④ 复制文件

⑤ 移动文件

⑥ 新建文件夹

⑦ 下载文件

⑧ 删除文件

## TODO

① 用户账户设置

② 文件分享

③ 文件转存

④ 在线解/压缩

⑤ 文件预览（支持常用文件）

⑥ 视频/音频在线播放

⑦ 上传文件夹

⑧ 全站文件管理（管理端，可以管理整个站点中所有的文件）

⑨ 下载非`mp4`格式视频（尽量做到，不保证）

⑩ 下载非`mp3`格式音频（尽量做到，不保证）

注：大家如果有想要实现的功能，可以留言评论。功能会出现在`TODO`中，但是不保证功能的实现时间。另，上述的`TODO`功能列表的实现时间并不是按照顺序来的。

## 运行环境

- `NodeJS`（可以启动`Vue3`的版本即可）

- `JDK1.8`

## 项目技术栈

- `Spring Boot 2.7.6`

- `MyBatis Spring Boot 2.3.0`

- `Fluent MyBatis 1.9.9`

- `mail 1.4.7`

- `Hutool 5.8.15`

- `Redis 6`

- `MySQL 8`

- `Vue 3.2.45`

## 部署方案

### `Docker`部署方式

[Docker 部署方式](https://gitee.com/yang_cloud/cloud-storage-disk/blob/master/Docker%E9%83%A8%E7%BD%B2.md)

## 系统截图

### 登录页

![](https://gitee.com/yang_cloud/cloud-storage-disk/raw/master/readme_images/login.png)

### 文件列表页

![](https://gitee.com/yang_cloud/cloud-storage-disk/raw/master/readme_images/file_list.png)

### 文件上传

![](https://gitee.com/yang_cloud/cloud-storage-disk/raw/master/readme_images/upload_file.png)

### 新建文件夹

![](https://gitee.com/yang_cloud/cloud-storage-disk/raw/master/readme_images/mkdir.png)

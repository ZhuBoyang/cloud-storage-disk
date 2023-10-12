# 煮雨云存储

## 项目介绍

一款开源的云存储（网盘）系统，支持个人存储或多人存储。

## 功能列表

- 注册（使用邮箱进行注册）
- 登录
- 上传文件
- 复制文件
- 移动文件
- 新建文件夹
- 下载文件
- 删除文件
- 用户账户设置
- 文件预览（已支持视频、音频预览）

## TODO

- 文件分享
- 文件转存
- 在线解/压缩
- 上传文件夹
- 全站文件管理（管理端，可以管理整个站点中所有的文件）
- 下载非`mp4`格式视频（尽量做到，不保证）
- 下载非`mp3`格式音频（尽量做到，不保证）

注：大家如果有想要实现的功能，可以留言评论。功能会出现在`TODO`中，但是不保证功能的实现时间。另，上述的`TODO`功能列表的实现时间并不是按照顺序来的。

## 运行环境

- `NodeJS`（可以启动`Vue3`的版本即可）

- `JDK1.8`

## 项目技术栈

- `Spring Boot 2.7.6`

- `MyBatis Spring Boot 2.3.0`

- `Fluent MyBatis 1.9.9`

- `Hutool 5.8.15`

- `Redis 7`

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

## 更新历史

> 0.3.1 BUG 修复

- 修复账户空间容量计算 BUG

> 0.3 音频文件预览（音频播放）

- 音频播放
- 音频列表
- 暂停、播放、上一首、下一首
- 音量控制、播放进度控制
- 播放模式切换：列表循环；单曲循环；顺序播放

> 0.2 视频文件预览（视频播放）

- 视频播放
- 视频列表
- 暂停、播放、上一个视频、下一个视频、快进、快退
- 音量控制、播放进度控制、进入或取消网页全屏、进入或取消全屏

> 0.1 网盘基础功能

- 创建文件夹
- 文件上传
- 文件移动
- 文件复制
- 文件下载
- 文件删除
- Docker 化部署

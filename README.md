# OJ代码测评系统

该项目是基于微服务的算法题在线评测系统，分为网关、用户、在线判题和代码沙箱4个微服务，其中用户和网关微服务复用了之前的开发社区项目。在系统前台，管理员可以创建、管理题目，用户可以搜索、查看题目，编写代码并进行在线自测和提交。在系统后端，自主实现了代码沙箱，能够根据接收的测试用例对代码进行编译、运行并给出输出结果。代码沙箱作为独立服务，可以提供给其他开发者使用。

后端实现了ACM模式的本地代码沙箱和Args模式的Docker沙箱（线上使用的是本地代码沙箱），实现了提交统计，支持在线测试。

## 在线访问
[lilyOj在线做题系统](http://43.139.241.66:8101)（测试账号：admin 密码：adminadmin）

## github仓库

- 后端-微服务地址：[lily-oj-microservice](https://github.com/lilyWhenVia/lily-OnlineJudge/tree/master/lily-oj-microservice)
- 后端-单体项目地址：[lilyOnlineJudge-backend-master](https://github.com/lilyWhenVia/lily-OnlineJudge/tree/master/lilyOnlineJudge-backend-master)
- 自主开发的Docker代码沙箱：[NativeCodeSandbox](https://github.com/lilyWhenVia/lily-OnlineJudge/tree/master/NativeCodeSandbox)
- 前端地址：[lilyoj-frontend-master](https://github.com/lilyWhenVia/lily-OnlineJudge/tree/master/lilyoj-frontend-master)

## 目录

## 项目结构



---

### Docker代码沙箱

使用Docker实现的代码沙箱和通过Java的`Runtime`实现的代码沙箱各有其优缺点，具体取决于您的需求和用例：

**Docker 实现的代码沙箱：**

优点：

1. **强大的隔离性：** Docker容器提供了强大的隔离性，可以将用户代码隔离到独立的容器中，防止其访问主机系统资源。
2. **资源限制：** Docker允许您精确地限制容器的资源使用，包括CPU、内存和磁盘空间。
3. **容易部署和管理：** Docker容器可以轻松部署和管理，可以快速启动和停止，也支持自动化部署和扩展。
4. **安全性：** 使用容器化技术，容易实现安全配置，可以控制容器的访问权限和网络连接，增强了安全性。

缺点：

1. **资源开销：** Docker容器相对较重，需要一定的系统资源和存储空间。
2. **启动时间：** 相比于`Runtime`方式，启动一个Docker容器需要更多时间。
3. **复杂性：** Docker容器的设置和管理相对较复杂，需要一定的学习曲线。

**通过Java的 `Runtime` 实现的代码沙箱：**

优点：

1. **轻量级：** 与Docker容器相比，使用`Runtime`执行外部进程的方式更加轻量级，不需要额外的容器化资源。
2. **启动快速：** 启动外部进程通常比启动Docker容器更快。
3. **简单：** 相对于Docker，使用`Runtime`的方式相对简单，无需熟悉Docker的配置和管理。

缺点：

1. **有限的隔离性：** 使用`Runtime`方式执行外部进程时，无法获得与Docker容器相同的隔离性和安全性。用户代码可能能够访问主机系统资源。
2. **资源限制：** 限制资源（如CPU、内存）的精确度较低，较难实现。
3. **安全性风险：** 由于较低的隔离性，可能存在安全性风险，尤其是当运行不受信任的代码时。


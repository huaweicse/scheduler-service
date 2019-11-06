# scheduler-service

定时任务服务。支持多种定时触发引擎，目前采用Quartz作为默认的任务引擎。

## 设计

![](docs/image/design.png)

## 界面

![](docs/image/jobs.png)

## 使用

1. 运行scheduler-website, edge-service, scheduler-server, 访问：

```
http://localhost:18090/ui/scheduler-website/jobs.html
```
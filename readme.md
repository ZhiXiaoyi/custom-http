# http工具包概要设计

[TOC]

---

## 修订历史

|文档版本|更新时间|内容|
|---|---|---|
|1.0.1|2017-12-29|创建|

## 概述
http客户端调用包，基于最新的httpclient4.5.3开发和spring容器，针对现有市民支付和二维码业务需求做封装。 1、http连接池。2、可配置正向代理。3、可配置忽略https中CA用户证书认证。4、提供form表单和body两种方式请求。包含功能 1、简便连接网关。2、连接第三方机构。
## 使用说明
### 工具类
#### CustomReqUtil（主调工具类）
工具类中分别封装了httpClient发送本项目网关和第三方机构的两个静态方法。

 ***(调用网关需要实现配置，调用第三方直接传入配置参数即可。)***  
#### HttpToolBeanMgr 引用方需实现Bean
```
<!-- http工具包注入 -->
<bean id="httpToolBeanMgr" class=" com.zhixiaoyi.custom.http.CoustomHttpToolBeanMgr">
    <constructor-arg name="gatewayConfigService" ref="gatewayConfigServiceImpl"></constructor-arg>
</bean>
```
#### IGatewayConfigService
在使用本工具调用网关方法的时候需要在项目中实现本接口，对网关的一些参数进行配置。
```
/**
 * http工具包--实现网关配置
 */
@Service
public class GatewayConfigServiceImpl implements IGatewayConfigService {
    @Autowired
    private GatewayInfoBO gatewayInfoBO;
    @Override
    public GatewayHttpSetting getGatewayHttpSetting() {
        GatewayHttpSetting gatewayHttpSetting = new GatewayHttpSetting();
        gatewayHttpSetting.setPrivateKey(gatewayInfoBO.getPrivateKey());
        gatewayHttpSetting.setDeveloperId(gatewayInfoBO.getDeveloperId());
        gatewayHttpSetting.setGatewayPublicKey(gatewayInfoBO.getPublicKey());
        gatewayHttpSetting.setGatewayUrl(gatewayInfoBO.getGatewayUrl());
        return gatewayHttpSetting;
    }
}
```
#### SignUtil
签名工具类，可以进行签名和验签操作。
#### HttpClientUtil
一个简单的httpClient连接工具类，包含Form表单提交和Body传递Json等字符串两种方式。

## 名称说明
### http连接池
在七层网络协议中，http协议属于应用层协议，主要聚焦在规范数据层面，定义报文规则，使接收方能正确解析和响应，而在应用层下面的传输层，主要聚焦数据传输层面，包括tcp、ftp协议。http请求（连接）最终必须绑定到一个tcp连接进行上下行传输。传统http请求流程，为收到http请求，封装http报文，建立tcp连接，传输数据，收到响应，断开tcp连接，请求完成，每次请求都要新建一个tcp连接。httpclient建立的连接池是传输层tcp连接。接收到http后，包装http报文，在发送时，从连接池中获取一个tcp连接进行传输，传输完毕后不关闭tcp连接，而是归还给连接池，由连接池维护tcp连接的生命周期，从而实现tcp连接的复用，减少系统和服务端频繁建立tcp连接的消耗，在高并发时这个损耗比较大。另一方面，在高并发时，通过连接池可以有效的减少并发tcp连接数，减少客户端服务监听端口消耗，监听端口数量是有限，从而提高系统吞吐量，用少量tcp连接处理大量http请求，增加处理性能。最后，对于服务端，并发的tcp连接减少了，减少了服务端处理并发峰值压力，增加服务端稳定性。

### 正向代理
考虑到上线后部署情况，要通过前置机对外发送请求，如上传订单到支付机构，通知受理终端等，封装包中可以配置是否使用代理。

### 忽略CA用户证书验证
第三方https可能没有购买ca证书，如果不添加忽略ca证书，会请求失败。

## 使用说明

### 工具类
#### CoustomHttpClientUtil（主调工具类）
工具类中封装了httpClient发送请求的静态方法。  
#### HttpURLConnectionUtil
工具类中封装了JDK中自带HttpURLConnection发送http请求静态方法，对于不需要连接池或者特殊需求连接，可以使用此方法。
#### HttpClientPool
此类中提供了，设置连接池属性的静态方法，不配置则用默认配置。  

### 配置属性
#### ConnectionTimeout
建立连接超时时间，创建tcp连接的超时时间。调小这个值，可以有效减少异常请求地址对连接的占用，将连接留给质量好的请求。
#### SocketTimeout
响应超时时间，两次响应之间间隔时间，对于一般请求只有一次响应，为服务端响应超时时间，对处理时间较长的接口，需要调整此值。调小此连接也可以提高响应速度，增加系统吞吐，会增加异常请求概率，需要采用重发机制，此时服务端要考虑幂等处理。httpclient中还有一个配置BackoffStrategyExec，可以对响应差的路由地址，进行降格处理，减少此路由连接数，把连接资源让给其他路由。
#### RequestConnectionTimeout
从连接池中获取连接的超时时间。在高并发情况下，连接池会不断创建新的连接，直到达到连接池最大连接数，此时连接池中已无空闲连接情况下，新的获取连接请求会处于等待状态，这个参数就是设置等待超时时间。调小这个时间，可以有效的提高响应速度并降低积压请求量，但是会增加请求失败概率。
#### PoolMaxTotal
连接池最大总连接数。
#### MaxPerRoute
连接池最大路由连接数，一个主机和端口组成一个路由，针对此路由设置的最大连接数。主要限制每个路由连接数，不会出现某个路由占用整个连接池连接数，导致其他路由生成不了连接情况。这个可以根据具体情况设置，如果已知同意时刻只有一个路由存在时，可以将路由连接数等于最大连接数。
#### IsIgnoreSSL
是否绕过ssl检查，默认不绕过。

### httpclient自带默认值
httpclient默认采用连接池，默认连接总数是20，每个路由连接数是2，凡创建连接时不设置连接池配置，都采用默认连接池配置。这是一个很坑的默认配置，所以尽量主动设置连接池配置。
创建默认连接写法有两种
方法一

```
 CloseableHttpClient httpClient = HttpClients.createDefault();
```
方法二

```
RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(30000)
        .setConnectionRequestTimeout(60000)
        .setSocketTimeout(5000)
        // 默认允许自动重定向
        .setRedirectsEnabled(true).build();
CloseableHttpClient httpClient = HttpClients.custom()
        .setDefaultRequestConfig(requestConfig)
        .build();
```




### 验证测试
说明：测试代码都放到同父工程下citizen_tool_test项目下。
#### 验证连接数
基础参数：建立链接超时时间30秒，响应超时时间5秒，获取链接超时时间60秒，设置线程池方式，连接池总数200，路由连接数20。 
服务端，开发一个测试接口，接口处理过程为睡眠2秒；  
客户端，起8000个线程，向服务端发起请求，分别采用HttpURLConnect无线程池方式，默认httpClient创建方式，设置线程池方式，然后在服务端监听端口占用情况，即可验证建立tcp链接数。  
服务端命令行,输入以下命令，输入当前监听端口和建立的tcp连接数，测试中服务端端口号8081

```
netstat -anop|grep 8081 |awk 'BEGIN {count=0} {count=count+1} END{print count}'
```
**结论：**   
1、HttpURLConnect无线程池方式，会逐渐创建连接,到达4300个tcp连接时，上下浮动，并出现大量建立连接超时异常，和出现部分响应超时请求，说明服务端servlet线程已耗尽，部分请求在服务端等待servlet线程时超时，并无法建立新的端口监听，首先，这种情况服务端会消耗连接进行等待，而这部分连接中一部分理论上不会被执行，但是客户端和服务端都已经建立这些连接，消耗资源，影响处理性能。另外服务端servlet线程耗尽，服务端并发达到最大，影响稳定性。如果客户端采用线程池，则可以减少等待线程消耗和降低服务端最高并发，将请求消耗前置，用较少tcp连接处理大量http请求，增加服务端稳定性，当然需要通过业务预测和监控，设置合适的线程池总数和路由数，避免服务端资源浪费。  
2、默认httpClient创建方式，很快创建2个tcp链接，在60秒内，全部正常，60秒后，全部为获取连接超时，符合预期。

```
org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
	at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.leaseConnection(PoolingHttpClientConnectionManager.java:292)
	at org.apache.http.impl.conn.PoolingHttpClientConnectionManager$1.get(PoolingHttpClientConnectionManager.java:269)
	at org.apache.http.impl.execchain.MainClientExec.execute(MainClientExec.java:191)
	at org.apache.http.impl.execchain.ProtocolExec.execute(ProtocolExec.java:185)
	at org.apache.http.impl.execchain.RetryExec.execute(RetryExec.java:89)
	at org.apache.http.impl.execchain.RedirectExec.execute(RedirectExec.java:111)
	at org.apache.http.impl.client.InternalHttpClient.doExecute(InternalHttpClient.java:185)
	at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:83)
	at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:108)
```
3、设置线程池方式，启动后很快建立20个连接，在60秒内，全部正常，60秒后，全部为获取连接超时，符合预期。
#### 性能测试
通过压测方式，测试出在服务端servlet耗尽情况下业务处理速度降低程度和失败率。待处理

### 调用案例
普通默认配置调用，默认请情况使用连接池，大小20个并发，每个路由2个并发。

```
String url="http://test.money.iego.cn/payqrcode-debug/api/cert/validQrcode";
Map<String,Object> param = new HashMap<>();
param.put("userName","张三");
param.put("sno","S222001");
String rst = BsHttpClientUtils.sendForm(url,param);
System.out.println("rst="+rst);
```
自定义连接池方式

```
//连接池配置，项目启动时初始化一次即可
HttpClientPoolConfig httpClientPoolConfig = new HttpClientPoolConfig();
httpClientPoolConfig.setIgnoreSSL(true); //忽略ssl验证
httpClientPoolConfig.setConnectionTimeout(20000);//获取连接超时时间（毫秒）
httpClientPoolConfig.setSocketTimeout(5000);//响应超时时间（毫秒）
httpClientPoolConfig.setPoolMaxTotal(100);//连接池大小
httpClientPoolConfig.setMaxPerRoute(50);//每个路由最大值
HttpClientPool.init(httpClientPoolConfig);

//http调用
String url="http://test.money.iego.cn/payqrcode-debug/api/cert/validQrcode";
Map<String,Object> param = new HashMap<>();
param.put("userName","张三");
param.put("sno","S222001");
String rst = BsHttpClientUtils.sendForm(url,param);
System.out.println("rst="+rst);
```






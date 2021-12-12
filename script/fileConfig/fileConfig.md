# 文件配置

<p align="center"><strong>简单的文件配置即可满足网关运行所需要的元数据条件</strong></p>

## 配置文件格式

- 默认为json格式
- 可通过SPI扩展机制完成其他格式文件的定制，请参考其他文件的扩展机制

## 配置范围

- 黑名单配置信息(cliviaBlacklistConfig)
- api配置信息(cliviaApiConfig)
- 节点安全信息(cliviaClientSecurityConfig)

## 配置信息

### cliviaBlacklistConfig

|  属性名称   | 属性含义  |  属性示例|是否必填|默认值|
|  ---  | ---  | ---|---|---|
| blacklistId  | 标识 | 123434NNasdas|否|无|
| blacklistIp  | 受限制的 | 172.10.20.54或者<br/>172.10.* . * 或者172.10.20.*|是|无|
| state  | 状态(0=启用,1=已关闭) | 0|是|无|
| groupId  | 逻辑组标识 | 1|是|无|

- state为0时，该配置将会生效。
- blacklistId建议在该json文件中保证唯一性。
- blacklistIp不要配置诸如http或者https前缀。该属性支持完整的的IP格式外也可以使用172.10.*.* 或者172.10.1.*的方式去配置黑名单IP
- 若所属黑名单IP属于同一个逻辑组，请配置相同的groupId。

### cliviaClientSecurityConfig

|  属性名称   | 属性含义  |  属性示例|是否必填|默认值|
|  ----  | ----  | ----|----|----|
| id  | 标识 | qwee11223|否|无|
| clientName  | 网关节点名称 | gateway_node_1|是|无|
| clientPwd  | 网关节点密码 | kl*&a233p|是|无|
| token  | 网关节点token | yuj78%42)Pm|是|无|
| state  | 状态(0=启用,1=已关闭) | 0|是|无|

- state为0时，该配置将会生效
- token在该json文件中必须保证唯一性
- clientName,clientPwd组合后需在整个json文件中保证唯一性

### cliviaApiConfig

|  属性名称   | 属性含义  |  属性示例|是否必填| 默认值|
    |  ----  | ----  | ----|----|----|
| apiId  | 标识 | qwee11223|否|无|
| apiName  | api名称 | 查询用户接口|否|无|
| groupId  | 逻辑组标识 | 1|是|无|
| rpcType  | 远程调用类型(http,springcloud,apachedubbo,alibabadubbo) | http|否|http|
| version  | 版本号 | v0.0.1|否|无|
| apiType  | api类型(0=应用,1=接口) | 1|否|1|
| apiEnabled  | 状态(0=启用,1=已关闭) | 0|否|0
| apiParamModify  | 请求参数重写(当enabled为true,网关过滤器会动态新增、修改、删除请求参数。) | {\"enabled\":true,\"type\":0,\"modify\":\"a=b,c=d\"}|否|无/
| apiHeader  | 请求头重写(当enabled为true,网关过滤器会根据addHeader<br />或者removeHeader添加固定请求头或者删除指定请求头)| {\"enabled\":true,\"addHeader\":\"TEST-HEADER=CLIVIA-TEST,<br />TEST-HEADER2=CLIVIA-TEST2\",<br />\"removeHeader\":\"TEST-HEADER2,TEST-HEADER3\"}|否|无|
| apiRewrite  | 请求重写(暂支持转发路径重写。当enable为true,网关过滤器<br />会根据rewritePath进行转发路径重写) | {\"enabled\":true,\"rewritePath\":\"/admin/server/test\"}|否|无|
| url  | 请求路径 | /clivia-server/api/test|是|无|
| methodType  | 请求类型(post,get)| post|否|post|
| apiReqSize  | 请求大小(当enabled为true,网关过滤器会根据maxSize<br />并结合content-length进行请求大小判断)| {\"enabled\":true,\"maxSize\":500}|否|无|
| blacklistEnabled  | 开启黑名单状态(0=启用,1=已关闭) | 0|否|1|
| apiHystrix  | 状态(0=启用,1=已关闭) | 0|否|无|
| apiRequestLimit  | 限流(当enabled为true时,网关过滤器会根据请求<br />每秒速率以及令牌桶大小进行限流)| {\"enabled\":true,\"replenishRate\":10,<br />\"burstCapacity\":20}|否|无|
| apiHttpRoute  |http/springcloud负载均衡配置(loadbalanceRouters为负载均衡列表，<br />loadbalanceType为负载均衡策略，retryTimes为重试次数。<br />当远程调用类型为springcloud,则直接利用serviceId获取<br />注册中心上的服务列表)| {\"loadbalanceRouters":[{"upstreamUrl":"http://localhost:1023","upstreamWeight":1,"enabled":true,"timestamp":1627545741216,"warmup":1},{"upstreamUrl":"http://localhost:1023","upstreamWeight":3,"enabled":true,"timestamp":1627545741216,"warmup":2}],<br />"loadbalanceType":"roundRobin","clientIp":null,"retryTimes":1,"timeOutMillis":2000,"serviceId":null}|是|无|
| apiNonHttproute  | 状态(0=启用,1=已关闭) | 0|否|无|
| apiAuth  | 验签(当enabled为true时,网关过滤器会对请求进行MD5加密验证) | {\"enabled\":false,\"secureKey\":23423sczxc,\"invalid\":失效时间}|否|无|
| appKey  | appKey(相同app下依赖该字段对请求进行合法性校验，文件配置无须和App进行关联，直接配置该属性即可) | kkasdasdasdas|是|无|

## 配置项

|  配置项名称   | 配置项含义  |  示例|是否必填| 默认值|
 |  ----  | ----  | ----|----|----|  
|clivia.admin.config.type| 网关元数据配置方式(clivia.admin.config.type=0时，启用数据库配置。<br />当clivia.admin.config.type=1时启用web配置。当clivia.admin.config.type=2时启用文件配置)|clivia.admin.config.type=2|否|0|
|clivia.admin.file.config.type|网关元数据配置文件类型,该属性值即为已定制文件的后缀|clivia.admin.file.config.type=yaml|否|json|
|clivia.admin.config.absoluteFilePath|网关元数据文件存放路径(该属性值格式以/开头并以/结尾)|clivia.admin.config.absoluteFilePath=/opt/|否|/opt/clivia/gateway/|
|clivia.admin.config.refreshInterval|网关元数据配置刷新间隔时间(时间单位为分钟)|clivia.admin.config.refresh.schduledPeriod=10|否|5|

## 其他文件的扩展机制

- 在工程的resource下新建META-INF目录，添加SPI文件描述信息
- SPI文件描述信息格式为文件后缀类型=com.clivia.config.api.CliviaFileConfigParser接口的实现类全限定名。请参考clivia-fileCore-center下的SPI文件信息。
- 实现内容

~~~
public interface CliviaFileConfigParser {

    /**
     * read and return the config file content. By implementing this interface, you need to return List <
     * CliviaFileApiInfo > or List < CliviaFileBlacklistInfo > through file name or clazz。
     *
     * @author palading_cr
     *
     */
    <T> List<T> readFileContent(File configFile, Class<?> clazz);

}
~~~

## 注意事项

- 文件类型仅支持一种，不支持多种文件类型同时存在

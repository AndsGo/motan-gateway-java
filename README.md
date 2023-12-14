# motan-gateway-java

#### 介绍
motan java 网关,作为前端访问后端的统一入口。

#### 软件架构
翻阅了下资料
https://github.com/weibocom/motan/issues/581
实现motan网关有两种方式
> 1.使用motan-go开发网关。
> 2.修改motan源码，进行自定义转发。

由于对go语言不熟，修改motan源码的工作量比较大。因此这里选择了一个比较折中的方式。使用springboot开发motan网关，在原有的motan的基础上包装一层转发逻辑，实现motan接口的动态转发。
基本思路如下
![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/095421_b8c1bebe_1822070.png "屏幕截图.png")

网关和服务间使用一个公共的motan接口的方法进行调用，然后由公共run方法去根据约定好的规矩进行转发，去调用实际的方法。
这个样做的好处就是我不用关心服务提供方的接口变动情况，只需要按照约定的给定参数就是可以直接调用。无需像普通的motan rpc服务调用需要频繁重启服务。

#### 安装教程
将happo-motan-client-core打包，提交到私服上和本地仓库。
在cleint上和server上引用
```
    <dependency>
        <groupId>com.happotech</groupId>
        <artifactId>happo-motan-client-core</artifactId>
        <version>1.0.1</version>
    </dependency>

```
#### 使用说明

##### 服务端使用
必须包含 String run(String param) 方法。
```
/**
 * @author songxulin
 * @date 2021/4/14
 */
public interface IActMotanService {

    String run(String param) throws Exception;
}
```
实现
```
@MotanService
public class ActMotanServiceImpl implements IActMotanService {


    @Override
    public String run(String param) throws Exception {
        return MotanRedirectUtil.responseRun(param,IActMotanService.class);
    }
}
```

#### 网关端使用
````
@RestController
public class MotanController {
    @MotanReferer
    private IMotanService iMotanService;
    @Autowired
    private LoginUtil loginUtil;
    //ApiUrlPrefix 为接口类的全路径
    private static final String ApiUrlPrefix = "/com/happotech/actmgr/motan/service/api/IMgrService/";

    @RequestMapping(ApiUrlPrefix + "{method}")
    @ResponseBody
    public JSONObject urcService(@RequestBody String body, @PathVariable("method") String method, HttpServletRequest request)
            throws Exception {
        loginUtil.checkTicket(body);
        return MotanRedirectUtil.requestRun(iActMotanService, body, ApiUrlPrefix, method);
    }
}


# 基于Redis存储的Bloom Filter

* [什么是BloomFilter](https://www.cnblogs.com/z941030/p/9218356.html)
* 简单使用demo见 [com.joe.DuplicateCheckTest](https://github.com/I-Guitar/RedisBloomFilter/blob/master/src/main/java/com/joe/DuplicateCheckTest.java)



#### 简要说明

* url 查重

```java
// redis操作，实现指定接口，调用工具类的时候传入
JedisOpt jedisOpt = new MyJedisOpt();

// url 查重，默认使用“DUPLICATE_”加域名作为nameSpace
// 默认配置为：
// 查重容量 = 10000000（一千万）
// 查重允许错误率 = 0.0001（万分之一）
// 查重nameSpace持续时间 = 一天
// 可在工具类中手动配置，或调用方法手动设置 DuplicateCheckUtils.addConf()
String url = "https://www.xunleicang.com/vod-read-id-9817.html";
boolean isExists = DuplicateCheckUtils.addUrlDuplicate(jedisOpt, url);  // 添加并返回查重
boolean isExists2 = DuplicateCheckUtils.isUrlExists(jedisOpt, url);  // 查询是否存在
```



* 一般字符串查重

```java
// 需指定对应的nameSpace
String nameSpace = "myNameSpace";
String str = "她冲向甲板，试图跳入大海结束一生。杰克及时发现并且在关键时刻以自己的真诚和独到的幽默说服";
// 手动添加配置，否则使用默认配置（默认配置与url查重相同）
DuplicateCheckUtils.addConf(nameSpace, 10000000L, 0.00001, 0);
// 添加并返回查重结果
boolean isExists3 = DuplicateCheckUtils.addDuplicate(jedisOpt, nameSpace, str);
// 查询是否存在
boolean isExists4 = DuplicateCheckUtils.isExists(jedisOpt, nameSpace, str);
```




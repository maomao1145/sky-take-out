## Redis入门
---
### Redis简介

Redis是一个基于内存的key-value(键值对)结构数据库

| key  | value |
| ---- | ----- |
| id   | 101   |
| name | 小智    |
| city | 北京    |
* 基于内存存储,读写性更高
* 适合存储热点数据(热点商品,咨询,新闻)

并不是对mysql的替换,是一种对mysql的补充

官网:https://redis.io
中文网:https://www.redis.net.cn/

### Redis的下载和安装

https://github.com/tporadowski/redis/releases




### Redis的使用

解压压缩包在路径中输入cmd
![[Pasted image 20260508161803.png]]

在当前界面ctrl+C退出服务

---

![[Pasted image 20260508162201.png]]
-h host -p 端口号
![[Pasted image 20260508162316.png|697]]

---
修改redis.windows.conf配置文件改成需要密码

去掉前面的#即可 保存退出重启服务
![[Pasted image 20260508162546.png ]]


### AnotherRedisDesktopManage
相当于对Redis的图形化界面
https://github.com/qishibo/AnotherRedisDesktopManager/releases/tag/v1.7.1


## Redis数据类型

* 字符串 string : 普通字符串 Redis中最简单的数据类型
* 哈希 hash : 也叫散列, 类似于java中的HashMap结构
* 列表 list : 按照插入顺序排序,可以有重复元素, 类似于java中的LinkedList
* 集合 set : 无序集合, 没有重复元素
* 有序集合 sort set / zset : 集合中每个元素关联一个分数(score), 根据分数升序排序, 没有重复元素
* 
![[Pasted image 20260508164129.png]]



## Redis中的常用命令

##### 字符串操作命令
  SET key value 设置指定key的值
  GET key 获取指定key的值
  SETEX key seconds value 设置指定key的值, 并将key1的过期时间设为senconds 秒
  SETEX key value 只有在key不存在的时候设置key的值
---
##### 哈希操作命令
  HSET key field value 将哈希表 key 的字段 field 的值设为 value
* HGET key field 获取存储在哈希表中指定字段的值
* HDEL key field 删除存储在哈希表中指定字段
* HKEYS key 获取哈希表中的所有字段
* HVALS key 获取哈希表中所有的值
  
   
  ![[Pasted image 20260508165958.png]]
---
##### 列表操作命令
* LPUSH key value1 [value2] 将一个或多个值插入到列表头部
* LRANGE key start stop 获取列表指定范围内的元素
* RPOP key 移除并获取列表最后一个元素
* LLEN key 获取列表长度
  ![[Pasted image 20260508170910.png]]

---
##### 集合操作命令

* SADD key member1 [member2] 向集合添加一个或多个成员
* SMEMBERS key 返回集合中所有的成员
* SCARD key 获取集合的成员数
* SINTER key1 [key2] 返回给定所有集合的交集
* SUNION key1 [key2] 返回所有给定集合的并集
* SREM key member1 [member2] 删除集合中一个或多个成员
* ![[Pasted image 20260508225451.png]]
---

##### 有序集合操作命令

* ZADD key score1 member1 [score2 member2] 向有序集合添加一个或多个成员
* ZRANGE key start stop [WITHSCORES] 通过索引区间返回集合中指定区间内的成员
* ZINCRBY key increment member 有序集合中对指定成员的分数加上增量 increment
* ZREM key member1 [member..] 移除有序集合中的一个或多个成员
* ![[Pasted image 20260508230438.png]]
---

##### 通用命令
KEYS pattern 查找所有符合给定模式(pattern)的key
EXISTS key 检查给定 key 是否存在
TYPE key 返回 key 所存储的类型
DEL key 该命令用于在 key 存在时删除 key

---
## 在java中操作Redis

Redis的Java客户端 常用的几种
* Jedis
* Lettuce
* Spring Data Redis
  Spring Data Redis是 Spring的一部分, 对Redis底层开发包进行了高度封装.
  在Spring项目中, 可以用Spring Data Redis来简化操作

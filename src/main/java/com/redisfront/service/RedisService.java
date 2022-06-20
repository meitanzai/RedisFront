package com.redisfront.service;

import com.redisfront.common.func.Fn;
import com.redisfront.model.ClusterNode;
import com.redisfront.model.ConnectInfo;
import com.redisfront.service.impl.RedisServiceImpl;
import redis.clients.jedis.ClusterPipeline;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisCluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RedisService {

    RedisService service = new RedisServiceImpl();

    ClusterPipeline getClusterPipeline(ConnectInfo connectInfo);

    JedisCluster getJedisCluster(ConnectInfo connectInfo);

    List<ClusterNode> getClusterNodes(ConnectInfo connectInfo);


    Map<String, Object> getClusterInfo(ConnectInfo connectInfo);

    Map<String, Object> getInfo(ConnectInfo connectInfo);

    Map<String, Object> getCpuInfo(ConnectInfo connectInfo);

    Map<String, Object> getMemoryInfo(ConnectInfo connectInfo);

    Map<String, Object> getServerInfo(ConnectInfo connectInfo);

    Map<String, Object> getKeySpace(ConnectInfo connectInfo);

    Map<String, Object> getClientInfo(ConnectInfo connectInfo);

    Map<String, Object> getStatInfo(ConnectInfo connectInfo);

    Boolean isClusterMode(ConnectInfo connectInfo);

    Long getKeyCount(ConnectInfo connectInfo);

    default Map<String, Object> strToMap(String str) {
        Map<String, Object> result = new HashMap<>();
        for (String s : str.split("\r\n")) {
            if (!Fn.startWith(s, "#") && Fn.isNotEmpty(s)) {
                String[] v = s.split(":");
                if (v.length > 1) {
                    result.put(v[0], v[1]);
                } else {
                    result.put(v[0], "");
                }
            }
        }
        return result;
    }

    default JedisClientConfig getJedisClientConfig(ConnectInfo connectInfo) {
        return DefaultJedisClientConfig
                .builder()
                .database(connectInfo.database())
                .user(connectInfo.user())
                .password(connectInfo.password())
                .build();
    }


}
package cn.devcms.redisfront.common.util;

import cn.devcms.redisfront.common.enums.ConnectEnum;
import cn.devcms.redisfront.common.func.Fn;
import cn.devcms.redisfront.common.ssl.SocketFactory;
import cn.devcms.redisfront.model.ConnectInfo;
import redis.clients.jedis.*;
import redis.clients.jedis.util.SafeEncoder;

import java.util.Arrays;
import java.util.List;

/**
 * RedisUtil
 *
 * @author Jin
 */
public class RedisUtil {

    public static Object sendCommand(ConnectInfo connect, Boolean enableCluster, String inputText) throws Exception {
        var connection = new Connection(new HostAndPort(connect.host(), connect.port()), createJedisClientConfig(connect));
        try (connection) {
            if (connection.ping()) {
                var commandList = new java.util.ArrayList<>(List.of(inputText.split(" ")));
                var command = Arrays.stream(Protocol.Command.values())
                        .filter(e -> Fn.equal(e.name(), commandList.get(0).toUpperCase()))
                        .findAny()
                        .orElse(null);
                commandList.remove(0);
                if (enableCluster) {
                    return connection.executeCommand(new ClusterCommandArguments(command).addObjects(commandList));
                }
                return encode(connection.executeCommand(new CommandArguments(command).addObjects(commandList)));
            } else {
                return "连接失败！";
            }

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static void main(String[] args) throws Exception {

        var object = RedisUtil.sendCommand(new ConnectInfo("A",
                        "127.0.0.1",
                        6379,
                        null,
                        null,
                        11,
                        false,
                        ConnectEnum.NORMAL),
                false,
                "SSCAN handing_oauth:client_id_to_access:test 0 COUNT 100");
        System.out.println();
        System.out.println();
    }

    private static DefaultJedisClientConfig createJedisClientConfig(ConnectInfo connect) throws Exception {
        if (connect.ssl()) {
            if (Fn.isNotNull(connect.sslConfig())) {
                var sslSocketFactory = SocketFactory.getSocketFactory(connect.sslConfig().publicKeyFilePath(), connect.sslConfig().grantFilePath(), connect.sslConfig().privateKeyFilePath(), connect.sslConfig().password());
                return DefaultJedisClientConfig
                        .builder()
                        .password(connect.password())
                        .database(connect.database())
                        .ssl(true)
                        .sslSocketFactory(sslSocketFactory)
                        .build();
            }
            return DefaultJedisClientConfig
                    .builder()
                    .password(connect.password())
                    .database(connect.database())
                    .ssl(true)
                    .build();
        } else {
            return DefaultJedisClientConfig.builder().user(connect.user()).password(connect.password()).database(connect.database()).build();
        }
    }

    private static Object encode(Object object) {
        if (object instanceof byte[] bytes) {
            return SafeEncoder.encode(bytes);
        } else if (object instanceof List<?> list) {
            return list.stream().parallel().map(RedisUtil::encode).toList();
        } else if (object instanceof Number number) {
            return number;
        } else if (object instanceof String str) {
            return str;
        } else if (object == null) {
            return ("null");
        }
        return object;
    }
}

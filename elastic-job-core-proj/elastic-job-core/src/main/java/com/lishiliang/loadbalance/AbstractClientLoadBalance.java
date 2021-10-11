package com.lishiliang.loadbalance;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lisl
 * @version 1.0
 * @desc : elastic-job多服务下 分布到不同服务运行的客户端负载均衡策略
 */
public abstract class AbstractClientLoadBalance {

    /**
     * 获取服务权重列表
     * @param serversWeight 例 : 192.168.50.16:1,192.168.50.18:-2,192.168.70.154:4,192.168.707.154:4
     * @param zkServerShadingItem
     * @return
     */
    public ConcurrentHashMap<String, Integer> getServersWeightMap(List<String> serversWeight, Map<String, Set<Integer>> zkServerShadingItem) {

        ConcurrentHashMap<String, Integer> serversWeightMap = new ConcurrentHashMap<>();
        if (serversWeight != null) {
            for (String serverWeight : serversWeight) {
                String[] pair = serverWeight.split(":");
                String key = pair[0];
                int weight = Integer.valueOf(pair[1]);
                //过滤掉不存在zk的服务 或者权重小于0的服务
                if (zkServerShadingItem.containsKey(key) && weight > 0) {
                    serversWeightMap.put(key, weight);
                }
            }
        }

        //如果没有配置权重 默认所有服务权重为1 即随机
        if (serversWeightMap.isEmpty()) {
            zkServerShadingItem.forEach((k,v)->{
                serversWeightMap.put(k, 1);
            });
        }

        return serversWeightMap;
    }

    /**
     * 通过jobName算出在哪个服务运行 然后返回一个当前服务所获取的zk分片
     * @param jobName
     * @param serversWeightMap 服务权重列表
     * @param zkServerShadingItem 服务在zk中获取的分片列表 (一个服务可能获取多个分片)
     * @return 返回需要向下执行的zk分片
     */
    public abstract int doSelect(final String jobName, ConcurrentHashMap<String, Integer> serversWeightMap, Map<String, Set<Integer>> zkServerShadingItem);


}

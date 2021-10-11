package com.lishiliang.loadbalance;



import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author lisl
 * @version 1.0
 * @desc : 带权重的随机策略
 */
public class RandomLoadBalance extends AbstractClientLoadBalance {

    /**
     *
     * @param jobName
     * @param serversWeightMap 服务权重列表
     * @param zkServerShadingItem 服务在zk中获取的分片列表 (一个服务可能获取多个分片)
     * @return 返回需要向下执行的zk分片
     */
    @Override
    public int doSelect(final String jobName, ConcurrentHashMap<String, Integer> serversWeightMap, Map<String, Set<Integer>> zkServerShadingItem) {

        //zk只有一个服务 直接返回该服务的一个分片
        if (zkServerShadingItem.entrySet().size() == 1) {
            return zkServerShadingItem.values().stream().flatMap(Collection::stream).findFirst().get();
        }
        //只有一个服务配置了权重 直接返回该服务的一个分片
        if (serversWeightMap.entrySet().size() == 1) {
            return new ArrayList<Integer>(zkServerShadingItem.get(serversWeightMap.keys().nextElement())).get(0);
        }

        //权重和
        int sumWeight = serversWeightMap.values().stream().reduce(Integer::sum).get();
        //hashcode取模 获取一个随机数(对于多个job来说是随机的)
        int random = Math.abs(jobName.hashCode() % sumWeight);

        //定位的结果服务
        String resultServer = null;
        for (Map.Entry<String, Integer> serversWeight : serversWeightMap.entrySet()) {
            if ((random -= serversWeight.getValue()) < 0) {
                resultServer = serversWeight.getKey();
                break;
            }
        }

        //返回一个当前服务所获得的一个zk分片
        return new ArrayList<Integer>(zkServerShadingItem.get(resultServer)).get(0);
    }


}

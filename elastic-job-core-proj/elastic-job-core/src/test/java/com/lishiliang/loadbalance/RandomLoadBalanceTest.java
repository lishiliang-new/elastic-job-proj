package com.lishiliang.loadbalance;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lisl
 * @version 1.0
 * @desc :
 */
public class RandomLoadBalanceTest {

    @Test
    public void test1() throws Exception {

        Map<String ,Integer> runCountMap = new HashMap<>();

        for (int i = 0; i < 10000; i++) {
            RandomLoadBalance randomLoadBalance = new RandomLoadBalance();

            String jobName = UUID.randomUUID().toString().substring(0, 10);
            String weight = "192.168.50.16:1,192.168.50.18:-2,192.168.70.154:4";


            Map<String, Set<Integer>> zkServerShadingItem = new HashMap<>();
            zkServerShadingItem.put("192.168.50.16", Sets.newHashSet(0));


            int item = randomLoadBalance.doSelect(jobName, randomLoadBalance.getServersWeightMap(Lists.newArrayList(weight.split(",")), zkServerShadingItem), zkServerShadingItem);
            zkServerShadingItem.forEach((k,v)->{
                if (v.contains(item)) {
                    runCountMap.put(k, runCountMap.getOrDefault(k, 0) + 1);
                }
            });
        }

        System.out.println(runCountMap);
    }

    @Test
    public void test2() throws Exception {

        Map<String ,Integer> runCountMap = new HashMap<>();

        for (int i = 0; i < 10000; i++) {
            RandomLoadBalance randomLoadBalance = new RandomLoadBalance();

            String jobName = UUID.randomUUID().toString().substring(0, 10);
            String weight = "192.168.50.16:1,192.168.50.18:2,192.168.70.154:4,192.168.707.154:4";

            Map<String, Set<Integer>> zkServerShadingItem = new HashMap<>();
            zkServerShadingItem.put("192.168.50.16", Sets.newHashSet(0));
            zkServerShadingItem.put("192.168.50.18", Sets.newHashSet(1));
            zkServerShadingItem.put("192.168.70.154", Sets.newHashSet(2));

            int item = randomLoadBalance.doSelect(jobName, randomLoadBalance.getServersWeightMap(Lists.newArrayList(weight.split(",")), zkServerShadingItem), zkServerShadingItem);
            zkServerShadingItem.forEach((k,v)->{
                if (v.contains(item)) {
                    runCountMap.put(k, runCountMap.getOrDefault(k, 0) + 1);
                }
            });
        }

        System.out.println(runCountMap);
    }

    @Test
    public void test3() throws Exception {
        Map<String ,Integer> runCountMap = new HashMap<>();

        for (int i = 0; i < 10000; i++) {
            RandomLoadBalance randomLoadBalance = new RandomLoadBalance();

            String jobName = UUID.randomUUID().toString().substring(0, 10);

            Map<String, Set<Integer>> zkServerShadingItem = new HashMap<>();
            zkServerShadingItem.put("10.80.1.111", Sets.newHashSet(0));
            zkServerShadingItem.put("10.80.1.112", Sets.newHashSet(1));
            zkServerShadingItem.put("10.80.1.113", Sets.newHashSet(2));

            String weight = "10.80.1.111:-1,10.80.1.112:4,10.80.1.113:1";
            ConcurrentHashMap<String, Integer> serversWeightMap = randomLoadBalance.getServersWeightMap(Lists.newArrayList(weight.split(",")), zkServerShadingItem);

            int item = randomLoadBalance.doSelect(jobName, serversWeightMap, zkServerShadingItem);
            zkServerShadingItem.forEach((k,v)->{
                if (v.contains(item)) {
                    runCountMap.put(k, runCountMap.getOrDefault(k, 0) + 1);
                }
            });
        }

        System.out.println(runCountMap);
    }

    @Test
    public void test5() throws Exception {


        String jobName = "pos_test";

        Map<String, Set<Integer>> zkServerShadingItem = new HashMap<>();
        zkServerShadingItem.put("10.80.1.111", Sets.newHashSet(0));
        zkServerShadingItem.put("10.80.1.112", Sets.newHashSet(1));
        zkServerShadingItem.put("10.80.1.113", Sets.newHashSet(2));

        String weight = "10.80.1.111:-1,10.80.1.112:4,10.80.1.113:1";
        RandomLoadBalance randomLoadBalance = new RandomLoadBalance();
        ConcurrentHashMap<String, Integer> serversWeightMap = randomLoadBalance.getServersWeightMap(Lists.newArrayList(weight.split(",")), zkServerShadingItem);

        int item = randomLoadBalance.doSelect(jobName, serversWeightMap, zkServerShadingItem);
        zkServerShadingItem.forEach((k,v)->{
            if (v.contains(item)) {
                System.out.println(String.format("当前任务:%s,定位到服务%s,分片项为%d", jobName, k, item));
            }
        });
    }



}
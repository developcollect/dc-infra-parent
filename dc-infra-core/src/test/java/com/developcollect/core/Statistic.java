package com.developcollect.core;

import cn.hutool.core.date.StopWatch;

import java.util.HashMap;
import java.util.Map;

public class Statistic {

    private final Map<String, Long> countMap = new HashMap<>();
    private StopWatch stopWatch;
    private String name;

    public Statistic() {
        this("");
    }

    public Statistic(String name) {
        this.name = name;
        this.stopWatch = StopWatch.create(name);
    }

    public void start() {
        stopWatch.start();
    }

    public void stop() {
        stopWatch.stop();
    }

    public Statistic add(String str) {
        countMap.put(str, countMap.getOrDefault(str, 0L) + 1);
        return this;
    }


    public String report() {
        StringBuilder sb = new StringBuilder();
        sb.append("统计\n");
        sb.append("\t").append("计数").append("\t\t").append("占比").append("\n");
        long total = 0;
        for (Map.Entry<String, Long> entry : countMap.entrySet()) {
            total += entry.getValue();
        }
        for (Map.Entry<String, Long> entry : countMap.entrySet()) {
            String str = entry.getKey();
            Long count = entry.getValue();
            sb.append(str).append(":").append("\t")
                    .append(count).append("\t\t")
                    .append((double) count / total).append("\t")
                    .append("\n");
        }
        sb.append("\n");
        sb.append("耗时：").append(stopWatch.getTotalTimeSeconds()).append("s");
        return sb.toString();
    }
}

package xyz.jzab.middleware.dynamic.thread.pool.sdk.domain;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jzab.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class DynamicThreadPoolService implements IDynamicThreadPoolService {
    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolService.class);

    private final String applicationName;

    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;

    public DynamicThreadPoolService(String applicationName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    @Override
    public List<ThreadPoolConfigEntity> queryThreadPoolList() {
        return threadPoolExecutorMap.keySet().stream().map(
            (key) -> {
                ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(key);
                return new ThreadPoolConfigEntity(applicationName, key,threadPoolExecutor);
            }
        ).collect(Collectors.toList());
    }

    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPollName) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPollName);

        if(null == threadPoolExecutor) return new ThreadPoolConfigEntity(applicationName,threadPollName);

        ThreadPoolConfigEntity vo = new ThreadPoolConfigEntity(applicationName, threadPollName, threadPoolExecutor);

        if(logger.isDebugEnabled()){
            logger.debug("动态线程池配置查询 应用名：{}，线程名：{}，池化配置：{}",applicationName,threadPollName, JSON.toJSONString(vo));
        }

        return vo;
    }

    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {
        // 如果应用名称不匹配或者配置实体为空，则直接返回
        if(null == threadPoolConfigEntity || !applicationName.equals(threadPoolConfigEntity.getAppName())) return;
        ThreadPoolExecutor executor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());
        // 如果线程池对象为空，直接返回
        if(null == executor) return;
        // 设置核心线程数和最大线程数
        executor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        executor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
    }
}

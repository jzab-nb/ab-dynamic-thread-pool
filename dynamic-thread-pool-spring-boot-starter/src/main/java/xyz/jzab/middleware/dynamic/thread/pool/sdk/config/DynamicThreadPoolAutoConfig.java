package xyz.jzab.middleware.dynamic.thread.pool.sdk.config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.jzab.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import xyz.jzab.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;


// 动态配置入口
@Configuration
public class DynamicThreadPoolAutoConfig {
    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

    @Bean("dynamicThreadPoolService")
    public IDynamicThreadPoolService dynamicThreadPoolService(ApplicationContext context, Map<String,ThreadPoolExecutor> threadPoolExecutorMap){
        // 获取应用名称
        String appName = context.getEnvironment().getProperty("spring.application.name");
        // 判断应用名称是否为空
        if(StringUtils.isBlank(appName)){
            appName = "缺省的";
            logger.info("应用未配置 spring.application.name 无法获取应用名称");
        }

        Set<String> threadPoolKeys = threadPoolExecutorMap.keySet();

        for(String threadPollKey: threadPoolKeys){
            ThreadPoolExecutor poolExecutor = threadPoolExecutorMap.get(threadPollKey);
            int poolSize = poolExecutor.getPoolSize();
            int corePoolSize = poolExecutor.getCorePoolSize();
            BlockingQueue<Runnable> workQueue = poolExecutor.getQueue();
            String simpleName = workQueue.getClass().getSimpleName();

            // 打印线程池
            logger.info("线程池信息：{} {} {} {}", threadPollKey,poolSize,corePoolSize,simpleName);
        }



        return new DynamicThreadPoolService(appName,threadPoolExecutorMap);
    }
}
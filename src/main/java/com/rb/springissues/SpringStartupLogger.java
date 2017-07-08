package com.rb.springissues;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

public class SpringStartupLogger extends InstantiationAwareBeanPostProcessorAdapter {

    private Map<String, TimingInfo> timingInfoMap = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (timingInfoMap.containsKey(beanName)) {
            LoggerBase.logPlain("*** before inst: already defined " + beanName);
        }
        TimingInfo timingInfo = timingInfoMap.computeIfAbsent(beanName, s -> new TimingInfo());
        timingInfo.setBeforeInst(System.currentTimeMillis());
        LoggerBase.logPlain("\t\tSpring startup: before instantiation of [{}] of [{}]", beanName, beanClass.getCanonicalName());
        return super.postProcessBeforeInstantiation(beanClass, beanName);

    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        TimingInfo timingInfo = timingInfoMap.computeIfAbsent(beanName, s -> new TimingInfo());
        if (timingInfo.afterInst > 0L) {
            LoggerBase.logPlain("*** after inst: already defined " + beanName);
        }
        timingInfo.setAfterInst(System.currentTimeMillis());
        LoggerBase.logPlain("\t\tSpring startup: after instantiation of [{}] of [{}]", beanName,
                bean.getClass().getCanonicalName());
        return super.postProcessAfterInstantiation(bean, beanName);

    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        TimingInfo timingInfo = timingInfoMap.computeIfAbsent(beanName, s -> new TimingInfo());
        if (timingInfo.beforeInit > 0L) {
            LoggerBase.logPlain("*** before init: already defined " + beanName);
        }
        timingInfo.setBeforeInit(System.currentTimeMillis());
        LoggerBase.logPlain("\t\tSpring startup: before initialization of [{}] of [{}]", beanName,
                bean.getClass().getCanonicalName());
        return super.postProcessBeforeInitialization(bean, beanName);

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        TimingInfo timingInfo = timingInfoMap.computeIfAbsent(beanName, s -> new TimingInfo());
        if (timingInfo.afterInit > 0L) {
            LoggerBase.logPlain("*** after init: already defined " + beanName);
        }
        timingInfo.setAfterInit(System.currentTimeMillis());
        LoggerBase.logPlain("\t\tSpring startup: after initialization of [{}] of [{}]", beanName,
                bean.getClass().getCanonicalName());
        LoggerBase.logPlain(
                "\t\tSpring startup summary: time for bean [{}] of [{}]: instantiation: [{}], initialization [{}], total [{}]",
                beanName, bean.getClass().getCanonicalName(),
                timingInfo.afterInst != 0L && timingInfo.beforeInst != 0L
                        ? timingInfo.afterInst - timingInfo.beforeInst
                        : "unknown",
                timingInfo.afterInit != 0L && timingInfo.beforeInit != 0L
                        ? timingInfo.afterInit - timingInfo.beforeInit
                        : "unknown",
                timingInfo.afterInit != 0L && timingInfo.beforeInst != 0L
                        ?
                        timingInfo.afterInit - timingInfo.beforeInst
                        : "unknown");
        return super.postProcessAfterInitialization(bean, beanName);
    }

    @Data
    private static class TimingInfo {
        private long beforeInst;
        private long afterInst;
        private long beforeInit;
        private long afterInit;
    }
}

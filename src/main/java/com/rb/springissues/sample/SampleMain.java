package com.rb.springissues.sample;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.rb.springissues.LoggerBase;

public class SampleMain {
    private static ClassPathXmlApplicationContext context;

    public static void main(String[] args) {
        context = new ClassPathXmlApplicationContext();
        context.setAllowBeanDefinitionOverriding(false);
        context.setAllowCircularReferences(false);
        context.setConfigLocation("sample/applicationContext.xml");
        context.refresh();
        LoggerBase.logPlain("=== Init done");
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = context.getBean(beanDefinitionName);
            if(bean instanceof LoggerBase) {
                ((LoggerBase) bean).nop();
            }
        }
        LoggerBase.logPlain("=== NOP done");
        LoggerBase.print();
    }
}

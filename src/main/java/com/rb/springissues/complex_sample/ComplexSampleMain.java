package com.rb.springissues.complex_sample;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.rb.springissues.LoggerBase;

public class ComplexSampleMain {

    private static ClassPathXmlApplicationContext context;

    public static void main(String[] args) {
        context = new ClassPathXmlApplicationContext();
        context.setAllowBeanDefinitionOverriding(false);
        context.setAllowCircularReferences(false);
        context.setConfigLocation("complex_sample/applicationContext.xml");
        context.refresh();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        LoggerBase.logPlain("=== Init done");
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

package com.rb.springissues.complex_sample;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.rb.springissues.LoggerBase;

public class PermissionTransactionInterceptor extends LoggerBase implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return invocation.proceed();
    }
}

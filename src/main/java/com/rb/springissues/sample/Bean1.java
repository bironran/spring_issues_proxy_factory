package com.rb.springissues.sample;

import javax.inject.Inject;
import javax.inject.Provider;

import com.rb.springissues.LoggerBase;

public class Bean1 extends LoggerBase {

    @Inject
    Provider<DummyBeanA> provider;
}

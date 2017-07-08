package com.rb.springissues.sample;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.rb.springissues.LoggerBase;

public class Bean5 extends LoggerBase {

    @Inject
    Provider<DummyBeanA> provider;

    @Inject
    @Named("bean4")
    Bean4 bean4;
}

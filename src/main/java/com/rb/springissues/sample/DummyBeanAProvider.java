package com.rb.springissues.sample;

import javax.inject.Provider;

public class DummyBeanAProvider implements Provider<DummyBeanA> {
    @Override
    public DummyBeanA get() {
        return null;
    }
}

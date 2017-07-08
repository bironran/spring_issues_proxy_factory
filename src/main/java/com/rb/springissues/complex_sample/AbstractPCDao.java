package com.rb.springissues.complex_sample;


import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.DataSource;

import lombok.Setter;

import com.rb.springissues.LoggerBase;

public class AbstractPCDao extends LoggerBase {
    @Inject
    @Setter
    private Provider<DataSource> dataSourceSupplier;
}

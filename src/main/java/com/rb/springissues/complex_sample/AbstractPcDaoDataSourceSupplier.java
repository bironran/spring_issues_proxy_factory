package com.rb.springissues.complex_sample;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Provider;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.AbstractDataSource;

import com.rb.springissues.LoggerBase;

public class AbstractPcDaoDataSourceSupplier extends LoggerBase implements Provider<DataSource> {

    @Override
    public DataSource get() {
        return new AbstractDataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                throw new RuntimeException("not a real DS");
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                throw new RuntimeException("not a real DS");
            }
        };
    }
}

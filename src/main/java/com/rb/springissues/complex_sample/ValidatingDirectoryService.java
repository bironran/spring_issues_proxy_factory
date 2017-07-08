package com.rb.springissues.complex_sample;

import lombok.Setter;

import com.rb.springissues.LoggerBase;

public class ValidatingDirectoryService extends LoggerBase implements DirectoryService {
    @Setter
    BrandSettingsDao brandSettingsDao;
}

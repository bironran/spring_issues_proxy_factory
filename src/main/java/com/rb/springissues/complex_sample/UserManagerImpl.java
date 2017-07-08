package com.rb.springissues.complex_sample;

import lombok.Setter;

import com.rb.springissues.LoggerBase;

public class UserManagerImpl extends LoggerBase implements UserManager {
    @Setter
    private DirectoryService directoryService;
}

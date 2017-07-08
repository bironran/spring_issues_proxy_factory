package com.rb.springissues.complex_sample;

import javax.inject.Inject;

import lombok.Setter;

import com.rb.springissues.LoggerBase;

public class PermissionFS2 extends LoggerBase implements Permission {

    @Inject
    private UserManager userManager;

    @Setter
    private DirectoryService directoryService;
}

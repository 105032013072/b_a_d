package com.bosssoft.platform.apidocs.codegenerator.java;

import java.io.IOException;

import com.bosssoft.platform.apidocs.codegenerator.TemplateProvider;

/**
 * Created by Darcy https://yedaxia.github.io/
 */
public class JavaTemplateProvider {

    public String provideTemplateForName(String templateName) throws IOException {
    	return TemplateProvider.provideTemplateForName(templateName);
    }
    
}

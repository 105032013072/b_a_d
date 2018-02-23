package com.bosssoft.platform.apidocs.codegenerator.ios;

import java.io.IOException;

import com.bosssoft.platform.apidocs.codegenerator.TemplateProvider;


public class ModelTemplateProvider {
	
	public String provideTemplateForName(String templateName) throws IOException {
		return TemplateProvider.provideTemplateForName(templateName);
    }

}

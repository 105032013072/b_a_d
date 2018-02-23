package com.bosssoft.platform.apidocs.codegenerator;

import java.io.FileInputStream;
import java.io.IOException;

import com.bosssoft.platform.apidocs.Resources;
import com.bosssoft.platform.apidocs.Utils;


public class TemplateProvider {
	public static String provideTemplateForName(String templateName) throws IOException {
		return Utils.streamToString(Resources.getCodeTemplateFile(templateName));
    }
}

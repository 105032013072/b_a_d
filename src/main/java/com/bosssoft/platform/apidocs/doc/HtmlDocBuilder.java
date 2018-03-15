package com.bosssoft.platform.apidocs.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;

import com.bosssoft.platform.apidocs.LogUtils;
import com.bosssoft.platform.apidocs.enumtype.HtmlType;
import com.bosssoft.platform.common.utils.FileUtils;
import com.bosssoft.platform.common.utils.StringUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class HtmlDocBuilder {
      
	/**
	 * 
	 * @param dataModel 用于构造页面的数据模型
	 * @param htmlPath 生成的页面的名称（全路径）
	 * @param htmlType 
	 * @throws Exception 
	 */
      public  static void buildHtml(Object dataModel,String htmlPath,HtmlType htmlType) throws Exception{
    	  String templatePath=getTemplatePath(htmlType);
    	  if(StringUtils.isNullOrBlank(templatePath)){
    		  LogUtils.error("cannot find the html template", "");;
    	  }
    	  
  
    	  Configuration configuration = new Configuration();  
          configuration.setDirectoryForTemplateLoading(new File(templatePath).getParentFile());  
          configuration.setObjectWrapper(new DefaultObjectWrapper());  
          configuration.setDefaultEncoding("UTF-8");   //这个一定要设置，不然在生成的页面中 会乱码  
          
          FileUtils.mkdir(new File(htmlPath), false);
          Template template = configuration.getTemplate(new File(templatePath).getName());
          Writer writer  = new OutputStreamWriter(new FileOutputStream(htmlPath),"UTF-8");  
          template.process(dataModel, writer); 
      }

	private static String getTemplatePath(HtmlType htmlType) throws Exception {
		String templatePath="";
		String parantPath=Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
		switch(htmlType){
		case CLASSCONTROLLER:
			templatePath=parantPath+File.separator+"controller"+File.separator+"controller_temp.html";
			break;
		case CLASSSERVICE:
			templatePath=parantPath+File.separator+"service"+File.separator+"service_temp.html";
			break;
		case CLASSMAPPER:
			templatePath=parantPath+File.separator+"mapper"+File.separator+"mapper_temp.html";
			break;
		case CLASSENTITY:
			templatePath=parantPath+File.separator+"entity"+File.separator+"entity_temp.html";
			break;
		case CLASSINDEX:
			templatePath=parantPath+File.separator+"index"+File.separator+"index_temp.html";
			break;
		case MODELINDEX:
			templatePath=parantPath+File.separator+"index"+File.separator+"model_index_temp.html";
			break;
		case WELCOM:
			templatePath=parantPath+File.separator+"index"+File.separator+"welcome.html";
			break;
		}
		return templatePath;
	}
      
}

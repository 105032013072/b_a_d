package com.bosssoft.platform.apidocs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

import javax.print.DocPrintJob;

import com.bosssoft.platform.apidocs.doc.HtmlDocGenerator;
import com.bosssoft.platform.apidocs.ext.rap.RapSupport;
import com.bosssoft.platform.common.utils.StringUtils;

/**
 *  main entrance
 */
public class Docs {

    private static final String CONFIG_FILE = "docs.config";

    public static void main(String[] args){
        DocsConfig config = loadProps();
        buildHtmlDocs(config);
    }

    /**
     * build html api docs
     */
    public static void buildHtmlDocs(DocsConfig config){
        DocContext.init(config);
        HtmlDocGenerator docGenerator = new HtmlDocGenerator();
        docGenerator.generateDocs();
        RapSupport rapSupport = new RapSupport(docGenerator.getControllerNodeList());
        rapSupport.postToRap();
	}

    /**
     * wrap response into a common structure,don't forget to put responseNode into map.
     *
     * default is:
     *
     * {
     *     code : 0,
     *     data: ${response}
     *     msg: 'success'
     * }
     *
     * @param responseWrapper
     */
	public static void setResponseWrapper(IResponseWrapper responseWrapper){
        DocContext.setResponseWrapper(responseWrapper);
    }

	private static DocsConfig loadProps(){
		 InputStream in = null;  
		try{
			in=new BufferedInputStream(new FileInputStream(CONFIG_FILE));
            Properties properties = new Properties();
            properties.load(new InputStreamReader(in,"utf-8"));
            DocsConfig config = new DocsConfig();
            //config.projectPath = properties.getProperty("projectPath", null);
        
            config.projectPath=properties.getProperty("﻿projectPath");
  
            if(config.projectPath == null){
                throw new RuntimeException("projectPath property is needed in the config file.");
            }

            config.docsPath = properties.getProperty("docsPath", null);
            config.codeTplPath = properties.getProperty("codeTplPath", null);
            config.mvcFramework = properties.getProperty("mvcFramework", "");
            String docTitle=properties.getProperty("doctitle");
            if(StringUtils.isNullOrBlank(docTitle)){
            	docTitle=new File(config.projectPath).getName()+"详细设计文档";
            }
            config.docTitle=docTitle;
            
            return config;
        }catch (Exception e){
            e.printStackTrace();

            try{
                File configFile = new File(CONFIG_FILE);
                configFile.createNewFile();
            }catch (Exception ex){
                e.printStackTrace();
            }

            throw new RuntimeException("you need to set projectPath property in " + CONFIG_FILE);
        }finally {
			if(in!=null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
    }

	public static class DocsConfig {

        String projectPath; // must set
        String docsPath; // default equals projectPath
        String codeTplPath; // if empty, use the default resources
        String mvcFramework; //spring, play, jfinal, generic, can be empty
        String docTitle;
        
        String rapHost;
        String rapLoginCookie;
        String rapProjectId;
        String rapAccount;
        String rapPassword;

        boolean isSpringMvcProject(){
            return mvcFramework != null && mvcFramework.equals("spring");
        }

        boolean isPlayProject(){
            return mvcFramework != null && mvcFramework.equals("play");
        }

        boolean isJfinalProject(){
            return mvcFramework != null && mvcFramework.equals("jfinal");
        }

        boolean isGeneric(){
            return mvcFramework != null && mvcFramework.equals("generic");
        }

        public void setProjectPath(String projectPath) {
            this.projectPath = projectPath;
        }

        public void setDocsPath(String docsPath) {
            this.docsPath = docsPath;
        }

        public void setCodeTplPath(String codeTplPath) {
            this.codeTplPath = codeTplPath;
        }

        public void setMvcFramework(String mvcFramework) {
            this.mvcFramework = mvcFramework;
        }

        public String getRapHost() {
            return rapHost;
        }

        public void setRapHost(String rapHost) {
            this.rapHost = rapHost;
        }

        public String getRapLoginCookie() {
            return rapLoginCookie;
        }

        /**
         * use http://rap.yedaxia.me , just set account and password would be better
         *
         * @param rapLoginCookie
         */
        @Deprecated
        public void setRapLoginCookie(String rapLoginCookie) {
            this.rapLoginCookie = rapLoginCookie;
        }

        public String getRapProjectId() {
            return rapProjectId;
        }

        public void setRapProjectId(String rapProjectId) {
            this.rapProjectId = rapProjectId;
        }

        public String getRapAccount() {
            return rapAccount;
        }

        public void setRapAccount(String rapAccount) {
            this.rapAccount = rapAccount;
        }

        public String getRapPassword() {
            return rapPassword;
        }

        public void setRapPassword(String rapPassword) {
            this.rapPassword = rapPassword;
        }

		public String getDocTitle() {
			return docTitle;
		}

		public void setDocTitle(String docTitle) {
			this.docTitle = docTitle;
		}
    }
}

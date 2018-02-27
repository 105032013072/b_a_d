package com.bosssoft.platform.apidocs.doc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.bosssoft.platform.apidocs.DocContext;
import com.bosssoft.platform.apidocs.LogUtils;
import com.bosssoft.platform.apidocs.Resources;
import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.parser.controller.AbsControllerParser;
import com.bosssoft.platform.apidocs.parser.mate.ControllerNode;

/**
 * Html Api docs generator
 *
 * @author yeguozhong yedaxia.github.com
 */
public class HtmlDocGenerator extends AbsDocGenerator {

    public HtmlDocGenerator() {
        super(DocContext.controllerParser(), DocContext.getServiceParser(),DocContext.getMapperParser(), 
        		DocContext.getEntityParser(),new HtmlControllerDocBuilder());
    }

    @Override
    void generateIndex(List<String> docFileNameList) {

        if(docFileNameList.isEmpty()){
            return;
        }

        try {
            InputStream tplIndexSteam = Resources.getTemplateFile("api-index.html.tpl");
            String indexTemplate = Utils.streamToString(tplIndexSteam);
            StringBuilder indexBuilder = new StringBuilder();
            for (String docName : docFileNameList) {
                indexBuilder.append(String.format("<li><a href=\"%s\">%s</a></li>",docName+".html",docName));
            }
            indexTemplate = indexTemplate.replace("${API_LIST}", indexBuilder.toString());
            Utils.writeToDisk(new File(DocContext.getDocPath(), "index.html"), indexTemplate);
        } catch (IOException e) {
            LogUtils.error("generate index html fail. ",e);
        }

        copyCssStyle();
    }

    private void copyCssStyle(){
        try{
            String cssFileName = "style.css";
            File cssFile = new File(DocContext.getDocPath(), cssFileName);
            Utils.writeToDisk(cssFile, Utils.streamToString(Resources.getTemplateFile(cssFileName)));;
        }catch (IOException  e){
            LogUtils.error("copyCssStyle fail",e);
        }

    }
}

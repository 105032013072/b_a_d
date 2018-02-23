package com.bosssoft.platform.apidocs.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bosssoft.platform.apidocs.DocContext;
import com.bosssoft.platform.apidocs.LogUtils;
import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.parser.controller.AbsControllerParser;
import com.bosssoft.platform.apidocs.parser.mate.ControllerNode;
import com.bosssoft.platform.apidocs.parser.mate.ServiceNode;
import com.bosssoft.platform.apidocs.parser.service.AbsServiceParser;

public abstract class AbsDocGenerator{

    private AbsControllerParser controllerParser;
    private AbsServiceParser serviceParser;
    private IControllerDocBuilder controllerDocBuilder;
    private List<String> docFileNameList = new ArrayList<>();
    private List<ControllerNode> controllerNodeList = new ArrayList<>();
    private List<ServiceNode> serviceNodeList=new ArrayList<>();
    
    public AbsDocGenerator(AbsControllerParser controllerParser,AbsServiceParser serviceParser, IControllerDocBuilder controllerDocBuilder) {
        this.controllerParser = controllerParser;
        this.serviceParser=serviceParser;
        this.controllerDocBuilder = controllerDocBuilder;
    }

    /**
     * generate api Docs
     */
    public void generateDocs(){
        LogUtils.info("generate api docs start...");
        
        LogUtils.info("generate api docs for controller...");
        //generateControllersDocs();
       
        LogUtils.info("generate api docs for service...");
        generateServicessDocs();
       
        generateIndex(docFileNameList);
        LogUtils.info("generate api docs done !!!");
    }

    private void generateServicessDocs() {
		File[] serviceFiles=DocContext.getServiceFiles();
		for (File serviceFile : serviceFiles) {
			try{
				 ServiceNode sNode=serviceParser.parse(serviceFile);
				 serviceNodeList.add(sNode);
				 LogUtils.info("start to parse service file : %s", serviceFile.getName());
			}catch(Exception e){
				LogUtils.error("generate docs for service file : "+serviceFile.getName(), e);
			}
		}
		
	}

	private void generateControllersDocs(){
        File[] controllerFiles = DocContext.getControllerFiles();
        for (File controllerFile : controllerFiles) {
            try {
                LogUtils.info("start to parse controller file : %s", controllerFile.getName());
                ControllerNode controllerNode = controllerParser.parse(controllerFile);
                if(controllerNode.getRequestNodes().isEmpty()){
                    continue;
                }
                controllerNodeList.add(controllerNode);
                LogUtils.info("start to generate docs for controller file : %s", controllerFile.getName());
                String controllerDocs = controllerDocBuilder.buildDoc(controllerNode);
                String docName = controllerNode.getDescription();
                docFileNameList.add(docName);
                Utils.writeToDisk(new File(DocContext.getDocPath(), docName+".html"),controllerDocs);
                LogUtils.info("success to generate docs for controller file : %s", controllerFile.getName());
            } catch (IOException e) {
                LogUtils.error("generate docs for controller file : "+controllerFile.getName()+" fail", e);
            }
        }

    }

    public List<ControllerNode> getControllerNodeList(){
        return controllerNodeList;
    }

	abstract void generateIndex(List<String> docFileNameList);
}

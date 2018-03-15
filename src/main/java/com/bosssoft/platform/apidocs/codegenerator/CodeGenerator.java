package com.bosssoft.platform.apidocs.codegenerator;

import java.io.File;
import java.io.IOException;

import com.bosssoft.platform.apidocs.DocContext;
import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.codegenerator.ios.ModelCodeGenerator;
import com.bosssoft.platform.apidocs.codegenerator.java.JavaCodeGenerator;
import com.bosssoft.platform.apidocs.doc.WordDocBuilder;
import com.bosssoft.platform.apidocs.parser.mate.FieldNode;
import com.bosssoft.platform.apidocs.parser.mate.ResponseNode;

public abstract class CodeGenerator {

	private ResponseNode responseNode;
	private File codePath;
	private String codeRelativePath;
	
	public CodeGenerator(ResponseNode responseNode){
		this.responseNode = responseNode;
		this.codeRelativePath = getRelativeCodeDir();
		this.codePath = new File(DocContext.getDocPath(), codeRelativePath);
		if (!this.codePath.exists()) {
			this.codePath.mkdirs();
		}
	}
	
	/**
	 * 生成代码
	 * @return 返回代码的相对目录
	 * @throws IOException 
	 */
	public String generateCode() throws IOException{
		if (responseNode.getChildNodes() == null || responseNode.getChildNodes().isEmpty()) {
			return "";
		}
		StringBuilder codeBodyBuilder = new StringBuilder();
		generateCodeForBuilder(responseNode,codeBodyBuilder);
		String sCodeTemplate = getCodeTemplate();
		CodeFileBuilder codeBuilder = new CodeFileBuilder(responseNode.getClassName(), codeBodyBuilder.toString(), sCodeTemplate);
		String javaFileName = responseNode.getClassName() + ".html";
		Utils.writeToDisk(new File(codePath, javaFileName), codeBuilder.build());
		String relateUrl = codeRelativePath + '/' + javaFileName;
		
		String mapkey=responseNode.getClassName();
	   if(codeRelativePath.contains(JavaCodeGenerator.JAVA_CODE_DIR)){
		   if(WordDocBuilder.getJavaCodeMap().get(mapkey)==null){
			   WordDocBuilder.addJavaCode(mapkey, codeBodyBuilder.toString());
			   WordDocBuilder.addJavaCodeOrder(mapkey, (WordDocBuilder.javaCodeOrder++));
		   }
		  
	   }else if(codeRelativePath.contains(ModelCodeGenerator.IOS_CODE_DIR)){
		   if(WordDocBuilder.getIosCodeMap().get(mapkey)==null){
			   WordDocBuilder.addIOSCode(mapkey, codeBodyBuilder.toString());
			   WordDocBuilder.addIOSCodeOrder(mapkey, (WordDocBuilder.iosCodeOrder++));
		   }
		   
	   }
	   
		return relateUrl;
	}
	
	private void generateCodeForBuilder(ResponseNode rootNode,StringBuilder codeBodyBuilder) throws IOException{
		codeBodyBuilder.append(generateNodeCode(rootNode));
		codeBodyBuilder.append('\n');
		for (FieldNode recordNode : rootNode.getChildNodes()) {
			if (recordNode.getChildResponseNode()!= null) {
				generateCodeForBuilder(recordNode.getChildResponseNode(), codeBodyBuilder);
			}
		}
	}
	
	/***
	 * 产生单个ResponseNode节点的Code
	 * @param respNode
	 * @return
	 * @throws IOException
	 */
	public abstract String generateNodeCode(ResponseNode respNode) throws IOException;
	
	/**
	 * 获取代码的写入的相对目录
	 * @return
	 */
	public abstract String getRelativeCodeDir();
	
	/**
	 * 获取最终的代码模板
	 * @return
	 */
	public abstract String getCodeTemplate();
}

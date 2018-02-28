package com.bosssoft.platform.apidocs.parser.mate;

import java.util.ArrayList;
import java.util.List;

public class InterfaceNode {
	//方法名
    private String methodName;
    
    private String description;
    
    //方法参数
    private List<ParamNode> paramNodes = new ArrayList<>();
    
    //方法的返回信息
    private ReturnNode returnNode;
    
    //抛出的异常信息
    private List<Explain> throwsNode=new ArrayList<>();

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ParamNode> getParamNodes() {
		return paramNodes;
	}

	public void setParamNodes(List<ParamNode> paramNodes) {
		this.paramNodes = paramNodes;
	}

	

	public ReturnNode getReturnNode() {
		return returnNode;
	}

	public void setReturnNode(ReturnNode returnNode) {
		this.returnNode = returnNode;
	}

	public List<Explain> getThrowsNode() {
		return throwsNode;
	}

	public void setThrowsNode(List<Explain> throwsNode) {
		this.throwsNode = throwsNode;
	}
    
	 public ParamNode getParamNodeByName(String name){
	        for(ParamNode node : paramNodes){
	            if(node.getName().equals(name)){
	                return node;
	            }
	        }
	        return null;
	    }
	 
	 
	 public Explain getThrowsNodeByName(String name){
		 for (Explain e : throwsNode) {
			if(e.getType().equals(name)){
				return e;
			}
		}
		 return null;
	 }
	 
	 public void addParamNode(ParamNode node){
		 paramNodes.add(node);
	 }
	 
	 public void addThrowsNode(Explain explain){
		 throwsNode.add(explain);
	 }
   
}

package com.bosssoft.platform.apidocs.parser.mate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.bosssoft.platform.apidocs.parser.controller.RequestMethod;

/**
 * a request node  corresponds to a controller method
 *
 * @author yeguozhong yedaxia.github.com
 */
public class RequestNode {

    private List<String> method = new ArrayList<>();
    private String url;
    private String description;
    private String methodName;
    private String returnString;
    private List<ParamNode> paramNodes = new ArrayList<>();
    private List<HeaderNode> header = new ArrayList<>();
    private Boolean deprecated = Boolean.FALSE;
    private ResponseNode responseNode;
    private String responseJson;
    private String  androidCodeUrl;
    private String  iOSCodeUrl;

    public List<String> getMethod() {
        if(method == null || (method != null && method.size() == 0)) {
            return Arrays.asList(RequestMethod.GET.name(), RequestMethod.POST.name());
        }
        return method;
    }

    public void setMethod(List<String> method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public List<HeaderNode> getHeader() {
        return header;
    }

    public void setHeader(List<HeaderNode> header) {
        this.header = header;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public ResponseNode getResponseNode() {
        return responseNode;
    }

    public void setResponseNode(ResponseNode responseNode) {
        this.responseNode = responseNode;
    }

    public void addMethod(String method) {
        this.method.add(method);
    }

    public void addHeaderNode(HeaderNode headerNode){
        header.add(headerNode);
    }

    public void addParamNode(ParamNode paramNode){
        paramNodes.add(paramNode);
    }

    public ParamNode getParamNodeByName(String name){
        for(ParamNode node : paramNodes){
            if(node.getName().equals(name)){
                return node;
            }
        }
        return null;
    }

	public String getAndroidCodeUrl() {
		return androidCodeUrl;
	}

	public void setAndroidCodeUrl(String androidCodeUrl) {
		this.androidCodeUrl = androidCodeUrl;
	}

	public String getiOSCodeUrl() {
		return iOSCodeUrl;
	}

	public void setiOSCodeUrl(String iOSCodeUrl) {
		this.iOSCodeUrl = iOSCodeUrl;
	}

	public String getResponseJson() {
		return responseJson;
	}

	public void setResponseJson(String responseJson) {
		this.responseJson = responseJson;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getReturnString() {
		return returnString;
	}

	public void setReturnString(String returnString) {
		this.returnString = returnString;
	}
    
    
}

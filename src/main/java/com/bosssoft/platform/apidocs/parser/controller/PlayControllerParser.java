package com.bosssoft.platform.apidocs.parser.controller;

import com.bosssoft.platform.apidocs.parser.mate.ParamNode;
import com.bosssoft.platform.apidocs.parser.mate.RequestNode;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import java.util.Arrays;

/**
 * user for play framework
 *
 * @author yeguozhong yedaxia.github.com
 */
public class PlayControllerParser extends AbsControllerParser {

    @Override
    protected void afterHandleMethod(RequestNode requestNode, MethodDeclaration md) {
        PlayRoutesParser.RouteNode routeNode = PlayRoutesParser.INSTANCE.getRouteNode(getControllerFile(), md.getNameAsString());
        if(routeNode == null){
            return;
        }

        String method = routeNode.method.toUpperCase();
        if("*".equals(method)) {
            requestNode.setMethod(Arrays.asList(RequestMethod.GET.name(), RequestMethod.POST.name()));
        } else {
            requestNode.addMethod(RequestMethod.valueOf(method).name());
        }

        requestNode.setUrl(routeNode.routeUrl);

       /* md.getParameters().forEach(p -> {
            String paraName  = p.getName().asString();
            ParamNode paramNode = requestNode.getParamNodeByName(paraName);
            if(paramNode != null){
                p.getOptionalAnnotationByName("Required").ifPresent(r -> {
                    paramNode.setRequired(true);
                });
            }
        });*/
        NodeList<Parameter> paramterList= md.getParameters();
        for (Parameter parameter : paramterList) {
        	String paraName  = parameter.getName().asString();
        	ParamNode paramNode = requestNode.getParamNodeByName(paraName);
        	if(paramNode!=null){
        		if(parameter.isAnnotationPresent("Required")){
        			paramNode.setRequired(true);
        		}
        	}
		}
        
    }

}

package com.bosssoft.platform.apidocs.parser.controller;

import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.parser.mate.RequestNode;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * use for JFinal
 *
 * @author yeguozhong yedaxia.github.com
 */
public class JFinalControllerParser extends AbsControllerParser{

    @Override
    protected void afterHandleMethod(RequestNode requestNode, MethodDeclaration md) {
        String methodName = md.getNameAsString();
        requestNode.setUrl(getUrl(methodName));
      /*  md.getOptionalAnnotationByName("ActionKey").ifPresent(an -> {
            if(an instanceof SingleMemberAnnotationExpr){
                String url = ((SingleMemberAnnotationExpr)an).getMemberValue().toString();
                requestNode.setMethod(Arrays.asList(RequestMethod.GET.name(), RequestMethod.POST.name()));
                requestNode.setUrl(Utils.removeQuotations(url));
                return;
            }
        });*/
        
		AnnotationExpr an = md.getAnnotationByName("ActionKey");
		if (an != null) {
			if (an instanceof SingleMemberAnnotationExpr) {
				String url = ((SingleMemberAnnotationExpr) an).getMemberValue().toString();
				requestNode.setMethod(Arrays.asList(RequestMethod.GET.name(), RequestMethod.POST.name()));
				requestNode.setUrl(Utils.removeQuotations(url));
			}
		}
        
    }

    private String getUrl(String methodName){
        JFinalRoutesParser.RouteNode routeNode = JFinalRoutesParser.INSTANCE.getRouteNode(getControllerFile().getAbsolutePath());
        return routeNode == null ? "" :routeNode.basicUrl +"/"+ methodName;
    }
}

package com.bosssoft.platform.apidocs.parser.controller;

import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.parser.mate.RequestNode;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

/**
 *
 * can apply to any java project, but you have to set the request url and method in annotation ${@link com.bosssoft.platform.apidocs.ApiDoc} by yourself.
 *
 * @author yeguozhong yedaxia.github.com
 */
public class GenericControllerParser extends AbsControllerParser {

    @Override
    protected void afterHandleMethod(RequestNode requestNode, MethodDeclaration md) {
       /* md.getOptionalAnnotationByName("ApiDoc").ifPresent(an -> {
            if(an instanceof NormalAnnotationExpr){
                ((NormalAnnotationExpr)an).getPairs().forEach(p -> {
                    String n = p.getNameAsString();
                    if(n.equals("url")){
                        requestNode.setUrl(Utils.removeQuotations(p.getValue().toString()));
                    }else if(n.equals("method")){
                        requestNode.addMethod(Utils.removeQuotations(p.getValue().toString()));
                    }
                });
            }
        });*/
    	
    	AnnotationExpr an=md.getAnnotationByName("ApiDoc");
    	if(an!=null){
    		 if(an instanceof NormalAnnotationExpr){
    			 NodeList<MemberValuePair> pairList= ((NormalAnnotationExpr)an).getPairs();
    			 for (MemberValuePair p : pairList) {
    				 String n = p.getNameAsString();
                     if(n.equals("url")){
                         requestNode.setUrl(Utils.removeQuotations(p.getValue().toString()));
                     }else if(n.equals("method")){
                         requestNode.addMethod(Utils.removeQuotations(p.getValue().toString()));
                     }
				}
    		 }
    	}
    	
    }

}

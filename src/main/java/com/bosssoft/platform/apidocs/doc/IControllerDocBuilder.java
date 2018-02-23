package com.bosssoft.platform.apidocs.doc;

import java.io.File;
import java.io.IOException;

import com.bosssoft.platform.apidocs.parser.mate.ControllerNode;
import com.bosssoft.platform.apidocs.parser.mate.ResponseNode;

/**
 * an interface of build a controller api docs
 *
 * @author yeguozhong yedaxia.github.com
 */
public interface IControllerDocBuilder {

    /**
     * build api docs and return as string
     *
     * @param controllerNode
     * @return
     */
    String buildDoc(ControllerNode controllerNode) throws IOException;

}

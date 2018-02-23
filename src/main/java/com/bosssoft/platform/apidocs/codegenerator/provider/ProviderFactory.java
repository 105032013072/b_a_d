package com.bosssoft.platform.apidocs.codegenerator.provider;

import com.bosssoft.platform.apidocs.codegenerator.IFieldProvider;

/**
 * Created by user on 2016/12/25.
 */
public class ProviderFactory {

    public static IFieldProvider createProvider(){
        return new DocFieldProvider();
    }
}

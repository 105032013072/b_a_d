import org.junit.Test;

import com.bosssoft.platform.apidocs.Docs;
import com.bosssoft.platform.apidocs.IResponseWrapper;
import com.bosssoft.platform.apidocs.Resources;
import com.bosssoft.platform.apidocs.parser.mate.ResponseNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yeguozhong yedaxia.github.com
 */
public class DocsTest {

    @Test
    public void test_generatePlayApiDocs(){
        Resources.setDebug();
        Docs.buildHtmlDocs(getDocsConfig(Projects.PlayProject));
    }

    @Test
    public void test_generateSpringApiDocs(){
        Resources.setDebug();
        Docs.buildHtmlDocs(getDocsConfig(Projects.SpringProject));
    }

    @Test
    public void test_generateGenericApiDocs(){
        Resources.setDebug();
        Docs.buildHtmlDocs(getDocsConfig(Projects.GenericProject));
    }

    @Test
    public void test_generateJFinalApiDocs(){
        Resources.setDebug();
        Docs.buildHtmlDocs(getDocsConfig(Projects.JFinalProject));
    }

    private Docs.DocsConfig getDocsConfig(String projectPath){
        Docs.DocsConfig config = new Docs.DocsConfig();
        config.setProjectPath(projectPath);
        config.setRapProjectId("1");
        config.setRapHost("http://rap.yedaxia.me");
        config.setRapAccount("***");
        config.setRapPassword("123456");
        return config;
    }
}

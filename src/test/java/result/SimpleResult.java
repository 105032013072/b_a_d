package result;

import com.bosssoft.platform.apidocs.RapMock;

/**
 * @author yeguozhong yedaxia.github.com
 */
public class SimpleResult {

    @RapMock(value="@ID")
    private String id; // id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

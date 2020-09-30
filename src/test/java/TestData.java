import cfg.CfgMgr;
import org.junit.Test;

/**
 * 测试
 *
 * <p>
 * create by xiongjieqing on 2020/9/30 16:32
 */
public class TestData {

    @Test
    public void testLoadData() {
        CfgMgr.setDir(".temp/data");
        CfgMgr.load();
        System.out.println("ok");
    }

}

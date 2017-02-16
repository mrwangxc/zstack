import org.zstack.testlib.CreationSpecGenerator

/**
 * Created by xing5 on 2017/2/14.
 */
class Test2 {
    @org.junit.Test
    void test() {
        //new CreationSpecGenerator().generate("/root/zstack-testlib/CreationSpec.groovy")
        simple(name: 1)
    }

    void simple(Map params) {
        println(params)
    }
}

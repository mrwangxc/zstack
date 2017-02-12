import org.zstack.testlib.Test

/**
 * Created by xing5 on 2017/2/12.
 */
class Test1 extends Test {
    @Override
    void setup() {
        spring {
            include("ZoneManager.xml")
        }
    }

    @Override
    void environment() {
        env {
            zone {
                name = "zone"
                description = "test"
            }
        }.deploy()
    }

    @Override
    void test() {
        println("hello world")
    }
}

import org.zstack.testlib.ActionNode

/**
 * Created by xing5 on 2017/2/14.
 */
class Test2 {
    class N {
        void hello() {

        }
    }

    @org.junit.Test
    void test() {
        ActionNode m = [:] as ActionNode
        m.run { println("hello")}
    }
}

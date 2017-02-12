package org.zstack.testlib

/**
 * Created by xing5 on 2017/2/12.
 */
abstract class Test {
    private Deployer deployer = new Deployer()

    protected void env(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=Deployer.EnvSpec.class) Closure c) {
        def code = c.rehydrate(deployer.envSpec, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    protected abstract void test()

    @org.junit.Test
    final void doTest() {
        test()
    }
}

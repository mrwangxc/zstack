package org.zstack.testlib

import org.zstack.header.exception.CloudRuntimeException
import org.zstack.utils.ShellUtils
import org.zstack.utils.Utils
import org.zstack.utils.logging.CLogger

/**
 * Created by xing5 on 2017/2/12.
 */
abstract class Test {
    static final CLogger logger = Utils.getLogger(this.getClass())

    private final int PHASE_NONE = 0
    private final int PHASE_SETUP = 1
    private final int PHASE_ENV = 2
    private final int PHASE_TEST = 3

    private Deployer deployer = new Deployer()
    private int phase = PHASE_NONE

    protected EnvSpec env(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=EnvSpec.class) Closure c) {
        def code = c.rehydrate(deployer.envSpec, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        return deployer.envSpec
    }

    protected void spring(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = Deployer.SpringSpec.class) Closure c) {
        def code = c.rehydrate(deployer.springSpec, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    protected void simulator(String path, Closure c) {
        Deployer.simulator(path, c)
    }

    abstract void setup()
    abstract void environment()
    abstract void test()

    protected boolean DEPLOY_DB = true
    protected boolean NEED_WEB_SERVER = true
    protected boolean API_PORTAL = true

    private void deployDB() {
        logger.info("Deploying database ...")
        String home = System.getProperty("user.dir")
        String baseDir = [home, "../"].join("/")
        Properties prop = new Properties()

        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("zstack.properties"))

            String user = System.getProperty("DB.user")
            if (user == null) {
                user = prop.getProperty("DB.user")
                if (user == null) {
                    user = prop.getProperty("DbFacadeDataSource.user")
                }
                if (user == null) {
                    throw new CloudRuntimeException("cannot find DB user in zstack.properties, please set either DB.user or DbFacadeDataSource.user")
                }
            }

            String password = System.getProperty("DB.password")
            if (password == null) {
                password = prop.getProperty("DB.password")
                if (password == null) {
                    password = prop.getProperty("DbFacadeDataSource.password")
                }
                if (password == null) {
                    throw new CloudRuntimeException("cannot find DB user in zstack.properties, please set either DB.password or DbFacadeDataSource.password")
                }
            }

            ShellUtils.run("build/deploydb.sh $user $password", baseDir, false)
            logger.info("Deploying database successfully")
        } catch (Exception e) {
            throw new CloudRuntimeException("Unable to deploy zstack database for testing", e)
        }
    }

    private void nextPhase() {
        phase ++
    }

    private void prepare() {
        nextPhase()
        if (API_PORTAL) {
            spring {
                include("ManagementNodeManager.xml")
                include("ApiMediator.xml")
                include("AccountManager.xml")
            }
        }

        setup()

        if (DEPLOY_DB) {
            deployDB()
        }

        deployer.buildBeanConstructor(NEED_WEB_SERVER)

        nextPhase()
        environment()
    }

    protected <T> T bean(Class<T> clz) {
        assert phase > PHASE_SETUP : "getBean() can only be called in method environment() or test()"
        return deployer.componentLoader.getComponent(clz)
    }

    @org.junit.Test
    final void doTest() {
        try {
            prepare()
            nextPhase()
            test()
        } catch (Throwable t) {
            logger.warn(t.message, t)
            System.exit(1)
        }
    }
}

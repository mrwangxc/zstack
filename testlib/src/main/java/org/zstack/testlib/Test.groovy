package org.zstack.testlib

import org.zstack.core.db.DatabaseFacade
import org.zstack.header.exception.CloudRuntimeException
import org.zstack.utils.ShellUtils
import org.zstack.utils.Utils
import org.zstack.utils.logging.CLogger

/**
 * Created by xing5 on 2017/2/12.
 */
abstract class Test implements CreationSpec {
    static final CLogger logger = Utils.getLogger(this.getClass())

    private final int PHASE_NONE = 0
    private final int PHASE_SETUP = 1
    private final int PHASE_ENV = 2
    private final int PHASE_TEST = 3

    static Deployer deployer = new Deployer()

    private int phase = PHASE_NONE

    protected EnvSpec env(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=EnvSpec.class) Closure c) {
        c.delegate = deployer.envSpec
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c()
        return deployer.envSpec
    }

    protected void spring(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SpringSpec.class) Closure c) {
        def code = c.rehydrate(deployer.springSpec, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
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
    protected boolean INCLUDE_CORE_SERVICES = true
    protected boolean DOC = ""

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

        if (INCLUDE_CORE_SERVICES) {
            spring {
                includeCoreServices()
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

    protected <T> T specByName(String name) {
        return deployer.envSpec.specByName(name) as T
    }

    protected <T> T specByUuid(String uuid) {
        return deployer.envSpec.specByUuid(uuid) as T
    }

    protected <T> T dbFindByUuid(String uuid, Class<T> voClz) {
        DatabaseFacade dbf = bean(DatabaseFacade.class)
        return dbf.findByUuid(uuid, voClz)
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

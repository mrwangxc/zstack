import org.zstack.testlib.ClusterSpec
import org.zstack.testlib.Test
import org.zstack.testlib.ZoneSpec

/**
 * Created by xing5 on 2017/2/12.
 * 1. 2
 * 3
 */
class Test1 extends Test {
    boolean success

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

                cluster {
                    name = "cluster"
                    hypervisorType = "KVM"

                    kvm {
                        name = "kvm"
                        managementIp = "localhost"
                        usedMem = 1000
                        totalCpu = 10
                    }
                }

                nfsPrimaryStorageSpec {
                    name = "nfs"
                    url = "localhost:/nfs"
                }

                attachPrimaryStorageToCluster("nfs", "cluster")
            }
        }.deploy()
    }

    @Override
    void test() {
        println("hello world ${specByName("zone")}")
    }
}

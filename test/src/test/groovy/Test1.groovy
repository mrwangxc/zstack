import org.zstack.testlib.Test

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
            nfsPrimaryStorage()
            kvm()
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

                    /*
                    kvm {
                        name = "kvm"
                        managementIp = "localhost"
                        usedMem = 1000
                        totalCpu = 10
                    }
                    */
                }

                nfsPrimaryStorage {
                    name = "nfs"
                    url = "localhost:/nfs"
                }

                attachPrimaryStorageToCluster("nfs", "cluster")
            }
        }.deploy()
    }

    @Override
    void test() {
        def inv = zone {
            name = "zone2"
        }

        println("hello world ${inv.name}")
    }
}

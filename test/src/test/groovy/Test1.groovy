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
            vyos()
            eip()
        }
    }

    @Override
    void environment() {
        env {
            def sid = account {
                name = "xin"
                password = "password"
            }.use()

            zone {
                name = "zone"
                description = "test"

                cluster {
                    name = "cluster"
                    hypervisorType = "KVM"

                    kvm {
                        name = "kvm"
                        managementIp = "localhost"
                        username = "root"
                        password = "password"
                        usedMem = 1000
                        totalCpu = 10
                    }
                }

                nfsPrimaryStorage {
                    name = "nfs"
                    url = "localhost:/nfs"
                }

                attachPrimaryStorageToCluster("nfs", "cluster")

                l2NoVlanNetwork {
                    name = "l2"
                    physicalInterface = "eth0"

                    l3Network {
                        name = "l3"
                        session = sid

                        service {
                            provider = "vrouter"
                            types = ["DHCP", "DNS"]
                            session = sid
                        }
                    }
                }

                attachL2NetworkToCluster("l2", "cluster")
            }
        }.deploy()
    }

    @Override
    void test() {
    }
}

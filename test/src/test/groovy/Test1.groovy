import org.zstack.testlib.DiskOfferingSpec
import org.zstack.testlib.EnvSpec
import org.zstack.testlib.Test
import org.zstack.utils.data.SizeUnit

/**
 * Created by xing5 on 2017/2/12.
 * 1. 2
 * 3
 */
class Test1 extends Test {
    boolean success
    DiskOfferingSpec diskOfferingSpec
    EnvSpec envSpec

    @Override
    void setup() {
        spring {
            nfsPrimaryStorage()
            kvm()
            vyos()
            eip()
            sftpBackupStorage()
        }
    }

    @Override
    void environment() {
        envSpec = env {
            def sid = account {
                name = "xin"
                password = "password"
            }.use()

            diskOffering {
                name = "diskOffering"
                diskSize = SizeUnit.GIGABYTE.toByte(10)
            }

            instanceOffering {
                name = "instanceOffering"
                memory = SizeUnit.GIGABYTE.toByte(8)
                cpu = 4
            }

            sftpBackupStorage {
                name = "sftp"
                url = "/sftp"
                username = "root"
                password = "password"
                hostname = "localhost"

                image {
                    name = "image1"
                    url  = "http://zstack.org/download/test.qcow2"
                }
            }

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

                virtualRouterOffering {
                    name = "vr"
                    memory = SizeUnit.MEGABYTE.toByte(512)
                    cpu = 2
                    managementL3Network = l3Network("l3")
                    publicL3Network = l3Network("l3")
                    image = image("vr")
                }
            }

            attachBackupStorageToZone("sftp", "zone")

        }.deploy()
    }

    @Override
    void test() {
        diskOfferingSpec = envSpec.find("diskOffering", DiskOfferingSpec.class)
        println("${diskOfferingSpec.name}")
    }
}

package org.zstack.testlib.test

import org.zstack.core.cloudbus.CloudBus
import org.zstack.testlib.Test

/**
 * Created by xing5 on 2017/2/12.
 */
class Test1 extends Test {
    void setup() {
        spring {
            include("kvm.xml")
        }
    }

    void environment() {
        env {
            zone {
                name = "zone"
                description = "test zone"

                cluster {
                    name = "cluster1"

                    kvm {
                        name = "host1"
                        managementIp = "localhost"
                    }
                }
            }
        }
    }

    void test() {
    }
}

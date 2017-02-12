package org.zstack.testlib

import org.zstack.kvm.KVMAgentCommands
import org.zstack.kvm.KVMConstant
import org.zstack.kvm.KVMHostInventory
import org.zstack.sdk.AddKVMHostAction

/**
 * Created by xing5 on 2017/2/12.
 */
class KVMHostSpec extends HostSpec {
    String username
    String password

    KVMHostInventory inventory

    KVMHostSpec() {
        super()
    }

    String create(String sessionUuid) {
        def a = new AddKVMHostAction()
        a.name = name
        a.description = description
        a.managementIp = managementIp
        a.username = username
        a.password = password
        a.userTags = userTags
        a.systemTags = systemTags
        a.clusterUuid = (parent as ClusterSpec).inventory.uuid
        a.sessionId = sessionUuid

        inventory = result(a.call()) as KVMHostInventory

        return inventory.uuid
    }

    static {
        Deployer.simulator(KVMConstant.KVM_HOST_FACT_PATH) {
            def rsp = new KVMAgentCommands.HostCapacityResponse()
            rsp.success = true
            rsp.usedCpu = usedCpu
            rsp.cpuNum = totalCpu
            rsp.totalMemory = totalMem
            rsp.usedMemory = usedMem
            rsp.cpuSpeed = 1
            return rsp
        }
    }
}

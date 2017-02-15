package org.zstack.testlib

import org.zstack.sdk.ClusterInventory
import org.zstack.sdk.CreateClusterAction

/**
 * Created by xing5 on 2017/2/12.
 */
class ClusterSpec implements Spec {
    String name
    String description
    String hypervisorType
    List<HostSpec> hosts = []
    Map<String, PrimaryStorageSpec> primaryStorage = [:]

    ClusterInventory inventory

    ClusterSpec() {
    }

    ClusterSpec(String name, String description, String hypervisorType) {
        this.name = name
        this.description = description
        this.hypervisorType = hypervisorType
    }

    KVMHostSpec kvm(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = KVMHostSpec.class) Closure c) {
        def hspec = new KVMHostSpec()
        def code = c.rehydrate(hspec, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        addChild(hspec)
        hosts.add(hspec)
        return hspec
    }

    SpecID create(String uuid, String sessionId) {
        inventory = createCluster {
            delegate.resourceUuid = uuid
            delegate.name = name
            delegate.description = description
            delegate.hypervisorType = hypervisorType
            delegate.zoneUuid = (parent as ZoneSpec).inventory.uuid
            delegate.sessionId = sessionId
            delegate.userTags = userTags
            delegate.systemTags = systemTags
        } as ClusterInventory

        return id(name, inventory.uuid)
    }
}

package org.zstack.testlib

import org.zstack.sdk.ClusterInventory
import org.zstack.sdk.CreateClusterAction

/**
 * Created by xing5 on 2017/2/12.
 */
class ClusterSpec implements Node, CreateAction, Tag {
    String name
    String description
    String hypervisorType
    private List<HostSpec> hosts = []

    ClusterInventory inventory

    ClusterSpec() {
    }

    ClusterSpec(String name, String description, String hypervisorType) {
        this.name = name
        this.description = description
        this.hypervisorType = hypervisorType
    }

    KVMHostSpec kvm(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HostSpec.class) Closure c) {
        def hspec = new KVMHostSpec()
        def code = c.rehydrate(hspec, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        addChild(hspec)
        hosts.add(hspec)
        return hspec
    }

    void accept(NodeVisitor v) {
        v.visit(this)
    }

    SpecID create(String sessionUuid) {
        def a = new CreateClusterAction()
        a.name = name
        a.description = description
        a.hypervisorType = hypervisorType
        a.zoneUuid = (parent as ZoneSpec).inventory.uuid
        a.sessionId = sessionUuid
        a.userTags = userTags
        a.systemTags = systemTags

        inventory = errorOut(a.call()) as ClusterInventory

        return id(name, inventory.uuid)
    }
}

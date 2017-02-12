package org.zstack.testlib

import org.zstack.sdk.CreateZoneAction
import org.zstack.sdk.ZoneInventory

/**
 * Created by xing5 on 2017/2/12.
 */
class ZoneSpec implements Node, CreateAction, Tag {
    String name
    String description
    private List<ClusterSpec> clusters = []

    private ZoneInventory inventory

    ZoneSpec(String name, String description) {
        this.name = name
        this.description = description
    }

    ZoneSpec() {
    }

    void cluster(String name, String description, String hypervisorType) {
        clusters.add(new ClusterSpec(name, description, hypervisorType))
    }

    void cluster(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ClusterSpec.class) Closure c) {
        def cspec = new ClusterSpec()
        def code = c.rehydrate(cspec, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        clusters.add(cspec)
        addChild(cspec)
    }

    void accept(NodeVisitor v) {
        v.visit(this)
    }

    String create(String sessionUuid) {
        def a = new CreateZoneAction()
        a.name = name
        a.description = description
        a.sessionId = sessionUuid
        a.userTags = userTags
        a.systemTags = systemTags
        inventory = errorOut(a.call()) as ZoneInventory

        return inventory.uuid
    }
}

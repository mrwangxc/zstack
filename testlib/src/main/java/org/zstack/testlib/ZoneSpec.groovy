package org.zstack.testlib

import org.zstack.sdk.AttachPrimaryStorageToClusterAction
import org.zstack.sdk.CreateZoneAction
import org.zstack.sdk.ZoneInventory

/**
 * Created by xing5 on 2017/2/12.
 */
class ZoneSpec implements Node, CreateAction, Tag, CreationSpec {
    String name
    String description
    List<ClusterSpec> clusters = []
    List<PrimaryStorageSpec> primaryStorage = []

    ZoneInventory inventory

    ZoneSpec(String name, String description) {
        this.name = name
        this.description = description
    }

    ZoneSpec() {
    }

    ClusterSpec cluster(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ClusterSpec.class) Closure c) {
        def cspec = new ClusterSpec()
        def code = c.rehydrate(cspec, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        addChild(cspec)
        clusters.add(cspec)
        return cspec
    }

    PrimaryStorageSpec nfsPrimaryStorage(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = PrimaryStorageSpec.class) Closure c) {
        def nspec = new NfsPrimaryStorageSpec()
        def code = c.rehydrate(nspec, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        addChild(nspec)
        primaryStorage.add(nspec)
        return nspec
    }

    void attachPrimaryStorageToCluster(String primaryStorageName, String clusterName) {
        ActionNode an = {
            def ps = primaryStorage.find { it.name == primaryStorageName }
            assert ps != null : "primary storage[$primaryStorageName] not found, check your environment()"
            def cluster = clusters.find { it.name == clusterName }
            assert cluster != null : "cluster[$clusterName] not found, check your environment()"

            def a = new AttachPrimaryStorageToClusterAction()
            a.clusterUuid = cluster.inventory.uuid
            a.primaryStorageUuid = ps.inventory.uuid
            a.sessionId = Test.deployer.envSpec.session.uuid
            errorOut(a.call())
        }

        addChild(an)
    }

    SpecID create(String sessionUuid) {
        def a = new CreateZoneAction()
        a.name = name
        a.description = description
        a.sessionId = sessionUuid
        a.userTags = userTags
        a.systemTags = systemTags
        inventory = errorOut(a.call()) as ZoneInventory

        return id(name, inventory.uuid)
    }
}

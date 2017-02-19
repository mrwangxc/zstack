package org.zstack.testlib

import org.zstack.sdk.AttachBackupStorageToZoneAction
import org.zstack.sdk.AttachL2NetworkToClusterAction
import org.zstack.sdk.AttachPrimaryStorageToClusterAction
import org.zstack.sdk.ZoneInventory
import org.zstack.utils.gson.JSONObjectUtil

/**
 * Created by xing5 on 2017/2/12.
 */
class ZoneSpec implements Spec {
    String name
    String description
    List<ClusterSpec> clusters = []
    List<PrimaryStorageSpec> primaryStorage = []
    List<L2NetworkSpec> l2Networks = []
    List<VirtualRouterOfferingSpec> virtualRouterOfferingSpecs = []

    private List<Closure> afterCreated = []

    ZoneInventory inventory

    ZoneSpec(String name, String description) {
        this.name = name
        this.description = description
    }

    ZoneSpec() {
    }

    ClusterSpec cluster(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ClusterSpec.class) Closure c) {
        def cspec = new ClusterSpec()
        def code = c.rehydrate(cspec, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        addChild(cspec)
        clusters.add(cspec)
        return cspec
    }

    PrimaryStorageSpec nfsPrimaryStorage(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PrimaryStorageSpec.class) Closure c) {
        def nspec = new NfsPrimaryStorageSpec()
        def code = c.rehydrate(nspec, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        addChild(nspec)
        primaryStorage.add(nspec)
        return nspec
    }

    L2NetworkSpec l2NoVlanNetwork(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = L2NoVlanNetworkSpec.class) Closure c) {
        def lspec = new L2NoVlanNetworkSpec()
        def code = c.rehydrate(lspec, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        addChild(lspec)
        l2Networks.add(lspec)
        return lspec
    }

    L2NetworkSpec l2VlanNetwork(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = L2VlanNetworkSpec.class) Closure c) {
        def lspec = new L2VlanNetworkSpec()
        def code = c.rehydrate(lspec, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        addChild(lspec)
        l2Networks.add(lspec)
        return lspec
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

    void attachL2NetworkToCluster(String l2NetworkName, String clusterName) {
        ActionNode an = {
            def l2 = l2Networks.find { it.name = l2NetworkName }
            assert l2 != null: "l2 network[$l2NetworkName] not found, check your environment()"
            def cluster = clusters.find { it.name == clusterName }
            assert cluster != null : "cluster[$clusterName] not found, check your environment()"

            def a = new AttachL2NetworkToClusterAction()
            a.clusterUuid = cluster.inventory.uuid
            a.l2NetworkUuid = l2.inventory.uuid
            a.sessionId = Test.deployer.envSpec.session.uuid
            errorOut(a.call())
        }

        addChild(an)
    }

    void attachBackupStorage(String...names) {
        afterCreated.addAll(names.collect { String bsName ->
            return {
                BackupStorageSpec bs = findSpec(bsName, BackupStorageSpec.class)
                assert bs != null: "cannot find the backup storage[$bsName], unable to do attachBackupStorageToZone()"

                def a = new AttachBackupStorageToZoneAction()
                a.zoneUuid = inventory.uuid
                a.backupStorageUuid = bs.inventory.uuid
                a.sessionId = Test.deployer.envSpec.session.uuid
                def res = a.call()
                assert res.error == null : "AttachBackupStorageToZoneAction failure: ${JSONObjectUtil.toJsonString(res.error)}"
            }
        })
    }

    VirtualRouterOfferingSpec virtualRouterOffering(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = VirtualRouterOfferingSpec.class) Closure c) {
        def spec = new VirtualRouterOfferingSpec()
        c.delegate = spec
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c()
        addChild(spec)
        virtualRouterOfferingSpecs.add(spec)
        return spec
    }

    SpecID create(String uuid, String sessionId) {
        inventory = createZone {
            delegate.resourceUuid = uuid
            delegate.name = name
            delegate.sessionId = sessionId
            delegate.description = description
            delegate.userTags = userTags
            delegate.systemTags = systemTags
        } as ZoneInventory

        return id(name, inventory.uuid)
    }

    @Override
    void postCreate() {
        afterCreated.each { it() }
    }
}

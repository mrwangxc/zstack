package org.zstack.testlib

import org.zstack.sdk.AttachL2NetworkToClusterAction
import org.zstack.sdk.AttachPrimaryStorageToClusterAction
import org.zstack.sdk.ClusterInventory

/**
 * Created by xing5 on 2017/2/12.
 */
class ClusterSpec implements Spec {
    String name
    String description
    String hypervisorType
    List<HostSpec> hosts = []
    Map<String, PrimaryStorageSpec> primaryStorage = [:]

    private List<Closure> postCreated = []

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

    void attachPrimaryStorage(String... names) {
        names.each { String primaryStorageName ->
            postCreated.add {
                def ps = findSpec(primaryStorageName, PrimaryStorageSpec.class) as PrimaryStorageSpec
                assert ps != null: "primary storage[$primaryStorageName] not found, check your environment()"

                def a = new AttachPrimaryStorageToClusterAction()
                a.clusterUuid = inventory.uuid
                a.primaryStorageUuid = ps.inventory.uuid
                a.sessionId = Test.deployer.envSpec.session.uuid
                errorOut(a.call())
            }
        }
    }

    void attachL2Network(String ...names) {
        names.each { String l2NetworkName ->
            postCreated.add {
                def l2 = findSpec(l2NetworkName, L2NetworkSpec.class) as L2NetworkSpec
                assert l2 != null: "l2 network[$l2NetworkName] not found, check your environment()"

                def a = new AttachL2NetworkToClusterAction()
                a.clusterUuid = inventory.uuid
                a.l2NetworkUuid = l2.inventory.uuid
                a.sessionId = Test.deployer.envSpec.session.uuid
                errorOut(a.call())
            }
        }
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

    @Override
    void postCreate() {
        postCreated.each { it() }
    }
}

package org.zstack.testlib

import org.zstack.sdk.VmInstanceInventory

/**
 * Created by xing5 on 2017/2/16.
 */
class VmSpec implements Spec, HasSession {
    Closure instanceOffering
    Closure image
    Closure rootDiskOffering
    Closure cluster
    Closure host
    Closure diskOfferings
    Closure l3Networks
    Closure defaultL3Network
    String name
    String description

    VmInstanceInventory inventory

    SpecID create(String uuid, String sessionId) {
        inventory = createVmInstance {
            delegate.resourceUuid = uuid
            delegate.name = name
            delegate.description = description
            delegate.sessionId = sessionId
            delegate.instanceOfferingUuid = instanceOffering()
            delegate.imageUuid = image()
            delegate.rootDiskOfferingUuid = rootDiskOffering()
            delegate.clusterUuid = cluster()
            delegate.hostUuid = host()
            delegate.dataDiskOfferingUuids = diskOfferings()
            delegate.l3NetworkUuids = l3Networks()
            delegate.defaultL3NetworkUuid = defaultL3Network()
        }

        return id(name, inventory.uuid)
    }
}

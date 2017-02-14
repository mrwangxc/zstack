package org.zstack.testlib

import org.zstack.sdk.PrimaryStorageInventory
import org.zstack.storage.primary.nfs.NfsPrimaryStorageKVMBackend

/**
 * Created by xing5 on 2017/2/13.
 */
class NfsPrimaryStorageSpec extends PrimaryStorageSpec {

    SpecID create(String uuid, String sessionUuid) {
        inventory = addNfsPrimaryStorage {
            delegate.resourceUuid = uuid
            delegate.name = name
            delegate.description = description
            delegate.url = url
            delegate.sessionId = sessionUuid
            delegate.zoneUuid = (parent as ZoneSpec).inventory.uuid
            delegate.userTags = userTags
            delegate.systemTags = systemTags
        } as PrimaryStorageInventory

        return id(name, inventory.uuid)
    }

    static {
        Deployer.simulator(NfsPrimaryStorageKVMBackend.MOUNT_PRIMARY_STORAGE_PATH) {

        }
    }
}

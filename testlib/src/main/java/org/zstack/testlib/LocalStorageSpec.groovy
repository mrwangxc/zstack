package org.zstack.testlib

import org.zstack.sdk.PrimaryStorageInventory

/**
 * Created by xing5 on 2017/2/20.
 */
class LocalStorageSpec extends PrimaryStorageSpec implements Spec {

    SpecID create(String uuid, String sessionId) {
        inventory = addLocalPrimaryStorage {
            delegate.resourceUuid = uuid
            delegate.name = name
            delegate.description = description
            delegate.url = url
            delegate.sessionId = sessionId
            delegate.zoneUuid = (parent as ZoneSpec).inventory.uuid
            delegate.userTags = userTags
            delegate.systemTags = systemTags
        } as PrimaryStorageInventory

        return id(name, inventory.uuid)
    }
}

package org.zstack.testlib

import org.zstack.sdk.AddNfsPrimaryStorageAction
import org.zstack.sdk.PrimaryStorageInventory

/**
 * Created by xing5 on 2017/2/13.
 */
class NfsPrimaryStorageSpec extends PrimaryStorageSpec {

    SpecID create(String sessionUuid) {
        def a = new AddNfsPrimaryStorageAction()
        a.name = name
        a.description = description
        a.url = url
        a.sessionId = sessionUuid
        a.zoneUuid = (parent as ZoneSpec).inventory.uuid
        a.userTags = userTags
        a.systemTags = systemTags

        inventory = errorOut(a.call()) as PrimaryStorageInventory

        return id(name, inventory.uuid)
    }
}

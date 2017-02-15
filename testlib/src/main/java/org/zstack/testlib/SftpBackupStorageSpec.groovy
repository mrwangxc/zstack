package org.zstack.testlib

/**
 * Created by xing5 on 2017/2/15.
 */
class SftpBackupStorageSpec extends BackupStorageSpec {
    String hostname
    String username
    String password

    SpecID create(String uuid, String sessionId) {
        inventory = addSftpBackupStorage {
            delegate.resourceUuid = uuid
            delegate.name = name
            delegate.description = description
            delegate.url = url
            delegate.sessionId = sessionId
            delegate.hostname = hostname
            delegate.username = username
            delegate.password = password
            delegate.userTags = userTags
            delegate.systemTags = systemTags
        }

        return id(name, inventory.uuid)
    }
}

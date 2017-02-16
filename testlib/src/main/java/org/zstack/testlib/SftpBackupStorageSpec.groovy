package org.zstack.testlib

import org.springframework.http.HttpEntity
import org.zstack.storage.backup.sftp.SftpBackupStorageCommands
import org.zstack.storage.backup.sftp.SftpBackupStorageConstant
import org.zstack.utils.gson.JSONObjectUtil

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

    static {
        Deployer.simulator(SftpBackupStorageConstant.CONNECT_PATH) { HttpEntity<String> e, EnvSpec spec ->
            def cmd = JSONObjectUtil.toObject(e.getBody(), SftpBackupStorageCommands.ConnectCmd.class)
            BackupStorageSpec bsSpec = spec.specByUuid(cmd.uuid)

            def rsp = new SftpBackupStorageCommands.ConnectResponse()
            rsp.totalCapacity = bsSpec.totalCapacity
            rsp.availableCapacity = bsSpec.availableCapacity
            return rsp
        }

        Deployer.simulator(SftpBackupStorageConstant.ECHO_PATH) {
            return [:]
        }

        Deployer.simulator(SftpBackupStorageConstant.DOWNLOAD_IMAGE_PATH) { HttpEntity<String> e, EnvSpec spec ->
            def cmd = JSONObjectUtil.toObject(e.getBody(), SftpBackupStorageCommands.DownloadCmd.class)
            BackupStorageSpec bsSpec = spec.specByUuid(cmd.uuid)

            def rsp = new SftpBackupStorageCommands.DownloadResponse()
            rsp.size = 0
            rsp.actualSize = 0
            rsp.availableCapacity = bsSpec.availableCapacity
            rsp.totalCapacity = bsSpec.totalCapacity
            return rsp
        }

        Deployer.simulator(SftpBackupStorageConstant.GET_IMAGE_SIZE) {
            def rsp = new SftpBackupStorageCommands.GetImageSizeRsp()
            rsp.actualSize = 0
            rsp.size = 0
            return rsp
        }

        Deployer.simulator(SftpBackupStorageConstant.DELETE_PATH) {
            return new SftpBackupStorageCommands.DeleteResponse()
        }

        Deployer.simulator(SftpBackupStorageConstant.PING_PATH) { HttpEntity<String> e, EnvSpec spec ->
            def cmd = JSONObjectUtil.toObject(e.body, SftpBackupStorageCommands.PingCmd.class)
            def rsp = new SftpBackupStorageCommands.PingResponse()
            rsp.uuid = cmd.uuid
            return rsp
        }

        Deployer.simulator(SftpBackupStorageConstant.CHECK_IMAGE_METADATA_FILE_EXIST) {
            def rsp = new SftpBackupStorageCommands.CheckImageMetaDataFileExistRsp()
            rsp.exist = true
            rsp.backupStorageMetaFileName = "bs_file_info.json"
            return rsp
        }
    }
}

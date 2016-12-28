package org.zstack.header.storage.backup

doc {
    title "在这里填写API标题"

    desc "在这里填写API描述"

    rest {
        request {
            url "DELETE /v1/backup-storage/{backupStorageUuid}/exported-images/{imageUuid}"

            header (OAuth: 'the-session-uuid')

            clz APIDeleteExportedImageFromBackupStorageMsg.class

            desc ""
            
			params {

				column {
					name "backupStorageUuid"
					enclosedIn ""
					desc "镜像存储UUID"
					inUrl true
					type "String"
					optional false
					since "0.6"
					
				}
				column {
					name "imageUuid"
					enclosedIn ""
					desc "镜像UUID"
					inUrl true
					type "String"
					optional false
					since "0.6"
					
				}
				column {
					name "systemTags"
					enclosedIn ""
					desc ""
					inUrl false
					type "List"
					optional true
					since "0.6"
					
				}
				column {
					name "userTags"
					enclosedIn ""
					desc ""
					inUrl false
					type "List"
					optional true
					since "0.6"
					
				}
			}
        }

        response {
            clz APIDeleteExportedImageFromBackupStorageEvent.class
        }
    }
}
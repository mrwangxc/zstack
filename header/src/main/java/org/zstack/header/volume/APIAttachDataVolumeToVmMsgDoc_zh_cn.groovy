package org.zstack.header.volume

doc {
    title "AttachDataVolumeToVm"

    category "volume"

    desc "挂载云盘到云主机上"

    rest {
        request {
			url "POST /v1/volumes/{vmeUuid}/vm-instances/{vmInstanceUuid}"


            header (OAuth: 'the-session-uuid')

            clz APIAttachDataVolumeToVmMsg.class

            desc ""
            
			params {

				column {
					name "vmInstanceUuid"
					enclosedIn ""
					desc "云主机UUID"
					location "url"
					type "String"
					optional false
					since "0.6"
					
				}
				column {
					name "volumeUuid"
					enclosedIn ""
					desc "云盘UUID"
					location "url"
					type "String"
					optional false
					since "0.6"
					
				}
				column {
					name "systemTags"
					enclosedIn ""
					desc "系统标签"
					location "body"
					type "List"
					optional true
					since "0.6"
					
				}
				column {
					name "userTags"
					enclosedIn ""
					desc "用户标签"
					location "body"
					type "List"
					optional true
					since "0.6"
					
				}
			}
        }

        response {
            clz APIAttachDataVolumeToVmEvent.class
        }
    }
}
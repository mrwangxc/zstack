package org.zstack.header.storage.snapshot

doc {
    title "在这里填写API标题"

    desc "在这里填写API描述"

    rest {
        request {
            url "GET /v1/volume-snapshots"

            header (OAuth: 'the-session-uuid')

            clz APIQueryVolumeSnapshotMsg.class

            desc ""
            
		params APIQueryMessage.class
        }

        response {
            clz APIQueryVolumeSnapshotReply.class
        }
    }
}
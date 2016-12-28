package org.zstack.network.securitygroup

doc {
    title "在这里填写API标题"

    desc "在这里填写API描述"

    rest {
        request {
            url "GET /v1/security-groups/rules"

            header (OAuth: 'the-session-uuid')

            clz APIQuerySecurityGroupRuleMsg.class

            desc ""
            
		params APIQueryMessage.class
        }

        response {
            clz APIQuerySecurityGroupRuleReply.class
        }
    }
}
package org.zstack.header.identity

doc {
    title "在这里填写API标题"

    desc "在这里填写API描述"

    rest {
        request {
            url "POST /v1/account/{accountUuid}/resources"

            header (OAuth: 'the-session-uuid')

            clz APIChangeResourceOwnerMsg.class

            desc ""
            
			params {

				column {
					name "accountUuid"
					enclosedIn "params"
					desc "账户UUID"
					inUrl true
					type "String"
					optional false
					since "0.6"
					
				}
				column {
					name "resourceUuid"
					enclosedIn "params"
					desc ""
					inUrl false
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
            clz APIChangeResourceOwnerEvent.class
        }
    }
}
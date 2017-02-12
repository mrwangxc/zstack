package org.zstack.testlib

import org.zstack.utils.gson.JSONObjectUtil

/**
 * Created by xing5 on 2017/2/12.
 */
trait CreateAction {
    // return uuid of the created resource
    abstract String create(String sessionUuid)

    def errorOut(res) {
        assert res.error == null : "API failure: ${JSONObjectUtil.toJsonString(res.error)}"
        return res.value.inventory
    }
}
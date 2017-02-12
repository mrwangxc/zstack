package org.zstack.testlib

import org.zstack.utils.gson.JSONObjectUtil

/**
 * Created by xing5 on 2017/2/12.
 */
trait CreateAction {
    // return uuid of the created resource
    abstract String create(String sessionUuid)

    Object result(Object ret) {
        def m = ret as Map
        if (m.error != null) {
            throw new TestException("API failure: ${JSONObjectUtil.toJsonString(m.error)}")
        }

        return m.value.inventory
    }
}
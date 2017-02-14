package org.zstack.testlib

import org.zstack.sdk.CreateZoneAction
import org.zstack.sdk.ZoneInventory
import org.zstack.utils.gson.JSONObjectUtil

/**
 * Created by xing5 on 2017/2/14.
 */
trait CreationSpec {
    def errorOut(res) {
        assert res.error == null : "API failure: ${JSONObjectUtil.toJsonString(res.error)}"
        return res.value.inventory
    }

    ZoneInventory zone(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = CreateZoneAction.class) Closure c) {
        def a = new CreateZoneAction()
        a.sessionId = Test.deployer.envSpec.session?.uuid
        def code = c.rehydrate(a, this, this)
        code()
        return errorOut(a.call()) as ZoneInventory
    }
}
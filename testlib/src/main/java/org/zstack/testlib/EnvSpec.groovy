package org.zstack.testlib

import org.zstack.header.identity.AccountConstant
import org.zstack.header.identity.SessionInventory
import org.zstack.sdk.LogInByAccountAction
import org.zstack.utils.gson.JSONObjectUtil

/**
 * Created by xing5 on 2017/2/12.
 */
class EnvSpec {
    private List<ZoneSpec> zones = []

    org.zstack.sdk.SessionInventory adminSession

    void zone(String name, String description) {
        zones.add(new ZoneSpec(name, description))
    }

    void zone(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ZoneSpec.class) Closure c)  {
        def zspec = new ZoneSpec()
        def code = c.rehydrate(zspec, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        zones.add(zspec)
    }

    void adminLogin() {
        LogInByAccountAction a = new LogInByAccountAction()
        a.accountName = AccountConstant.INITIAL_SYSTEM_ADMIN_NAME
        a.password = AccountConstant.INITIAL_SYSTEM_ADMIN_PASSWORD
        def res = a.call()
        assert res.error == null : "Login failure: ${JSONObjectUtil.toJsonString(res.error)}"
        adminSession = res.value.inventory
    }

    void deploy() {
        adminLogin()

        zones.each { node ->
            node.walk { (it as CreateAction).create(adminSession.uuid) }
        }
    }
}

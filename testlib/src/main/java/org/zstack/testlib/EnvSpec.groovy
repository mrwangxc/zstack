package org.zstack.testlib

import org.zstack.header.identity.AccountConstant
import org.zstack.sdk.LogInByAccountAction
import org.zstack.sdk.SessionInventory
import org.zstack.utils.gson.JSONObjectUtil

/**
 * Created by xing5 on 2017/2/12.
 */
class EnvSpec {
    private List<ZoneSpec> zones = []

    SessionInventory session

    Map specsByName = [:]
    Map specsByUuid = [:]

    void zone(String name, String description) {
        zones.add(new ZoneSpec(name, description))
    }

    ZoneSpec zone(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ZoneSpec.class) Closure c)  {
        def zspec = new ZoneSpec()
        def code = c.rehydrate(zspec, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        zones.add(zspec)
        return zspec
    }

    void adminLogin() {
        login(AccountConstant.INITIAL_SYSTEM_ADMIN_NAME, AccountConstant.INITIAL_SYSTEM_ADMIN_PASSWORD)
    }

    void login(String accountName, String password) {
        LogInByAccountAction a = new LogInByAccountAction()
        a.accountName = accountName
        a.password = password
        def res = a.call()
        assert res.error == null : "Login failure: ${JSONObjectUtil.toJsonString(res.error)}"
        session = res.value.inventory
    }

    def specByUuid(String uuid) {
        return specsByUuid[uuid]
    }

    def specByName(String name) {
        return specsByName[name]
    }

    void deploy() {
        adminLogin()

        zones.each { it.deploy() }
    }
}

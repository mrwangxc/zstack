package org.zstack.testlib

import org.zstack.header.identity.AccountConstant
import org.zstack.sdk.AttachBackupStorageToZoneAction
import org.zstack.sdk.LogInByAccountAction
import org.zstack.sdk.SessionInventory
import org.zstack.utils.gson.JSONObjectUtil

/**
 * Created by xing5 on 2017/2/12.
 */
class EnvSpec implements Node {
    private List<ZoneSpec> zones = []
    List<AccountSpec> accounts = []

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
        addChild(zspec)
        return zspec
    }

    AccountSpec account(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = AccountSpec.class) Closure c) {
        def aspec = new AccountSpec()
        def code = c.rehydrate(aspec, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        addChild(aspec)
        accounts.add(aspec)
        return aspec
    }

    InstanceOfferingSpec instanceOffering(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = InstanceOfferingSpec.class) Closure c) {
        def spec = new InstanceOfferingSpec()
        def code = c.rehydrate(spec, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        addChild(spec)
        return spec
    }

    BackupStorageSpec sftpBackupStorage(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SftpBackupStorageSpec.class) Closure c) {
        def spec = new SftpBackupStorageSpec()
        def code = c.rehydrate(spec, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        addChild(spec)
        return spec
    }

    void attachBackupStorageToZone(String backupStorageName, String zoneName) {
        BackupStorageSpec bs = find(backupStorageName, BackupStorageSpec.class)
        assert bs != null: "cannot find the backup storage[$backupStorageName], unable to do attachBackupStorageToZone()"
        ZoneSpec zone = find(zoneName, ZoneSpec.class)
        assert zone != null: "cannot find the zone[$zoneName], unable tot do attachBackupStorageToZone()"

        ActionNode an = {
            def a = new AttachBackupStorageToZoneAction()
            a.zoneUuid = zone.inventory.uuid
            a.backupStorageUuid = bs.inventory.uuid
            a.sessionId = session.uuid
            def res = a.call()
            assert res.error == null : "AttachBackupStorageToZoneAction failure: ${JSONObjectUtil.toJsonString(res.error)}"
        }

        addChild(an)
    }

    void adminLogin() {
        session = login(AccountConstant.INITIAL_SYSTEM_ADMIN_NAME, AccountConstant.INITIAL_SYSTEM_ADMIN_PASSWORD)
    }

    SessionInventory login(String accountName, String password) {
        LogInByAccountAction a = new LogInByAccountAction()
        a.accountName = accountName
        a.password = password
        def res = a.call()
        assert res.error == null : "Login failure: ${JSONObjectUtil.toJsonString(res.error)}"
        return res.value.inventory
    }

    def specByUuid(String uuid) {
        return specsByUuid[uuid]
    }

    def specByName(String name) {
        return specsByName[name]
    }

    void deploy() {
        adminLogin()

        children.each {
            if (it instanceof Node) {
                it.deploy()
            }
        }
    }
}

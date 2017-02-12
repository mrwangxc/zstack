package org.zstack.testlib

/**
 * Created by xing5 on 2017/2/12.
 */
class EnvSpec {
    private List<ZoneSpec> zones = []

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
}

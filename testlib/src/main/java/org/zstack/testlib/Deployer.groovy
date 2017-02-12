package org.zstack.testlib

import org.zstack.utils.data.SizeUnit

/**
 * Created by xing5 on 2017/2/12.
 */

class Deployer {
    class ZoneSpec {
        String name
        String description
        private List<ClusterSpec> clusters = []

        ZoneSpec(String name, String description) {
            this.name = name
            this.description = description
        }

        ZoneSpec() {
        }

        void cluster(String name, String description, String hypervisorType) {
            clusters.add(new ClusterSpec(name, description, hypervisorType))
        }

        void cluster(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ClusterSpec.class) Closure c) {
            def cspec = new ClusterSpec()
            def code = c.rehydrate(cspec, this, this)
            code.resolveStrategy = Closure.DELEGATE_ONLY
            code()
            clusters.add(cspec)
        }
    }

    class ClusterSpec {
        String name
        String description
        String hypervisorType
        private List<HostSpec> hosts = []

        ClusterSpec() {
        }

        ClusterSpec(String name, String description, String hypervisorType) {
            this.name = name
            this.description = description
            this.hypervisorType = hypervisorType
        }

        void host(String name, String description, String managementIp,
                  Long totalMem, Long usedMem, Integer totalCpu, Integer usedCpu) {
            hosts.add(new HostSpec(name, description, managementIp, totalMem, usedMem, totalCpu, usedCpu))
        }

        void host(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HostSpec.class) Closure c) {
            def hspec = new HostSpec()
            def code = c.rehydrate(hspec, this, this)
            code.resolveStrategy = Closure.DELEGATE_ONLY
            code()
            hosts.add(hspec)
        }
    }

    class HostSpec {
        String name
        String description
        String managementIp = "127.0.0.1"
        Long totalMem = SizeUnit.GIGABYTE.toByte(32)
        Long usedMem = 0
        Integer totalCpu = 32
        Integer usedCpu = 0

        HostSpec(String name, String description, String managementIp, Long totalMem, Long usedMem, Integer totalCpu, Integer usedCpu) {
            this.name = name
            this.description = description
            this.managementIp = managementIp
            this.totalMem = totalMem
            this.usedMem = usedMem
            this.totalCpu = totalCpu
            this.usedCpu = usedCpu
        }

        HostSpec() {
        }
    }

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

    EnvSpec envSpec = new EnvSpec()
}

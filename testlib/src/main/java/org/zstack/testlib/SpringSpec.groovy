package org.zstack.testlib

/**
 * Created by xing5 on 2017/2/14.
 */
class SpringSpec {
    List<String> CORE_SERVICES = [
            "HostManager.xml",
            "ZoneManager.xml",
            "ClusterManager.xml",
            "PrimaryStorageManager.xml",
            "BackupStorageManager.xml",
            "ImageManager.xml",
            "HostAllocatorManager.xml",
            "ConfigurationManager.xml",
            "VolumeManager.xml",
            "NetworkManager.xml",
            "VmInstanceManager.xml",
            "AccountManager.xml",
            "NetworkService.xml",
            "volumeSnapshot.xml",
            "tag.xml",
    ]

    Set<String> xmls = []
    boolean all

    void include(String xml) {
        xmls.add(xml)
    }

    void includeAll() {
        all = true
    }

    void includeCoreServices() {
        CORE_SERVICES.each { include(it) }
    }

    void nfsPrimaryStorage() {
        include("NfsPrimaryStorage.xml")
    }

    void kvm() {
        include("Kvm.xml")
    }
}

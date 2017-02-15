package org.zstack.testlib

import org.zstack.sdk.BackupStorageInventory
import org.zstack.utils.data.SizeUnit

/**
 * Created by xing5 on 2017/2/15.
 */
abstract class BackupStorageSpec implements Spec {
    String name
    String description
    String url
    Long totalCapacity = SizeUnit.GIGABYTE.toByte(1000)
    Long availableCapacity = SizeUnit.GIGABYTE.toByte(1000)

    public BackupStorageInventory inventory

    ImageSpec image(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ImageSpec.class) Closure c) {
        def i = new ImageSpec()
        def code = c.rehydrate(i, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        addChild(i)
        return i
    }
}

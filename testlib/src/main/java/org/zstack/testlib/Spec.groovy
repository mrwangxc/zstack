package org.zstack.testlib

/**
 * Created by xing5 on 2017/2/15.
 */
trait Spec implements Node, CreateAction, Tag, CreationSpec {
    String uuid() {
        assert this.hasProperty("inventory") != null: "${this.class.typeName} doesn't have a field[inventory] defined," +
                "unable to produce a uuid() function"

        return {
            return inventory.uuid
        }
    }

    def findSpec(String name, Class type) {
        return Test.deployer.envSpec.find(name, type)
    }
}
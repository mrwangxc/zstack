package org.zstack.testlib

/**
 * Created by xing5 on 2017/2/12.
 */
trait Node {
    Node parent
    List<Object> children = []
    List<Node> friends = []

    void accept(NodeVisitor v) {
        v.visit(this)
    }

    void addChild(Object child) {
        if (child instanceof Node) {
            child.parent = this
        }

        children.add(child)
    }

    void addFriend(Node friend) {
        friends.add(friend)
        friend.friends.add(this)
    }

    void walkNode(Object n, Closure c) {
        if (n instanceof Node) {
            c(n)

            n.children.each {
                walkNode(it, c)
            }
        } else if (n instanceof ActionNode) {
            n.run()
        } else {
            assert true: "unknown node type ${n.class}"
        }
    }

    void walk(Closure c) {
        walkNode(this, c)
    }

    void deploy(String sessionUuid = null) {
        sessionUuid = sessionUuid == null ? Test.deployer.envSpec.session?.uuid : sessionUuid
        assert sessionUuid != null : "Not login yet!!! You need either call deploy() with a session uuid or call login() method" +
                " in environment() of the test case"

        walk {
            SpecID id = (it as CreateAction).create(sessionUuid)
            Test.deployer.envSpec.specsByName[id.name] = it
            Test.deployer.envSpec.specsByUuid[id.uuid] = it
        }
    }
}

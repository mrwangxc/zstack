package org.zstack.testlib

import org.zstack.core.Platform

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

    def find(Node n, String name, Class type) {
        if (type.isAssignableFrom(n.class) && n.name == name) {
            return n
        }

        for (Object c : n.children) {
            if (c instanceof Node) {
                def ret = find(c, name, type)
                if (ret != null) {
                    return ret
                }
            }
        }

        return null
    }

    def find(String name, Class type) {
        return find(this, name, type)
    }

    void deploy(String sessionId = null) {
        sessionId = sessionId == null ? Test.deployer.envSpec.session?.uuid : sessionId
        assert sessionId != null : "Not login yet!!! You need either call deploy() with a session uuid or call login() method" +
                " in environment() of the test case"

        walk {
            def uuid = Platform.getUuid()
            Test.deployer.envSpec.specsByUuid[uuid] = it

            if (it instanceof HasSession && it.session != null) {
                sessionId = it.session()
            }

            SpecID id = (it as CreateAction).create(uuid, sessionId)
            if (id != null) {
                Test.deployer.envSpec.specsByName[id.name] = it
            }
        }
    }
}

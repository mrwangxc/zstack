package org.zstack.testlib

/**
 * Created by xing5 on 2017/2/12.
 */
trait Node {
    Node parent
    List<Node> children = []
    List<Node> friends = []

    void accept(NodeVisitor v) {
        v.visit(this)
    }

    void addChild(Node child) {
        child.parent = this
        children.add(child)
    }

    void addFriend(Node friend) {
        friends.add(friend)
        friend.friends.add(this)
    }

    void walkNode(Node n, Closure c) {
        c(n)

        n.children.each {
            walkNode(it, c)
        }
    }

    void walk(Closure c) {
        walkNode(this, c)
    }
}

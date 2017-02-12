package org.zstack.testlib

/**
 * Created by xing5 on 2017/2/12.
 */
trait Node {
    Node parent
    List<Node> children = []
    List<Node> friends = []

    abstract void accept(NodeVisitor v)

    void addChild(Node child) {
        child.parent = this
        children.add(child)
    }

    void addFriend(Node friend) {
        friends.add(friend)
        friend.friends.add(this)
    }
}

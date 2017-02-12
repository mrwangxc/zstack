package org.zstack.testlib

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import org.zstack.core.CoreGlobalProperty
import org.zstack.header.cluster.ClusterInventory
import org.zstack.header.rest.RESTConstant
import org.zstack.header.zone.ZoneInventory
import org.zstack.kvm.KVMHostInventory
import org.zstack.sdk.AddKVMHostAction
import org.zstack.sdk.CreateAccountAction
import org.zstack.sdk.CreateClusterAction
import org.zstack.sdk.CreateZoneAction
import org.zstack.utils.data.SizeUnit
import org.zstack.utils.gson.JSONObjectUtil

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by xing5 on 2017/2/12.
 */

class Deployer {
    trait Node {
        Node parent
        List<Node> children = []
        List<Node> friends = []

        abstract void accept(Visitor v)

        void addChild(Node child) {
            child.parent = this
            children.add(child)
        }

        void addFriend(Node friend) {
            friends.add(friend)
            friend.friends.add(this)
        }
    }

    trait Tag {
        List<String> userTags
        List<String> systemTags
    }

    interface CreateAction {
        void create(String sessionUuid)
    }

    interface Visitor {
        void visit(Node n)
    }

    class ZoneSpec implements Node, CreateAction, Tag {
        String name
        String description
        private List<ClusterSpec> clusters = []

        private ZoneInventory inventory

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
            addChild(cspec)
        }

        void accept(Visitor v) {
            v.visit(this)
        }

        void create(String sessionUuid) {
            def a = new CreateZoneAction()
            a.name = name
            a.description = description
            a.sessionId = sessionUuid
            a.userTags = userTags
            a.systemTags = systemTags
            inventory = result(a.call()) as ZoneInventory
        }
    }

    class ClusterSpec implements Node, CreateAction, Tag {
        String name
        String description
        String hypervisorType
        private List<HostSpec> hosts = []

        ClusterInventory inventory

        ClusterSpec() {
        }

        ClusterSpec(String name, String description, String hypervisorType) {
            this.name = name
            this.description = description
            this.hypervisorType = hypervisorType
        }

        void kvm(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HostSpec.class) Closure c) {
            def hspec = new KVMHostSpec()
            def code = c.rehydrate(hspec, this, this)
            code.resolveStrategy = Closure.DELEGATE_ONLY
            code()
            hosts.add(hspec)
            addChild(hspec)
        }

        void accept(Visitor v) {
            v.visit(this)
        }

        void create(String sessionUuid) {
            def a = new CreateClusterAction()
            a.name = name
            a.description = description
            a.hypervisorType = hypervisorType
            a.zoneUuid = (parent as ZoneSpec).inventory.uuid
            a.sessionId = sessionUuid
            a.userTags = userTags
            a.systemTags = systemTags

            inventory = result(a.call()) as ClusterInventory
        }
    }

    abstract class HostSpec implements Node, CreateAction, Tag {
        String name
        String description
        String managementIp = "127.0.0.1"
        Long totalMem = SizeUnit.GIGABYTE.toByte(32)
        Long usedMem = 0
        Integer totalCpu = 32
        Integer usedCpu = 0

        HostSpec() {
        }

        void accept(Visitor v) {
            v.visit(this)
        }
    }

    class KVMHostSpec extends HostSpec {
        String username
        String password

        KVMHostInventory inventory

        KVMHostSpec() {
            super()
        }

        void create(String sessionUuid) {
            def a = new AddKVMHostAction()
            a.name = name
            a.description = description
            a.managementIp = managementIp
            a.username = username
            a.password = password
            a.userTags = userTags
            a.systemTags = systemTags
            a.clusterUuid = (parent as ClusterSpec).inventory.uuid
            a.sessionId = sessionUuid

            inventory = result(a.call()) as KVMHostInventory
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

    private Object result(Object ret) {
        def m = ret as Map
        if (m.error != null) {
            throw new TestException("API failure: ${JSONObjectUtil.toJsonString(m.error)}")
        }

        return m.value.inventory
    }

    EnvSpec envSpec = new EnvSpec()

    private static Map<String, Closure> httpHandlers = [:]
    private static RestTemplate restTemplate

    static {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory()
        factory.setReadTimeout(CoreGlobalProperty.REST_FACADE_READ_TIMEOUT)
        factory.setConnectTimeout(CoreGlobalProperty.REST_FACADE_CONNECT_TIMEOUT)
        restTemplate = new RestTemplate(factory)
    }

    Deployer() {
    }

    private static void replyAsyncHttpCall(HttpEntity<String> entity, Object rsp) {
        String taskUuid = entity.getHeaders().getFirst(RESTConstant.TASK_UUID)
        String callbackUrl = entity.getHeaders().getFirst(RESTConstant.CALLBACK_URL)
        String rspBody = JSONObjectUtil.toJsonString(rsp)
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        headers.setContentLength(rspBody.length())
        headers.set(RESTConstant.TASK_UUID, taskUuid)
        HttpEntity<String> rreq = new HttpEntity<String>(rspBody, headers)
        restTemplate.exchange(callbackUrl, HttpMethod.POST, rreq, String.class)
    }

    static void handleSimulatorHttpRequests(HttpServletRequest req, HttpServletResponse rsp) {
        def url = req.getRequestURI()
        def handler = httpHandlers[url]
        if (handler == null) {
            rsp.sendError(HttpStatus.NOT_FOUND.value(), "cannot find simulator handler for ${req.getRequestURI()}")
            return
        }

        StringBuilder sb = new StringBuilder()
        String line
        while ((line = req.getReader().readLine()) != null) {
            sb.append(line)
        }
        req.getReader().close()

        HttpHeaders header = new HttpHeaders()
        for (Enumeration e = req.getHeaderNames() ; e.hasMoreElements() ;) {
            String name = e.nextElement().toString()
            header.add(name, req.getHeader(name))
        }

        def entity = new HttpEntity<String>(sb.toString(), header)
        try {
            replyAsyncHttpCall(entity, handler(entity))
        } catch (Throwable t) {
            rsp.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), t.message)
        }
    }
}

package org.zstack.testlib;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by xing5 on 2017/2/12.
 */
@Component
public class SimulatorController extends AbstractUrlHandlerMapping {
    @RequestMapping(
            value = "/**",
            method = {RequestMethod.POST}
    )
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Deployer.handleSimulatorHttpRequests(request, response);
    }

    public SimulatorController() {
        for (String path : Deployer.getRegisteredUrlPath()) {
            registerHandler(path, this);
        }
    }
}

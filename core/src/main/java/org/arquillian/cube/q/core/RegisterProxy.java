package org.arquillian.cube.q.core;

import org.arquillian.cube.q.spi.Proxy;
import org.arquillian.cube.q.spi.ProxyManager;
import org.arquillian.cube.spi.Cube;
import org.arquillian.cube.spi.CubeRegistry;
import org.arquillian.cube.spi.event.lifecycle.AfterAutoStart;
import org.arquillian.cube.spi.event.lifecycle.AfterStart;
import org.arquillian.cube.spi.event.lifecycle.AfterStop;
import org.arquillian.cube.spi.event.lifecycle.BeforeStart;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;

public class RegisterProxy {

    @Inject
    private Instance<Proxy> proxyInst;
    
    @Inject
    private Instance<ServiceLoader> serviceLoaderInst;

    public void registerToxiProxyProxies(@Observes AfterAutoStart event, CubeRegistry registry) {
        Proxy proxy = proxyInst.get();
        if (proxy != null) {
            Cube<?> cube = registry.getCube(proxy.getName());

            final ProxyManager proxyManager = serviceLoaderInst.get().onlyOne(ProxyManager.class);
            if (cube != null) {
                proxyManager.proxyStarted(cube);
            }

            proxyManager.populateProxies();
        }

    }

    /**public void registerProxy(AfterStart event, CubeRegistry registry) {
        
        Proxy proxy = proxyInst.get();
        Cube<?> cube = registry.getCube(event.getCubeId());
        if(cube != null && isNotProxyCube(cube, proxy)) {
            serviceLoaderInst.get().onlyOne(ProxyManager.class).cubeStarted(cube);
        }
    }

    public void createProxyClient(AfterStart event, CubeRegistry registry) {
        
        Proxy proxy = proxyInst.get();
        
        Cube<?> cube = registry.getCube(event.getCubeId());
        if(cube != null && isProxyCube(cube, proxy)) {
            serviceLoaderInst.get().onlyOne(ProxyManager.class).proxyStarted(cube);
        }
        
    }**/

    public void unregisterProxy(@Observes AfterStop event, CubeRegistry registry) {
        Proxy proxy = proxyInst.get();
        if (proxy != null) {
            Cube<?> cube = registry.getCube(event.getCubeId());
            if (cube != null && isNotProxyCube(cube, proxy)) {
                serviceLoaderInst.get().onlyOne(ProxyManager.class).cubeStopped(cube);
            }
        }
    }
    
    private boolean isNotProxyCube(Cube<?> cube, Proxy proxy) {
        return !isProxyCube(cube, proxy);
    }

    private boolean isProxyCube(Cube<?> cube, Proxy proxy) {
        return proxy.getName().equals(cube.getId());
    }
}

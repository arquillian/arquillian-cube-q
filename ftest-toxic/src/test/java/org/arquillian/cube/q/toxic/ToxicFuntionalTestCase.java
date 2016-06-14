package org.arquillian.cube.q.toxic;

import java.net.SocketException;
import java.net.URL;

import org.arquillian.cube.HostIp;
import org.arquillian.cube.impl.util.IOUtil;
import org.arquillian.cube.q.api.Q;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class) @Ignore
public class ToxicFuntionalTestCase {

    @ArquillianResource
    private Q Q;

    @HostIp
    private String ip;
    
    @Test @InSequence(1)
    public void should() throws Exception {
        
        URL url = new URL("http://" + ip + ":" + 8085);
        String response = IOUtil.asString(url.openStream());
        Assert.assertNotNull(response);
    }

    @Test(expected = SocketException.class) @InSequence(2)
    public void shouldNot() throws Exception {
        Q.on("server2", 80).timeout(5000, () -> {

            URL url = new URL("http://" + ip + ":" + 8085);
            String response = IOUtil.asString(url.openStream());
            System.out.println(response);
            
        });
    }
}

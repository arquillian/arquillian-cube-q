package org.arquillian.cube.q.toxic.client;

import org.arquillian.cube.q.toxic.client.ToxiProxyClient.ToxicType;

public interface ToxiProxy {

    void register(String name, String listen, String upstream);
    
    Scenario given(String name);

    public interface Scenario {

        Scenario given(String name);

        Scenario downstream(ToxicType type);

        Scenario upstream(ToxicType type);

        void then(Callable callable) throws Exception;
    }
    
    public interface Callable {
        void call() throws Exception;
    }
}

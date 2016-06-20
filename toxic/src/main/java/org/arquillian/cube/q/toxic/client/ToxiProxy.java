package org.arquillian.cube.q.toxic.client;

public interface ToxiProxy {

    void register(String name, String listen, String upstream);
    
    Scenario given(String name);

    public interface Scenario {

        Scenario given(String name);
        Scenario using(ToxiProxyClient.BaseToxic toxic);
        void then(Callable callable) throws Exception;
    }
    
    public interface Callable {
        void call() throws Exception;
    }
}

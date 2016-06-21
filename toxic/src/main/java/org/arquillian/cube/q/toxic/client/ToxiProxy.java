package org.arquillian.cube.q.toxic.client;

import java.util.List;

public interface ToxiProxy {

    void register(String name, String listen, String upstream);
    
    Scenario given(String name);

    public interface Scenario {

        Scenario given(String name);
        Scenario using(List<ToxiProxyClient.BaseToxic> toxic);
        void then(Callable callable) throws Exception;
        void execute() throws Exception;
    }
    
    public interface Callable {
        void call() throws Exception;
    }
}

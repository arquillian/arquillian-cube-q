package org.arquillian.cube.q.toxic.client;

import org.arquillian.cube.q.api.Q;

import java.util.List;

public interface ToxiProxy {

    void register(String name, String listen, String upstream);

    Scenario given(String name);

    interface Scenario {

        Scenario given(String name);

        Scenario using(List<ToxiProxyClient.BaseToxic> toxic);

        void then(Callable callable) throws Exception;

        void then(Q.RunCondition runCondition, Callable callable) throws Exception;

        void execute() throws Exception;

        void execute(Q.RunCondition runCondition) throws Exception;

        void update() throws Exception;
    }

    interface Callable {
        void call() throws Exception;
    }
}

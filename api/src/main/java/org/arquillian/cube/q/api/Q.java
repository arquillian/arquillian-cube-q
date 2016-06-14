package org.arquillian.cube.q.api;

public interface Q {

    Action on(String machine, int port);
    
    public interface Action {
        
        void down(Perform perform) throws Exception;
        void timeout(int millis, Perform perform) throws Exception;
        void latency(int millis, int jitter, Perform perform) throws Exception;
        void bandwidth(int kbs, Perform perform) throws Exception;
        void slowclose(int delay, Perform perform) throws Exception;
        void slice(int average_size, int size_variation, int delay, Perform perform) throws Exception;
    }
    
    public interface Perform {
        
        void execute() throws Exception;
    }
}

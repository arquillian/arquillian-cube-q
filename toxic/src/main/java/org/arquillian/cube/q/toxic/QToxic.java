package org.arquillian.cube.q.toxic;

import org.arquillian.cube.q.api.Q;
import org.arquillian.cube.q.toxic.client.ToxiProxy;
import org.arquillian.cube.q.toxic.client.ToxiProxyClient;
import org.arquillian.cube.q.toxic.client.ToxiProxyScenario;

public class QToxic implements Q {

    private ToxiProxyScenario scenario;
    
    public QToxic(ToxiProxyScenario scenario) {
        this.scenario = scenario;
    }

    @Override
    public Action on(String machine, int port) {
        return new ToxicAction(machine + ":" + port);
    }

    public class ToxicAction implements Action {

        public String name;
        
        public ToxicAction(String name) {
            this.name = name;
        }

        @Override
        public void down(Perform perform) throws Exception {
            final ToxiProxyClient.Down down = new ToxiProxyClient.Down("down", ToxicDirectionStream.DOWNSTREAM.name(), 1f);
            run(name, down, perform);
        }

        @Override
        public void timeout(TimeoutType timeType, Perform perform) throws Exception {
            final ToxiProxyClient.Timeout toxic = new ToxiProxyClient.Timeout("timeout_downstream", ToxicDirectionStream.DOWNSTREAM.name(), 1f, timeType.getValue());
            run(name, toxic, perform);
        }

        @Override
        public void timeout(TimeoutType timeType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream, Perform perform) throws Exception {
            final ToxiProxyClient.Timeout toxic = new ToxiProxyClient.Timeout("timeout_" + toxicDirectionStream.name().toLowerCase(), toxicDirectionStream.name(), toxicityType.getValue(), timeType.getValue());
            run(name, toxic, perform);
        }

        @Override
        public void latency(LatencyType latencyType, final Perform perform) throws Exception {
            final ToxiProxyClient.Latency toxic = new ToxiProxyClient.Latency("latency_downstream", ToxicDirectionStream.DOWNSTREAM.name(), 1f, latencyType.getValue(), 0);
            run(name, toxic, perform);
        }

        @Override
        public void latency(LatencyType latencyType, JitterType jitterType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream, Perform perform) throws Exception {
            final ToxiProxyClient.Latency toxic = new ToxiProxyClient.Latency("latency_" + toxicDirectionStream.name().toLowerCase(), toxicDirectionStream.name(), toxicityType.getValue(), latencyType.getValue(), jitterType.getValue());
            run(name, toxic, perform);
        }

        @Override
        public void bandwidth(RateType rateType, Perform perform) throws Exception {
            final ToxiProxyClient.Bandwidth toxic = new ToxiProxyClient.Bandwidth("bandwidth_downstream", ToxicDirectionStream.DOWNSTREAM.name(), 1f, rateType.getValue());
            run(name, toxic, perform);
        }

        @Override
        public void bandwidth(RateType rateType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream, Perform perform) throws Exception {
            final ToxiProxyClient.Bandwidth toxic = new ToxiProxyClient.Bandwidth("bandwidth_" + toxicDirectionStream.name().toLowerCase(), toxicDirectionStream.name(), toxicityType.getValue(), rateType.getValue());
            run(name, toxic, perform);
        }

        @Override
        public void slowClose(DelayType delayType, Perform perform) throws Exception {
            final ToxiProxyClient.SlowClose toxic = new ToxiProxyClient.SlowClose("slowclose_downstream", ToxicDirectionStream.DOWNSTREAM.name(), 1f, delayType.getValue());
            run(name, toxic, perform);
        }

        @Override
        public void slowClose(DelayType delayType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream, Perform perform) throws Exception {
            final ToxiProxyClient.SlowClose toxic = new ToxiProxyClient.SlowClose("slowclose_" + toxicDirectionStream.name().toLowerCase(), toxicDirectionStream.name(), toxicityType.getValue(), delayType.getValue());
            run(name, toxic, perform);
        }

        @Override
        public void slice(SliceAverageSizeType sliceAverageSizeType, DelayType delayType, Perform perform) throws Exception {
            final ToxiProxyClient.Slice toxic = new ToxiProxyClient.Slice("slice_downstream", ToxicDirectionStream.DOWNSTREAM.name(), 1f, sliceAverageSizeType.getValue(), delayType.getValue(), 0);
            run(name, toxic, perform);
        }

        @Override
        public void slice(SliceAverageSizeType sliceAverageSizeType, DelayType delayType, SliceSizeVariationType sliceSizeVariationType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream, Perform perform) throws Exception {
            final ToxiProxyClient.Slice toxic = new ToxiProxyClient.Slice("slice_" + toxicDirectionStream.name().toLowerCase(), toxicDirectionStream.name(), toxicityType.getValue(), sliceAverageSizeType.getValue(), delayType.getValue(), sliceSizeVariationType.getValue());
            run(name, toxic, perform);
        }


        private void run(String name, ToxiProxyClient.BaseToxic toxic, final Perform perform) throws Exception {
            scenario.given(name)
                .using(toxic)
                .then(new ToxiProxy.Callable() {
                        
                        @Override
                        public void call() throws Exception {
                            perform.execute();
                        }
                    });
        }
    } 
}

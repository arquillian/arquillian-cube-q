package org.arquillian.cube.q.toxic;

import org.arquillian.cube.q.api.NetworkChaos;
import org.arquillian.cube.q.toxic.client.ToxiProxyClient;
import org.arquillian.cube.q.toxic.client.ToxiProxyScenario;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QNetworkChaosToxic implements NetworkChaos {

    private ToxiProxyScenario scenario;

    @Inject
    @ApplicationScoped
    private InstanceProducer<ToxicAction> toxicActionInstanceProducer;

    public QNetworkChaosToxic(ToxiProxyScenario scenario) {
        this.scenario = scenario;
    }

    @Override
    public Action on(String machine, int port) {
        ToxicAction toxicAction = new ToxicAction(machine + ":" + port);
        toxicActionInstanceProducer.set(toxicAction);
        return toxicActionInstanceProducer.get();
    }

    public class ToxicAction implements Action {

        private List<ToxiProxyClient.BaseToxic> toxics = new ArrayList<>();

        private String name;

        public ToxicAction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public Action down() {
            final ToxiProxyClient.Down down = new ToxiProxyClient.Down("down", ToxicDirectionStream.DOWNSTREAM.name(), 1f);
            toxics.add(down);
            return this;
        }

        @Override
        public Action timeout(TimeoutType timeType) {
            final ToxiProxyClient.Timeout toxic = new ToxiProxyClient.Timeout("timeout_downstream", ToxicDirectionStream.DOWNSTREAM.name(), 1f, timeType);
            toxics.add(toxic);
            return this;
        }

        @Override
        public Action timeout(TimeoutType timeType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream) {
            final ToxiProxyClient.Timeout toxic = new ToxiProxyClient.Timeout("timeout_" + toxicDirectionStream.name().toLowerCase(), toxicDirectionStream.name(), toxicityType.getValue(), timeType);
            toxics.add(toxic);

            return this;
        }

        @Override
        public Action latency(LatencyType latencyType) {
            final ToxiProxyClient.Latency toxic = new ToxiProxyClient.Latency("latency_downstream", ToxicDirectionStream.DOWNSTREAM.name(), 1f, latencyType, JitterType.noJitter());
            toxics.add(toxic);

            return this;
        }

        @Override
        public Action latency(LatencyType latencyType, JitterType jitterType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream) {
            final ToxiProxyClient.Latency toxic = new ToxiProxyClient.Latency("latency_" + toxicDirectionStream.name().toLowerCase(), toxicDirectionStream.name(), toxicityType.getValue(), latencyType, jitterType);
            toxics.add(toxic);

            return this;
        }

        @Override
        public Action bandwidth(RateType rateType) {
            final ToxiProxyClient.Bandwidth toxic = new ToxiProxyClient.Bandwidth("bandwidth_downstream", ToxicDirectionStream.DOWNSTREAM.name(), 1f, rateType);
            toxics.add(toxic);

            return this;
        }

        @Override
        public Action bandwidth(RateType rateType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream) {
            final ToxiProxyClient.Bandwidth toxic = new ToxiProxyClient.Bandwidth("bandwidth_" + toxicDirectionStream.name().toLowerCase(), toxicDirectionStream.name(), toxicityType.getValue(), rateType);
            toxics.add(toxic);

            return this;
        }

        @Override
        public Action slowClose(DelayType delayType) {
            final ToxiProxyClient.SlowClose toxic = new ToxiProxyClient.SlowClose("slowclose_downstream", ToxicDirectionStream.DOWNSTREAM.name(), 1f, delayType);
            toxics.add(toxic);

            return this;
        }

        @Override
        public Action slowClose(DelayType delayType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream) {
            final ToxiProxyClient.SlowClose toxic = new ToxiProxyClient.SlowClose("slowclose_" + toxicDirectionStream.name().toLowerCase(), toxicDirectionStream.name(), toxicityType.getValue(), delayType);
            toxics.add(toxic);

            return this;
        }

        @Override
        public Action slice(SliceAverageSizeType sliceAverageSizeType, DelayType delayType) {
            final ToxiProxyClient.Slice toxic = new ToxiProxyClient.Slice("slice_downstream", ToxicDirectionStream.DOWNSTREAM.name(), 1f, sliceAverageSizeType.getValue(), delayType, 0);
            toxics.add(toxic);

            return this;
        }

        @Override
        public Action slice(SliceAverageSizeType sliceAverageSizeType, DelayType delayType, SliceSizeVariationType sliceSizeVariationType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream) {
            final ToxiProxyClient.Slice toxic = new ToxiProxyClient.Slice("slice_" + toxicDirectionStream.name().toLowerCase(), toxicDirectionStream.name(), toxicityType.getValue(), sliceAverageSizeType.getValue(), delayType, sliceSizeVariationType.getValue());
            toxics.add(toxic);

            return this;
        }

        @Override
        public void exec() throws Exception {
            run();
        }

        @Override
        public void exec(Perform perform) throws Exception {
            run(perform);
        }

        @Override
        public void exec(RunCondition runCondition, Perform perform) throws Exception {
            run(runCondition, perform);
        }

        private void run() throws Exception {
            scenario.given(name)
                    .using(toxics)
                    .execute();
        }

        private void run(final RunCondition runCondition, final Perform perform) throws Exception {

            final List<ToxiProxyClient.BaseToxic> toxicsWithDistributedValues = filterToxicsContainingDistributedValues(toxics);

            scenario.given(name)
                    .using(toxics)
                    .then(runCondition, () -> {
                        while (runCondition.isExecutable()) {
                            perform.execute();

                            //update distributed values for next round
                            scenario.given(name)
                                    .using(toxicsWithDistributedValues)
                                    .update();
                        }
                    });
        }

        private void run(final Perform perform) throws Exception {
            scenario.given(name)
                .using(toxics)
                .then(perform::execute);
        }

        private List<ToxiProxyClient.BaseToxic> filterToxicsContainingDistributedValues(List<ToxiProxyClient.BaseToxic> toxics) {
            final List<ToxiProxyClient.BaseToxic> toxicsWithDistributedValues = new ArrayList<>();

            for (ToxiProxyClient.BaseToxic toxic : toxics) {
                if (toxic.hasAnyDistributedField()) {
                    toxicsWithDistributedValues.add(toxic);
                }
            }

            return Collections.unmodifiableList(toxicsWithDistributedValues);
        }

    }
}

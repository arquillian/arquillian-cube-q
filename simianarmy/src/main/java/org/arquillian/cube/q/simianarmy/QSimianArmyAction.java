package org.arquillian.cube.q.simianarmy;

import org.arquillian.cube.q.api.OperativeSystemChaos;
import org.arquillian.cube.q.simianarmy.client.BlockPortSimianArmyChaosScript;
import org.arquillian.cube.q.simianarmy.client.BurnCpuSimianArmyChaosScript;
import org.arquillian.cube.q.simianarmy.client.BurnIoSimianArmyChaosScript;
import org.arquillian.cube.q.simianarmy.client.FillDiskSimianArmyChaosScript;
import org.arquillian.cube.q.simianarmy.client.KillProcessSimianArmyChaosScript;
import org.arquillian.cube.q.simianarmy.client.NullRouteSimianArmyChaosScript;
import org.arquillian.cube.q.simianarmy.client.SimianArmyScriptChaos;
import org.arquillian.cube.spi.Cube;

import java.util.ArrayList;
import java.util.List;

public class QSimianArmyAction implements OperativeSystemChaos.Action {

    private Cube cube;
    private List<SimianArmyScriptChaos> scripts = new ArrayList<>();

    public QSimianArmyAction(Cube cube) {
        this.cube = cube;
    }

    @Override
    public OperativeSystemChaos.Action burnCpu(OperativeSystemChaos.NumberCpuType numberCpu) {
        scripts.add(new BurnCpuSimianArmyChaosScript(numberCpu.getValue()));
        return this;
    }

    @Override
    public OperativeSystemChaos.Action burnIo() {
        scripts.add(new BurnIoSimianArmyChaosScript());
        return this;
    }

    @Override
    public OperativeSystemChaos.Action failDns() {
        return blockPort(OperativeSystemChaos.PortSizeType.port(53));
    }

    @Override
    public OperativeSystemChaos.Action fillDisk(OperativeSystemChaos.SizeType size) {
        scripts.add(new FillDiskSimianArmyChaosScript(size.getValue()));
        return this;
    }

    @Override
    public OperativeSystemChaos.Action killProcess(String processName) {
        scripts.add(new KillProcessSimianArmyChaosScript(processName));
        return this;
    }

    @Override
    public OperativeSystemChaos.Action nullRoute() {
        scripts.add(new NullRouteSimianArmyChaosScript());
        return this;
    }

    @Override
    public OperativeSystemChaos.Action blockPort(OperativeSystemChaos.PortSizeType port) {
        scripts.add(new BlockPortSimianArmyChaosScript(port.getValue()));
        return this;
    }

    @Override
    public void exec() throws Exception {
        executeScripts();
    }

    @Override
    public void exec(Perform perform) throws Exception {
        try {
            executeScripts();
            perform.execute();
        } finally {
            killDDProcess();
        }

    }

    @Override
    public void exec(RunCondition runCondition, Perform perform) throws Exception {
        try {
            executeScripts();
            while (runCondition.isExecutable()) {
                perform.execute();
            }
        } finally {
            killDDProcess();
        }

    }

    private void killDDProcess() {
        new KillProcessSimianArmyChaosScript("dd").apply(cube);
    }

    private void executeScripts() {
        for (SimianArmyScriptChaos script : this.scripts) {
            script.apply(this.cube);
        }
    }
}

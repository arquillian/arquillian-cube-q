package org.arquillian.cube.q.simianarmy.client;

import java.util.ArrayList;
import java.util.List;

public class BurnCpuSimianArmyChaosScript extends SimianArmyScriptChaos {

    private int numberCpu;

    public BurnCpuSimianArmyChaosScript(int numberCpu) {
        super("burncpu");
        this.numberCpu = numberCpu;
    }


    @Override
    public String[] postProcessScript(String[] chaosScriptsContent) {
        List<String> commands = new ArrayList<>();

        for (int i=0; i < numberCpu; i++) {

            for (String chaosScriptContent : chaosScriptsContent) {
                commands.add(chaosScriptContent);
            }

        }

        return commands.toArray(new String[commands.size()]);
    }
}

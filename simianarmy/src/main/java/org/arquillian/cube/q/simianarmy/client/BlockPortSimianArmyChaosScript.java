package org.arquillian.cube.q.simianarmy.client;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockPortSimianArmyChaosScript extends SimianArmyScriptChaos {

    private Integer[] ports;

    public BlockPortSimianArmyChaosScript(Integer[] ports) {
        super("blockport");
        this.ports = ports;
    }

    @Override
    public String[] postProcessScript(String[] chaosScriptsContent) {

        final List<String> commands = new ArrayList<>();

        for (Integer port : ports) {

            Map<String, Object> params = new HashMap<>();
            params.put("port", port);
            StrSubstitutor substitutor = new StrSubstitutor(params);

            for (String chaosScriptContent : chaosScriptsContent) {
                commands.add(substitutor.replace(chaosScriptContent));
            }
        }

        return commands.toArray(new String[commands.size()]);
    }
}

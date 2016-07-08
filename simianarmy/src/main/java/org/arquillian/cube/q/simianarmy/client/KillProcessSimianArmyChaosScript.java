package org.arquillian.cube.q.simianarmy.client;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KillProcessSimianArmyChaosScript extends SimianArmyScriptChaos {

    private String process;

    public KillProcessSimianArmyChaosScript(String process) {
        super("killprocess");
        this.process = process;
    }

    @Override
    public String[] postProcessScript(String[] chaosScriptsContent) {

        List<String> commands = new ArrayList<>();

        Map<String, Object> params = new HashMap<>();
        params.put("process", process);

        StrSubstitutor substitutor = new StrSubstitutor(params);

        for (String chaosScriptContent : chaosScriptsContent) {
            commands.add(substitutor.replace(chaosScriptContent));
        }

        return commands.toArray(new String[commands.size()]);
    }
}

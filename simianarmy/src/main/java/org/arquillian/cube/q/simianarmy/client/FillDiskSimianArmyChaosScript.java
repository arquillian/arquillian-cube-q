package org.arquillian.cube.q.simianarmy.client;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FillDiskSimianArmyChaosScript extends SimianArmyScriptChaos {

    private long size;

    public FillDiskSimianArmyChaosScript(long size) {
        super("filldisk");
        this.size = size;
    }

    @Override
    public String[] postProcessScript(String[] chaosScriptsContent) {

        List<String> commands = new ArrayList<>();

        Map<String, Object> params = new HashMap<>();
        params.put("size", size);
        StrSubstitutor substitutor = new StrSubstitutor(params);

        for(String chaosScriptContent : chaosScriptsContent) {
            commands.add(substitutor.replace(chaosScriptContent));
        }

        return commands.toArray(new String[commands.size()]);
    }
}

package org.arquillian.cube.q.simianarmy.client;

import org.arquillian.cube.impl.util.IOUtil;
import org.arquillian.cube.spi.Cube;
import org.arquillian.cube.spi.metadata.CanExecuteProcessInContainer;

/**
 * Base class to execute scripts to given cube.
 */
public abstract class SimianArmyScriptChaos {

    private static final String SCRIPTS_PACKAGE = "scripts";

    private String chaosScript;
    private String[] chaosScriptContent;

    public SimianArmyScriptChaos(String chaosScript) {
        this.chaosScript = chaosScript;
        this.chaosScriptContent = IOUtil.asArrayString(
            SimianArmyScriptChaos.class.getResourceAsStream("/" + SCRIPTS_PACKAGE + "/" + this.chaosScript));
    }

    /**
     * Method that in case of scripts that requires to inject some values to the ones provided by the caller must
     * implement to substitute the values on given script.
     * Also can be used to modify the script in any situation it might require external parameters
     *
     * @param chaosScriptContent
     *     of script as read.
     *
     * @return Chaos script with the substitutions.
     */
    public String[] postProcessScript(String[] chaosScriptContent) {
        return chaosScriptContent;
    }

    public void apply(Cube cube) {
        String[] scriptsToExecute = postProcessScript(this.chaosScriptContent);

        for (String scriptToExecute : scriptsToExecute) {
            if (cube.hasMetadata(CanExecuteProcessInContainer.class)) {
                final String[] command = scriptToExecute.split("\\s+");
                final CanExecuteProcessInContainer executeProcess =
                    (CanExecuteProcessInContainer) cube.getMetadata(CanExecuteProcessInContainer.class);
                executeProcess.exec(command);
            }
        }
    }
}

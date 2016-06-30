package org.arquillian.cube.q.pumba.util;

import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;

import java.util.ArrayList;
import java.util.Collection;

public class CommandLineUtil {

    private final static int PUMBA_INDEX = 0;
    private final static int HOST_INDEX = 1;
    private final static int IP_INDEX = 2;
    private final static int TLS_INDEX = 3;
    private final static int RUN_INDEX = 4;
    private final static int RANDOM_INDEX = 5;
    private final static int CHAOS_INDEX = 6;
    private final static int CHAOS_COMMAND_INDEX = 7;

    private CommandLineUtil() {
        super();
    }

    public static final Collection<String> pumbaCli(String chaosCommand, boolean random, CubeDockerConfiguration cubeDockerConfiguration) {

        String[] runningCommand = new String[8];
        runningCommand[PUMBA_INDEX] = "pumba";
        runningCommand[RUN_INDEX] = "run";
        runningCommand[CHAOS_INDEX] = "--chaos";

        if (random) {
            runningCommand[RANDOM_INDEX] = "--random";
        }

        if (!isNativeDocker(cubeDockerConfiguration)) {
            runningCommand[HOST_INDEX] = "--host";
            runningCommand[IP_INDEX] = cubeDockerConfiguration.getDockerServerUri();
            runningCommand[TLS_INDEX] = "--tlsverify";
        }

        runningCommand[CHAOS_COMMAND_INDEX] = chaosCommand;


        return createCommandLine(runningCommand);

    }

    private static boolean isNativeDocker(CubeDockerConfiguration cubeDockerConfiguration) {
        return cubeDockerConfiguration.getDockerServerUri().startsWith("unix");
    }

    private static Collection<String> createCommandLine(String[] cmd) {
        Collection<String> cli = new ArrayList<>();
        for(String c : cmd) {
            if (c != null && !"".equals(c.trim())) {
                cli.add(c);
            }
        }

        return cli;
    }

}

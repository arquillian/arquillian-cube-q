package org.arquillia.cube.q.simianarmy.client;

import org.arquillian.cube.q.simianarmy.client.BlockPortSimianArmyChaosScript;
import org.arquillian.cube.q.simianarmy.client.BurnCpuSimianArmyChaosScript;
import org.arquillian.cube.q.simianarmy.client.BurnIoSimianArmyChaosScript;
import org.arquillian.cube.q.simianarmy.client.FillDiskSimianArmyChaosScript;
import org.arquillian.cube.q.simianarmy.client.KillProcessSimianArmyChaosScript;
import org.arquillian.cube.q.simianarmy.client.NullRouteSimianArmyChaosScript;
import org.arquillian.cube.spi.Cube;
import org.arquillian.cube.spi.metadata.CanExecuteProcessInContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimianArmyScriptChaosTest {

    @Mock
    Cube cube;

    @Mock
    CanExecuteProcessInContainer canExecuteProcessInContainer;

    @Before
    public void configureMocks() {
        when(cube.hasMetadata(CanExecuteProcessInContainer.class)).thenReturn(true);
        when(cube.getMetadata(CanExecuteProcessInContainer.class)).thenReturn(canExecuteProcessInContainer);
    }

    @Test
    public void shouldBlockPort() {
        final BlockPortSimianArmyChaosScript blockPortSimianArmyChaosScript =
            new BlockPortSimianArmyChaosScript(new Integer[] {80, 8080});
        blockPortSimianArmyChaosScript.apply(cube);

        verify(canExecuteProcessInContainer).exec("iptables", "-A", "INPUT", "-p", "tcp", "-m", "tcp", "--dport", "80",
            "-j", "DROP");
        verify(canExecuteProcessInContainer).exec("iptables", "-A", "INPUT", "-p", "udp", "-m", "udp", "--dport", "80",
            "-j", "DROP");
        verify(canExecuteProcessInContainer).exec("iptables", "-A", "INPUT", "-p", "tcp", "-m", "tcp", "--dport", "8080",
            "-j", "DROP");
        verify(canExecuteProcessInContainer).exec("iptables", "-A", "INPUT", "-p", "udp", "-m", "udp", "--dport", "8080",
            "-j", "DROP");
    }

    @Test
    public void shouldBurnCpu() {
        final BurnCpuSimianArmyChaosScript burnCpuSimianArmyChaosScript = new BurnCpuSimianArmyChaosScript(2);
        burnCpuSimianArmyChaosScript.apply(cube);

        verify(canExecuteProcessInContainer, times(2)).exec("dd", "if=/dev/zero", "of=/dev/null");
    }

    @Test
    public void shouldBurnIo() {
        BurnIoSimianArmyChaosScript burnIoSimianArmyChaosScript = new BurnIoSimianArmyChaosScript();
        burnIoSimianArmyChaosScript.apply(cube);

        verify(canExecuteProcessInContainer).exec("dd", "if=/dev/urandom", "of=/burn", "bs=1M", "count=1024",
            "iflag=fullblock");
    }

    @Test
    public void shouldFillDisk() {
        final FillDiskSimianArmyChaosScript fillDiskSimianArmyChaosScript = new FillDiskSimianArmyChaosScript(10000);
        fillDiskSimianArmyChaosScript.apply(cube);

        verify(canExecuteProcessInContainer).exec("dd", "if=/dev/urandom", "of=/burn", "bs=1M", "count=10000",
            "iflag=fullblock");
    }

    @Test
    public void shouldKillProcess() {
        KillProcessSimianArmyChaosScript killProcessSimianArmyChaosScript = new KillProcessSimianArmyChaosScript("java");
        killProcessSimianArmyChaosScript.apply(cube);

        verify(canExecuteProcessInContainer).exec("pkill", "-f", "java");
    }

    @Test
    public void shouldNullRoute() {
        NullRouteSimianArmyChaosScript nullRouteSimianArmyChaosScript = new NullRouteSimianArmyChaosScript();
        nullRouteSimianArmyChaosScript.apply(cube);

        verify(canExecuteProcessInContainer).exec("ip", "route", "add", "blackhole", "10.0.0.0/8");
    }
}

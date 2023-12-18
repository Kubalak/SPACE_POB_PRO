package pl.psk.termdemo.ssh;

import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;

public class VTClientShellFactory implements ShellFactory {
    @Override
    public Command createShell(ChannelSession channel) {
        return new VT100SSHClientHandler();
    }
}

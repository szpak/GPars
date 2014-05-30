// GPars - Groovy Parallel Systems
//
// Copyright © 2008-11  The original author or authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package groovyx.gpars.remote;

import groovyx.gpars.remote.message.HostIdMsg;
import groovyx.gpars.serial.SerialMsg;
import io.netty.channel.Channel;

/**
 * Represents connection to remote host
 *
 * @author Alex Tkachman
 */
public abstract class RemoteConnection {
    private final LocalHost localHost;

    private RemoteHost host;

    protected RemoteConnection(final LocalHost provider) {
        this.localHost = provider;
    }

    public void onMessage(final SerialMsg msg) {
        if (host == null) {
            host = (RemoteHost) localHost.getSerialHost(msg.hostId, this);
        } else {
            throw new IllegalStateException("Unexpected message: " + msg);
        }
    }

    @SuppressWarnings({"EmptyMethod"})
    public void onException(final Throwable cause) {
    }

    public void onConnect() {
        write(new HostIdMsg(localHost.getId()));
    }

    public void onDisconnect() {
        localHost.onDisconnect(host);
    }

    public abstract void write(SerialMsg msg);

    public RemoteHost getHost() {
        return host;
    }

    public void setHost(final RemoteHost host) {
        this.host = host;
    }

    public abstract void disconnect();
}

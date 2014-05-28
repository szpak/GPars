// GPars - Groovy Parallel Systems
//
// Copyright © 2008-10, 2014  The original author or authors
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

package groovyx.gpars.remote.netty;

import groovyx.gpars.remote.BroadcastDiscovery;
import groovyx.gpars.remote.LocalHost;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Transport provider using Netty
 *
 * @author Alex Tkachman
 */
public class NettyTransportProvider extends LocalHost {

    private final Map<UUID, NettyClient> clients = Collections.synchronizedMap(new HashMap<UUID, NettyClient>());

    final NettyServer server;

    final BroadcastDiscovery broadcastDiscovery;

    public NettyTransportProvider(String address) throws InterruptedException {
        server = new NettyServer(this, address);
        server.start();

        System.err.printf("Server listens on: %s:%d%n", server.getAddress().getHostString(), server.getAddress().getPort());

        this.broadcastDiscovery = new BroadcastDiscovery(getId(), server.getAddress()) {
            @Override
            protected void onDiscovery(final UUID uuid, final SocketAddress address) throws InterruptedException {
                if (uuid.equals(getId())) {
                    return;
                }

                NettyClient client = clients.get(uuid);
                if (client == null) {
                    client = new NettyClient(NettyTransportProvider.this, ((InetSocketAddress)address).getHostString(), ((InetSocketAddress)address).getPort());
                    client.addDisconnectListener(() -> { System.out.println("Disconnected!"); clients.remove(uuid); });
                    client.start();
                    clients.put(uuid, client);
                }
            }
        };

       broadcastDiscovery.start();
    }

    @Override
    public void disconnect() throws InterruptedException {
        super.disconnect();
        broadcastDiscovery.stop();

        server.stop();

        for (final NettyClient client : clients.values()) {
            client.stop();
        }
    }
}

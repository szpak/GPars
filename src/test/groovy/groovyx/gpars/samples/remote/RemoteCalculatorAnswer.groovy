package groovyx.gpars.samples.remote

import groovyx.gpars.actor.Actors
import groovyx.gpars.remote.LocalNode
import groovyx.gpars.remote.netty.NettyTransportProvider;

println "Remote Calculator (Answer)"

def transport = new NettyTransportProvider("10.0.0.1", 9000)

def mainNode = new LocalNode(transport, {
    println "HI, I am $id"

    react { a ->
        react { b ->
            reply a + b
        }
    }
})

mainNode.mainActor.join()
transport.disconnect()
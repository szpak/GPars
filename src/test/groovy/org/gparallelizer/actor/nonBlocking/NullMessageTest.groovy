//  GParallelizer
//
//  Copyright © 2008-9  The original author or authors
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License. 

package org.gparallelizer.actor.impl

import java.util.concurrent.CountDownLatch
import org.gparallelizer.dataflow.DataFlowVariable
import static org.gparallelizer.actor.Actors.actor
import org.gparallelizer.actor.Actors

public class NullMessageTest extends GroovyTestCase {
    public void testNullMesage() {
        volatile def result = ''
        final def latch = new CountDownLatch(1)
        final AbstractPooledActor actor = actor {
            react {
                result = it
                latch.countDown()
            }
        }.start()
        actor << null
        latch.await()
        assertNull result
    }

    public void testNullMesageFromActor() {
        volatile def result = ''
        final def latch = new CountDownLatch(1)
        final AbstractPooledActor actor = actor {
            react {
                result = it
                latch.countDown()
            }
        }.start()
        Actors.actor {
            actor << null
            latch.await()
        }.start()
        latch.await()
        assertNull result
    }

    public void testNullMesageFromActorWithReply() {
        final def result = new DataFlowVariable()
        final AbstractPooledActor actor = actor {
            react {
                reply 10
            }
        }.start()
        Actors.actor {
            actor << null
            react {
                result << it
            }
        }.start()
        assertEquals 10, result.val
    }
}
// GPars - Groovy Parallel Systems
//
// Copyright © 2008-10  The original author or authors
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

package org.codehaus.gpars.javademo.benchmark

//#! /usr/bin/env groovy


import groovyx.gpars.actor.Actors
import groovyx.gpars.actor.DynamicDispatchActor

void execute(final int actorCount) {
    final long n = 1000000000l
    final double delta = 1.0d / n
    private final long sliceSize = n / actorCount
    final long startTimeNanos = System.nanoTime()

    final accumulator = new DDAAccumulator(actorCount)
    accumulator.start()
    for (index in 0..<actorCount) {
        final localIndex = index
        Actors.actor {
            accumulator << (new ProcessSlice(localIndex, sliceSize, delta)).compute()
        }
    }

    accumulator.join()
    final double pi = 4.0d * accumulator.sum * delta
    final double elapseTime = (System.nanoTime() - startTimeNanos) / 1e9
    println("==== Groovy/Java GPars DynamicDispatchActorScript pi = " + pi)
    println("==== Groovy/Java GPars DynamicDispatchActorScript iteration count = " + n)
    println("==== Groovy/Java GPars DynamicDispatchActorScript elapse = " + elapseTime)
    println("==== Groovy/Java GPars DynamicDispatchActorScript processor count = " + Runtime.runtime.availableProcessors());
    println("==== Groovy/Java GPars DynamicDispatchActorScript actor count = " + actorCount)
}

final class DDAAccumulator extends DynamicDispatchActor {

    private final actorCount
    def sum = 0.0
    private def counter = 0

    def DDAAccumulator(actorCount) {
        this.actorCount = actorCount
    }

    void onMessage(value) {
        sum += value
        counter += 1
        if (counter == actorCount) {
            terminate()
        }
    }
}
execute(1)
println()
execute(2)
println()
execute(8)
println()
execute(32)

/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.sun.tools.sjavac.server;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An sjavac implementation that keeps track of idleness and shuts down the
 * given Terminable upon idleness timeout.
 *
 * An idleness timeout kicks in {@code idleTimeout} milliseconds after the last
 * request is completed.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class IdleResetSjavac implements Sjavac {

    private final Sjavac delegate;
    private final AtomicInteger outstandingCalls = new AtomicInteger();
    private final Terminable toShutdown;
    private final Timer idlenessTimer = new Timer();
    private final long idleTimeout;

    // Class invariant: idlenessTimerTask != null <-> idlenessTimerTask is scheduled
    private TimerTask idlenessTimerTask;

    public IdleResetSjavac(Sjavac delegate,
                            Terminable toShutdown,
                            long idleTimeout) {
        this.delegate = delegate;
        this.toShutdown = toShutdown;
        this.idleTimeout = idleTimeout;
        scheduleTimeout();
    }

    @Override
    public SysInfo getSysInfo() {
        startCall();
        try {
            return delegate.getSysInfo();
        } finally {
            endCall();
        }
    }

    @Override
    public CompilationResult compile(String protocolId,
                                     String invocationId,
                                     String[] args,
                                     List<File> explicitSources,
                                     Set<URI> sourcesToCompile,
                                     Set<URI> visibleSources) {
        startCall();
        try {
            return delegate.compile(protocolId,
                                    invocationId,
                                    args,
                                    explicitSources,
                                    sourcesToCompile,
                                    visibleSources);
        } finally {
            endCall();
        }
    }

    private void startCall() {
        // Was there no outstanding calls before this call?
        if (outstandingCalls.incrementAndGet() == 1) {
            // Then the timer task must have been scheduled
            if (idlenessTimerTask == null)
                throw new IllegalStateException("Idle timeout already cancelled");
            // Cancel timeout task
            idlenessTimerTask.cancel();
            idlenessTimerTask = null;
        }
    }

    private void endCall() {
        if (outstandingCalls.decrementAndGet() == 0) {
            // No more outstanding calls. Schedule timeout.
            scheduleTimeout();
        }
    }

    private void scheduleTimeout() {
        if (idlenessTimerTask != null)
            throw new IllegalStateException("Idle timeout already scheduled");
        idlenessTimerTask = new TimerTask() {
            public void run() {
                toShutdown.shutdown("Server has been idle for " + (idleTimeout / 1000) + " seconds.");
            }
        };
        idlenessTimer.schedule(idlenessTimerTask, idleTimeout);
    }

    @Override
    public void shutdown() {
        idlenessTimer.cancel();
        delegate.shutdown();
    }

    @Override
    public String serverSettings() {
        return delegate.serverSettings();
    }
}

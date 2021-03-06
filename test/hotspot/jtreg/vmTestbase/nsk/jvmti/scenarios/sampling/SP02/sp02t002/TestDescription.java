/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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


/*
 * @test
 *
 * @summary converted from VM Testbase nsk/jvmti/scenarios/sampling/SP02/sp02t002.
 * VM Testbase keywords: [quick, jpda, jvmti, noras]
 * VM Testbase readme:
 * DESCRIPTION
 *     This JVMTI test is for SP02 scenario of "time sampling profiling" area.
 *     This test checks that JVMTI methods GetStackTrace() returns expected list
 *     of stack frames including frame for tested method.
 *     Threads are supended/resumed individually by SuspendThread() and ResumeThread().
 *     Checked statements:
 *         - number of stack frames returned by GetFramesCount() should be equal or
 *           less than number returned by successive call to GetStackTrace()
 *         - list of stack frames returned by GetStackTrace() should include
 *           frame for tested method
 *     Tested threads:
 *         Running             - running in Java method
 *         Entering            - entering monitor in synchronized block
 *         Waiting             - waiting on Object.wait()
 *         Sleeping            - sleeping in Thread.sleep()
 *         RunningInterrupted  - running after interruption
 *         RunningNative       - running in native method
 *     Testcases:
 *         - start threads
 *         - check stack frames of not suspended threads
 *         - suspend each threads
 *         - check stack frames of suspended threads
 *         - resume each threads
 *         - check stack frames of resumed threads
 * COMMENTS
 *     Fixed according to test bug:
 *     6405644 TEST_BUG: no proper sync with agent thread in sp02t001/sp02t003
 *
 * @library /vmTestbase
 *          /test/lib
 * @run driver jdk.test.lib.FileInstaller . .
 * @run main/othervm/native
 *      -agentlib:sp02t002=-waittime=5
 *      nsk.jvmti.scenarios.sampling.SP02.sp02t002
 */


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
 * @summary converted from VM Testbase nsk/jvmti/AttachOnDemand/attach010.
 * VM Testbase keywords: [jpda, jvmti, noras, feature_282, vm6, jdk]
 * VM Testbase readme:
 * Description :
 *     Test tries to load java agent to the VM after the VM has started using
 *     Attach API (com.sun.tools.attach).
 *     Test is based on the nsk.share.aod framework. In this framework each java
 *     agent starts new thread from the 'agentmain' method, and all test checks are executed
 *     in this thread.
 *     This test checks that loaded agent can use and change streams System.in, System.out and System.err.
 *
 * @library /vmTestbase
 *          /testlibrary
 *          /test/lib
 * @run driver jdk.test.lib.FileInstaller . .
 * @build nsk.share.aod.AODTestRunner
 *        nsk.share.aod.TargetApplicationWaitingAgents
 *
 * @comment create attach010Agent00.jar in current directory
 * @build nsk.jvmti.AttachOnDemand.attach010.attach010Agent00
 * @run driver ClassFileInstaller nsk.jvmti.AttachOnDemand.attach010.attach010Agent00
 * @build ExecDriver
 * @run driver PropertyResolvingWrapper ExecDriver --cmd
 *      ${compile.jdk}/bin/jar
 *      -cfm attach010Agent00.jar ${test.src}/attach010Agent00.mf
 *      nsk/jvmti/AttachOnDemand/attach010/attach010Agent00.class
 *
 * @run main/othervm PropertyResolvingWrapper
 *      nsk.share.aod.AODTestRunner
 *      -jdk ${test.jdk}
 *      "-javaOpts=-XX:+UsePerfData ${test.vm.opts} ${test.java.opts}"
 *      -target nsk.share.aod.TargetApplicationWaitingAgents
 *      -ja attach010Agent00.jar
 */


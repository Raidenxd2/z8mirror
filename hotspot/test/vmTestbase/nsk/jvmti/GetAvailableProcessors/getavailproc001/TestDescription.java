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
 * @summary converted from VM Testbase nsk/jvmti/GetAvailableProcessors/getavailproc001.
 * VM Testbase keywords: [quick, jpda, jvmti, noras]
 * VM Testbase readme:
 * DESCRIPTION
 *     This JVMTI test exercises JVMTI thread function GetAvailableProcessors().
 *     This tests checks that GetAvailableProcessors() returns number of
 *     available processors never smaller than one.
 *     The following checks are performed for GetAvailableProcessors():
 *         - in JVM_OnLoad() on OnLoad phase
 *         - in VM_INIT event callback on live phase
 *         - in agent thread on live phase
 *         - in VM_DEATH callback on live phase
 *     Each check passes if GetAvailableProcessors() returns no errors and
 *     any number of processors not less than one.
 *     If all checks are successful, then test pases with exit status 95.
 *     If any check fails or other error ocuurs, then agent sets FAIL status
 *     and the test fails with exit status 97.
 *     If the last check (in VM_DEATH callback) fails, then C-language exit()
 *     function is used to force VM exit with fail status 97.
 * COMMENTS
 *
 * @library /vmTestbase
 *          /test/lib
 * @run driver jdk.test.lib.FileInstaller . .
 * @run main/othervm/native
 *      -agentlib:getavailproc001=-waittime=5
 *      nsk.jvmti.GetAvailableProcessors.getavailproc001
 */


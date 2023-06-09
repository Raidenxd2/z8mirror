/*
 * Copyright (c) 2017, 2018, Oracle and/or its affiliates. All rights reserved.
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
 * @summary converted from VM Testbase metaspace/stressHierarchy/stressHierarchy011.
 * VM Testbase keywords: [nonconcurrent, javac, no_cds]
 *
 * @requires vm.opt.final.ClassUnloading
 * @library /vmTestbase
 *          /testlibrary /testlibrary/whitebox /test/lib
 * @run driver jdk.test.lib.FileInstaller . .
 * @build sun.hotspot.WhiteBox
 * @run driver ClassFileInstaller sun.hotspot.WhiteBox
 *                                sun.hotspot.WhiteBox$WhiteBoxPermission
 * @comment generate and compile metaspace.stressHierarchy.common.HumongousClass
 * @run driver metaspace.stressHierarchy.common.GenClassesBuilder
 * @run main/othervm
 *      -XX:MaxMetaspaceSize=450m
 *      -Xss10m
 *      -Xbootclasspath/a:.
 *      -XX:+UnlockDiagnosticVMOptions
 *      -XX:+WhiteBoxAPI
 *      metaspace.stressHierarchy.common.StressHierarchy3
 *      -treeDepth 70
 *      -minLevelSize 10
 *      -maxLevelSize 100
 *      -hierarchyType MIXED
 *      -triggerUnloadingByFillingMetaspace
 */

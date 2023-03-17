/*
 * Copyright (c) 2010, 2018, Oracle and/or its affiliates. All rights reserved.
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
 * @summary converted from VM Testbase vm/mlvm/anonloader/stress/byteMutation.
 * VM Testbase keywords: [feature_mlvm, nonconcurrent]
 *
 * @library /vmTestbase
 *          /test/lib
 * @run driver jdk.test.lib.FileInstaller . .
 *
 * @comment build test class and indify classes
 * @build vm.mlvm.anonloader.stress.byteMutation.Test
 * @run driver vm.mlvm.share.IndifiedClassesBuilder
 *
 * @run main/othervm vm.mlvm.anonloader.stress.byteMutation.Test -stressIterationsFactor 100000
 */

package vm.mlvm.anonloader.stress.byteMutation;

import vm.mlvm.anonloader.share.AnonkTestee01;
import vm.mlvm.anonloader.share.StressClassLoadingTest;
import vm.share.FileUtils;
import vm.share.options.Option;

/**
 * The test does the following in a cycle:
 *
 * <ol>
 * <li>Takes bytes from a valid class file
 * <li>Sets 1 to 5 bytes in random positions to random values
 * <li>Tries to load such class using:
 *     <ul>
 *       <li>a custom class loader,
 *       <li>{@link sun.misc.Unsafe#defineAnonymousClass}
 *           when {@code -unsafeLoad true} option is passed to the test.
 *     </ul>
 * </ol>
 *
 * <p>In most cases the resulting class file is invalid and rejected by
 * the VM verifier. But this test tries to find pathological cases, such
 * as infinite loops during verification or VM crashes.
 *
 * <p>NB: There is a tool to load invalid classes saved by this test.
 * Please see tool documentation at {@link vm.mlvm.tools.LoadClass}
 *
 */
public class Test extends StressClassLoadingTest {
    private final static Class<?> HOST_CLASS = AnonkTestee01.class;
    private final byte[] testeeBytes;
    @Option(name = "mutationCount", default_value = "3",
            description = "How many bytes to mutate in a class")
    private int mutationCount = 3;

    /**
     * Constructs the test.
     * @throws Exception if there are any errors when
     * reading {@link vm.mlvm.anonloader.share.AnonkTestee01} class bytecodes.
     */
    public Test() throws Exception {
        this.testeeBytes = FileUtils.readClass(AnonkTestee01.class.getName());
    }

    /**
     * Returns {@link vm.mlvm.anonloader.share.AnonkTestee01} class to the
     * parent.
     * @return {@link vm.mlvm.anonloader.share.AnonkTestee01} class.
     */
    @Override
    protected Class<?> getHostClass() {
        return HOST_CLASS;
    }

    /**
     * Takes {@link vm.mlvm.anonloader.share.AnonkTestee01} class bytecodes
     * and modifies mutationCount bytes setting them to random values.
     * @return {@link vm.mlvm.anonloader.share.AnonkTestee01} class bytecodes with modified bytes.
     */
    @Override
    protected byte[] generateClassBytes() {
        // TODO: there is non-zero probability that generated bytecode will be
        // valid, so it should be a subject of fuzzing mechanism
        byte[] alteredBytes = testeeBytes.clone();
        for (int j = 0; j < mutationCount; ++j) {
            alteredBytes[getRNG().nextInt(alteredBytes.length)] = (byte) getRNG().nextInt(256);
        }
        return alteredBytes;
    }

    /**
     * Runs the test.
     * @param args Test arguments.
     */
    public static void main(String[] args) {
        StressClassLoadingTest.launch(args);
    }
}

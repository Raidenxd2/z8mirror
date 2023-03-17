/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
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

/**
 * @test
 * @bug 8211936
 * @summary Tests of BigDecimal.intValueExact
 */
import java.math.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.AbstractMap.SimpleImmutableEntry;

public class IntValueExactTests {
    public static void main(String... args) {
        int failures = 0;

        failures += intValueExactSuccessful();
        failures += intValueExactExceptional();

        if (failures > 0) {
            throw new RuntimeException("Incurred " + failures +
                                       " failures while testing intValueExact.");
        }
    }

    private static int simpleIntValueExact(BigDecimal bd) {
        return bd.toBigIntegerExact().intValue();
    }

    private static int intValueExactSuccessful() {
        int failures = 0;

        // Strings used to create BigDecimal instances on which invoking
        // intValueExact() will succeed.
        Map<BigDecimal, Integer> successCases = Stream.of(
            new SimpleImmutableEntry<>(new BigDecimal("2147483647"),    Integer.MAX_VALUE), // 2^31 -1
            new SimpleImmutableEntry<>(new BigDecimal("2147483647.0"),  Integer.MAX_VALUE),
            new SimpleImmutableEntry<>(new BigDecimal("2147483647.00"), Integer.MAX_VALUE),

            new SimpleImmutableEntry<>(new BigDecimal("-2147483648"),   Integer.MIN_VALUE), // -2^31
            new SimpleImmutableEntry<>(new BigDecimal("-2147483648.0"), Integer.MIN_VALUE),
            new SimpleImmutableEntry<>(new BigDecimal("-2147483648.00"),Integer.MIN_VALUE),

            new SimpleImmutableEntry<>(new BigDecimal("1e0"),    1),
            new SimpleImmutableEntry<>(new BigDecimal(BigInteger.ONE, -9),   1_000_000_000),

            new SimpleImmutableEntry<>(new BigDecimal("0e13"),   0), // Fast path zero
            new SimpleImmutableEntry<>(new BigDecimal("0e32"),   0),
            new SimpleImmutableEntry<>(new BigDecimal("0e512"), 0),

            new SimpleImmutableEntry<>(new BigDecimal("10.000000000000000000000000000000000"), 10))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (Map.Entry<BigDecimal, Integer> testCase : successCases.entrySet()) {
            BigDecimal bd = testCase.getKey();
            int expected = testCase.getValue();
            try {
                int intValueExact = bd.intValueExact();
                if (expected != intValueExact ||
                    intValueExact != simpleIntValueExact(bd)) {
                    failures++;
                    System.err.println("Unexpected intValueExact result " + intValueExact +
                                       " on " + bd);
                }
            } catch (Exception e) {
                failures++;
                System.err.println("Error on " + bd + "\tException message:" + e.getMessage());
            }
        }
        return failures;
    }

    private static int intValueExactExceptional() {
        int failures = 0;
        List<BigDecimal> exceptionalCases = Stream.of(
            new BigDecimal("2147483648"), // Integer.MAX_VALUE + 1
            new BigDecimal("2147483648.0"),
            new BigDecimal("2147483648.00"),
            new BigDecimal("-2147483649"), // Integer.MIN_VALUE - 1
            new BigDecimal("-2147483649.1"),
            new BigDecimal("-2147483649.01"),

            new BigDecimal("9999999999999999999999999999999"),
            new BigDecimal("10000000000000000000000000000000"),

            new BigDecimal("0.99"),
            new BigDecimal("0.999999999999999999999")
        ).collect(Collectors.toList());

        for (BigDecimal bd : exceptionalCases) {
            try {
                int intValueExact = bd.intValueExact();
                failures++;
                System.err.println("Unexpected non-exceptional intValueExact on " + bd);
            } catch (ArithmeticException e) {
                // Success;
            }
        }
        return failures;
    }
}

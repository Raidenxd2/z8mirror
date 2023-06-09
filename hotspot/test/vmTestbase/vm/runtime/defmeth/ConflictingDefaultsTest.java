/*
 * Copyright (c) 2013, 2018, Oracle and/or its affiliates. All rights reserved.
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

package vm.runtime.defmeth;

import nsk.share.test.TestBase;
import vm.runtime.defmeth.shared.DefMethTest;
import vm.runtime.defmeth.shared.annotation.KnownFailure;
import vm.runtime.defmeth.shared.annotation.NotApplicableFor;
import vm.runtime.defmeth.shared.data.*;
import static vm.runtime.defmeth.shared.data.method.body.CallMethod.Invoke.*;
import static vm.runtime.defmeth.shared.data.method.body.CallMethod.IndexbyteOp.*;
import vm.runtime.defmeth.shared.builder.TestBuilder;
import static vm.runtime.defmeth.shared.ExecutionMode.*;

/**
 * Tests on conflicting defaults.
 *
 * It is allowable to inherit a default through multiple paths (such as
 * through a diamond-shaped interface hierarchy), but the resolution procedure
 * is looking for a unique, most specific default-providing interface.
 *
 * If one default shadows  another (where a subinterface provides a different
 * default for an extension method declared in a superinterface), then the less
 * specific interface is pruned from consideration no matter where it appears
 * in the inheritance hierarchy.  If two or more extended interfaces provide
 * default implementations, and one is not a superinterface of the other, then
 * neither is used and a linkage exception is thrown indicating conflicting
 * default implementations.
 */
public class ConflictingDefaultsTest extends DefMethTest {
    public static void main(String[] args) {
        TestBase.runTest(new ConflictingDefaultsTest(), args);
    }

    /*
     * Conflict
     *
     * interface I { int m() default { return 1; } }
     * interface J { int m() default { return 2; } }
     * class C implements I, J {}
     *
     * TEST: C c = new C(); c.m() ==> ICCE
     */
    public void testConflict() {
        TestBuilder b = factory.getBuilder();

        Interface I = b.intf("I")
                .defaultMethod("m", "()I").returns(1).build()
            .build();

        Interface J = b.intf("J")
                .defaultMethod("m", "()I").returns(2).build()
            .build();

        ConcreteClass C = b.clazz("C").implement(I,J).build();

        b.test().callSite(C, C, "m","()I")
                .throws_(IncompatibleClassChangeError.class)
            .done()

        .run();
    }

    /*
     * Maximally-specific Default (0.6.3 spec change)
     *
     * interface I { int m(); }
     * interface J { int m() default { return 2; } }
     * class C implements I, J {}
     *
     * TEST: C c = new C(); c.m() return 2
     */
    public void testMaximallySpecificDefault() {
        TestBuilder b = factory.getBuilder();

        Interface I = b.intf("I")
                .abstractMethod("m", "()I").build()
            .build();

        Interface J = b.intf("J")
                .defaultMethod("m", "()I").returns(2).build()
            .build();

        ConcreteClass C = b.clazz("C").implement(I,J).build();

        b.test().callSite(C, C, "m","()I")
                .returns(2)
            .done()

        .run();
    }

    /*
     * Reabstract
     *
     * interface I { int m() default { return 1; } }
     * interface J extends I { int m(); }
     * class C implements J {}
     *
     * TEST: C c = new C(); c.m() ==> AME
     */
    public void testReabstract() {
        TestBuilder b = factory.getBuilder();

        Interface I = b.intf("I")
                .defaultMethod("m", "()I").returns(1).build()
            .build();

        Interface J = b.intf("J").extend(I)
                .abstractMethod("m", "()I").build()
            .build();

        ConcreteClass C = b.clazz("C").implement(J).build();

        b.test().callSite(C, C, "m","()I")
                .throws_(AbstractMethodError.class)
            .done()

        .run();
    }

    /*
     * Reabstract2
     *
     * interface I { int m() default { return 1; } }
     * interface J extends I { int m(); }
     * class C implements J {}
     * class D extends C { callSuper C.m}
     *
     * TEST: C c = new C(); c.m() ==> AME
     * TEST: J j = new C(); j.m() ==> AME
     * TEST: I i = new C(); i.m() ==> AME
     * TEST: D d = new D(); d.m() ==> callSuper C.m ==> AME
     */
    public void testReabstract2() {
        TestBuilder b = factory.getBuilder();

        Interface I = b.intf("I")
                .defaultMethod("m", "()I").returns(1).build()
            .build();

        Interface J = b.intf("J").extend(I)
                .abstractMethod("m", "()I").build()
            .build();

        ConcreteClass C = b.clazz("C").implement(J).build();
        ConcreteClass D = b.clazz("D").extend(C)
                .concreteMethod("m", "()I").callSuper(C, "m", "()I").build()
            .build();

        b.test().callSite(C, C, "m","()I")
                .throws_(AbstractMethodError.class)
            .done()
         .test().callSite(J, C, "m","()I")
                .throws_(AbstractMethodError.class)
            .done()
         .test().callSite(I, C, "m","()I")
                .throws_(AbstractMethodError.class)
            .done()
         .test().callSite(D, D, "m","()I")
                .throws_(AbstractMethodError.class)
            .done()

        .run();
    }

    /*
     * ReabstractConflictingDefaults
     *
     * interface I { int m() default { return 1; } }
     * interface J { int m() default { return 2; } }
     * interface K extends I,J { int m(); }
     * class A implements I,J {}
     * class C extends A implements K {}
     *
     * TEST: A c = new C(); c.m() ==> AME
     */
    public void testReabstractConflictingDefaults() {
        TestBuilder b = factory.getBuilder();

        Interface I = b.intf("I")
                .defaultMethod("m", "()I").returns(1).build()
            .build();

        Interface J = b.intf("J")
                .defaultMethod("m", "()I").returns(2).build()
            .build();

        Interface K = b.intf("K").extend(I,J)
                .abstractMethod("m", "()I").build()
            .build();

        ConcreteClass A = b.clazz("A").implement(I,J).build();
        ConcreteClass C = b.clazz("C").extend(A).implement(K).build();

        b.test().callSite(A, C, "m","()I")
                .throws_(AbstractMethodError.class)
            .done()

        .run();
    }


    /*
     * ReabstractConflictingDefaultsInvokeInterface
     *
     * interface I { int m() default { return 1; } }
     * interface J { int m() default { return 2; } }
     * interface K extends I,J { int m(); }
     * interface L extends K { }
     * class A implements I,J {}
     * class C extends A implements K {}
     * class D extends C implements L {}
     *
     * TEST: I i = new A(); i.m() ==> ICCE
     * TEST: K k = new C(); k.m() ==> AME
     * TEST: L l = new D(); l.m() ==> AME
     */
    public void testReabstractConflictingDefaultsInvokeInterface() {
        TestBuilder b = factory.getBuilder();

        Interface I = b.intf("I")
                .defaultMethod("m", "()I").returns(1).build()
            .build();

        Interface J = b.intf("J")
                .defaultMethod("m", "()I").returns(2).build()
            .build();

        Interface K = b.intf("K").extend(I,J)
                .abstractMethod("m", "()I").build()
            .build();

        Interface L = b.intf("L").extend(K)
            .build();

        ConcreteClass A = b.clazz("A").implement(I,J).build();
        ConcreteClass C = b.clazz("C").extend(A).implement(K).build();
        ConcreteClass D = b.clazz("D").extend(C).implement(L).build();

        b.test().callSite(I, A, "m","()I")
                .throws_(IncompatibleClassChangeError.class)
            .done()
         .test().callSite(K, C, "m","()I")
                .throws_(AbstractMethodError.class)
            .done()
         .test().callSite(L, D, "m","()I")
                .throws_(AbstractMethodError.class)
            .done()

        .run();
    }

    /*
     * ReabstractConflictingDefaultsSuper
     *
     * interface I { int m() default { return 1; } }
     * interface J { int m() default { return 2; } }
     * interface K extends I,J { int m(); }
     * interface L extends K { }
     * class A implements I,J {}
     * class C extends A implements K {}
     * class D extends C implements L {int m() {callSuper A.m }
     *
     * TEST: I i = new A(); i.m() ==> ICCE
     * TEST: K k = new C(); CallSuper k.m() ==> AME
     * TEST: L l = new D(); l.m() ==> AME
     */
    public void testReabstractConflictingDefaultsSuper() {
        TestBuilder b = factory.getBuilder();

        Interface I = b.intf("I")
                .defaultMethod("m", "()I").returns(1).build()
            .build();

        Interface J = b.intf("J")
                .defaultMethod("m", "()I").returns(2).build()
            .build();

        Interface K = b.intf("K").extend(I,J)
                .abstractMethod("m", "()I").build()
            .build();

        Interface L = b.intf("L").extend(K)
            .build();

        ConcreteClass A = b.clazz("A").implement(I,J).build();
        ConcreteClass C = b.clazz("C").extend(A).implement(K).build();
        ConcreteClass D = b.clazz("D").extend(C).implement(L)
                .concreteMethod("m", "()I").callSuper(A, "m", "()I").build()
            .build();

        b.test().callSite(I, A, "m","()I")
                .throws_(IncompatibleClassChangeError.class)
            .done()
         .test().callSite(L, D, "m","()I")
                .throws_(AbstractMethodError.class)
            .done()

        .run();
    }

    /*
     * Shadow
     *
     * interface I { int m() default { return 1; } }
     * interface J extends I { int m() default { return 2; } }
     * class C implements J {}
     *
     * TEST: [I|J|C] c = new C(); c.m() == 2;
     */
    public void testShadow() {
        TestBuilder b = factory.getBuilder();

        Interface I = b.intf("I")
                .defaultMethod("m", "()I").returns(1).build()
            .build();

        Interface J = b.intf("J").extend(I)
                .defaultMethod("m", "()I").returns(2).build()
            .build();

        ConcreteClass C = b.clazz("C").implement(J).build();

        b.test().callSite(I, C, "m","()I")
                .returns(2)
            .done()
        .test()
                .callSite(J, C, "m","()I")
                .returns(2)
            .done()
        .test()
                .callSite(C, C, "m","()I")
                .returns(2)
            .done()

        .run();
    }

    /*
     * Disqualified
     *
     * interface I { int m() default { return 1; } }
     * interface J extends I { int m() default { return 2; } }
     * class C implements I, J {}
     *
     * TEST: [I|J|C] c = new C(); c.m() == 2;
     */
    public void testDisqualified() {
        TestBuilder b = factory.getBuilder();

        Interface I = b.intf("I")
                .defaultMethod("m", "()I").returns(1).build()
            .build();

        Interface J = b.intf("J").extend(I)
                .defaultMethod("m", "()I").returns(2).build()
            .build();

        ConcreteClass C = b.clazz("C").implement(I,J).build();

        b.test()
                .callSite(I, C, "m","()I")
                .returns(2)
            .done()
        .test()
                .callSite(J, C, "m","()I")
                .returns(2)
            .done()
        .test()
                .callSite(C, C, "m","()I")
                .returns(2)
            .done()

        .run();
    }

    /*
     * Mixed arity
     *
     * interface I { int m() default { return 1; } }
     * interface J { int m(int i) default { return 2; } }
     * class C implements I, J {}
     *
     * TEST: I i = new C(); i.m() == 1; i.m(0) ==> NSME
     * TEST: J j = new C(); j.m() ==> NSME; j.m(0) == 2
     * TEST: C c = new C(); c.m() == 1; c.m(0) == 2
     */
    @KnownFailure(modes = { INVOKE_EXACT, INVOKE_GENERIC, INDY }) // IncompatibleClassChangeError instead of NoSuchMethodError
    public void testMixedArity1() {
        TestBuilder b = factory.getBuilder();

        Interface I = b.intf("I")
                .defaultMethod("m", "()I").returns(1).build()
            .build();

        Interface J = b.intf("J")
                .defaultMethod("m", "(I)I").returns(2).build()
            .build();

        ConcreteClass C = b.clazz("C").implement(I,J).build();

        // I i = new C(); ...
        b.test()
                .callSite(I, C, "m","()I")
                .returns(1)
            .done()
        .test()
                .callSite(I, C, "m","(I)I")
                .params(0)
                .throws_(NoSuchMethodError.class)
            .done()

        // J j = new C(); ...
        .test()
                .callSite(J, C, "m","()I")
                .throws_(NoSuchMethodError.class)
            .done()
        .test()
                .callSite(J, C, "m","(I)I")
                .params(0)
                .returns(2)
            .done()

        // C c = new C(); ...
        .test()
                .callSite(C, C, "m","()I")
                .returns(1)
            .done()
        .test()
                .callSite(C, C, "m","(I)I")
                .params(0)
                .returns(2)
            .done()

        .run();
    }

    /*
     * Mixed arity
     *
     * interface I { int m() default { return 1; } }
     * interface J { int m() default { return 2; } }
     * class C implements I, J { int m(int i) { return 3; }}
     *
     * TEST: I i = new C(); i.m() ==> ICCE
     * TEST: J j = new C(); j.m() ==> ICCE
     * TEST: C c = new C(); c.m() ==> ICCE; c.m(0) == 3
     */
    public void testMixedArity2() {
        TestBuilder b = factory.getBuilder();

        Interface I = b.intf("I")
                .defaultMethod("m", "()I").returns(1).build()
            .build();

        Interface J = b.intf("J")
                .defaultMethod("m", "()I").returns(2).build()
            .build();

        ConcreteClass C = b.clazz("C").implement(I,J)
                .concreteMethod("m", "(I)I").returns(3).build()
            .build();

        // I i = new C(); ...
        b.test()
                .callSite(I, C, "m","()I")
                .throws_(IncompatibleClassChangeError.class)
            .done()

        // J j = new C(); ...
        .test()
                .callSite(J, C, "m","()I")
                .throws_(IncompatibleClassChangeError.class)
            .done()

        // C c = new C(); ...
        .test()
                .callSite(C, C, "m","()I")
                .throws_(IncompatibleClassChangeError.class)
            .done()
        .test()
                .callSite(C, C, "m","(I)I")
                .params(0)
                .returns(3)
            .done()

        .run();
    }
}

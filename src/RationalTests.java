 package org.roundrockisd.stonypoint.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.roundrockisd.stonypoint.Rational;

public class RationalTests {
    private static void assertEqualsRational(int expectedNum, int expectedDen, Rational actual) {
        int actualNum = actual.numerator();
        int actualDen = actual.denominator();
        if (expectedNum * actualDen != expectedDen * actualNum) {
            throw new AssertionError(String.format("expected Rational equivalent to <%d/%d> but was: <%d/%d>", expectedNum, expectedDen, actualNum, actualDen));
        }
    }

    private static Method getMethod(String name, Class<?>... parameterTypes) {
        try {
            return Rational.class.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            String s = Arrays.toString(parameterTypes);
            fail(String.format("no such method: %s(%s)", name, s.substring(1, s.length() - 1)));
        }

        return null;
    }

    @DisplayName("add(Rational)")
    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AddTests {
        private Method mike;

        @BeforeAll
        void getMethod() {
            mike = RationalTests.getMethod("add", Rational.class);
        }

        private Rational invoke(Rational self, Rational rhs) {
            try {
                return (Rational) mike.invoke(self, rhs);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private void addTest(int aN, int aD, int bN, int bD, int rN, int rD) {
            Rational alpha = new Rational(aN, aD);
            Rational bravo = new Rational(bN, bD);
            Rational result = invoke(alpha, bravo);
            assertNotNull(result);
            assertEqualsRational(rN, rD, result);
        }

        @DisplayName("10/7 + 2/15")
        @Test
        void t0() {
            addTest(10, 7, 2, 15, 164, 105);
        }

        @DisplayName("0/7 + 3/3")
        @Test
        void t1() {
            addTest(0, 7, 3, 3, 1, 1);
        }

        @DisplayName("1/4 + 18/4")
        @Test
        void t2() {
            addTest(1, 4, 18, 4, 19, 4);
        }
    }

    @DisplayName("equals()")
    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class EqualsTests {
        @DisplayName("equals(null) ==> false")
        @Test
        void t0() {
            assertFalse(new Rational(0, 1).equals(null));
        }

        @DisplayName("foo.equals(foo) ==> true")
        @Test
        void t1() {
            Rational foo = new Rational(0, 1);
            assertTrue(foo.equals(foo));
        }

        @DisplayName("foo.equals(new Object()) ==> false")
        @Test
        void t2() {
            Rational foo = new Rational(0, 1);
            assertFalse(foo.equals(new Object()));
        }

        @DisplayName("1/2 equals 2/4 ==> true")
        @Test
        void t3() {
            Rational alpha = new Rational(1, 2);
            Rational bravo = new Rational(2, 4);
            assertTrue(alpha.equals((Object) bravo));
            assertTrue(bravo.equals((Object) alpha));
        }

        @DisplayName("1/2 equals 3/4 ==> false")
        @Test
        void t4() {
            Rational alpha = new Rational(1, 2);
            Rational bravo = new Rational(3, 4);
            assertFalse(alpha.equals((Object) bravo));
            assertFalse(bravo.equals((Object) alpha));
        }

        @DisplayName("0/1 equals 0/9 ==> true")
        @Test
        void t5() {
            Rational alpha = new Rational(0, 1);
            Rational bravo = new Rational(0, 9);
            assertTrue(alpha.equals((Object) bravo));
            assertTrue(bravo.equals((Object) alpha));
        }

        @DisplayName("0/1 equals 0/9 ==> true")
        @Test
        void t6() {
            Rational alpha = new Rational(0, 1);
            Rational bravo = new Rational(0, 9);
            assertTrue(alpha.equals((Object) bravo));
            assertTrue(bravo.equals((Object) alpha));
        }

        @DisplayName("3/2 equals 27/18 ==> true")
        @Test
        void t7() {
            Rational alpha = new Rational(3, 2);
            Rational bravo = new Rational(27, 18);
            assertTrue(alpha.equals((Object) bravo));
            assertTrue(bravo.equals((Object) alpha));
        }

        @DisplayName("3/2 equals 27/19 ==> false")
        @Test
        void t8() {
            Rational alpha = new Rational(3, 2);
            Rational bravo = new Rational(27, 19);
            assertFalse(alpha.equals((Object) bravo));
            assertFalse(bravo.equals((Object) alpha));
        }

        @DisplayName("1/1 equals 1/1 ==> true")
        @Test
        void t9() {
            Rational alpha = new Rational(1, 1);
            Rational bravo = new Rational(1, 1);
            assertTrue(alpha.equals((Object) bravo));
            assertTrue(bravo.equals((Object) alpha));
        }
    }

    @DisplayName("multiply(Rational)")
    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class MultiplyTests {
        private Method mike;

        @BeforeAll
        void getMethod() {
            mike = RationalTests.getMethod("multiply", Rational.class);
        }

        private Rational invoke(Rational self, Rational rhs) {
            try {
                return (Rational) mike.invoke(self, rhs);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private void multiplyTest(int aN, int aD, int bN, int bD, int rN, int rD) {
            Rational alpha = new Rational(aN, aD);
            Rational bravo = new Rational(bN, bD);
            Rational result = invoke(alpha, bravo);
            assertNotNull(result);
            assertEqualsRational(rN, rD, result);
        }

        @DisplayName("1/2 * 3/4")
        @Test
        void t0() {
            multiplyTest(1, 2, 3, 4, 3, 8);
        }

        @DisplayName("1/9 * 0/5")
        @Test
        void t1() {
            multiplyTest(1, 9, 0, 5, 0, 1);
        }

        @DisplayName("9/2 * 5/5")
        @Test
        void t2() {
            multiplyTest(9, 2, 5, 5, 9, 2);
        }
    }

    @DisplayName("reduce()")
    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ReduceTests {
        private Method mike;

        @BeforeAll
        void getMethod() {
            mike = RationalTests.getMethod("reduce");
        }

        private void invoke(Rational self) {
            try {
                mike.invoke(self);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private void reduceTest(int aN, int aD, int rN, int rD) {
            Rational alpha = new Rational(aN, aD);
            invoke(alpha);
            assertEquals(rN, alpha.numerator(), "incorrect numerator after reduce()");
            assertEquals(rD, alpha.denominator(), "incorrect denominator after reduce()");
        }

        @DisplayName("9/3 -> 3/1")
        @Test
        void t0() {
            reduceTest(9, 3, 3, 1);
        }

        @DisplayName("2/5 -> 2/5")
        @Test
        void t1() {
            reduceTest(2, 5, 2, 5);
        }

        @DisplayName("0/7 -> 0/1")
        @Test
        void t2() {
            reduceTest(0, 7, 0, 1);
        }

        @DisplayName("198/209 -> 18/19")
        @Test
        void t3() {
            reduceTest(198, 209, 18, 19);
        }
    }

    @DisplayName("toString()")
    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ToStringTests {
        @DisplayName("65/77")
        @Test
        void t0() {
            assertEquals("65/77", new Rational(65, 77).toString());
        }

        @DisplayName("88/83")
        @Test
        void t1() {
            assertEquals("88/83", new Rational(88, 83).toString());
        }

        @DisplayName("0/1")
        @Test
        void t2() {
            assertEquals("0/1", new Rational(0, 1).toString());
        }
    }
}
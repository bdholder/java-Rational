# Rational

Our first attempt at handling rational numbers might look something like this.

```java
static int[] rationalMultiply(int[] left, int[] right) {
    int[] result = new int[2];
    result[0] = left[0] * right[0];
    result[1] = left[1] * right[1];
    return result;
}
```

A rational number is a pair of integers, so we represent each rational number as an `int[]` of length 2, pass them to our `rationalMultiply` method, which returns an array of length 2 representing the result. This approach will work, but it has some serious drawbacks.

To someone reading the code, it isn't necessarily obvious that a variable is intended to represent a rational number. An `int[]` of length 2 could be a lot of things: a rational number, a complex number, a point in the plane, a rectangle, etc. Using an `int[]` also makes it easy for the writer of the code to make mistakes as well. For example, if we also have a `complexMultiply` method that multiplies two Gaussian integers (complex numbers whose real and imaginary components are integers) together, the writer may accidentally pass an `int[]` that represents a rational to `complexMultiply`, and the compiler would have no way of knowing that this was a mistake. The compiler also can't prevent arrays of length other than 2 being passed to `rationalMultiply`.

Even though both rational numbers and Gaussian integers are represented with pairs of integers, they should be treated differently by the program. The type system allows the programmer to give the compiler additional information so that it can prevent these kinds of mistakes from happening.

Our next step will be to create a class to represent rational numbers. 

```java
// file: Rational.java

public class Rational {
    public int num;
    public int den;

    public static Rational multiply(Rational lhs, Rational rhs) {
        Rational result = new Rational();
        result.num = lhs.num * rhs.num;
        result.den = lhs.den * rhs.den;
        return result;
    }
}
```

```java
// file: Main.java

public class Main {
    public static void main(String[] args) {
        Rational alpha = new Rational();
        alpha.num = 1;
        alpha.den = 2;

        Rational bravo = new Rational();
        bravo.num = 3;
        bravo.den = 5;

        Rational charlie = Rational.multiply(alpha, bravo);
        System.out.println(charlie.num + "/" + charlie.den);
    }
}
```

A few changes have been made. We created a class called `Rational`. By convention, class names in Java are written in camel case with the first letter capitalized. Rather than using an array to store two `int`s, we have created two `int` variables declared in the body of the class. Recall that variables declared in a class, rather than a method, are called *fields*. Since we didn't mark `num` and `den` as `static`, each instance of the `Rational` class gets its own copy of them.

We moved the `rationalMultiply` method into the `Rational` class and renamed it to multiply. Methods that deal only with particular types, in this case `Rational`, should be placed in the class that defines that type. We don't need the cumbersome name `rationalMultiply` anymore. We can simply call it `multiply`, and invoke it on two `Rational` values by writing `Rational.multiply(foo, bar)`. We can't simply write `multiply(foo, bar)`, because then the compiler would assume that we were talking about a method in class `Main`. Since no such method exists, we would get a compiler error.

```
error: cannot find symbol
      Rational charlie = multiply(alpha, bravo);
                         ^
symbol:   method multiply(Rational,Rational)
location: class Main
```

## Adding a constructor
Our code works, but it is inconvenient to create an instance of `Rational` and then set the values of `num` and `den` afterward. Adding a constructor to the class will allow us to accomplish this in a single line.

```java
public class Rational {
    public int num;
    public int den;

    public Rational(int n, int d) {
        this.num = n;
        this.den = d;
    }

    public static Rational multiply(Rational lhs, Rational rhs) {
        int n = lhs.num * rhs.num;
        int d = lhs.den * rhs.den;
        return new Rational(n, d);
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        Rational alpha = new Rational(1, 2);
        Rational bravo = new Rational(3, 5);

        Rational charlie = Rational.multiply(alpha, bravo);
        System.out.println(charlie.num + "/" + charlie.den);
    }
}
```

Not only does the constructor allow us to create and initialize a `Rational` in one line, it prevents the programmer from creating a new `Rational` and then forgetting to assign values to `num` and `den`. Recall that Java provides a default constructor if and only if no constructor is explicitly declared. Since we now have an explicit constructor, attempting to create a `Rational` by writing `Rational alpha = new Rational();` will cause a compiler error.

```
error: constructor Rational in class Rational cannot be applied to given types;
      Rational alpha = new Rational();
                       ^
required: int,int
found: no arguments
reason: actual and formal argument lists differ in length
```

The "formal argument list" in the error message is just the signature of the constructor, `Rational(int, int)`, minus the name, and the "actual argument list" is the list of values supplied in the method call.

## Making `multiply` non-static
We can simplify the syntax for invoking the `multiply` method by removing the `static` modifier from the method header. Then, instead of writing `Rational.multiply(alpha, bravo)`, we write `alpha.multiply(bravo)`.

```java
public class Rational {
    public int num;
    public int den;

    public Rational(int n, int d) {
        this.num = n;
        this.den = d;
    }

    public Rational multiply(Rational rhs) {
        int n = this.num * rhs.num;
        int d = this.den * rhs.den;
        return new Rational(n, d);
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        Rational alpha = new Rational(1, 2);
        Rational bravo = new Rational(3, 5);

        Rational charlie = alpha.multiply(bravo);
        System.out.println(charlie.num + "/" + charlie.den);
    }
}
```

The new `multiply` method is nearly identical to the old one. Other than removing `static`, the only other change was removing `lhs` from the parameter list and changing it to `this` in the method body. If we think of `alpha.multiply(bravo)` as shorthand for `Rational.multiply(alpha, bravo)`, then the method header of `static Rational multiply(Rational lhs, Rational rhs)` is *almost* equivalent to `Rational multiply(Rational this, Rational rhs)`. The only difference is that non-`static` methods use *dynamic dispatch*. Recall that this means the actual method invoked depends on the *run-time type* of the caller.

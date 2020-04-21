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

## Overriding the `toString` method
As we have discussed, every class in Java ultimately extends `Object`, and so every class inherits some `toString` method. Since `Object`'s `toString` just returns a `String` with the name of the class and its hash code, we will usually want to override `toString`. Recall that *overloading* refers to multiple methods having the same name but different signatures, while *overriding* refers to "changing the definition," if you will, of a method inherited from a superclass.

When overriding a method, always use the annotation `@Override`. If you make a mistake, for example by attempting to override a method that wasn't inherited or getting the method signature incorrect, the compiler will raise an error altering you to the mistake. Otherwise, the code will compile, but most likely will not behave the way the you expect it to.

In our case, actually implementing `toString` is pretty easy. In fact, we pretty much already have the code.

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

    @Override
    public String toString() {
        return this.num + "/" + this.den;
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        Rational alpha = new Rational(1, 2);
        Rational bravo = new Rational(3, 5);

        Rational charlie = alpha.multiply(bravo);
        System.out.println(charlie);

    }
}
```

We moved the logic using `+` that was previously in the `println` call in `main` to our new `toString` method. Don't forget `@Override`! When one of the operands of the `+` operator is a `String`, Java will convert the other operand to a `String` and "glue them together." For example `7 + "foo" ==> "7foo"`. The fancy term for the gluing process is *concatenation*.

We don't need to explicitly invoke `toString` on `charlie` when we pass it to `println` because `println` will do that automatically.

Note that if you need to build a `String` out of more than a handful of pieces, string concatenation isn't the best strategy. This is because `String`s are immutable in Java, meaning that once they are created, their contents cannot be changed. When we glue two `String`s together, we are actually creating an entirely new `String` object. Repeating this process many times for intermediate values wastes time and memory, so use Java's `StringBuilder` class instead.

## Implementing `reduce`
We want a method that reduces our `Rational` to its lowest terms. To do this, we need to determine the *greatest common divisor*, or *GCD*, of the numerator and denominator. The *GCD* of a pair of integers, *a* and *b*, is the largest integer that evenly divides both *a* and *b*.

For our discussion here, we will assume that both `num` and `den` are positive integers.

The simplest strategy to compute the GCD of the numerator and denominator would be to set a variable `d` to the value of the numerator, test whether it divides both the numerator and denominator, and decrement `d` if it does not. The following code snippet is an example of an implementation of this algorithm.

```java
int gcd(int a, int b) {
    int d = a;
    while (a % d != 0 || b % d != 0) {
        d--;
    }
    return d;
}
```

Clearly, the first value of `d` that divides both `num` and `den` must be the largest such integer, since we started with the largest possible value that divides `num`. The only concern is that the algorithm may loop forever. However, because 1 evenly divides any integer, the algorithm will definitely terminate, either when `d` reaches 1 or earlier.

While this algorithm works, it relatively inefficient. We will use a variant of an algorithm described by the ancient Greek mathematician Euclid, called the Euclidean algorithm.

Suppose we have two positive integers `a` and `b`, with `a >= b` and `b > 0`. We make the following observations. First, the greatest common divisor of an integer `m` and 0 is simply `m`, because `m * 1 == m` and `m * 0 == 0`. Second, if `d` is a divisor of `a` and `b`, and `r == a % b`, then `d` is also a divisor of `r`. The strategy of the Euclidean algorithm then, is to repeatedly "take remainders" until we have a remainder of 0, in which case we have found the GCD of the original integers.

An example will help to clarify the process. Let's suppose that `a` and `b` are 20 and 12, respectively. We first compute `20 % 12` which is `8`. Now, 12 becomes our `a` and 8 becomes our `b`, and the process continues. `12 % 8` is 4, and `8 % 4` is 0, so 4 is the GCD of 20 and 12. We simply did

```
20 % 12 ==> 8
12 % 8 ==> 4
8 % 4 ==> 0
```

It's okay if the starting values are swapped; that is, if 12 is assigned to `a` and 20 assigned to `b`. They will be automatically swapped by the algorithm.

```
12 % 20 ==> 12
20 % 12 ==> 8
12 % 8 ==> 4
8 % 4 ==> 0
```

We get the same answer: 4.

Whenever we construct an algorithm, we want to ensure that it actually gives the correct result for every input. It's easy to "believe" that our naive algorithm works, but it is not as easy to see why the Euclidean algorithm gives the correct answer. Proving that the Euclidean algorithm always gives the correct answer is beyond the scope of this lesson, but you can probably informally convince yourself that it is correct. You can read more about the [Euclidean algorithm](https://en.wikipedia.org/wiki/Euclidean_algorithm) on Wikipedia.

A general purpose implementation of the Euclidean algorithm might look as follows.

```java
int gcd(int a, int b) {
    while (b > 0) {
        int r = a % b;
        a = b;
        b = r;
    }
    return a;
}
```

We can adapt this algorithm for our `Rational` class easily. We have a couple of options. One is to create a `private` method that returns the GCD of our numerator and denominator. This would be the right choice if we needed to compute the GCD in more than one place in our class, or if the algorithm were sufficiently complex that it would be more readable to place it elsewhere. As a rule of thumb, if you can't fit all the code for a method on the screen at once, you should break the method up into smaller pieces, especially if you are repeating similar segments of code.

However, since we're only using this code in one place and it isn't lengthy, we'll simply keep it inside our `reduce` method.

```java
public class Rational {
    public int num;
    public int den;

    public Rational(int num, int den) {
        this.num = num;
        this.den = den;
    }

    public Rational multiply(Rational rhs) {
        int n = this.num * rhs.num;
        int d = this.den * rhs.den;
        return new Rational(n, d);
    }

    public void reduce() {
        int a = this.num;
        int b = this.den;
        while (b > 0) {
            int r = a % b;
            a = b;
            b = r;
        }
        this.num /= a;
        this.den /= a;
    }

    @Override
    public String toString() {
        return this.num + "/" + this.den;
    }
}
```

## Implementing `equals`
The `equals` method is the trickiest of the methods to implement because it requires some understanding of how Java's type system works. The `equals` method inherited from `Object` simply tests whether two objects are identical; that is, whether they refer to the same object on the heap. We want two `Rational` objects to be considered equivalent if they represent the same rational number. If *a*/*b* and *c*/*d* are rational numbers, they are considered equivalent if and only if *ad* = *bc*.

If we look at the [documentation for the class Object](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html), we can see that `Object`'s `equals` method has the signature `equals(Object)`.

An easy mistake to make is to attempt to implement `equals` by writing

```java
@Override
public boolean equals(Rational obj) {
    // ...
}
```

When we try to compile the code, we get an error.

```
error: method does not override or implement a method from a supertype
```

This is because our method has the signature `equals(Rational)` while `Object`'s is `equals(Object)`. If we had forgotten to tell the compiler that we intended to override a method by writing `@Override`, then the code would have compiled, but it would not work correctly.

Our next attempt might look as follows.

```java
@Override
public boolean equals(Object obj) {
    return this.num * obj.den == this.den * obj.num;
}
```

Now we have two errors from the compiler.

```
error: cannot find symbol
    return this.num * obj.den == this.den * obj.num;
                         ^
  symbol:   variable den
  location: variable obj of type Object
error: cannot find symbol
    return this.num * obj.den == this.den * obj.num;
                                               ^
  symbol:   variable num
  location: variable obj of type Object
```

Hey, wha' happened? The problem is that the parameter `obj` has a compile-time (static) type of `Object`. This means that any object can be passed as an argument to our `equals` method. For example, it would be perfectly fine to write `foo.equals(new Object())` or `foo.equals("howdy")`. Since Java can only guarantee that the argument is an `Object`, the compiler will not let us assume that the `num` and `den` fields will exist.

Let's try assigning `obj` to a variable of type `Rational`.

```java
@Override
public boolean equals(Object obj) {
    Rational other = obj;
    return this.num * other.den == this.den * other.num;
}
```

```
error: incompatible types: Object cannot be converted to Rational
    Rational other = obj;
                     ^
```

The compiler doesn't like that either. Because `Rational` is not a supertype of `Object`, this assignment is potentially dangerous. If the compiler allowed this to happen, then we might assign something like a `String` to a `Rational`. Would that be a good idea? I don't think so!

We can use the cast operator to convince the compiler to compile the code.

```java
@Override
public boolean equals(Object obj) {
    Rational other = (Rational) obj;
    return this.num * other.den == this.den * other.num;
}
```

The code finally compiles. It even works...sometimes. It works for this snippet

```java
Rational alpha = new Rational(1, 3);
Rational bravo = new Rational(2, 6);
System.out.println(alpha.equals(bravo));
```

The next snippet, however, throws a `ClassCastException` at run time.

```java
Rational alpha = new Rational(1, 3);
String bravo = "foo";
System.out.println(alpha.equals(bravo));
```

```
Exception in thread "main" java.lang.ClassCastException: class java.lang.String cannot be cast to class Rational
```

For reference types, the cast operator `(T)`, where `T` is some type, doesn't actually do anything. It's a promise to the compiler. The compiler will not allow us to write `Rational other = obj` because it cannot guarantee that `obj` refers to a `Rational`. However, because the compiler errs on the side of caution—it would rather prohibit safe code than allow unsafe code—Java provides us a mechanism to assure the compiler that the desired operation is typesafe. When we write `(Rational) obj`, we're really telling the compiler, "I have knowledge about `obj` that you don't, and I know it contains a `Rational`, so it's okay to compile this code." The compiler still doesn't trust us completely, so wherever a cast happens, it inserts some code to check that the object actually is what we're claiming it will be. If it isn't, the program crashes with a `ClassCastException`.

Our latest version of `equals` doesn't actually do anything to fix the problem that the compiler detected. We accept any `Object`, so we currently have no guarantee that `obj` holds a `Rational`. Let's see how to fix that.

### `instanceof`
The `instanceof` operator returns a `boolean` value that is `true` if the given object is an instance of the given type. For example, after executing `Rational foo = new Rational(1, 3)`, the expressions `foo instanceof Rational` and `foo instanceof Object` both evaluate to `true`. After executing `Object bar = new Object()`, the expression `bar instanceof String` evaluates to `false`.

Using `instanceof` allows us to kill two birds with one stone because `null instanceof T` evaluates to `false` for any reference type `T`, so we don't have to worry about accidentally dereferencing a `null` value.

Let's rewrite `equals` using `instanceof`.

```java
@Override
public boolean equals(Object obj) {
    if (obj instanceof Rational) {
        Rational other = (Rational) obj;
        return this.num * other.den == this.den * other.num;
    } else {
        return false;
    }
}
```

This code actually works the way it is supposed to. We know that the cast to `Rational` will succeed because that code is only executed if `obj instanceof Rational` evaluates to `true`, and we also know that `obj` is non-`null`, so we won't get a `NullPointerException`.

We can add an optimization to our code by checking to see if `obj` refers to the same object that `this` does, but it is not strictly necessary. This optimization is useful when computing `equals` is otherwise "expensive."

```java
@Override
public boolean equals(Object obj) {
    if (obj == this) {
        return true;
    } else if (obj instanceof Rational) {
        Rational other = (Rational) obj;
        return this.num * other.den == this.den * other.num;
    } else {
        return false;
    }
}
```

## Implementing `add`
`add` is really no different from `multiply`, so we'll just present the solution.

```java
public Rational add(Rational rhs) {
    int n = this.num * rhs.den + this.den * rhs.num;
    int d = this.den * rhs.den;
    return new Rational(n, d);
}
```

## Final refinements
At this point, our `Rational` class looks like this.

```java
public class Rational {
    public int num;
    public int den;

    public Rational(int num, int den) {
        this.num = num;
        this.den = den;
    }

    public Rational add(Rational rhs) {
        int n = this.num * rhs.den + this.den * rhs.num;
        int d = this.den * rhs.den;
        return new Rational(n, d);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Rational) {
            Rational other = (Rational) obj;
            return this.num * other.den == this.den * other.num;
        } else {
            return false;
        }
    }

    public Rational multiply(Rational rhs) {
        int n = this.num * rhs.num;
        int d = this.den * rhs.den;
        return new Rational(n, d);
    }

    public void reduce() {
        int a = this.num;
        int b = this.den;
        while (b > 0) {
            int r = a % b;
            a = b;
            b = r;
        }
        this.num /= a;
        this.den /= a;
    }

    @Override
    public String toString() {
        return this.num + "/" + this.den;
    }
}
```

This is slightly different from the skeleton provided in `Rational.java`. Our two fields, `num` and `den`, are `public`. Let's look at why it makes sense to make them `private`.

When a field has `public` access, anyone can read and write that field. It may be tempting to think that this is a convenience, but in fact it can cause problems. Our `Rational` class, like most classes, need to preserve certain *invariants*: a predicate that is always true for any instance of a class. One invariant for our `Rational` class is that the denominator of a `Rational` should never be 0, but currently, we have no way to enforce that.

A user could accidentally set the denominator through careless code, making the value stored by the `Rational` object meaningless. If a bug involving `Rational` shows up somewhere in the code, it could be caused by incorrect code in the `Rational` class itself or anywhere the code is used.

However, if we make `num` and `den` private, we can ensure that `den` is never set to 0, and we know that if a bug is discovered in the `Rational` class, it must involve code in the `Rational` class itself since external code cannot alter its fields.

So, let's make `Rational`'s fields private.

```java
private int num;
private int den;
```

A private class member cannot be accessed at all, not even to read it. If we try something like

```java
Rational foo = new Rational(1, 2);
System.out.println(foo.num);
```

we get a compiler error.

```
error: num has private access in Rational
System.out.println(foo.num);
                      ^
```

If we want a user to be able to read these values, we need to provide *accessor methods*, sometimes called *getters*.

```java
public int numerator() {
    return this.num;
}

public int denominator() {
    return this.den;
}
```

This allows a user to read the values of `num` and `den` without being able to tamper with them. We could choose to allow a user to set the values of `num` and `den` as well by providing *mutator methods* or *setters*.

```java
public void setNumerator(int n) {
    this.num = n;
}
```

You might wonder how this is any better than cutting out the middleman by making `num` public. In this case, there really isn't one. However, with a mutator method for `den`, we could do some validation.

```java
public void setDenominator(int d) {
    if (d == 0) {
        throw new IllegalArgumentException();
    }

    this.den = d;
}
```

If the user attempts to assign 0 to a denominator, an exception will now be immediately thrown indicating that a programming error has occurred. Since it's always an error to assign 0 to a denominator, the earlier an exception related to that error can be triggered, the better, because it will make the task of discovering the root cause easier.

If this validation was not present, the programming error may only manifest much later when certain `Rational` values are behaving unexpectedly.

Keeping fields private and exposing them using getters or not at all is a good idea. Whether setters and getters represent good design is beyond the scope of our discussion.

We won't add mutator methods to `Rational`, but let's add some validation in the constructor to prevent a user from creating a `Rational` with a denominator of 0.

```java
public Rational(int num, int den) {
    if (den == 0) {
        throw new IllegalArgumentException();
    }
    this.num = num;
    this.den = den;
}
```
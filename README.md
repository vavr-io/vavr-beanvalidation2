[![Build Status](https://travis-ci.org/vavr-io/vavr-beanvalidation2.png)](https://travis-ci.org/vavr-io/vavr-beanvalidation2)

# Vavr-Beanvalidation 2.0

This module provides support for bean validation 2.0 (JSR380). Can be used with any service provider of the bean validation spec 
e.g. `org.hibernate.validator:hibernate-validator`

Added validations:

- `@Size` for vavr's `Traversable<T>`
- `@NotEmpty` for vavr's `Value<T>`
- All available validations can be applied to nested Tuple Values. see example below

# Using the module

Add the dependency to your classpath. For maven:

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr-beanvalidation2</artifactId>
    <version>0.9.2</version>
</dependency>
```

For the bean validation service provider to pick it up the constraints must be registered.
Add the following to your `validation.xml`:

```xml
...
<constraint-mapping>META-INF/constraints-vavr.xml</constraint-mapping>
...
```

Or register via java config of your validation provider. See `javax.validation.Configuration#addMapping`.

Since it would be very tedious to register all the `ValueExtractor`s for the tuple elements by yourself,
configuration is automatically done for you via Java service loader. 
The definition in `META-INF/services` is picked up by the validation service provider.

Now JSR 380 validations will work on vavr types. e.g. 

```java
public class TestBean {

    @Size(min = 1, max = 2)
    private Seq<Integer> seqWithOneOrTwoElems = List.of(0);

    @NotEmpty
    private Either<String, Integer> mustNotBeLeftOrNull = Either.right(42);
    
    private Tuple3<@NotBlank String, @NotBlank String, @NotNull Integer> allElementsMustBeProvided = Tuple.of("a", "x", 3);

    // getters and setters
    
}
```
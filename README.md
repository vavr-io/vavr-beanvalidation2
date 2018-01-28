# Vavr-Beanvalidation 2.0

This module provides support for bean validation 2.0 (JSR380). Can be used with any service provider of the bean validation spec 
e.g. `org.hibernate.validator:hibernate-validator`

Added validations:

- `@Size` for vavr's `Traversable<T>`
- `@NotEmpty` for vavr's `Value<T>`

# Using the module

It is sufficient for the module to be in your classpath for the bean validation service provider to pick it up. For maven:

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr-beanvalidation2</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Now JSR 380 validations will work on vavr types. e.g. 

```java
public class TestBean {

    @Size(min = 1, max = 2)
    private Seq<Integer> seqWithOneOrTwoElems = List.of(0);

    @NotEmpty
    private Either<String, Integer> mustNotBeLeftOrNull = Either.right(42);

    // getters and setters
    
}
```
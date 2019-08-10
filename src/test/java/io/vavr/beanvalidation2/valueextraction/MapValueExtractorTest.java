/*  __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2019 Vavr, http://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vavr.beanvalidation2.valueextraction;

import io.vavr.beanvalidation2.ValidatorSupplier;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashMultimap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Multimap;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class MapValueExtractorTest {

    private static final String SINGLE_CHAR = "^[a-z]$";
    private Validator validator;

    @Before
    public void setUp() {
        this.validator = ValidatorSupplier.INSTANCE.get();
    }

    private <T> void validateAndAssertNoViolations(T target) {
        Collection<ConstraintViolation<T>> violations = validator.validate(target);
        assertThat(violations).isEmpty();
    }

    private <T> void validateAndAssertSingleViolation(
            T target, Class<?> constraint,
            String position,
            Class<?> type,
            String fieldName,
            String entryName
    ) {
        Collection<ConstraintViolation<T>> violations = validator.validate(target);

        assertThat(violations).isNotEmpty().hasSize(1);

        ConstraintViolation<T> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).isNotEmpty().hasSize(2);

        assertThat(violation.getConstraintDescriptor().getAnnotation()).isInstanceOf(constraint);
        validateMapPropertyPath(
                violation.getPropertyPath(), type, position,
                fieldName, entryName
        );
    }

    private void validateMapPropertyPath(
            Path propertyPath, Class<?> type, String key,
            String fieldName, String entryName
    ) {
        List<Path.Node> nodes = List.ofAll(propertyPath);
        assertThat(nodes).hasSize(2);

        // head element -> map
        assertThat(nodes.head().getKind()).isEqualTo(ElementKind.PROPERTY);
        assertThat(nodes.head().getName()).isEqualTo(fieldName);

        // last element -> entry
        assertThat(nodes.last().getKind()).isEqualTo(ElementKind.CONTAINER_ELEMENT);
        assertThat(nodes.last().getName()).isEqualTo("<map " + entryName + ">");
        assertThat(nodes.last().getKey()).isEqualTo(key);
        assertThat(nodes.last().as(Path.ContainerElementNode.class).getContainerClass()).isEqualTo(type);
    }

    @Test
    public void defaultConstructionShouldValidate() {
        validateAndAssertNoViolations(new TestBean());
    }

    @Test
    public void havingEmptyValueShouldNotValidate() {
        TestBean bean = new TestBean();
        bean.put("b", "");
        validateAndAssertSingleViolation(
                bean, NotBlank.class,
                "b", Map.class, "entries", "value"
        );
    }

    @Test
    public void havingEmptyValueForMultimapShouldNotValidate() {
        TestBean bean = new TestBean();
        bean.add("a", "");
        validateAndAssertSingleViolation(
                bean, NotBlank.class,
                "a", Multimap.class, "multientries", "value"
        );
    }

    @Test
    public void havingComplexKeyShouldNotValidate() {
        TestBean bean = new TestBean();
        bean.put("bad", "ok");
        validateAndAssertSingleViolation(
                bean, Pattern.class,
                "bad", Map.class, "entries", "key"
        );
    }

    @Test
    public void havingComplexKeyForMultimapShouldNotValidate() {
        TestBean bean = new TestBean();
        bean.add("bad", "ok");
        validateAndAssertSingleViolation(
                bean, Pattern.class,
                "bad", Multimap.class, "multientries", "key"
        );
    }

    @Test
    public void defaultConstructionShouldValidateForJava() {
        validateAndAssertNoViolations(new JavaTestBean());
    }

    @Test
    public void havingEmptyValueShouldNotValidateForJava() {
        JavaTestBean bean = new JavaTestBean();
        bean.put("b", "");
        validateAndAssertSingleViolation(
                bean, NotBlank.class,
                "b", java.util.Map.class, "entries", "value"
        );
    }

    @Test
    public void havingComplexKeyShouldNotValidateForJava() {
        JavaTestBean bean = new JavaTestBean();
        bean.put("bad", "ok");
        validateAndAssertSingleViolation(
                bean, Pattern.class,
                "bad", java.util.Map.class,"entries",  "key"
        );
    }

    private static class TestBean {
        private Map<@Pattern(regexp = SINGLE_CHAR) String, @NotBlank String> entries =
                HashMap.of("a", "b");

        private Multimap<@Pattern(regexp = SINGLE_CHAR) String, @NotBlank String> multientries =
                HashMultimap.withSet().of(entries.head());

        void put(String key, String value) {
            entries = entries.put(key, value);
        }

        void add(String key, String value) {
            multientries = multientries.put(key, value);
        }
    }

    private static class JavaTestBean {
        private java.util.Map<@Pattern(regexp = SINGLE_CHAR) String, @NotBlank String> entries =
                new java.util.HashMap<>();

        JavaTestBean() {
            put("a", "b");
        }

        void put(String key, String value) {
            entries.put(key, value);
        }
    }
}
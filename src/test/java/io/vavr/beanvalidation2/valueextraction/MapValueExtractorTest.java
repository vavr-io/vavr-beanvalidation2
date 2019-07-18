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

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

public class MapValueExtractorTest {

    protected static final String SINGLE_CHAR = "^[a-z]$";
    private Validator validator;

    @Before
    public void setUp() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private <T> void validateAndAssertNoViolations(T target) {
        Collection<ConstraintViolation<T>> violations = validator.validate(target);
        assertThat(violations).isEmpty();
    }

    private <T> void validateAndAssertSingleViolation(T target, Class<?> constraint, String key) {
        Collection<ConstraintViolation<T>> violations = validator.validate(target);

        assertThat(violations).isNotEmpty().hasSize(1);

        ConstraintViolation<T> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).isNotEmpty().hasSize(2);

        assertThat(violation.getConstraintDescriptor().getAnnotation()).isInstanceOf(constraint);

        Iterator<Path.Node> iterator = violation.getPropertyPath().iterator();

        Path.Node parent = iterator.next();
        assertThat(parent.getName()).isEqualToIgnoringCase("map");

        Path.Node child = iterator.next();
        assertThat(child.getKey().toString()).isEqualToIgnoringCase(key);
    }

    @Test
    public void defaultConstructionShouldValidate() {
        validateAndAssertNoViolations(new TestBean());
    }

    @Test
    public void havingEmptyValueShouldNotValidate() {
        TestBean bean = new TestBean();
        bean.put("b", "");
        validateAndAssertSingleViolation(bean, NotBlank.class, "b");
    }

    @Test
    public void havingComplexKeyShouldNotValidate() {
        TestBean bean = new TestBean();
        bean.put("bad", "ok");
        validateAndAssertSingleViolation(bean, Pattern.class, "bad");
    }

    @Test
    public void jvmDefaultConstructionShouldValidate() {
        validateAndAssertNoViolations(new JvmTestBean());
    }

    @Test
    public void jvmHavingEmptyValueShouldNotValidate() {
        JvmTestBean bean = new JvmTestBean();
        bean.put("b", "");
        validateAndAssertSingleViolation(bean, NotBlank.class, "b");
    }

    @Test
    public void jvmHavingComplexKeyShouldNotValidate() {
        JvmTestBean bean = new JvmTestBean();
        bean.put("bad", "ok");
        validateAndAssertSingleViolation(bean, Pattern.class, "bad");
    }

    private static class TestBean {
        private Map<@Pattern(regexp = SINGLE_CHAR) String, @NotBlank String> map =
                HashMap.of("a", "b");

        void put(String key, String value) {
            map = map.put(key, value);
        }
    }

    private static class JvmTestBean {
        private java.util.Map<@Pattern(regexp = SINGLE_CHAR) String, @NotBlank String> map =
                new java.util.HashMap<>();

        JvmTestBean() {
            put("a", "b");
        }

        void put(String key, String value) {
            map.put(key, value);
        }
    }
}
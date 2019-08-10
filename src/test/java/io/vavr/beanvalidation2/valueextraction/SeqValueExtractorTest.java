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
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

public class SeqValueExtractorTest {

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
            T target, int position, String type
    ) {
        Collection<ConstraintViolation<T>> violations = validator.validate(target);

        assertThat(violations).isNotEmpty().hasSize(1);

        ConstraintViolation<T> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).isNotEmpty().hasSize(2);

        assertThat(violation.getConstraintDescriptor().getAnnotation()).isInstanceOf(NotBlank.class);

        Iterator<Path.Node> iterator = violation.getPropertyPath().iterator();

        assertThat(violation.getPropertyPath().toString())
                .isEqualTo("letters[2].<" + type + " element>");

        Path.Node parent = iterator.next();
        assertThat(parent.getName()).isEqualToIgnoringCase("letters");

        Path.Node child = iterator.next();
        assertThat(child.getName()).isEqualToIgnoringCase("<" + type + " element>");
        assertThat(child.getIndex()).isEqualTo(position);
    }

    @Test
    public void defaultConstructionShouldValidate() {
        validateAndAssertNoViolations(new TestBean());
    }

    @Test
    public void havingEmptyValueShouldNotValidate() {
        TestBean bean = new TestBean();
        bean.add("");
        validateAndAssertSingleViolation(bean, 2, "sequence");
    }

    @Test
    public void defaultConstructionShouldValidateForJava() {
        validateAndAssertNoViolations(new JavaTestBean());
    }

    @Test
    public void havingEmptyValueShouldNotValidateForJava() {
        JavaTestBean bean = new JavaTestBean();
        bean.add("");
        validateAndAssertSingleViolation(bean, 2, "list");
    }

    private static class TestBean {
        private Seq<@NotBlank String> letters = List.of("a", "b");

        void add(String value) {
            letters = letters.append(value);
        }
    }

    private static class JavaTestBean {
        private java.util.List<@NotBlank String> letters =
                new ArrayList<>(Arrays.asList("a", "b"));

        void add(String value) {
            letters.add(value);
        }
    }
}
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

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

public class SeqValueExtractorTest {

    private Validator validator;

    @Before
    public void setUp() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private <T> void validateAndAssertNoViolations(T target) {
        Collection<ConstraintViolation<T>> violations = validator.validate(target);
        assertThat(violations).isEmpty();
    }

    private <T> void validateAndAssertSingleViolation(T target, int position) {
        Collection<ConstraintViolation<T>> violations = validator.validate(target);

        assertThat(violations).isNotEmpty().hasSize(1);

        ConstraintViolation<T> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).isNotEmpty().hasSize(2);

        assertThat(violation.getConstraintDescriptor().getAnnotation()).isInstanceOf(NotBlank.class);

        Iterator<Path.Node> iterator = violation.getPropertyPath().iterator();

        Path.Node parent = iterator.next();
        assertThat(parent.getName()).isEqualToIgnoringCase("list");

        Path.Node child = iterator.next();
        assertThat(child.getName()).isEqualToIgnoringCase("<seq element>");
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
        validateAndAssertSingleViolation(bean, 2);
    }

    private static class TestBean {
        private Seq<@NotBlank String> list = List.of("a", "b");

        void add(String value) {
            list = list.append(value);
        }
    }
}
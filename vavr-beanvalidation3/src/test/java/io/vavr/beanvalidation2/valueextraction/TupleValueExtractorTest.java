/*  __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2017 Vavr, http://vavr.io
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
package io.vavr.beanvalidation3.valueextraction;

import io.vavr.Tuple;
import io.vavr.Tuple0;
import io.vavr.Tuple1;
import io.vavr.Tuple3;
import io.vavr.beanvalidation3.ValidatorSupplier;
import org.junit.Before;
import org.junit.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

public class TupleValueExtractorTest {

    private Validator validator;
    private TestBean bean = null;

    @Before
    public void setUp() {
        this.validator = ValidatorSupplier.INSTANCE.get();
        this.bean = new TestBean();
    }

    private void validateAndAssertNoViolations() {
        Collection<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertThat(violations).isEmpty();
    }

    private void validateAndAssertSingleViolationAtIndex(String property, int index, Class<?> constraint) {
        Collection<ConstraintViolation<TestBean>> violations = validator.validate(bean);

        assertThat(violations).isNotEmpty().hasSize(1);

        ConstraintViolation<TestBean> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).isNotEmpty().hasSize(2);

        assertThat(violation.getConstraintDescriptor().getAnnotation()).isInstanceOf(constraint);

        Iterator<Path.Node> iterator = violation.getPropertyPath().iterator();

        Path.Node parent = iterator.next();
        assertThat(parent.getName()).isEqualToIgnoringCase(property);

        Path.Node offendingNode = iterator.next();
        assertThat(offendingNode.getIndex()).isEqualTo(index);
        assertThat(offendingNode.getName()).isEqualTo("<element>");
    }

    @Test
    public void testDefaultConstruction_shouldValidate() {
        validateAndAssertNoViolations();
    }

    @Test
    public void testTuple1_firstHavingNullValue_shouldNotValidate() {
        bean.setT1(Tuple.of(null));
        validateAndAssertSingleViolationAtIndex("t1", 1, NotBlank.class);
    }

    @Test
    public void testTuple3_firstHavingNullValue_shouldNotValidate() {
        bean.setT3(bean.getT3().update1(null));
        validateAndAssertSingleViolationAtIndex("t3", 1, NotBlank.class);
    }

    @Test
    public void testTuple3_secondHavingEmptyValue_shouldNotValidate() {
        bean.setT3(bean.getT3().update2(""));
        validateAndAssertSingleViolationAtIndex("t3", 2, NotBlank.class);
    }

    @Test
    public void testTuple3_thirdHavingNullValue_shouldNotValidate() {
        bean.setT3(bean.getT3().update3(null));
        validateAndAssertSingleViolationAtIndex("t3", 3, NotNull.class);
    }


    private static class TestBean {

        private Tuple0 t0;

        private Tuple1<@NotBlank String> t1 = Tuple.of("a");

        private Tuple3<@NotBlank String, @NotBlank String, @NotNull Integer> t3 = Tuple.of("a", "x", 3);

        public Tuple1<String> getT1() {
            return t1;
        }

        public void setT1(Tuple1<String> t1) {
            this.t1 = t1;
        }

        public Tuple3<String, String, Integer> getT3() {
            return t3;
        }

        public void setT3(Tuple3<String, String, Integer> t3) {
            this.t3 = t3;
        }
    }

}
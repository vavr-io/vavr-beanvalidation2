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
package io.vavr.beanvalidation2.constraintvalidators;

import io.vavr.beanvalidation2.ValidatorSupplier;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;

import static io.vavr.beanvalidation2.constraintvalidators.ValidatorTestUtil.assertSingleViolation;
import static org.assertj.core.api.Assertions.assertThat;

public class NotEmptyValidatorForValueTest {

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

    private void validateAndAssertSingleOffendingProperty(TestBean bean, String property) {
        Collection<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertSingleViolation(violations, property, NotEmpty.class);
    }

    @Test
    public void testDefaultConstruction_shouldValidate() {
        validateAndAssertNoViolations();
    }

    @Test
    public void testNotEmptyTraversable_butIsEmpty_shouldNotValidate() {
        bean.setNotEmptyTraversable(List.empty());
        validateAndAssertSingleOffendingProperty(bean, "notEmptyTraversable");
    }

    @Test
    public void testNotEmptyTraversable_butIsNull_shouldNotValidate() {
        bean.setNotEmptyTraversable(null);
        validateAndAssertSingleOffendingProperty(bean, "notEmptyTraversable");
    }

    @Test
    public void testNotEmptyOption_butIsNone_shouldNotValidate() {
        bean.setNotEmptyOption(Option.none());
        validateAndAssertSingleOffendingProperty(bean, "notEmptyOption");
    }

    @Test
    public void testNotEmptyOption_butIsNull_shouldNotValidate() {
        bean.setNotEmptyOption(null);
        validateAndAssertSingleOffendingProperty(bean, "notEmptyOption");
    }

    @Test
    public void testNotEmptyTry_butIsFailure_shouldNotValidate() {
        bean.setNotEmptyTry(Try.failure(new IllegalArgumentException()));
        validateAndAssertSingleOffendingProperty(bean, "notEmptyTry");
    }

    @Test
    public void testNotEmptyTry_butIsNull_shouldNotValidate() {
        bean.setNotEmptyTry(null);
        validateAndAssertSingleOffendingProperty(bean, "notEmptyTry");
    }

    @Test
    public void testNotEmptyEither_butIsLeft_shouldNotValidate() {
        bean.setNotEmptyEither(Either.left("ERROR"));
        validateAndAssertSingleOffendingProperty(bean, "notEmptyEither");
    }

    @Test
    public void testNotEmptyEither_butIsNull_shouldNotValidate() {
        bean.setNotEmptyEither(null);
        validateAndAssertSingleOffendingProperty(bean, "notEmptyEither");
    }

    private static class TestBean {

        @NotEmpty
        private Traversable<Integer> notEmptyTraversable = Vector.of(0);

        @NotEmpty
        private Option<Integer> notEmptyOption = Option.some(1);

        @NotEmpty
        private Try<Integer> notEmptyTry = Try.success(1);

        @NotEmpty
        private Either<String, Integer> notEmptyEither = Either.right(42);

        public Traversable<Integer> getNotEmptyTraversable() {
            return notEmptyTraversable;
        }

        public void setNotEmptyTraversable(Traversable<Integer> notEmptyTraversable) {
            this.notEmptyTraversable = notEmptyTraversable;
        }

        public Option<Integer> getNotEmptyOption() {
            return notEmptyOption;
        }

        public void setNotEmptyOption(Option<Integer> notEmptyOption) {
            this.notEmptyOption = notEmptyOption;
        }

        public Try<Integer> getNotEmptyTry() {
            return notEmptyTry;
        }

        public void setNotEmptyTry(Try<Integer> notEmptyTry) {
            this.notEmptyTry = notEmptyTry;
        }

        public Either<String, Integer> getNotEmptyEither() {
            return notEmptyEither;
        }

        public void setNotEmptyEither(Either<String, Integer> notEmptyEither) {
            this.notEmptyEither = notEmptyEither;
        }
    }

}
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
package io.vavr.beanvalidation2;

import io.vavr.collection.*;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Size;
import java.util.Collection;

import static io.vavr.beanvalidation2.ValidationTestUtil.assertSingleViolation;
import static org.assertj.core.api.Assertions.assertThat;

public class SizeValidatorForTraversableTest {

    private Validator validator;
    private TestBean bean = null;

    @Before
    public void setUp() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        this.bean = new TestBean();
    }

    private void validateAndAssertNoViolations() {
        Collection<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertThat(violations).isEmpty();
    }

    private void validateAndAssertSingleOffendingProperty(TestBean bean, String property) {
        Collection<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertSingleViolation(violations, property, Size.class);
    }

    @Test
    public void testDefaultConstruction_shouldValidate() {
        validateAndAssertNoViolations();
    }

    @Test
    public void testAtLeastOne_butIsEmpty_shouldNotValidate() {
        bean.setAtLeastOne(List.empty());
        validateAndAssertSingleOffendingProperty(bean, "atLeastOne");
    }

    @Test
    public void testAtLeastOne_withOneEntry_shouldValidate() {
        bean.setAtLeastOne(List.of(1));
        validateAndAssertNoViolations();
    }

    @Test
    public void testAtLeastOne_withMoreThanOneEntry_shouldValidate() {
        bean.setAtLeastOne(List.of(1, 2, 3));
        validateAndAssertNoViolations();
    }

    @Test
    public void testMaxOne_butHasTwo_shouldNotValidate() {
        bean.setMaxOne(List.of(1, 2));
        validateAndAssertSingleOffendingProperty(bean, "maxOne");
    }

    @Test
    public void testMaxOne_withOneEntry_shouldValidate() {
        bean.setMaxOne(List.of(1));
        validateAndAssertNoViolations();
    }

    @Test
    public void testMaxOne_empty_shouldValidate() {
        bean.setMaxOne(List.empty());
        validateAndAssertNoViolations();
    }

    @Test
    public void testExactlyOne_butHasTwo_shouldNotValidate() {
        bean.setExactlyOne(HashSet.of(1, 2));
        validateAndAssertSingleOffendingProperty(bean, "exactlyOne");
    }

    @Test
    public void testExactlyOne_withOneEntry_shouldValidate() {
        bean.setExactlyOne(HashSet.of(1));
        validateAndAssertNoViolations();
    }

    @Test
    public void testExactlyOne_empty_shouldNotValidate() {
        bean.setExactlyOne(HashSet.empty());
        validateAndAssertSingleOffendingProperty(bean, "exactlyOne");
    }

    @Test
    public void testExactlyOneMap_butHasTwo_shouldNotValidate() {
        bean.setExactlyOneMap(HashMap.of(1, "one", 2, "two"));
        validateAndAssertSingleOffendingProperty(bean, "exactlyOneMap");
    }

    @Test
    public void testExactlyOneMap_withOneEntry_shouldValidate() {
        bean.setExactlyOneMap(HashMap.of(1, "one"));
        validateAndAssertNoViolations();
    }

    @Test
    public void testExactlyOneMap_empty_shouldNotValidate() {
        bean.setExactlyOneMap(HashMap.empty());
        validateAndAssertSingleOffendingProperty(bean, "exactlyOneMap");
    }

    private static class TestBean {

        @Size(min = 1)
        private List<Integer> atLeastOne = null;

        @Size(max = 1)
        private Seq<Integer> maxOne = null;

        @Size(min = 1, max = 1)
        private Set<Integer> exactlyOne = null;

        @Size(min = 1, max = 1)
        private Map<Integer, String> exactlyOneMap = null;

        public List<Integer> getAtLeastOne() {
            return atLeastOne;
        }

        public void setAtLeastOne(List<Integer> atLeastOne) {
            this.atLeastOne = atLeastOne;
        }

        public Seq<Integer> getMaxOne() {
            return maxOne;
        }

        public void setMaxOne(Seq<Integer> maxOne) {
            this.maxOne = maxOne;
        }

        public Set<Integer> getExactlyOne() {
            return exactlyOne;
        }

        public void setExactlyOne(Set<Integer> exactlyOne) {
            this.exactlyOne = exactlyOne;
        }

        public Map<Integer, String> getExactlyOneMap() {
            return exactlyOneMap;
        }

        public void setExactlyOneMap(Map<Integer, String> exactlyOneMap) {
            this.exactlyOneMap = exactlyOneMap;
        }

    }

}
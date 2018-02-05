package io.vavr.beanvalidation2.valueextraction;

import io.vavr.*;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Iterator;

import static io.vavr.beanvalidation2.ValidationTestUtil.assertSingleViolation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class TupleValueExtractorTest {

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
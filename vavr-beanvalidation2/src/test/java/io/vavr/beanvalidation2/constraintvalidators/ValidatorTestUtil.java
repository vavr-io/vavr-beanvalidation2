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

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidatorTestUtil {

    public static <T> void assertSingleViolation(Collection<ConstraintViolation<T>> violations, String property, Class<?> constraint) {
        assertThat(violations).isNotEmpty().hasSize(1);

        ConstraintViolation<T> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).isNotEmpty().hasSize(1);

        assertThat(violation.getConstraintDescriptor().getAnnotation()).isInstanceOf(constraint);

        Path.Node offendingNode = violation.getPropertyPath().iterator().next();
        assertThat(offendingNode.getName()).isEqualToIgnoringCase(property);
    }

}

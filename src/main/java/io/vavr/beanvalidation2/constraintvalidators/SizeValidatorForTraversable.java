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

import io.vavr.collection.Traversable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Size;


public class SizeValidatorForTraversable implements ConstraintValidator<Size, Traversable<?>> {

    private int min;
    private int max;

    @Override
    public void initialize(Size constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();

        if (min < 0) {
            throw new IllegalArgumentException("The min parameter cannot be negative.");
        } else if (max < min) {
            throw new IllegalArgumentException("The max parameter cannot be less than the min parameter");
        }
    }

    @Override
    public boolean isValid(Traversable<?> value, ConstraintValidatorContext context) {
        return value == null || value.size() >= min && value.size() <= max;
    }
}


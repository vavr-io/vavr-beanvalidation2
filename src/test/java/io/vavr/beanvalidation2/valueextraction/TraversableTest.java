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
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.constraints.Size;

import static org.assertj.core.api.Assertions.assertThat;

public class TraversableTest {
	private Validator validator;

	@Before
	public void setUp() {
		this.validator = ValidatorSupplier.INSTANCE.get();
	}

	@Test
	public void testOnValidBean() {
		// given
		TestBean subject = new TestBean();

		// when
		Set<ConstraintViolation<TestBean>> violations =
				HashSet.ofAll(validator.validate(subject));

		// then
		assertThat(violations).hasSize(0);
	}

	@Test
	public void testOnInvalidBean() {
		// given
		TestBean subject = new TestBean();
		subject.add("to-long-message-to-be-valid");

		// when
		Set<ConstraintViolation<TestBean>> violations =
				HashSet.ofAll(validator.validate(subject));

		// then
		assertThat(violations).hasSize(1);
		ConstraintViolation<TestBean> violation = violations.head();
		List<Path.Node> nodes = List.ofAll(violation.getPropertyPath());
		assertThat(nodes).hasSize(2);

		// head element -> map
		assertThat(nodes.head().getKind()).isEqualTo(ElementKind.PROPERTY);
		assertThat(nodes.head().getName()).isEqualTo("messages");

		// last element -> entry
		assertThat(nodes.last().getKind()).isEqualTo(ElementKind.CONTAINER_ELEMENT);
		assertThat(nodes.last().getName()).isEqualTo("<iterable element>");
		assertThat(nodes.last().as(Path.ContainerElementNode.class).getContainerClass())
				.isEqualTo(Set.class);
	}

	private static final class TestBean {
		private Set<@Size(max = 10) String> messages = HashSet.of("valid", "elements");

		void add(String message) {
			messages = messages.add(message);
		}
	}
}

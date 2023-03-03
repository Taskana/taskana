/*-
 * #%L
 * pro.taskana:taskana-common
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.internal.util;

public class Quadruple<A, S, D, F> {

  private final A first;
  private final S second;
  private final D third;
  private final F fourth;

  private Quadruple(A first, S second, D third, F fourth) {
    this.first = first;
    this.second = second;
    this.third = third;
    this.fourth = fourth;
  }

  public static <A, S, D, F> Quadruple<A, S, D, F> of(A a, S s, D d, F f) {
    return new Quadruple<>(a, s, d, f);
  }

  public A getFirst() {
    return first;
  }

  public S getSecond() {
    return second;
  }

  public D getThird() {
    return third;
  }

  public F getFourth() {
    return fourth;
  }
}

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

public class Triplet<L, M, R> {

  private final L left;
  private final M middle;
  private final R right;

  private Triplet(L left, M middle, R right) {
    this.left = left;
    this.middle = middle;
    this.right = right;
  }

  public static <L, M, R> Triplet<L, M, R> of(L left, M middle, R right) {
    return new Triplet<>(left, middle, right);
  }

  public L getLeft() {
    return left;
  }

  public M getMiddle() {
    return middle;
  }

  public R getRight() {
    return right;
  }
}

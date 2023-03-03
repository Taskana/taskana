/*-
 * #%L
 * pro.taskana:taskana-core
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
package pro.taskana.monitor.api.reports.item;

import pro.taskana.monitor.api.reports.Report;

/**
 * The QueryItemPreprocessor is used when adding {@linkplain QueryItem}s into a {@linkplain Report}.
 * It defines a processing step which is executed on each {@linkplain QueryItem} before inserting it
 * into the {@linkplain Report}.
 *
 * @param <I> Item class which is being pre processed.
 */
@FunctionalInterface
public interface QueryItemPreprocessor<I extends QueryItem> {

  I apply(I item);
}

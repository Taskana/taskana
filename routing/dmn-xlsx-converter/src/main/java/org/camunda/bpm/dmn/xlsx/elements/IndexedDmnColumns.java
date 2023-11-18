/* Licensed under the Apache License, Version 2.0 (the "License");
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
 */
package org.camunda.bpm.dmn.xlsx.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.Output;

/**
 * @author Thorben Lindhauer
 */
public class IndexedDmnColumns {

  protected Map<Input, String> inputColumns = new HashMap<>();
  protected Map<Output, String> outputColumns = new HashMap<>();

  // as they appear in the resulting DMN table
  protected List<Input> orderedInputs = new ArrayList<>();
  protected List<Output> orderedOutputs = new ArrayList<>();

  public List<Input> getOrderedInputs() {
    return orderedInputs;
  }

  public List<Output> getOrderedOutputs() {
    return orderedOutputs;
  }

  public String getSpreadsheetColumn(Input input) {
    return inputColumns.get(input);
  }

  public String getSpreadsheetColumn(Output output) {
    return outputColumns.get(output);
  }

  public void addInput(String column, Input input) {
    this.orderedInputs.add(input);
    this.inputColumns.put(input, column);
  }

  public void addOutput(String column, Output output) {
    this.orderedOutputs.add(output);
    this.outputColumns.put(output, column);
  }
}

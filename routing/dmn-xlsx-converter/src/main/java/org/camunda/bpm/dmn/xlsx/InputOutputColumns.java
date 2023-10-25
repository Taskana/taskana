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
package org.camunda.bpm.dmn.xlsx;

import java.util.ArrayList;
import java.util.List;
import org.camunda.bpm.dmn.xlsx.elements.HeaderValuesContainer;

/**
 * @author Thorben Lindhauer
 */
public class InputOutputColumns {

  protected List<HeaderValuesContainer> inputHeaders;
  protected List<HeaderValuesContainer> outputHeaders;

  public InputOutputColumns() {
    this.inputHeaders = new ArrayList<HeaderValuesContainer>();
    this.outputHeaders = new ArrayList<HeaderValuesContainer>();
  }

  public void addOutputHeader(HeaderValuesContainer hvc) {
    this.outputHeaders.add(hvc);
  }

  public void addInputHeader(HeaderValuesContainer hvc) {
    this.inputHeaders.add(hvc);
  }

  public List<HeaderValuesContainer> getOutputHeaders() {
    return outputHeaders;
  }

  public List<HeaderValuesContainer> getInputHeaders() {
    return inputHeaders;
  }
}

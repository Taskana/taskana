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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Matcher;
import org.camunda.bpm.dmn.xlsx.DmnValueRangeConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * @author Thorben Lindhauer
 *
 */
public class DmnValueRangeMatcherTest {

  @ParameterizedTest
  @CsvSource({
      "[1..9], true ",
      "]1..9], true ",
      "[1..9[, true ",
      "]1..9[, true ",
      "[100..900], true ",
      "[10.1..909], true ",
      "[10..90.1], true ",
      "[10.1..90.1], true ",
      "text, false ",
      "[a..b], false ",
      "[100..a], false ",
      "[100..900, false ",
      "100..900, false ",
      "[100900], false ",
      "[100.900], false ",
      "[date and time(\"2018-05-17T00:00:00\")..date and time(\"2018-11-17T24:00:00\")], true"
  })
  public void shouldMatchInclusiveInterval(String input, boolean shouldMatch)
  {
    Matcher matcher = DmnValueRangeConverter.RANGE_REGEX.matcher(input);

    assertThat(matcher.matches()).isEqualTo(shouldMatch);
  }
}

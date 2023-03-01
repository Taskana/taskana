/*-
 * #%L
 * pro.taskana:taskana-rest-spring-example-common
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
package pro.taskana.example.ldap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import pro.taskana.common.rest.models.AccessIdRepresentationModel;
import pro.taskana.rest.test.TaskanaSpringBootTest;

/** Test Ldap attachment. */
@TaskanaSpringBootTest
@ActiveProfiles({"emptySearchRoots"})
class LdapEmptySearchRootsTest extends LdapTest {

  @Test
  void should_FindGroupsForUser_When_UserIdIsProvided() throws Exception {
    List<AccessIdRepresentationModel> groups =
        ldapClient.searchGroupsAccessIdIsMemberOf("user-2-2");
    assertThat(groups)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsExactlyInAnyOrder(
            "cn=ksc-users,cn=groups,ou=test,o=taskana",
            "cn=organisationseinheit ksc 2,cn=organisationseinheit ksc,"
                + "cn=organisation,ou=test,o=taskana");
  }

  @Test
  void should_ReturnFullDnForUser_When_AccessIdOfUserIsGiven() throws Exception {
    String dn = ldapClient.searchDnForAccessId("otheruser");
    assertThat(dn).isEqualTo("uid=otheruser,cn=other-users,ou=test,o=taskana");
  }
}

/*-
 * #%L
 * pro.taskana:taskana-rest-spring
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
package pro.taskana.user.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.rest.test.TaskanaSpringBootTest;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.models.User;
import pro.taskana.user.internal.models.UserImpl;
import pro.taskana.user.rest.models.UserRepresentationModel;

/** Test for {@linkplain UserRepresentationModelAssembler}. */
@TaskanaSpringBootTest
class UserRepresentationModelAssemblerTest {
  private final UserRepresentationModelAssembler assembler;
  private final UserService userService;

  @Autowired
  UserRepresentationModelAssemblerTest(
      UserRepresentationModelAssembler assembler, UserService userService) {
    this.assembler = assembler;
    this.userService = userService;
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingUserEntityToRepresentationModel() {
    UserImpl user = (UserImpl) userService.newUser();
    user.setId("user-1-2");
    user.setGroups(Set.of("group1", "group2"));
    user.setFirstName("Hans");
    user.setLastName("Georg");
    user.setFullName("Hans Georg");
    user.setLongName("Georg, Hans - user-1-2");
    user.setEmail("hans.georg@web.com");
    user.setPhone("1234");
    user.setMobilePhone("01574275632");
    user.setOrgLevel4("Novatec");
    user.setOrgLevel3("BPM");
    user.setOrgLevel2("Human Workflow");
    user.setOrgLevel1("TASKANA");
    user.setData("xy");
    user.setDomains(Set.of("DOMAIN_A", "DOMAIN_B"));

    UserRepresentationModel repModel = assembler.toModel(user);
    testEquality(user, repModel);
  }

  @Test
  void should_ReturnEntity_When_ConvertingUserRepresentationModelToEntity() {
    UserRepresentationModel repModel = new UserRepresentationModel();
    repModel.setUserId("user-1-2");
    repModel.setGroups(Set.of("group1", "group2"));
    repModel.setFirstName("Hans");
    repModel.setLastName("Georg");
    repModel.setFullName("Hans Georg");
    repModel.setLongName("Georg, Hans - user-1-2");
    repModel.setEmail("hans.georg@web.com");
    repModel.setPhone("1234");
    repModel.setMobilePhone("01574275632");
    repModel.setOrgLevel4("Novatec");
    repModel.setOrgLevel3("BPM");
    repModel.setOrgLevel2("Human Workflow");
    repModel.setOrgLevel1("TASKANA");
    repModel.setData("xy");
    repModel.setDomains(Set.of("DOMAIN_A", "DOMAIN_B"));

    User user = assembler.toEntityModel(repModel);
    testEquality(user, repModel);
  }

  @Test
  void should_BeEqual_When_ConvertingEntityToRepModelAndBackToEntity() {
    UserImpl user = (UserImpl) userService.newUser();
    user.setId("user-1-2");
    user.setGroups(Set.of("group1", "group2"));
    user.setFirstName("Hans");
    user.setLastName("Georg");
    user.setFullName("Hans Georg");
    user.setLongName("Georg, Hans - user-1-2");
    user.setEmail("hans.georg@web.com");
    user.setPhone("1234");
    user.setMobilePhone("01574275632");
    user.setOrgLevel4("Novatec");
    user.setOrgLevel3("BPM");
    user.setOrgLevel2("Human Workflow");
    user.setOrgLevel1("TASKANA");
    user.setData("xy");
    user.setDomains(Set.of("DOMAIN_A", "DOMAIN_B"));

    UserRepresentationModel repModel = assembler.toModel(user);
    User userAfterConversion = assembler.toEntityModel(repModel);

    assertThat(user)
        .hasNoNullFieldsOrProperties()
        .isNotSameAs(userAfterConversion)
        .isEqualTo(userAfterConversion);
  }

  private static void testEquality(User entity, UserRepresentationModel repModel) {
    assertThat(entity).hasNoNullFieldsOrProperties();
    assertThat(repModel).hasNoNullFieldsOrProperties();

    assertThat(entity.getId()).isEqualTo(repModel.getUserId());
    assertThat(entity.getGroups()).isEqualTo(repModel.getGroups());
    assertThat(entity.getFirstName()).isEqualTo(repModel.getFirstName());
    assertThat(entity.getLastName()).isEqualTo(repModel.getLastName());
    assertThat(entity.getFullName()).isEqualTo(repModel.getFullName());
    assertThat(entity.getLongName()).isEqualTo(repModel.getLongName());
    assertThat(entity.getEmail()).isEqualTo(repModel.getEmail());
    assertThat(entity.getPhone()).isEqualTo(repModel.getPhone());
    assertThat(entity.getMobilePhone()).isEqualTo(repModel.getMobilePhone());
    assertThat(entity.getOrgLevel4()).isEqualTo(repModel.getOrgLevel4());
    assertThat(entity.getOrgLevel3()).isEqualTo(repModel.getOrgLevel3());
    assertThat(entity.getOrgLevel2()).isEqualTo(repModel.getOrgLevel2());
    assertThat(entity.getOrgLevel1()).isEqualTo(repModel.getOrgLevel1());
    assertThat(entity.getData()).isEqualTo(repModel.getData());
    assertThat(entity.getDomains()).isEqualTo(repModel.getDomains());
  }
}

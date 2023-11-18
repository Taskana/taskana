package pro.taskana.user.rest.assembler;

import java.util.List;
import org.springframework.stereotype.Component;
import pro.taskana.common.rest.assembler.CollectionRepresentationModelAssembler;
import pro.taskana.user.api.models.User;
import pro.taskana.user.internal.models.UserImpl;
import pro.taskana.user.rest.models.UserCollectionRepresentationModel;
import pro.taskana.user.rest.models.UserRepresentationModel;

/**
 * The assembler transforms a {@link User} to its resource counterpart {@linkplain
 * UserRepresentationModel} and vice versa.
 */
@Component
public class UserRepresentationModelAssembler
    implements CollectionRepresentationModelAssembler<
        User, UserRepresentationModel, UserCollectionRepresentationModel> {

  @Override
  public UserRepresentationModel toModel(User entity) {
    UserRepresentationModel repModel = new UserRepresentationModel();
    repModel.setUserId(entity.getId());
    repModel.setGroups(entity.getGroups());
    repModel.setPermissions(entity.getPermissions());
    repModel.setFirstName(entity.getFirstName());
    repModel.setLastName(entity.getLastName());
    repModel.setFullName(entity.getFullName());
    repModel.setLongName(entity.getLongName());
    repModel.setEmail(entity.getEmail());
    repModel.setPhone(entity.getPhone());
    repModel.setMobilePhone(entity.getMobilePhone());
    repModel.setOrgLevel4(entity.getOrgLevel4());
    repModel.setOrgLevel3(entity.getOrgLevel3());
    repModel.setOrgLevel2(entity.getOrgLevel2());
    repModel.setOrgLevel1(entity.getOrgLevel1());
    repModel.setData(entity.getData());
    repModel.setDomains(entity.getDomains());

    return repModel;
  }

  public User toEntityModel(UserRepresentationModel repModel) {
    UserImpl user = new UserImpl();
    user.setId(repModel.getUserId());
    user.setGroups(repModel.getGroups());
    user.setPermissions(repModel.getPermissions());
    user.setFirstName(repModel.getFirstName());
    user.setLastName(repModel.getLastName());
    user.setFullName(repModel.getFullName());
    user.setLongName(repModel.getLongName());
    user.setEmail(repModel.getEmail());
    user.setPhone(repModel.getPhone());
    user.setMobilePhone(repModel.getMobilePhone());
    user.setOrgLevel4(repModel.getOrgLevel4());
    user.setOrgLevel3(repModel.getOrgLevel3());
    user.setOrgLevel2(repModel.getOrgLevel2());
    user.setOrgLevel1(repModel.getOrgLevel1());
    user.setData(repModel.getData());
    user.setDomains(repModel.getDomains());

    return user;
  }

  @Override
  public UserCollectionRepresentationModel buildCollectionEntity(
      List<UserRepresentationModel> content) {
    return new UserCollectionRepresentationModel(content);
  }
}

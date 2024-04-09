import {
  WorkbasketsCustomisation,
  ClassificationsCustomisation,
  AccessItemsCustomisation,
  TasksCustomisation,
  ClassificationCategoryImages,
  GlobalCustomisation
} from 'app/shared/models/customisation';
import { Selector } from '@ngxs/store';
import { EngineConfigurationStateModel, EngineConfigurationState } from './engine-configuration.state';

export class EngineConfigurationSelectors {
  @Selector([EngineConfigurationState])
  static globalCustomisation(state: EngineConfigurationStateModel): GlobalCustomisation {
    return state.customisation[state.language].global;
  }

  @Selector([EngineConfigurationState])
  static workbasketsCustomisation(state: EngineConfigurationStateModel): WorkbasketsCustomisation {
    return state.customisation[state.language].workbaskets;
  }

  @Selector([EngineConfigurationState])
  static classificationsCustomisation(state: EngineConfigurationStateModel): ClassificationsCustomisation {
    return state.customisation[state.language].classifications;
  }

  @Selector([EngineConfigurationState])
  static accessItemsCustomisation(state: EngineConfigurationStateModel): AccessItemsCustomisation {
    return state.customisation[state.language].workbaskets['access-items'];
  }

  @Selector([EngineConfigurationState])
  static tasksCustomisation(state: EngineConfigurationStateModel): TasksCustomisation {
    return state.customisation[state.language].tasks;
  }

  @Selector([EngineConfigurationState])
  static selectCategoryIcons(state: EngineConfigurationStateModel): ClassificationCategoryImages {
    return {
      ...state.customisation[state.language].classifications.categories
    };
  }
}

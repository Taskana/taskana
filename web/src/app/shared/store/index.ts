import { EngineConfigurationState } from './engine-configuration-store/engine-configuration.state';
import { ClassificationState } from './classification-store/classification.state';
import { WorkbasketState } from './workbasket-store/workbasket.state';
import { AccessItemsManagementState } from './access-items-management-store/access-items-management.state';
import { FilterState } from './filter-store/filter.state';
import { WorkplaceState } from './workplace-store/workplace.state';
import { SettingsState } from './settings-store/settings.state';

export const STATES = [
  EngineConfigurationState,
  ClassificationState,
  WorkbasketState,
  AccessItemsManagementState,
  FilterState,
  WorkplaceState,
  SettingsState
];

import { EngineConfigurationState } from './engine-configuration-store/engine-configuration.state';
import { ClassificationState } from './classification-store/classification.state';
import { WorkbasketState } from './workbasket-store/workbasket.state';
import { AccessItemsManagementState } from './access-items-management-store/access-items-management.state';
import { FilterState } from './filter-store/filter.state';

export const STATES = [
  EngineConfigurationState,
  ClassificationState,
  WorkbasketState,
  AccessItemsManagementState,
  FilterState
];

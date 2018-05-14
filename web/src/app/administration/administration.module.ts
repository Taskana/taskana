// tslint:enable:max-line-length
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Ng2AutoCompleteModule } from 'ng2-auto-complete';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { AlertModule } from 'ngx-bootstrap';
import { SharedModule } from 'app/shared/shared.module';
import { AdministrationRoutingModule } from './administration-routing.module';

/**
 * Components
 */
import { WorkbasketListComponent } from './workbasket/master/list/workbasket-list.component';
import { WorkbasketListToolbarComponent } from './workbasket/master/list/workbasket-list-toolbar/workbasket-list-toolbar.component'
import { WorkbasketDetailsComponent } from './workbasket/details/workbasket-details.component';
import { WorkbasketInformationComponent } from './workbasket/details/information/workbasket-information.component';
import { DistributionTargetsComponent } from './workbasket/details/distribution-targets/distribution-targets.component';
import { DualListComponent } from './workbasket/details/distribution-targets/dual-list/dual-list.component';
import { AccessItemsComponent } from './workbasket/details/access-items/access-items.component';
import { NoAccessComponent } from './workbasket/details/noAccess/no-access.component';
import { FilterComponent } from './components/filter/filter.component';
import { IconTypeComponent } from './components/type-icon/icon-type.component';
import { PaginationComponent } from './workbasket/master/list/pagination/pagination.component';
import { ClassificationListComponent } from './classification/master/list/classification-list.component';
import { ClassificationDetailsComponent } from './classification/details/classification-details.component';
import { ImportExportComponent } from './components/import-export/import-export.component';
import { ClassificationTypesSelectorComponent } from 'app/shared/classification-types-selector/classification-types-selector.component';
import { SortComponent } from './components/sort/sort.component';

/**
 * Services
 */
import { WorkbasketService } from './services/workbasket/workbasket.service';
import { SavingWorkbasketService } from './services/saving-workbaskets/saving-workbaskets.service';
import { ClassificationDefinitionService } from './services/classification-definition/classification-definition.service';
import { WorkbasketDefinitionService } from './services/workbasket-definition/workbasket-definition.service';
import { ClassificationsService } from './services/classifications/classifications.service';
import { ClassificationTypesService } from './services/classification-types/classification-types.service';
import { ClassificationCategoriesService } from './services/classification-categories-service/classification-categories.service';


const MODULES = [
  CommonModule,
  FormsModule,
  Ng2AutoCompleteModule,
  AngularSvgIconModule,
  AlertModule,
  SharedModule,
  AdministrationRoutingModule
];

const DECLARATIONS = [
  WorkbasketListComponent,
  WorkbasketListToolbarComponent,
  AccessItemsComponent,
  WorkbasketDetailsComponent,
  WorkbasketInformationComponent,
  NoAccessComponent,
  FilterComponent,
  IconTypeComponent,
  DistributionTargetsComponent,
  DualListComponent,
  PaginationComponent,
  ClassificationListComponent,
  ImportExportComponent,
  ClassificationTypesSelectorComponent,
  ClassificationDetailsComponent,
  SortComponent
];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [
    WorkbasketService,
    ClassificationDefinitionService,
    WorkbasketDefinitionService,
    SavingWorkbasketService,
    ClassificationsService,
    ClassificationTypesService,
    ClassificationCategoriesService,
  ]
})
export class AdministrationModule {
}
// tslint:enable:max-line-length

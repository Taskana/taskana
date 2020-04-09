import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { AlertModule, TypeaheadModule } from 'ngx-bootstrap';
import { SharedModule } from 'app/shared/shared.module';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { ClassificationTypesSelectorComponent } from 'app/shared/classification-types-selector/classification-types-selector.component';
import { ClassificationCategoriesService } from 'app/shared/services/classifications/classification-categories.service';
import { AccessItemsManagementComponent } from 'app/administration/access-items-management/access-items-management.component';
import { AdministrationRoutingModule } from './administration-routing.module';
/**
 * Components
 */
import { WorkbasketListComponent } from './workbasket/master/workbasket-list.component';
import { WorkbasketListToolbarComponent } from './workbasket/master/workbasket-list-toolbar/workbasket-list-toolbar.component';
import { WorkbasketDetailsComponent } from './workbasket/details/workbasket-details.component';
import { WorkbasketInformationComponent } from './workbasket/details/information/workbasket-information.component';
import { DistributionTargetsComponent } from './workbasket/details/distribution-targets/distribution-targets.component';
import { DualListComponent } from './workbasket/details/distribution-targets/dual-list/dual-list.component';
import { AccessItemsComponent } from './workbasket/details/access-items/access-items.component';
import { ClassificationListComponent } from './classification/master/list/classification-list.component';
import { ClassificationDetailsComponent } from './classification/details/classification-details.component';
import { ImportExportComponent } from './components/import-export/import-export.component';
/**
 * Services
 */
import { SavingWorkbasketService } from './services/saving-workbaskets/saving-workbaskets.service';
import { ClassificationDefinitionService } from './services/classification-definition/classification-definition.service';
import { WorkbasketDefinitionService } from './services/workbasket-definition/workbasket-definition.service';
import { ImportExportService } from './services/import-export/import-export.service';

const MODULES = [
  CommonModule,
  FormsModule,
  ReactiveFormsModule,
  AngularSvgIconModule,
  AlertModule,
  SharedModule,
  AdministrationRoutingModule,
  TypeaheadModule,
  InfiniteScrollModule,
];

const DECLARATIONS = [
  WorkbasketListComponent,
  WorkbasketListToolbarComponent,
  AccessItemsComponent,
  WorkbasketDetailsComponent,
  WorkbasketInformationComponent,
  DistributionTargetsComponent,
  DualListComponent,
  ClassificationListComponent,
  ImportExportComponent,
  ClassificationTypesSelectorComponent,
  ClassificationDetailsComponent,
  AccessItemsManagementComponent
];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [
    ClassificationDefinitionService,
    WorkbasketDefinitionService,
    SavingWorkbasketService,
    ClassificationCategoriesService,
    ImportExportService,
  ]
})
export class AdministrationModule {
}

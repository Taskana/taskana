import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { AlertModule, TypeaheadModule } from 'ngx-bootstrap';
import { SharedModule } from 'app/shared/shared.module';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { TreeModule } from 'angular-tree-component';

import { ClassificationTypesSelectorComponent } from 'app/administration/components/classification-types-selector/classification-types-selector.component';
import { ClassificationCategoriesService } from 'app/shared/services/classification-categories/classification-categories.service';
import { AccessItemsManagementComponent } from 'app/administration/components/access-items-management/access-items-management.component';
import { MatRadioModule } from '@angular/material/radio';
import { AdministrationRoutingModule } from './administration-routing.module';
/**
 * Components
 */
import { WorkbasketListComponent } from './components/workbasket-list/workbasket-list.component';
import { WorkbasketListToolbarComponent } from './components/workbasket-list-toolbar/workbasket-list-toolbar.component';
import { WorkbasketDetailsComponent } from './components/workbasket-details/workbasket-details.component';
import { WorkbasketInformationComponent } from './components/workbasket-information/workbasket-information.component';
import { WorkbasketDistributionTargetsComponent } from './components/workbasket-distribution-targets/workbasket-distribution-targets.component';
import { WorkbasketDualListComponent } from './components/workbasket-dual-list/workbasket-dual-list.component';
import { WorkbasketAccessItemsComponent } from './components/workbasket-access-items/workbasket-access-items.component';
import { ClassificationListComponent } from './components/classification-list/classification-list.component';
import { ClassificationDetailsComponent } from './components/classification-details/classification-details.component';
import { ImportExportComponent } from './components/import-export/import-export.component';
/**
 * Services
 */
import { SavingWorkbasketService } from './services/saving-workbaskets.service';
import { ClassificationDefinitionService } from './services/classification-definition.service';
import { WorkbasketDefinitionService } from './services/workbasket-definition.service';
import { ImportExportService } from './services/import-export.service';
import { ClassificationOverviewComponent } from './components/classification-overview/classification-overview.component';
import { WorkbasketOverviewComponent } from './components/workbasket-overview/workbasket-overview.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';

const MODULES = [
  CommonModule,
  FormsModule,
  ReactiveFormsModule,
  AngularSvgIconModule,
  AlertModule,
  SharedModule,
  AdministrationRoutingModule,
  TypeaheadModule,
  InfiniteScrollModule
];

const DECLARATIONS = [
  WorkbasketOverviewComponent,
  WorkbasketListComponent,
  WorkbasketListToolbarComponent,
  WorkbasketAccessItemsComponent,
  WorkbasketDetailsComponent,
  WorkbasketInformationComponent,
  WorkbasketDistributionTargetsComponent,
  WorkbasketDualListComponent,
  ClassificationOverviewComponent,
  ClassificationListComponent,
  ClassificationTypesSelectorComponent,
  ClassificationDetailsComponent,
  ImportExportComponent,
  AccessItemsManagementComponent
];

@NgModule({
  declarations: DECLARATIONS,
  imports: [MODULES, MatRadioModule, MatFormFieldModule, MatSelectModule],
  providers: [
    ClassificationDefinitionService,
    WorkbasketDefinitionService,
    SavingWorkbasketService,
    ClassificationCategoriesService,
    ImportExportService
  ]
})
export class AdministrationModule {}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { AlertModule, TypeaheadModule } from 'ngx-bootstrap';
import { SharedModule } from 'app/shared/shared.module';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { ClassificationTypesSelectorComponent } from 'app/shared/classification-types-selector/classification-types-selector.component';
import { ClassificationCategoriesService } from 'app/shared/services/classifications/classification-categories.service';
import { AccessItemsManagementComponent } from 'app/administration/components/access-items-management/access-items-management.component';
import { AdministrationRoutingModule } from './administration-routing.module';
import { TreeModule } from 'angular-tree-component';
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
import { ClassificationTreeComponent } from './components/classification-tree/tree.component';

/**
 * Services
 */
import { SavingWorkbasketService } from './services/saving-workbaskets.service';
import { ClassificationDefinitionService } from './services/classification-definition.service';
import { WorkbasketDefinitionService } from './services/workbasket-definition.service';
import { ImportExportService } from './services/import-export.service';
import { TreeService } from './services/tree.service';

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
  TreeModule.forRoot()
];

const DECLARATIONS = [
  WorkbasketListComponent,
  WorkbasketListToolbarComponent,
  WorkbasketAccessItemsComponent,
  WorkbasketDetailsComponent,
  WorkbasketInformationComponent,
  WorkbasketDistributionTargetsComponent,
  WorkbasketDualListComponent,
  ClassificationListComponent,
  ImportExportComponent,
  ClassificationTypesSelectorComponent,
  ClassificationDetailsComponent,
  AccessItemsManagementComponent,
  ClassificationTreeComponent
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
    TreeService
  ]
})
export class AdministrationModule {
}

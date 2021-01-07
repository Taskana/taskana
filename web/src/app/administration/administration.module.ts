import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { AlertModule, TypeaheadModule } from 'ngx-bootstrap';
import { SharedModule } from 'app/shared/shared.module';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';

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
import { WorkbasketDistributionTargetsListComponent } from './components/workbasket-distribution-targets-list/workbasket-distribution-targets-list.component';
import { WorkbasketAccessItemsComponent } from './components/workbasket-access-items/workbasket-access-items.component';
import { ClassificationListComponent } from './components/classification-list/classification-list.component';
import { ClassificationDetailsComponent } from './components/classification-details/classification-details.component';
import { ImportExportComponent } from './components/import-export/import-export.component';
import { AdministrationOverviewComponent } from './components/administration-overview/administration-overview.component';

import { ClassificationOverviewComponent } from './components/classification-overview/classification-overview.component';
import { WorkbasketOverviewComponent } from './components/workbasket-overview/workbasket-overview.component';
/**
 * Services
 */
import { SavingWorkbasketService } from './services/saving-workbaskets.service';
import { ClassificationDefinitionService } from './services/classification-definition.service';
import { WorkbasketDefinitionService } from './services/workbasket-definition.service';
import { ImportExportService } from './services/import-export.service';

/**
 * Material Design
 */
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRippleModule } from '@angular/material/core';
import { MatTableModule } from '@angular/material/table';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';

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
  WorkbasketDistributionTargetsListComponent,
  ClassificationOverviewComponent,
  ClassificationListComponent,
  ClassificationTypesSelectorComponent,
  ClassificationDetailsComponent,
  ImportExportComponent,
  AccessItemsManagementComponent,
  AdministrationOverviewComponent
];

@NgModule({
  declarations: DECLARATIONS,
  imports: [
    MODULES,
    MatRadioModule,
    MatFormFieldModule,
    MatSelectModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    MatTabsModule,
    MatInputModule,
    MatTooltipModule,
    MatDividerModule,
    MatListModule,
    MatProgressBarModule,
    MatToolbarModule,
    MatCheckboxModule,
    MatRippleModule,
    MatTableModule,
    MatDialogModule,
    MatExpansionModule
  ],
  providers: [
    ClassificationDefinitionService,
    WorkbasketDefinitionService,
    SavingWorkbasketService,
    ClassificationCategoriesService,
    ImportExportService
  ],
  entryComponents: []
})
export class AdministrationModule {}

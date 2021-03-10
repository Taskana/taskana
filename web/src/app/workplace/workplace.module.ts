import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { AlertModule } from 'ngx-bootstrap/alert';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { SharedModule } from 'app/shared/shared.module';
import { ClassificationCategoriesService } from 'app/shared/services/classification-categories/classification-categories.service';
import { WorkplaceRoutingModule } from './workplace-routing.module';

import { TaskListToolbarComponent } from './components/task-list-toolbar/task-list-toolbar.component';
import { TaskMasterComponent } from './components/task-master/task-master.component';
import { TaskdetailsComponent } from './components/taskdetails/taskdetails.component';
import { TaskdetailsGeneralFieldsComponent } from './components/taskdetails-general/general-fields.component';
import { TaskdetailsCustomFieldsComponent } from './components/taskdetails-custom-fields/custom-fields.component';
import { TaskdetailsAttributeComponent } from './components/taskdetails-attribute/attribute.component';
import { TaskComponent } from './components/task/task.component';
import { GeneralFieldsExtensionComponent } from './components/taskdetails-general-fields-extension/general-fields-extension.component';
import { TaskListComponent } from './components/task-list/task-list.component';

import { TaskService } from './services/task.service';
import { TokenInterceptor } from './services/token-interceptor.service';
import { WorkplaceService } from './services/workplace.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatRadioModule } from '@angular/material/radio';
import { MatMenuModule } from '@angular/material/menu';

const MODULES = [
  TypeaheadModule.forRoot(),
  AccordionModule.forRoot(),
  BsDropdownModule.forRoot(),
  CommonModule,
  FormsModule,
  HttpClientModule,
  AngularSvgIconModule,
  WorkplaceRoutingModule,
  AlertModule,
  SharedModule,
  MatFormFieldModule,
  MatAutocompleteModule,
  MatInputModule
];

const DECLARATIONS = [
  TaskListToolbarComponent,
  TaskMasterComponent,
  TaskdetailsComponent,
  TaskdetailsGeneralFieldsComponent,
  TaskdetailsCustomFieldsComponent,
  TaskdetailsAttributeComponent,
  TaskComponent,
  GeneralFieldsExtensionComponent,
  TaskListComponent
];

@NgModule({
  declarations: DECLARATIONS,
  imports: [
    MODULES,
    MatListModule,
    MatBadgeModule,
    MatTooltipModule,
    MatIconModule,
    MatButtonModule,
    MatRadioModule,
    MatMenuModule
  ],
  providers: [
    TaskService,
    ClassificationCategoriesService,
    WorkplaceService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    }
  ]
})
export class WorkplaceModule {}

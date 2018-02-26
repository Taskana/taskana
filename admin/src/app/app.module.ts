/**
 * Modules
 */
import { BrowserModule } from '@angular/platform-browser';
import { NgModule,  } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AlertModule } from 'ngx-bootstrap';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { TreeModule } from 'angular-tree-component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

/**
 * Components
 */
import { AppComponent } from './app.component';
import { WorkbasketListComponent } from './workbasket/list/workbasket-list.component';
import { CategorieslistComponent } from './categorieslist/categorieslist.component';
import { CategoriestreeComponent } from './categoriestree/categoriestree.component';
import { CategoryeditorComponent } from './categoryeditor/categoryeditor.component';
import { CategoriesadministrationComponent } from './categoriesadministration/categoriesadministration.component';
import { WorkbasketAuthorizationComponent } from './workbasket-authorization/workbasket-authorization.component';
import { WorkbasketDistributiontargetsComponent } from './workbasket-distributiontargets/workbasket-distributiontargets.component';
import { WorkbasketDetailsComponent } from './workbasket/details/workbasket-details.component';
import { WorkbasketInformationComponent } from './workbasket/details/information/workbasket-information.component';
import { NoAccessComponent } from './workbasket/noAccess/no-access.component';
import { SpinnerComponent } from './shared/spinner/spinner.component';
import { FilterComponent } from './shared/filter/filter.component';
import { IconTypeComponent } from './shared/type-icon/icon-type.component';
import { AlertComponent } from './shared/alert/alert.component';
import { GeneralMessageModalComponent } from './shared/general-message-modal/general-message-modal.component';

//Shared
import { MasterAndDetailComponent} from './shared/masterAndDetail/master-and-detail.component';

/**
 * Services
 */
import { WorkbasketService } from './services/workbasket.service';
import { MasterAndDetailService } from './services/master-and-detail.service';
import { HttpClientInterceptor } from './services/http-client-interceptor.service';
import { PermissionService } from './services/permission.service';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AlertService } from './services/alert.service';

/**
 * Pipes
 */
import { MapValuesPipe } from './pipes/map-values.pipe';
import { RemoveNoneTypePipe } from './pipes/remove-none-type';

const MODULES =     [
                    BrowserModule,
                    FormsModule,
                    TabsModule.forRoot(),
                    TreeModule,
                    AppRoutingModule,
                    AlertModule.forRoot(),
                    AngularSvgIconModule,
                    HttpClientModule,
                    BrowserAnimationsModule
                    ];

const DECLARATIONS =  [  
                      AppComponent,
                      WorkbasketListComponent,
                      CategorieslistComponent,
                      CategoriestreeComponent,
                      CategoryeditorComponent,
                      CategoriesadministrationComponent,
                      WorkbasketAuthorizationComponent,
                      WorkbasketDetailsComponent,
                      WorkbasketDistributiontargetsComponent,
                      MasterAndDetailComponent,
                      WorkbasketInformationComponent,
                      NoAccessComponent,
                      SpinnerComponent,
                      FilterComponent,
                      IconTypeComponent,
                      AlertComponent,
                      GeneralMessageModalComponent,
                      MapValuesPipe,
                      RemoveNoneTypePipe
                    ];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [
    WorkbasketService,
    MasterAndDetailService,
    PermissionService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpClientInterceptor,
      multi: true
    },
    AlertService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

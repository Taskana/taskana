/**
 * Modules
 */
import { BrowserModule } from '@angular/platform-browser';
import { NgModule,  } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule, JsonpModule, Http } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AlertModule } from 'ngx-bootstrap';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { TreeModule } from 'angular-tree-component';

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

//Shared
import { MasterAndDetailComponent} from './shared/masterAndDetail/master-and-detail.component';

/**
 * Services
 */
import { WorkbasketService } from './services/workbasketservice.service';
import { MasterAndDetailService } from './services/master-and-detail.service';
import { HttpExtensionService } from './services/http-extension.service';
import { PermissionService } from './services/permission.service';


const MODULES =     [
                    BrowserModule,
                    FormsModule,
                    HttpModule,
                    JsonpModule,
                    TabsModule.forRoot(),
                    TreeModule,
                    AppRoutingModule,
                    AlertModule.forRoot(),
                    AngularSvgIconModule,
                    HttpClientModule
                    ];

const COMPONENTS =  [  
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
                      NoAccessComponent
                    ];
@NgModule({
  declarations: COMPONENTS,
  imports: MODULES,
  providers: [
    WorkbasketService,
    MasterAndDetailService,
    PermissionService,
    { provide: Http, useClass: HttpExtensionService }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

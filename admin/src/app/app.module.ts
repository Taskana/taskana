/**
 * Modules
 */
import { BrowserModule } from '@angular/platform-browser';
import { NgModule,  } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule, JsonpModule } from '@angular/http';
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
import { WorkbasketeditorComponent } from './workbasketeditor/workbasketeditor.component';
import { CategorieslistComponent } from './categorieslist/categorieslist.component';
import { CategoriestreeComponent } from './categoriestree/categoriestree.component';
import { CategoryeditorComponent } from './categoryeditor/categoryeditor.component';
import { CategoriesadministrationComponent } from './categoriesadministration/categoriesadministration.component';
import { WorkbasketadministrationComponent } from './workbasketadministration/workbasketadministration.component';
import { WorkbasketAuthorizationComponent } from './workbasket-authorization/workbasket-authorization.component';
import { WorkbasketDetailsComponent } from './workbasket-details/workbasket-details.component';
import { WorkbasketDistributiontargetsComponent } from './workbasket-distributiontargets/workbasket-distributiontargets.component';
//Shared
import { MasterAndDetailComponent} from './shared/masterAndDetail/master-and-detail.component';


/**
 * Services
 */

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
                      WorkbasketeditorComponent,
                      CategorieslistComponent,
                      CategoriestreeComponent,
                      CategoryeditorComponent,
                      CategoriesadministrationComponent,
                      WorkbasketadministrationComponent,
                      WorkbasketAuthorizationComponent,
                      WorkbasketDetailsComponent,
                      WorkbasketDistributiontargetsComponent,
                      MasterAndDetailComponent
                    ];
@NgModule({
  declarations: COMPONENTS,
  imports: MODULES,
  providers: [HttpClientModule],
  bootstrap: [AppComponent]
})
export class AppModule { }

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { AlertModule } from 'ngx-bootstrap';
import { ChartsModule } from 'ng2-charts';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { HttpClientModule } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';

import { AppComponent } from './app.component';
import { TasksComponent } from './tasks/tasks.component';
import { WorkbasketComponent } from './workbasket/workbasket.component';
import { Report } from "./report/report.component";
import { MapToIterable } from "./pipes/mapToIterable";
import { OrderBy } from "./pipes/orderBy";

@NgModule({
  declarations: [
    AppComponent,
    TasksComponent,
    WorkbasketComponent,
    Report,
    MapToIterable,
    OrderBy

  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AlertModule.forRoot(),
    ChartsModule,
    TabsModule.forRoot(),
    HttpClientModule,
    AngularSvgIconModule
  ],
  providers: [HttpClientModule],
  bootstrap: [AppComponent]
})
export class AppModule {
}

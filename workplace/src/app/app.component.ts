import { Component, Input } from '@angular/core';
import { DataService } from './services/data.service';
import { RestConnectorService } from './services/rest-connector.service';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [ DataService, RestConnectorService ]
})
export class AppComponent {
  
  workplaceUrl: string = environment.taskanaWorkplaceUrl;
  adminUrl: string = environment.taskanaAdminUrl;
  monitorUrl: string = environment.taskanaMonitorUrl;

}

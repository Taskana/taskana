import {Component} from '@angular/core';
import {DataService} from './services/data.service';
import {environment} from '../environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [DataService]
})
export class AppComponent {

  workplaceUrl: string = environment.taskanaWorkplaceUrl;
  adminUrl: string = environment.taskanaAdminUrl;
  monitorUrl: string = environment.taskanaMonitorUrl;

}

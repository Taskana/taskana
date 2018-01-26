import { Component } from '@angular/core';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  monitorUrl: string = environment.taskanaMonitorUrl;
  adminUrl: string = environment.taskanaAdminUrl;
  workplaceUrl: string = environment.taskanaWorkplaceUrl;
}

import { Component, Input } from '@angular/core';
import { DataService } from './services/data.service';
import { RestConnectorService } from './services/rest-connector.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [ DataService, RestConnectorService ]
})
export class AppComponent {
}

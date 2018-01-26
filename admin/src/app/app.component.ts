import { Component, OnInit } from '@angular/core';
import { environment } from '../environments/environment';
import { Router, ActivatedRoute, NavigationStart } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{
  title = 'Taskana administration';

  adminUrl: string = environment.taskanaAdminUrl;
  monitorUrl: string = environment.taskanaMonitorUrl;
  workplaceUrl: string = environment.taskanaWorkplaceUrl;
  workbasketsRoute : boolean = true;

  constructor( private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(){
    this.router.events.subscribe(event => {
      if(event instanceof NavigationStart) {  
        if(event.url.indexOf('categories') !== -1){
          this.workbasketsRoute = false;
        }
      }
    });
  }
}

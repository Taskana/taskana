import { Component, OnInit } from '@angular/core';
import { SelectedRouteService } from 'app/shared/services/selected-route/selected-route';
import { Subscription } from 'rxjs';
import { expandRight } from 'app/shared/animations/expand.animation';
import { SidenavService } from '../../services/sidenav/sidenav.service';

@Component({
  selector: 'taskana-shared-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss'],
  animations: [expandRight]
})
export class NavBarComponent implements OnInit {
  selectedRoute = '';
  titleWorkbaskets = 'Workbaskets';
  titleClassifications = 'Classifications';
  titleAccessItems = 'Access items';
  titleMonitor = 'Monitor';
  titleWorkplace = 'Workplace';
  titleHistory = 'History';
  titleSettings = 'Settings';
  toggle: boolean = false;
  title = this.titleWorkplace;

  selectedRouteSubscription: Subscription;

  constructor(private selectedRouteService: SelectedRouteService, private sidenavService: SidenavService) {}

  ngOnInit() {
    this.selectedRouteSubscription = this.selectedRouteService.getSelectedRoute().subscribe((value: string) => {
      // does not work
      this.selectedRoute = value;
      this.setTitle(value);
    });
  }

  toggleSidenav() {
    this.toggle = !this.toggle;
    this.sidenavService.toggleSidenav();
  }

  setTitle(value: string = '') {
    if (value.includes('workbaskets')) {
      this.title = this.titleWorkbaskets;
    } else if (value.includes('classifications')) {
      this.title = this.titleClassifications;
    } else if (value.includes('monitor')) {
      this.title = this.titleMonitor;
    } else if (value.includes('workplace')) {
      this.title = this.titleWorkplace;
    } else if (value.includes('access-items')) {
      this.title = this.titleAccessItems;
    } else if (value.includes('history')) {
      this.title = this.titleHistory;
    } else if (value.includes('settings')) {
      this.title = this.titleSettings;
    }
  }
}

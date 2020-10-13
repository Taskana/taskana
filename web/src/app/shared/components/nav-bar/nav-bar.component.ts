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
  title = '';

  titleAdministration = 'Administration';
  titleWorkbaskets = 'Workbaskets';
  titleClassifications = 'Classifications';
  titleAccessItems = 'Access items';
  titleMonitor = 'Monitor';
  titleWorkplace = 'Workplace';
  titleHistory = 'History';
  showTitle: boolean = true;
  toggle: boolean = false;

  selectedRouteSubscription: Subscription;

  constructor(private selectedRouteService: SelectedRouteService, private sidenavService: SidenavService) {}

  ngOnInit() {
    this.selectedRouteSubscription = this.selectedRouteService.getSelectedRoute().subscribe((value: string) => {
      this.selectedRoute = value;
      this.setTitle(value);
    });
  }

  toggleSidenav() {
    this.toggle = !this.toggle;
    this.sidenavService.toggle_sidenav();
  }

  setTitle(value: string = 'workbaskets') {
    if (value.indexOf('workbaskets') === 0) {
      this.title = this.titleWorkbaskets;
    } else if (value.indexOf('classifications') === 0) {
      this.title = this.titleClassifications;
    } else if (value.indexOf('monitor') === 0) {
      this.title = this.titleMonitor;
    } else if (value.indexOf('workplace') === 0) {
      this.title = this.titleWorkplace;
    } else if (value.indexOf('access-items') === 0) {
      this.title = this.titleAccessItems;
    } else if (value.indexOf('history') === 0) {
      this.title = this.titleHistory;
    }
  }
}

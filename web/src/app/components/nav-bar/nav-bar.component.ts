import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from 'environments/environment';
import { SelectedRouteService } from 'app/services/selected-route/selected-route';
import { Subscription } from 'rxjs';
import { DomainService } from 'app/services/domain/domain.service';
import { BusinessAdminGuard } from 'app/guards/business-admin-guard';
import { MonitorGuard } from 'app/guards/monitor-guard';
import { WindowRefService } from 'app/services/window/window.service';
import { UserGuard } from 'app/guards/user-guard';
import { TaskanaEngineService } from '../../services/taskana-engine/taskana-engine.service';
import { expandRight } from 'app/shared/animations/expand.animation';
@Component({
  selector: 'taskana-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss'],
  animations: [expandRight],
})
export class NavBarComponent implements OnInit, OnDestroy {

  selectedRoute = '';
  route: string;
  title = '';

  titleAdministration = 'Administration';
  titleWorkbaskets = 'Workbaskets';
  titleClassifications = 'Classifications';
  titleAccessItems = 'Access items';
  titleMonitor = 'Monitor';
  titleWorkplace = 'Workplace';
  showNavbar = false;
  domains: Array<string> = [];
  selectedDomain: string;
  version: string;

  adminUrl = './administration';
  monitorUrl = './monitor';
  workplaceUrl = './workplace';

  administrationAccess = false;
  monitorAccess = false;
  workplaceAccess = false;

  selectedRouteSubscription: Subscription;
  getDomainsSubscription: Subscription;

  constructor(
    private selectedRouteService: SelectedRouteService,
    private domainService: DomainService,
    private taskanaEngineService: TaskanaEngineService,
    private window: WindowRefService) { }

  ngOnInit() {
    this.selectedRouteSubscription = this.selectedRouteService.getSelectedRoute().subscribe((value: string) => {
      this.selectedRoute = value;
      this.setTitle(value);
    });
    this.getDomainsSubscription = this.domainService.getDomains().subscribe(domains => {
      this.domains = domains;
    });

    this.domainService.getSelectedDomain().subscribe(domain => {
      this.selectedDomain = domain;
    });

    this.taskanaEngineService.getVersion().subscribe(restVersion => {
      this.version = restVersion.version;
    });

    this.administrationAccess = this.taskanaEngineService.hasRole(BusinessAdminGuard.roles);
    this.monitorAccess = this.taskanaEngineService.hasRole(MonitorGuard.roles);
    this.workplaceAccess = this.taskanaEngineService.hasRole(UserGuard.roles);
  }

  switchDomain(domain) {
    this.domainService.switchDomain(domain);
  }

  toogleNavBar() {
    this.showNavbar = !this.showNavbar;
  }


  logout() {
    this.taskanaEngineService.logout().subscribe(() => {
    })
    this.window.nativeWindow.location.href = environment.taskanaRestUrl + '/logout';
  }

  showDomainSelector(): boolean {
    return this.selectedRoute.indexOf('administration') !== -1
      || this.selectedRoute.indexOf('workbaskets') !== -1
      || this.selectedRoute.indexOf('classifications') !== -1
  }

  private setTitle(value: string = 'workbaskets') {
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
    }
  }

  ngOnDestroy(): void {
    if (this.selectedRouteSubscription) { this.selectedRouteSubscription.unsubscribe(); }
    if (this.getDomainsSubscription) { this.getDomainsSubscription.unsubscribe(); }
  }
}

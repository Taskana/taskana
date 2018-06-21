import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from 'environments/environment';
import { SelectedRouteService } from 'app/services/selected-route/selected-route';
import { Subscription } from 'rxjs/Subscription';
import { trigger, state, style, transition, keyframes, animate } from '@angular/animations';
import { DomainService } from 'app/services/domain/domain.service';
import { BusinessAdminGuard } from 'app/guards/business-admin-guard';
import { MonitorGuard } from 'app/guards/monitor-guard';
import { WindowRefService } from 'app/services/window/window.service';
import { UserGuard } from 'app/guards/user-guard';
import { TaskanaEngineService } from '../../services/taskana-engine/taskana-engine.service';
@Component({
  selector: 'taskana-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss'],
  animations: [
    trigger('toggle', [
      transition('void => *', animate('300ms ease-in', keyframes([
        style({ opacity: 0, width: '0px' }),
        style({ opacity: 1, width: '150px' }),
        style({ opacity: 1, width: '*' })]))),
      transition('* => void', animate('300ms ease-out', keyframes([
        style({ opacity: 1, width: '*' }),
        style({ opacity: 0, width: '150px' }),
        style({ opacity: 0, width: '0px' })])))
    ]
    )],
})
export class NavBarComponent implements OnInit, OnDestroy {

  selectedRoute = '';
  route: string;
  title = '';

  titleAdministration = 'Administration';
  titleWorkbaskets = 'Workbaskets';
  titleClassifications = 'Classifications';
  titleMonitor = 'Monitor';
  titleWorkplace = 'Workplace';
  showNavbar = false;
  domains: Array<string> = [];
  selectedDomain: string;

  adminUrl = './administration';
  monitorUrl = './monitor';
  workplaceUrl = './workplace';

  administrationAccess = false;
  monitorAccess = false;
  workplaceAccess = false;

  selectedRouteSubscription: Subscription;

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
    this.domainService.getDomains().subscribe(domains => {
      this.domains = domains;
    });

    this.domainService.getSelectedDomain().subscribe(domain => {
      this.selectedDomain = domain;
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
    this.window.nativeWindow.location.href = environment.taskanaRestUrl + '/login?logout';
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
    }
  }

  ngOnDestroy(): void {
    if (this.selectedRouteSubscription) { this.selectedRouteSubscription.unsubscribe(); }
  }
}

import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from 'environments/environment';
import { SelectedRouteService } from 'app/services/selected-route/selected-route';
import { Subscription } from 'rxjs/Subscription';
import { trigger, state, style, transition, keyframes, animate } from '@angular/animations';
import { DomainService } from 'app/services/domain/domain.service';
import { RolesGuard } from 'app/guards/roles-guard';
import { WindowRefService } from 'app/services/window/window.service';
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
  titleAdministration = 'Administration';
  titleMonitor = 'Monitor';
  titleWorkplace = 'Workplace';
  title = 'Administration';
  showNavbar = false;
  domains: Array<string> = [];
  selectedDomain: string;

  adminUrl = './administration';
  monitorUrl = './monitor';
  workplaceUrl = './workplace';

  administrationAccess = false;

  selectedRouteSubscription: Subscription;

  constructor(
    private selectedRouteService: SelectedRouteService,
    private domainService: DomainService,
    private roleGuard: RolesGuard,
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

    this.roleGuard.canActivate().subscribe(hasAccess => {
      this.administrationAccess = hasAccess
    });
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

  private setTitle(value: string = 'workbaskets') {
    if (value.indexOf('workbaskets') === 0 || value.indexOf('classifications') === 0) {
      this.title = this.titleAdministration;
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

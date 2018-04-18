import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from 'environments/environment';
import { SelectedRouteService } from 'app/services/selected-route/selected-route';
import { Subscription } from 'rxjs/Subscription';
import { trigger, state, style, transition, keyframes, animate } from '@angular/animations';
import { DomainService } from 'app/services/domain/domain.service';

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
  title = 'Taskana administration';
  showNavbar = false;
  domains: Array<string> = [];
  selectedDomain: string;

  adminUrl: string = environment.taskanaAdminUrl;
  monitorUrl: string = environment.taskanaMonitorUrl;
  workplaceUrl: string = environment.taskanaWorkplaceUrl;

  selectedRouteSubscription: Subscription;

  constructor(
    private selectedRouteService: SelectedRouteService,
    private domainService: DomainService) { }

  ngOnInit() {
    this.selectedRouteSubscription = this.selectedRouteService.getSelectedRoute().subscribe((value: string) => {
      this.selectedRoute = value;
    });
    this.domainService.getDomains().subscribe(domains => {
      this.domains = domains;
    });

    this.domainService.getSelectedDomain().subscribe(domain => {
      this.selectedDomain = domain;
    });
  }

  selectDomain(domain) {
    this.domainService.selectDomain(domain);
  }

  toogleNavBar() {
    this.showNavbar = !this.showNavbar;
  }

  ngOnDestroy(): void {
    if (this.selectedRouteSubscription) { this.selectedRouteSubscription.unsubscribe(); }
  }
}

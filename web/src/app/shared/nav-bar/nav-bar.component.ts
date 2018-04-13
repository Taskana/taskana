import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from 'environments/environment';
import { SelectedRouteService } from 'app/services/selected-route/selected-route';
import { Subscription } from 'rxjs/Subscription';
import { trigger, state, style, transition, keyframes, animate } from '@angular/animations';

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

  adminUrl: string = environment.taskanaAdminUrl;
  monitorUrl: string = environment.taskanaMonitorUrl;
  workplaceUrl: string = environment.taskanaWorkplaceUrl;

  selectedRouteSubscription: Subscription

  constructor(private selectedRouteService: SelectedRouteService) { }

  ngOnInit() {
    this.selectedRouteSubscription = this.selectedRouteService.getSelectedRoute().subscribe((value: string) => {
      this.selectedRoute = value;
    })
  }

  ngOnDestroy(): void {
    if (this.selectedRouteSubscription) { this.selectedRouteSubscription.unsubscribe(); }
  }

  toogleNavBar() {
    this.showNavbar = !this.showNavbar;
  }

}

import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Router } from '@angular/router';

@Injectable()
export class SelectedRouteService {
  public selectedRouteTriggered = new Subject<string>();

  private detailRoutes: Array<string> = [
    'workplace',
    'administration/workbaskets',
    'administration/classifications',
    'monitor',
    'administration/access-items-management',
    'history',
    'settings'
  ];

  constructor(private router: Router) {}

  selectRoute(value) {
    this.selectedRouteTriggered.next(this.getRoute(value));
  }

  getSelectedRoute(): Observable<string> {
    return this.selectedRouteTriggered.asObservable();
  }

  private getRoute(event): string {
    if (!event) {
      return this.checkUrl(this.router.url);
    }
    return this.checkUrl(event.url);
  }

  private checkUrl(url: string): string {
    return this.detailRoutes.find((routeDetail) => url.includes(routeDetail)) || '';
  }
}

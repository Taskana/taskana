import { Injectable, OnInit } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { Router, ActivatedRoute, NavigationStart } from '@angular/router';

@Injectable()
export class SelectedRouteService {
  public selectedRouteTriggered = new Subject<string>();

  private detailRoutes: Array<string> = ['workbaskets', 'classifications', 'monitor', 'workplace', 'access-items-management', 'history'];

  constructor(private router: Router) { }


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
    return this.detailRoutes.find(routeDetail => url.indexOf(routeDetail) !== -1) || '';
  }
}

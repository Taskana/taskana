import { Injectable, OnInit } from '@angular/core';
import { Subject ,  Observable } from 'rxjs';
import { Router, ActivatedRoute, NavigationStart } from '@angular/router';

@Injectable()
export class SelectedRouteService {

    public selectedRouteTriggered = new Subject<string>();

    private detailRoutes: Array<string> = ['workbaskets', 'classifications', 'monitor', 'workplace'];

    constructor(private router: Router) { }


    selectRoute(value) {
        this.selectedRouteTriggered.next(this.getRoute(value));
    }

    getSelectedRoute(): Observable<string> {
        return this.selectedRouteTriggered.asObservable();
    }

    private getRoute(event): string {
        if (event === undefined) {
            return this.checkUrl(this.router.url);
        }
        return this.checkUrl(event.url)
    }

    private checkUrl(url: string): string {
        for (const routeDetail of this.detailRoutes) {
            if (url.indexOf(routeDetail) !== -1) {
                return routeDetail;
            }
        }
        return '';
    }
}

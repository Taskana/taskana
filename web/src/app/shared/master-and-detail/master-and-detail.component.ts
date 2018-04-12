import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, Routes, ActivatedRoute, NavigationStart, RouterEvent } from '@angular/router';
import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service'

@Component({
    selector: 'taskana-master-and-detail',
    templateUrl: './master-and-detail.component.html',
    styleUrls: ['./master-and-detail.component.scss'],

})
export class MasterAndDetailComponent implements OnInit {
    private classifications = 'classifications';
    private workbaskets = 'workbaskets';
    private detailRoutes: Array<string> = ['/workbaskets/(detail', 'classifications/(detail'];
    private sub: any;

    showDetail: Boolean = false;
    currentRoute = '';
    constructor(private route: ActivatedRoute, private router: Router, private masterAndDetailService: MasterAndDetailService) {
    }

    ngOnInit(): void {
        this.showDetail = this.showDetails();
        this.masterAndDetailService.setShowDetail(this.showDetail ? true : false);
        this.router.events.subscribe(event => {
            if (event instanceof NavigationStart) {
                this.showDetail = this.showDetails(event);
                this.masterAndDetailService.setShowDetail(this.showDetail ? true : false);
            }
        });
    }

    backClicked(): void {
        this.router.navigate(['../'], { relativeTo: this.route });
    }

    private showDetails(event?: RouterEvent): Boolean {
        if (event === undefined) {
            return this.checkUrl(this.router.url);
        }
        return this.checkUrl(event.url)
    }

    private checkUrl(url: string): Boolean {
        this.checkRoute(url);
        for (const routeDetail of this.detailRoutes) {
            if (url.indexOf(routeDetail) !== -1) {
                return true;
            }
        }
        return false;
    }

    private checkRoute(url: string) {
        if (url.indexOf(this.workbaskets) !== -1) {
            this.currentRoute = this.workbaskets;
        } else if (url.indexOf(this.classifications) !== -1) {
            this.currentRoute = this.classifications;
        }
    }
}

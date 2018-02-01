import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, Routes,  ActivatedRoute, NavigationStart,  RouterEvent } from '@angular/router';

@Component({
    selector: 'master-and-detail',
    templateUrl: './master-and-detail.component.html',
    styleUrls: ['./master-and-detail.component.scss'],

})
export class MasterAndDetailComponent implements OnInit{
    private detailRoutes: Array<string> = ['/workbaskets/(detail', 'clasifications'];
    private sub: any;

    showDetail: Boolean = false;
    constructor(private route: ActivatedRoute, private router: Router){
    }

    ngOnInit(): void {
        this.showDetail = this.showDetails();
        this.router.events.subscribe(event => {
            if(event instanceof NavigationStart) { 
                this.showDetail = this.showDetails(event);
            }
        });
    }

    backClicked(): void {
        this.router.navigate(['../'], { relativeTo: this.route });
    }

    private showDetails(event? : RouterEvent): Boolean {
        if(event === undefined) {
            return this.checkUrl(this.router.url);
        }
        return this.checkUrl(event.url)
    }

    private checkUrl(url: string): Boolean {
        for(let routeDetail of this.detailRoutes){
            if(url.indexOf(routeDetail) !== -1){
                return true;
            }    
        }
        return false;
    }
}
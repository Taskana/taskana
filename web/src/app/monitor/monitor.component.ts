import { Component, OnInit, HostListener, OnDestroy } from '@angular/core';


@Component({
    selector: 'taskana-monitor',
    templateUrl: './monitor.component.html',
    styleUrls: ['./monitor.component.scss']
})
export class MonitorComponent implements OnInit, OnDestroy {

    tabSelected = 'tasks';

    ngOnInit(): void {
    }
    ngOnDestroy(): void {
    }

    selectTab(tab) {
        this.tabSelected = tab;
    }
}

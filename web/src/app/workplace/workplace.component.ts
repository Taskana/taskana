import { Component, OnInit, HostListener, OnDestroy } from '@angular/core';
import { RestConnectorService } from './services/rest-connector.service';

@Component({
    selector: 'taskana-workplace',
    templateUrl: './workplace.component.html'
})
export class WorkplaceComponent implements OnInit, OnDestroy {
    constructor() { }
    ngOnInit(): void {
    }
    ngOnDestroy(): void {
    }
}

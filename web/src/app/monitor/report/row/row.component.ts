import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ReportInfoDataIterable} from "../../models/report-info-data";

@Component({
    selector: '[monitor-report-row]',
    templateUrl: './row.component.html',
    styleUrls: ['./row.component.scss']
})
export class ReportRowComponent implements OnInit {

    @Output()
    expand: EventEmitter<boolean> = new EventEmitter<boolean>();
    @Input()
    headers: Array<string>;
    expanded = false;
    foldable: boolean;

    constructor() {
    }

    private _row: ReportInfoDataIterable;

    get row(): ReportInfoDataIterable {
        return this._row;
    }

    @Input() set row(row: ReportInfoDataIterable) {
        this._row = row;
        this.foldable = !!row.val.foldableRows;
    }

    ngOnInit() {
    }

    toggleFold() {
        if (this.foldable) {
            this.expanded = !this.expanded;
            this.expand.emit(this.expanded);
        }
    }
}

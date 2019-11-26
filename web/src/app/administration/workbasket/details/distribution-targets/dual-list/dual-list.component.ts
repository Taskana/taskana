import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { FilterModel } from 'app/models/filter';
import { Side } from '../distribution-targets.component';
import { expandDown } from 'app/shared/animations/expand.animation';

@Component({
  selector: 'taskana-dual-list',
  templateUrl: './dual-list.component.html',
  styleUrls: ['./dual-list.component.scss'],
  animations: [expandDown]
})
export class DualListComponent implements OnInit {

  @Input() distributionTargets: Array<WorkbasketSummary>;
  @Input() distributionTargetsSelected: Array<WorkbasketSummary>;
  @Output() performDualListFilter = new EventEmitter<{ filterBy: FilterModel, side: Side }>();
  @Input() requestInProgress = false;
  @Input() loadingItems ? = false;
  @Input() side: Side;
  @Input() header: string;
  @Output() scrolling = new EventEmitter<Side>();
  @Input() allSelected;
  @Output() allSelectedChange = new EventEmitter<boolean>();

  sideNumber = 0;
  toolbarState = false;

  ngOnInit() {
    this.sideNumber = this.side === Side.LEFT ? 0 : 1;
  }

  selectAll(selected: boolean) {
    this.distributionTargets.forEach((element: any) => {
      element.selected = selected;
    });
    this.allSelectedChange.emit(this.allSelected);
  }

  onScroll() {
    this.scrolling.emit(this.side);
  }

  performAvailableFilter(filterModel: FilterModel) {
    this.performDualListFilter.emit({ filterBy: filterModel, side: this.side });
  }

  changeToolbarState(state: boolean) {
    this.toolbarState = state;
  }

}

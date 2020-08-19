import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { Filter } from 'app/shared/models/filter';
import { expandDown } from 'app/shared/animations/expand.animation';
import { Side } from '../workbasket-distribution-targets/workbasket-distribution-targets.component';

@Component({
  selector: 'taskana-administration-workbasket-dual-list',
  templateUrl: './workbasket-dual-list.component.html',
  styleUrls: ['./workbasket-dual-list.component.scss'],
  animations: [expandDown]
})
export class WorkbasketDualListComponent implements OnInit {
  @Input() distributionTargets: WorkbasketSummary[];
  @Input() distributionTargetsSelected: WorkbasketSummary[];
  @Output() performDualListFilter = new EventEmitter<{ filterBy: Filter; side: Side }>();
  @Input() requestInProgress = false;
  @Input() loadingItems? = false;
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

  performAvailableFilter(filterModel: Filter) {
    this.performDualListFilter.emit({ filterBy: filterModel, side: this.side });
  }

  changeToolbarState(state: boolean) {
    this.toolbarState = state;
  }
}

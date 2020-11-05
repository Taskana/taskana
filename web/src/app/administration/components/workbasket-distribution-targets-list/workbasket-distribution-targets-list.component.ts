import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { Filter } from 'app/shared/models/filter';
import { expandDown } from 'app/shared/animations/expand.animation';
import { Side } from '../workbasket-distribution-targets/workbasket-distribution-targets.component';
import { MatDialog } from '@angular/material/dialog';
import { WorkbasketDistributionTargetsListDialogComponent } from '../workbasket-distribution-targets-list-dialog/workbasket-distribution-targets-list-dialog.component';

@Component({
  selector: 'taskana-administration-workbasket-distribution-targets-list',
  templateUrl: './workbasket-distribution-targets-list.component.html',
  styleUrls: ['./workbasket-distribution-targets-list.component.scss'],
  animations: [expandDown]
})
export class WorkbasketDistributionTargetsListComponent implements OnInit {
  @Input() distributionTargets: WorkbasketSummary[];
  @Input() distributionTargetsSelected: WorkbasketSummary[];
  @Input() requestInProgress = false;
  @Input() loadingItems? = false;
  @Input() header: string;
  @Input() allSelected;

  @Output() performDualListFilter = new EventEmitter<{ filterBy: Filter; side: Side }>();
  @Output() scrolling = new EventEmitter<Side>();
  @Output() allSelectedChange = new EventEmitter<boolean>();

  sideNumber = 0;

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
}

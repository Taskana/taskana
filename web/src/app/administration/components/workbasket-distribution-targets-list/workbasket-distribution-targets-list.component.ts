import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { Filter } from 'app/shared/models/filter';
import { expandDown } from 'app/shared/animations/expand.animation';
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

  @Output() performDualListFilter = new EventEmitter<{ filterBy: Filter }>();
  @Output() scrolling = new EventEmitter<boolean>();
  @Output() allSelectedChange = new EventEmitter<boolean>();

  ngOnInit() {}

  selectAll(selected: boolean) {
    this.distributionTargets.forEach((element: any) => {
      element.selected = selected;
    });
    this.allSelectedChange.emit(this.allSelected);
  }

  onScroll() {
    this.scrolling.emit(true);
  }
  performAvailableFilter(filterModel: Filter) {
    this.performDualListFilter.emit({ filterBy: filterModel });
  }
}

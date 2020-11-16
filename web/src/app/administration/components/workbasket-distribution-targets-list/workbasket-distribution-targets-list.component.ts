import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { Filter } from 'app/shared/models/filter';
import { expandDown } from 'app/shared/animations/expand.animation';
import { takeUntil } from 'rxjs/operators';
import { WorkbasketSummaryRepresentation } from '../../../shared/models/workbasket-summary-representation';
import { TaskanaQueryParameters } from '../../../shared/util/query-parameters';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { Subject } from 'rxjs';

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
  @Input() isDialog = false;

  //distributionTargets: WorkbasketSummary[];

  @Output() performDualListFilter = new EventEmitter<{ filterBy: Filter }>();
  @Output() scrolling = new EventEmitter<boolean>();
  @Output() allSelectedChange = new EventEmitter<boolean>();

  destroy$ = new Subject<void>();

  constructor(private workbasketService: WorkbasketService) {}

  ngOnInit() {
    if (this.isDialog) {
      //this.getDistributionTargets();
    }
  }

  selectAll(selected: boolean) {
    this.distributionTargets.forEach((element: any) => {
      element.selected = selected;
    });
    this.allSelectedChange.emit(this.allSelected);
  }

  getDistributionTargets() {
    this.workbasketService
      .getWorkBasketsSummary(true)
      .pipe(takeUntil(this.destroy$))
      .subscribe((distributionTargetsAvailable: WorkbasketSummaryRepresentation) => {
        if (TaskanaQueryParameters.page === 1) {
          this.distributionTargets = [];
        }
        this.distributionTargets.push(...distributionTargetsAvailable.workbaskets);
        console.log(this.distributionTargets);
      });
  }

  onScroll() {
    this.scrolling.emit(true);
    console.log("I'M SCROLLING");
    //this.getDistributionTargets();
  }

  performAvailableFilter(filterModel: Filter) {
    this.performDualListFilter.emit({ filterBy: filterModel });
  }
}

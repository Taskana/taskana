import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter,
  AfterContentChecked,
  ChangeDetectorRef,
  ViewChild,
  OnChanges
} from '@angular/core';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { Filter } from 'app/shared/models/filter';
import { expandDown } from 'app/shared/animations/expand.animation';
import { takeUntil } from 'rxjs/operators';
import { WorkbasketSummaryRepresentation } from '../../../shared/models/workbasket-summary-representation';
import { TaskanaQueryParameters } from '../../../shared/util/query-parameters';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { Subject } from 'rxjs';
import { Side } from '../workbasket-distribution-targets/workbasket-distribution-targets.component';
import { MatSelectionList } from '@angular/material/list';

@Component({
  selector: 'taskana-administration-workbasket-distribution-targets-list',
  templateUrl: './workbasket-distribution-targets-list.component.html',
  styleUrls: ['./workbasket-distribution-targets-list.component.scss'],
  animations: [expandDown]
})
export class WorkbasketDistributionTargetsListComponent implements OnInit, AfterContentChecked {
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

  toolbarState = false;
  component = '';
  @ViewChild('workbasket') distributionTargetsList: MatSelectionList;

  constructor(private changeDetector: ChangeDetectorRef) {}

  ngOnInit() {
    this.allSelected = !this.allSelected;
  }

  ngAfterContentChecked(): void {
    this.changeDetector.detectChanges();
  }

  selectAll(selected: boolean) {
    if (typeof this.distributionTargetsList !== 'undefined') {
      this.allSelected = !this.allSelected;
      this.distributionTargetsList.options.map((item) => (item['selected'] = selected));
    }
    this.distributionTargets.map((item) => (item['selected'] = selected));
    this.allSelectedChange.emit(this.allSelected);
  }

  setComponent(component: string) {
    this.component = component;
  }

  onScroll() {
    this.scrolling.emit(this.side);
  }

  performAvailableFilter(filterModel: Filter) {
    if (this.component === 'distribution-target') {
      this.performDualListFilter.emit({ filterBy: filterModel, side: this.side });
    }
  }

  changeToolbarState(state: boolean) {
    this.toolbarState = state;
  }
}

import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter,
  AfterContentChecked,
  ChangeDetectorRef,
  ViewChild
} from '@angular/core';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { expandDown } from 'app/shared/animations/expand.animation';
import { Side } from '../workbasket-distribution-targets/workbasket-distribution-targets.component';
import { MatSelectionList } from '@angular/material/list';
import { Pair } from '../../../shared/models/pair';
import { WorkbasketQueryFilterParameter } from '../../../shared/models/workbasket-query-filter-parameter';

@Component({
  selector: 'taskana-administration-workbasket-distribution-targets-list',
  templateUrl: './workbasket-distribution-targets-list.component.html',
  styleUrls: ['./workbasket-distribution-targets-list.component.scss'],
  animations: [expandDown]
})
export class WorkbasketDistributionTargetsListComponent implements OnInit, AfterContentChecked {
  @Input() distributionTargets: WorkbasketSummary[];
  @Input() side: Side;
  @Input() header: string;
  @Input() allSelected;
  @Output() performDualListFilter = new EventEmitter<Pair<Side, WorkbasketQueryFilterParameter>>();

  toolbarState = false;
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
  }

  performAvailableFilter(pair: Pair<string, WorkbasketQueryFilterParameter>) {
    if (pair.left === 'distribution-target') {
      this.performDualListFilter.emit({ left: this.side, right: pair.right });
    }
  }

  changeToolbarState(state: boolean) {
    this.toolbarState = state;
  }
}

import { Component, OnInit, Input, AfterContentChecked, ChangeDetectorRef, ViewChild } from '@angular/core';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { expandDown } from 'app/shared/animations/expand.animation';
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
  @Input() side: Side;
  @Input() header: string;
  @Input() allSelected;
  @Input() component;

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

  changeToolbarState(state: boolean) {
    this.toolbarState = state;
  }
}

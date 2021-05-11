import {
  Component,
  Input,
  AfterContentChecked,
  ChangeDetectorRef,
  ViewChild,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { expandDown } from 'app/shared/animations/expand.animation';
import { AllSelected, Side } from '../workbasket-distribution-targets/workbasket-distribution-targets.component';
import { MatSelectionList } from '@angular/material/list';

@Component({
  selector: 'taskana-administration-workbasket-distribution-targets-list',
  templateUrl: './workbasket-distribution-targets-list.component.html',
  styleUrls: ['./workbasket-distribution-targets-list.component.scss'],
  animations: [expandDown]
})
export class WorkbasketDistributionTargetsListComponent implements AfterContentChecked, OnChanges {
  @Input() distributionTargets: WorkbasketSummary[];
  @Input() side: Side;
  @Input() header: string;
  @Input() allSelected;
  @Input() component;

  @Output() allSelectedEmitter = new EventEmitter<AllSelected>();

  toolbarState = false;
  @ViewChild('workbasket') distributionTargetsList: MatSelectionList;

  constructor(private changeDetector: ChangeDetectorRef) {}

  ngAfterContentChecked(): void {
    this.changeDetector.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (typeof changes.allSelected?.currentValue !== 'undefined') {
      this.selectAll(changes.allSelected.currentValue);
    }
  }

  selectAll(selected: boolean) {
    if (typeof this.distributionTargetsList !== 'undefined') {
      this.allSelected = selected;
      this.distributionTargetsList.options.map((item) => (item['selected'] = selected));
      this.distributionTargets.map((item) => (item['selected'] = selected));

      this.allSelectedEmitter.emit({ value: this.allSelected, side: this.side });
    }
  }

  changeToolbarState(state: boolean) {
    this.toolbarState = state;
  }
}

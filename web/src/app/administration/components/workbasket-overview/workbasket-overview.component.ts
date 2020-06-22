import { Component, OnInit } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import { Observable, Subject } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { Workbasket } from '../../../shared/models/workbasket';
import { ACTION } from '../../../shared/models/action';
import { SelectWorkbasket, SetActiveAction } from '../../../shared/store/workbasket-store/workbasket.actions';

@Component({
  selector: 'app-workbasket-overview',
  templateUrl: './workbasket-overview.component.html',
  styleUrls: ['./workbasket-overview.component.scss']
})
export class WorkbasketOverviewComponent implements OnInit {
  showDetail = false;
  @Select(WorkbasketSelectors.selectedWorkbasket) selectedWorkbasket$: Observable<Workbasket>;
  private destroy$ = new Subject<void>();
  routerParams: any;

  constructor(
    private route: ActivatedRoute,
    private store: Store
  ) {

  }

  ngOnInit() {
    if (this.route.firstChild) {
      this.route.firstChild.params
        .pipe(takeUntil(this.destroy$))
        .subscribe(params => {
          this.routerParams = params;

          if (this.routerParams.id) {
            this.showDetail = true;
            this.store.dispatch(new SelectWorkbasket(this.routerParams.id));
          }
        });
    }

    this.selectedWorkbasket$
      .pipe(takeUntil(this.destroy$))
      .subscribe(selectedClassification => {
        this.showDetail = !!selectedClassification;
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

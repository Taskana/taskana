import { Component, OnInit } from '@angular/core';
<<<<<<< HEAD
import { Select, Store } from '@ngxs/store';
import { Observable, Subject } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';

import { CreateWorkbasket,
  SelectWorkbasket,
  SetActiveAction } from '../../../shared/store/workbasket-store/workbasket.actions';
=======
>>>>>>> TSK-1215 initialized workbasket-overview

@Component({
  selector: 'app-workbasket-overview',
  templateUrl: './workbasket-overview.component.html',
  styleUrls: ['./workbasket-overview.component.scss']
})
export class WorkbasketOverviewComponent implements OnInit {
<<<<<<< HEAD
  showDetail = false;
  @Select(WorkbasketSelectors.selectedWorkbasketAndAction) selectedWorkbasketAndAction$: Observable<any>;
  destroy$ = new Subject<void>();
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
            if (this.routerParams.id === 'new-workbasket') {
              this.store.dispatch(new CreateWorkbasket());
            } else {
              this.store.dispatch(new SelectWorkbasket(this.routerParams.id));
            }
          }
        });
    }
    this.selectedWorkbasketAndAction$
      .pipe(takeUntil(this.destroy$))
      .subscribe(state => {
        this.showDetail = !!state.selectedWorkbasket || state.action === 0;
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
=======

  constructor() { }

  ngOnInit() {
  }

>>>>>>> TSK-1215 initialized workbasket-overview
}

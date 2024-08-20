import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { Select, Store } from '@ngxs/store';
import { takeUntil } from 'rxjs/operators';
import { ClassificationSelectors } from '../../../shared/store/classification-store/classification.selectors';
import {
  GetClassifications,
  SelectClassification,
  CreateClassification
} from '../../../shared/store/classification-store/classification.actions';
import { Classification } from '../../../shared/models/classification';

@Component({
  selector: 'kadai-administration-classification-overview',
  templateUrl: './classification-overview.component.html',
  styleUrls: ['./classification-overview.component.scss']
})
export class ClassificationOverviewComponent implements OnInit, OnDestroy {
  showDetail = false;
  @Select(ClassificationSelectors.selectedClassification) selectedClassification$: Observable<Classification>;
  private destroy$ = new Subject<void>();
  routerParams: any;

  constructor(private route: ActivatedRoute, private store: Store) {}

  ngOnInit() {
    if (this.route.firstChild) {
      this.route.firstChild.params.pipe(takeUntil(this.destroy$)).subscribe((params) => {
        this.routerParams = params;

        if (this.routerParams.id) {
          this.showDetail = true;
          this.store
            .dispatch(new SelectClassification(this.routerParams.id))
            .subscribe(() => this.store.dispatch(new GetClassifications()));
        }
        if (this.routerParams.id && this.routerParams.id.indexOf('new-classification') !== -1) {
          this.store.dispatch(new CreateClassification());
        }
      });
    }

    this.selectedClassification$.pipe(takeUntil(this.destroy$)).subscribe((selectedClassification) => {
      this.showDetail = !!selectedClassification;
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

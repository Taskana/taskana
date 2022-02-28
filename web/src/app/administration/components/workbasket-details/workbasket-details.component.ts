import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of, Subject, timeout } from 'rxjs';
import { Workbasket } from 'app/shared/models/workbasket';
import { ACTION } from 'app/shared/models/action';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { Actions, ofActionSuccessful, Select, Store } from '@ngxs/store';
import { catchError, filter, take, takeUntil } from 'rxjs/operators';
import {
  WorkbasketAndComponentAndAction,
  WorkbasketSelectors
} from '../../../shared/store/workbasket-store/workbasket.selectors';
import { Location } from '@angular/common';
import {
  CopyWorkbasket,
  DeselectWorkbasket,
  OnButtonPressed,
  SelectComponent,
  UpdateWorkbasket,
  UpdateWorkbasketDistributionTargets
} from '../../../shared/store/workbasket-store/workbasket.actions';
import { ButtonAction } from '../../models/button-action';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { cloneDeep } from 'lodash';

@Component({
  selector: 'taskana-administration-workbasket-details',
  templateUrl: './workbasket-details.component.html',
  styleUrls: ['./workbasket-details.component.scss']
})
export class WorkbasketDetailsComponent implements OnInit, OnDestroy {
  workbasket: Workbasket;
  action: ACTION;

  @Select(WorkbasketSelectors.selectedComponent)
  selectedTab$: Observable<number>;

  @Select(WorkbasketSelectors.badgeMessage)
  badgeMessage$: Observable<string>;

  @Select(WorkbasketSelectors.selectedWorkbasketAndComponentAndAction)
  selectedWorkbasketAndComponentAndAction$: Observable<WorkbasketAndComponentAndAction>;

  @Select(WorkbasketSelectors.selectedWorkbasket)
  selectedWorkbasket$: Observable<Workbasket>;

  destroy$ = new Subject<void>();

  @Input() expanded: boolean;

  constructor(
    private location: Location,
    private route: ActivatedRoute,
    private router: Router,
    private domainService: DomainService,
    private requestInProgressService: RequestInProgressService,
    private store: Store,
    private ngxsActions$: Actions
  ) {}

  ngOnInit() {
    this.getWorkbasketFromStore();
  }

  getWorkbasketFromStore() {
    /*
        get workbasket from store only when (to avoid discarding changes):
        a) workbasket with another ID is selected (includes copying)
        b) empty workbasket is created
      */
    this.selectedWorkbasketAndComponentAndAction$.pipe(takeUntil(this.destroy$)).subscribe((object) => {
      const workbasket = object.selectedWorkbasket;
      const action = object.action;

      const isAnotherId = this.workbasket?.workbasketId !== workbasket?.workbasketId;
      const isCreation = action !== this.action && action === ACTION.CREATE;
      if (isAnotherId || isCreation) {
        this.workbasket = cloneDeep(workbasket);
      }

      this.action = action;
    });

    // c) saving the workbasket
    this.ngxsActions$.pipe(ofActionSuccessful(UpdateWorkbasket), takeUntil(this.destroy$)).subscribe(() => {
      this.store
        .dispatch(new UpdateWorkbasketDistributionTargets())
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
          this.selectedWorkbasket$
            .pipe(
              take(5),
              timeout(250),
              catchError(() => of(null)),
              filter((val) => val !== null)
            )
            .subscribe((wb) => (this.workbasket = wb));
        });
    });
  }

  selectComponent(index) {
    this.store.dispatch(new SelectComponent(index));
  }

  onSubmit() {
    this.store.dispatch(new OnButtonPressed(ButtonAction.SAVE));
  }

  onRestore() {
    this.store.dispatch(new OnButtonPressed(ButtonAction.UNDO));
  }

  onCopy() {
    this.store.dispatch(new OnButtonPressed(ButtonAction.COPY));
    this.store.dispatch(new CopyWorkbasket(this.workbasket));
  }

  onRemoveAsDistributionTarget() {
    this.store.dispatch(new OnButtonPressed(ButtonAction.REMOVE_AS_DISTRIBUTION_TARGETS));
  }

  onRemoveWorkbasket() {
    this.store.dispatch(new OnButtonPressed(ButtonAction.DELETE));
  }

  onClose() {
    this.store.dispatch(new OnButtonPressed(ButtonAction.CLOSE));
    this.store.dispatch(new DeselectWorkbasket());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { Workbasket } from 'app/shared/models/workbasket';
import { ACTION } from 'app/shared/models/action';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { Select, Store } from '@ngxs/store';
import { takeUntil } from 'rxjs/operators';
import {
  WorkbasketAndComponentAndAction,
  WorkbasketSelectors
} from '../../../shared/store/workbasket-store/workbasket.selectors';
import { Location } from '@angular/common';
import {
  CopyWorkbasket,
  DeselectWorkbasket,
  OnButtonPressed,
  SelectComponent
} from '../../../shared/store/workbasket-store/workbasket.actions';
import { ButtonAction } from '../../models/button-action';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { WorkbasketComponent } from '../../models/workbasket-component';

@Component({
  selector: 'taskana-administration-workbasket-details',
  templateUrl: './workbasket-details.component.html',
  styleUrls: ['./workbasket-details.component.scss']
})
export class WorkbasketDetailsComponent implements OnInit, OnDestroy {
  workbasket: Workbasket;
  action: ACTION;
  selectedComponent: WorkbasketComponent;

  @Select(WorkbasketSelectors.selectedComponent)
  selectedTab$: Observable<number>;

  @Select(WorkbasketSelectors.badgeMessage)
  badgeMessage$: Observable<string>;

  @Select(WorkbasketSelectors.selectedWorkbasketAndComponentAndAction)
  selectedWorkbasketAndComponentAndAction$: Observable<WorkbasketAndComponentAndAction>;

  destroy$ = new Subject<void>();

  @Input() expanded: boolean;

  constructor(
    private location: Location,
    private route: ActivatedRoute,
    private router: Router,
    private domainService: DomainService,
    private requestInProgressService: RequestInProgressService,
    private store: Store
  ) {}

  ngOnInit() {
    this.getWorkbasketFromStore();
  }

  getWorkbasketFromStore() {
    // this is necessary since we receive workbaskets from store even when there is no update
    // this would unintentionally discard changes

    this.selectedWorkbasketAndComponentAndAction$.pipe(takeUntil(this.destroy$)).subscribe((object) => {
      const workbasket = object.selectedWorkbasket;
      const action = object.action;
      const component = object.selectedComponent;

      // get workbasket from store when:
      // a) workbasket with another ID is selected (includes copying)
      // b) empty workbasket is created
      // c) saving the workbasket

      const isAnotherId = this.workbasket?.workbasketId !== workbasket?.workbasketId;
      const isCreation = action !== this.action && action === ACTION.CREATE;
      const isSameComponent = component === this.selectedComponent;
      if (isAnotherId || isCreation || isSameComponent) {
        this.workbasket = { ...workbasket };
      }

      this.action = action;
      this.selectedComponent = component;
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

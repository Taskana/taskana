import { Component, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { Workbasket } from 'app/shared/models/workbasket';
import { ACTION } from 'app/shared/models/action';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { Select, Store } from '@ngxs/store';
import { takeUntil } from 'rxjs/operators';
import { WorkbasketAndAction, WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { TaskanaDate } from '../../../shared/util/taskana.date';
import { Location } from '@angular/common';
import { WorkbasketType } from '../../../shared/models/workbasket-type';
import {
  DeselectWorkbasket,
  OnButtonPressed,
  SelectComponent
} from '../../../shared/store/workbasket-store/workbasket.actions';
import { ButtonAction } from '../../models/button-action';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';

@Component({
  selector: 'taskana-administration-workbasket-details',
  templateUrl: './workbasket-details.component.html',
  styleUrls: ['./workbasket-details.component.scss']
})
export class WorkbasketDetailsComponent implements OnInit, OnDestroy, OnChanges {
  workbasket: Workbasket;
  workbasketCopy: Workbasket;
  selectedId: string;
  action: ACTION;
  badgeMessage = '';

  @Select(WorkbasketSelectors.selectedWorkbasket)
  selectedWorkbasket$: Observable<Workbasket>;

  @Select(WorkbasketSelectors.selectedComponent)
  selectedTab$: Observable<number>;

  @Select(WorkbasketSelectors.workbasketActiveAction)
  activeAction$: Observable<ACTION>;

  @Select(WorkbasketSelectors.selectedWorkbasketAndAction)
  selectedWorkbasketAndAction$: Observable<WorkbasketAndAction>;

  destroy$ = new Subject<void>();

  constructor(
    private location: Location,
    private route: ActivatedRoute,
    private router: Router,
    private domainService: DomainService,
    private importExportService: ImportExportService,
    private requestInProgressService: RequestInProgressService,
    private store: Store
  ) {}

  ngOnInit() {
    this.selectedWorkbasketAndAction$.pipe(takeUntil(this.destroy$)).subscribe((selectedWorkbasketAndAction) => {
      this.action = selectedWorkbasketAndAction.action;
      if (this.action === ACTION.CREATE) {
        this.selectedId = undefined;
        this.badgeMessage = 'Creating new workbasket';
        this.initWorkbasket();
      } else if (this.action === ACTION.COPY) {
        // delete this.workbasket.key;
        this.workbasketCopy = this.workbasket;
        this.getWorkbasketInformation();
        this.badgeMessage = `Copying workbasket: ${this.workbasket.key}`;
      } else if (typeof selectedWorkbasketAndAction.selectedWorkbasket !== 'undefined') {
        this.workbasket = { ...selectedWorkbasketAndAction.selectedWorkbasket };
        this.getWorkbasketInformation(this.workbasket);
      }
    });

    this.importExportService
      .getImportingFinished()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.workbasket) {
          this.getWorkbasketInformation(this.workbasket);
        }
      });
  }

  ngOnChanges(changes?: SimpleChanges) {}

  addDateToWorkbasket(workbasket: Workbasket) {
    const date = TaskanaDate.getDate();
    workbasket.created = date;
    workbasket.modified = date;
  }

  initWorkbasket() {
    const emptyWorkbasket: Workbasket = {};
    emptyWorkbasket.domain = this.domainService.getSelectedDomainValue();
    emptyWorkbasket.type = WorkbasketType.PERSONAL;
    this.addDateToWorkbasket(emptyWorkbasket);
    this.workbasket = emptyWorkbasket;
  }

  backClicked(): void {
    this.router.navigate(['./'], { relativeTo: this.route.parent });
  }

  getWorkbasketInformation(selectedWorkbasket?: Workbasket) {
    let workbasketIdSelected: string;
    if (selectedWorkbasket) {
      workbasketIdSelected = selectedWorkbasket.workbasketId;
    }
    this.requestInProgressService.setRequestInProgress(true);
    if (!workbasketIdSelected && this.action === ACTION.CREATE) {
      // CREATE
      this.workbasket = {};
      this.domainService
        .getSelectedDomain()
        .pipe(takeUntil(this.destroy$))
        .subscribe((domain) => {
          this.workbasket.domain = domain;
        });
      this.requestInProgressService.setRequestInProgress(false);
    } else if (!workbasketIdSelected && this.action === ACTION.COPY) {
      // COPY
      this.workbasket = { ...this.workbasketCopy };
      delete this.workbasket.workbasketId;
      this.requestInProgressService.setRequestInProgress(false);
    }
    if (workbasketIdSelected) {
      this.workbasket = selectedWorkbasket;
      this.requestInProgressService.setRequestInProgress(false);
      this.checkDomainAndRedirect();
    }
  }

  checkDomainAndRedirect() {
    this.domainService
      .getSelectedDomain()
      .pipe(takeUntil(this.destroy$))
      .subscribe((domain) => {
        if (domain !== '' && this.workbasket && this.workbasket.domain !== domain) {
          this.backClicked();
        }
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

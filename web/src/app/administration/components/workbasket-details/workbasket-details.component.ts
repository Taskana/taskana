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
import { ICONTYPES } from '../../../shared/models/icon-types';
import {
  SelectAccessItems,
  SelectComponent,
  SelectDistributionTargets,
  SelectInformation
} from '../../../shared/store/workbasket-store/workbasket.actions';

@Component({
  selector: 'taskana-administration-workbasket-details',
  templateUrl: './workbasket-details.component.html',
  styleUrls: ['./workbasket-details.component.scss']
})
export class WorkbasketDetailsComponent implements OnInit, OnDestroy, OnChanges {
  workbasket: Workbasket;
  workbasketCopy: Workbasket;
  selectedId: string;
  requestInProgress = false;
  action: ACTION;
  tabSelected = 'information';
  badgeMessage = '';

  @Select(WorkbasketSelectors.selectedWorkbasket)
  selectedWorkbasket$: Observable<Workbasket>;

  @Select(WorkbasketSelectors.workbasketActiveAction)
  activeAction$: Observable<ACTION>;

  @Select(WorkbasketSelectors.selectedWorkbasketAndAction)
  selectedWorkbasketAndAction$: Observable<WorkbasketAndAction>;

  destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private domainService: DomainService,
    private importExportService: ImportExportService,
    private store: Store
  ) {}

  ngOnInit() {
    this.selectedWorkbasketAndAction$.pipe(takeUntil(this.destroy$)).subscribe((selectedWorkbasketAndAction) => {
      this.action = selectedWorkbasketAndAction.action;
      if (this.action === ACTION.CREATE) {
        this.tabSelected = 'information';
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
    emptyWorkbasket.type = ICONTYPES.PERSONAL;
    this.addDateToWorkbasket(emptyWorkbasket);
    this.workbasket = emptyWorkbasket;
  }

  backClicked(): void {
    this.router.navigate(['./'], { relativeTo: this.route.parent });
  }

  selectTab(tab) {
    this.tabSelected = this.action === ACTION.CREATE ? 'information' : tab;
  }

  getWorkbasketInformation(selectedWorkbasket?: Workbasket) {
    let workbasketIdSelected: string;
    if (selectedWorkbasket) {
      workbasketIdSelected = selectedWorkbasket.workbasketId;
    }
    this.requestInProgress = true;
    if (!workbasketIdSelected && this.action === ACTION.CREATE) {
      // CREATE
      this.workbasket = {};
      this.domainService
        .getSelectedDomain()
        .pipe(takeUntil(this.destroy$))
        .subscribe((domain) => {
          this.workbasket.domain = domain;
        });
      this.requestInProgress = false;
    } else if (!workbasketIdSelected && this.action === ACTION.COPY) {
      // COPY
      this.workbasket = { ...this.workbasketCopy };
      delete this.workbasket.workbasketId;
      this.requestInProgress = false;
    }
    if (workbasketIdSelected) {
      this.workbasket = selectedWorkbasket;
      this.requestInProgress = false;
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

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';

import { Workbasket } from 'app/shared/models/workbasket';
import { ACTION } from 'app/shared/models/action';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { MasterAndDetailService } from 'app/shared/services/master-and-detail/master-and-detail.service';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { Select, Store } from '@ngxs/store';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { TaskanaDate } from '../../../shared/util/taskana.date';
import { ICONTYPES } from '../../../shared/models/icon-types';

@Component({
  selector: 'taskana-administration-workbasket-details',
  templateUrl: './workbasket-details.component.html'
})
export class WorkbasketDetailsComponent implements OnInit, OnDestroy {
  workbasket: Workbasket;
  workbasketCopy: Workbasket;
  selectedId: string;
  showDetail = false;
  requestInProgress = false;
  action: ACTION;
  tabSelected = 'information';

  @Select(WorkbasketSelectors.selectedWorkbasket)
  selectedWorkbasket$: Observable<Workbasket>;

  @Select(WorkbasketSelectors.workbasketActiveAction)
  activeAction$: Observable<ACTION>;

  destroy$ = new Subject<void>();

  constructor(private service: WorkbasketService,
    private route: ActivatedRoute,
    private router: Router,
    private domainService: DomainService,
    private importExportService: ImportExportService,
    private store: Store) {
  }

  ngOnInit() {
    this.selectedWorkbasket$
      .pipe(takeUntil(this.destroy$))
      .subscribe(selectedWorkbasket => {
        console.log('selected workbasket changed');
        this.getWorkbasketInformation(selectedWorkbasket);
      });

    this.activeAction$.pipe(takeUntil(this.destroy$))
      .subscribe(activeAction => {
        this.action = activeAction;
        console.log(this.action);
        if (this.action === ACTION.CREATE) {
          this.tabSelected = 'information';
          this.selectedId = undefined;
          this.initWorkbasket();
        } else if (this.action === ACTION.COPY) {
          // delete this.workbasket.key;
          this.workbasketCopy = this.workbasket;
          this.getWorkbasketInformation();
        }
      });

    this.importExportService.getImportingFinished()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.workbasket) {
          this.getWorkbasketInformation(this.workbasket);
        }
      });
  }

  addDateToWorkbasket(workbasket: Workbasket) {
    const date = TaskanaDate.getDate();
    workbasket.created = date;
    workbasket.modified = date;
  }

  initWorkbasket() {
    const emptyWorkbasket = new Workbasket();
    emptyWorkbasket.domain = this.domainService.getSelectedDomainValue();
    emptyWorkbasket.type = ICONTYPES.PERSONAL;
    this.addDateToWorkbasket(emptyWorkbasket);
    this.workbasket = emptyWorkbasket;
    console.log(this.workbasket);
  }

  backClicked(): void {
    this.service.selectWorkBasket();
    this.router.navigate(['./'], { relativeTo: this.route.parent });
  }

  selectTab(tab) {
    this.tabSelected = this.action === ACTION.CREATE ? 'information' : tab;
  }

  private getWorkbasketInformation(selectedWorkbasket?: Workbasket) {
    let workbasketIdSelected: string;
    if (selectedWorkbasket) {
      workbasketIdSelected = selectedWorkbasket.workbasketId;
    }
    this.requestInProgress = true;
    console.log('working');
    if (!workbasketIdSelected && this.action === ACTION.CREATE) { // CREATE
      this.workbasket = new Workbasket();
      this.domainService.getSelectedDomain()
        .pipe(takeUntil(this.destroy$))
        .subscribe(domain => {
          this.workbasket.domain = domain;
        });
      this.requestInProgress = false;
    } else if (!workbasketIdSelected && this.action === ACTION.COPY) { // COPY
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

  private checkDomainAndRedirect() {
    this.domainService.getSelectedDomain()
      .pipe(takeUntil(this.destroy$))
      .subscribe(domain => {
        if (domain !== '' && this.workbasket && this.workbasket.domain !== domain) {
          this.backClicked();
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';

import { Workbasket } from 'app/shared/models/workbasket';
import { ACTION } from 'app/shared/models/action';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { MasterAndDetailService } from 'app/shared/services/master-and-detail/master-and-detail.service';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { Select } from '@ngxs/store';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';

@Component({
  selector: 'taskana-workbasket-details',
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

  destroy$ = new Subject<void>();

  constructor(private service: WorkbasketService,
    private route: ActivatedRoute,
    private router: Router,
    private masterAndDetailService: MasterAndDetailService,
    private domainService: DomainService,
    private errorsService: NotificationService,
    private importExportService: ImportExportService) {
  }

  ngOnInit() {
    this.selectedWorkbasket$
      .pipe(takeUntil(this.destroy$))
      .subscribe(selectedWorkbasket => {
        this.getWorkbasketInformation(selectedWorkbasket);
      });

    this.route.params.subscribe(params => {
      const { id } = params;
      delete this.action;
      if (id) {
        if (id.indexOf('new-workbasket') !== -1) {
          this.tabSelected = 'information';
          this.action = ACTION.CREATE;
          this.getWorkbasketInformation();
        } else if (id.indexOf('copy-workbasket') !== -1) {
          if (!this.selectedId) {
            this.router.navigate(['./'], { relativeTo: this.route.parent });
            return;
          }
          this.action = ACTION.COPY;
          delete this.workbasket.key;
          this.workbasketCopy = this.workbasket;
          this.getWorkbasketInformation();
        } else {
          this.selectWorkbasket(id);
        }
      }
    });

    this.masterAndDetailService.getShowDetail()
      .pipe(takeUntil(this.destroy$))
      .subscribe(showDetail => {
        this.showDetail = showDetail;
      });

    this.importExportService.getImportingFinished()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.workbasket) {
          this.getWorkbasketInformation(this.workbasket);
        }
      });
  }

  backClicked(): void {
    this.service.selectWorkBasket();
    this.router.navigate(['./'], { relativeTo: this.route.parent });
  }

  selectTab(tab) {
    this.tabSelected = this.action === ACTION.CREATE ? 'information' : tab;
  }

  private selectWorkbasket(id: string) {
    this.selectedId = id;
    this.service.selectWorkBasket(id);
  }

  private getWorkbasketInformation(selectedWorkbasket?: Workbasket) {
    let workbasketIdSelected: string;
    if (selectedWorkbasket) {
      workbasketIdSelected = selectedWorkbasket.workbasketId;
    }
    this.requestInProgress = true;

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

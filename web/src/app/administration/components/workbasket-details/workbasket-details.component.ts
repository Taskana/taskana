import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';

import { Workbasket } from 'app/shared/models/workbasket';
import { ACTION } from 'app/shared/models/action';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { MasterAndDetailService } from 'app/shared/services/master-and-detail/master-and-detail.service';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { NOTIFICATION_TYPES } from '../../../shared/models/notifications';
import { NotificationService } from '../../../shared/services/notifications/notification.service';

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

  private workbasketSelectedSubscription: Subscription;
  private workbasketSubscription: Subscription;
  private routeSubscription: Subscription;
  private masterAndDetailSubscription: Subscription;
  private domainSubscription: Subscription;
  private importingExportingSubscription: Subscription;
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
    this.workbasketSelectedSubscription = this.service.getSelectedWorkBasket().subscribe(workbasketIdSelected => {
      delete this.workbasket;
      this.getWorkbasketInformation(workbasketIdSelected);
    });

    this.routeSubscription = this.route.params.subscribe(params => {
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

    this.masterAndDetailSubscription = this.masterAndDetailService.getShowDetail().subscribe(showDetail => {
      this.showDetail = showDetail;
    });

    this.importingExportingSubscription = this.importExportService.getImportingFinished().subscribe(() => {
      if (this.workbasket) { this.getWorkbasketInformation(this.workbasket.workbasketId); }
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

  private getWorkbasketInformation(workbasketIdSelected?: string) {
    this.requestInProgress = true;

    if (!workbasketIdSelected && this.action === ACTION.CREATE) { // CREATE
      this.workbasket = new Workbasket();
      this.domainSubscription = this.domainService.getSelectedDomain().subscribe(domain => {
        this.workbasket.domain = domain;
      });
      this.requestInProgress = false;
    } else if (!workbasketIdSelected && this.action === ACTION.COPY) { // COPY
      this.workbasket = { ...this.workbasketCopy };
      delete this.workbasket.workbasketId;
      this.requestInProgress = false;
    }
    if (workbasketIdSelected) {
      this.workbasketSubscription = this.service.getWorkBasket(workbasketIdSelected).subscribe(workbasket => {
        this.workbasket = workbasket;
        this.requestInProgress = false;
        this.checkDomainAndRedirect();
      }, error => {
        this.errorsService.triggerError(NOTIFICATION_TYPES.FETCH_ERR_4, error);
      });
    }
  }

  private checkDomainAndRedirect() {
    this.domainSubscription = this.domainService.getSelectedDomain().subscribe(domain => {
      if (domain !== '' && this.workbasket && this.workbasket.domain !== domain) {
        this.backClicked();
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.workbasketSelectedSubscription) { this.workbasketSelectedSubscription.unsubscribe(); }
    if (this.workbasketSubscription) { this.workbasketSubscription.unsubscribe(); }
    if (this.routeSubscription) { this.routeSubscription.unsubscribe(); }
    if (this.masterAndDetailSubscription) { this.masterAndDetailSubscription.unsubscribe(); }
    if (this.domainSubscription) { this.domainSubscription.unsubscribe(); }
    if (this.importingExportingSubscription) { this.importingExportingSubscription.unsubscribe(); }
  }
}

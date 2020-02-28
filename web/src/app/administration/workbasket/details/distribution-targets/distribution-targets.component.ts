import { Component, Input, OnDestroy, SimpleChanges, OnChanges, ViewChild, ElementRef } from '@angular/core';
import { Subscription } from 'rxjs';

import { Workbasket } from 'app/models/workbasket';
import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';
import { WorkbasketDistributionTargetsResource } from 'app/models/workbasket-distribution-targets-resource';
import { MessageModal } from 'app/models/message-modal';
import { ACTION } from 'app/models/action';
import { AlertModel, AlertType } from 'app/models/alert';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { AlertService } from 'app/services/alert/alert.service';
import { SavingWorkbasketService, SavingInformation } from 'app/administration/services/saving-workbaskets/saving-workbaskets.service';
import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { Page } from 'app/models/page';
import { OrientationService } from 'app/services/orientation/orientation.service';
import { Orientation } from 'app/models/orientation';
import { ALERT_TYPES, ERROR_TYPES } from '../../../../services/general-modal/errors';

export enum Side {
  LEFT,
  RIGHT
}
@Component({
  selector: 'taskana-workbaskets-distribution-targets',
  templateUrl: './distribution-targets.component.html',
  styleUrls: ['./distribution-targets.component.scss']
})
export class DistributionTargetsComponent implements OnChanges, OnDestroy {
  @Input()
  workbasket: Workbasket;

  @Input()
  action: string;

  @Input()
  active: string;

  badgeMessage = '';

  distributionTargetsSubscription: Subscription;
  workbasketSubscription: Subscription;
  workbasketFilterSubscription: Subscription;
  savingDistributionTargetsSubscription: Subscription;
  orientationSubscription: Subscription;

  distributionTargetsSelectedResource: WorkbasketDistributionTargetsResource;
  distributionTargetsLeft: Array<WorkbasketSummary>;
  distributionTargetsRight: Array<WorkbasketSummary>;
  distributionTargetsSelected: Array<WorkbasketSummary>;
  distributionTargetsClone: Array<WorkbasketSummary>;
  distributionTargetsSelectedClone: Array<WorkbasketSummary>;

  requestInProgressLeft = false;
  requestInProgressRight = false;
  loadingItems = false;
  modalErrorMessage: string;
  side = Side;
  private initialized = false;
  page: Page;
  cards: number;
  selectAllLeft = false;
  selectAllRight = false;

  @ViewChild('panelBody', { static: false })
  private panelBody: ElementRef;

  constructor(
    private workbasketService: WorkbasketService,
    private alertService: AlertService,
    private savingWorkbaskets: SavingWorkbasketService,
    private generalModalService: GeneralModalService,
    private requestInProgressService: RequestInProgressService,
    private orientationService: OrientationService
  ) { }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.initialized && changes.active && changes.active.currentValue === 'distributionTargets') {
      this.init();
    }
    if (changes.action) {
      this.setBadge();
    }
  }

  onScroll(side: Side) {
    if (side === this.side.LEFT && this.page.totalPages > TaskanaQueryParameters.page) {
      this.loadingItems = true;
      this.getNextPage(side);
    }
  }

  moveDistributionTargets(side: number) {
    if (side === Side.LEFT) {
      const itemsLeft = this.distributionTargetsLeft.length;
      const itemsRight = this.distributionTargetsRight.length;
      const itemsSelected = this.getSelectedItems(this.distributionTargetsLeft);
      this.distributionTargetsSelected = this.distributionTargetsSelected.concat(itemsSelected);
      this.distributionTargetsRight = this.distributionTargetsRight.concat(itemsSelected);
      if (((itemsLeft - itemsSelected.length) <= TaskanaQueryParameters.pageSize) && ((itemsLeft + itemsRight) < this.page.totalElements)) {
        this.getNextPage(side);
      }
    } else {
      const itemsSelected = this.getSelectedItems(this.distributionTargetsRight);
      this.distributionTargetsSelected = this.removeSelectedItems(this.distributionTargetsSelected, itemsSelected);
      this.distributionTargetsRight = this.removeSelectedItems(this.distributionTargetsRight, itemsSelected);
      this.distributionTargetsLeft = this.distributionTargetsLeft.concat(itemsSelected);
    }

    this.uncheckSelectAll(side);
  }

  onSave() {
    this.requestInProgressService.setRequestInProgress(true);
    this.workbasketService.updateWorkBasketsDistributionTargets(
      this.distributionTargetsSelectedResource._links.self.href, this.getSeletedIds()
    ).subscribe(response => {
      this.requestInProgressService.setRequestInProgress(false);
      this.distributionTargetsSelected = response.distributionTargets;
      this.distributionTargetsSelectedClone = Object.assign([], this.distributionTargetsSelected);
      this.distributionTargetsClone = Object.assign([], this.distributionTargetsLeft);
      // new Key ALERT_TYPES.SUCCESS_ALERT_8
      this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS,
        `Workbasket  ${this.workbasket.name} : Distribution targets were saved successfully`));
      return true;
    },
    error => {
      // new Key ERROR_TYPES.SAVE_ERR_3
      this.generalModalService.triggerMessage(
        new MessageModal('There was error while saving your workbasket\'s distribution targets', error)
      );
      this.requestInProgressService.setRequestInProgress(false);
      return false;
    });
    return false;
  }

  onClear() {
      // new key ALERT_TYPES.INFO_ALERT
    this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'Reset edited fields'));
    this.distributionTargetsLeft = Object.assign([], this.distributionTargetsClone);
    this.distributionTargetsRight = Object.assign([], this.distributionTargetsSelectedClone);
    this.distributionTargetsSelected = Object.assign([], this.distributionTargetsSelectedClone);
  }

  performFilter(dualListFilter: any) {
    this.fillDistributionTargets(dualListFilter.side, undefined);
    this.onRequest(false, dualListFilter.side);
    this.workbasketFilterSubscription = this.workbasketService.getWorkBasketsSummary(true, '', '', '',
      dualListFilter.filterBy.filterParams.name, dualListFilter.filterBy.filterParams.description, '',
      dualListFilter.filterBy.filterParams.owner, dualListFilter.filterBy.filterParams.type, '',
      dualListFilter.filterBy.filterParams.key, '', true)
      .subscribe(resultList => {
        this.fillDistributionTargets(dualListFilter.side, (resultList.workbaskets));
        this.onRequest(true, dualListFilter.side);
      });
  }

  ngOnDestroy(): void {
    if (this.distributionTargetsSubscription) { this.distributionTargetsSubscription.unsubscribe(); }
    if (this.workbasketSubscription) { this.workbasketSubscription.unsubscribe(); }
    if (this.workbasketFilterSubscription) { this.workbasketFilterSubscription.unsubscribe(); }
    if (this.savingDistributionTargetsSubscription) { this.savingDistributionTargetsSubscription.unsubscribe(); }
    if (this.orientationSubscription) { this.orientationSubscription.unsubscribe(); }
  }

  private init() {
    this.onRequest();
    if (!this.workbasket._links.distributionTargets) {
      return;
    }
    this.distributionTargetsSubscription = this.workbasketService.getWorkBasketsDistributionTargets(
      this.workbasket._links.distributionTargets.href
    ).subscribe(
      (distributionTargetsSelectedResource: WorkbasketDistributionTargetsResource) => {
        this.distributionTargetsSelectedResource = distributionTargetsSelectedResource;
        this.distributionTargetsSelected = distributionTargetsSelectedResource.distributionTargets;
        this.distributionTargetsSelectedClone = Object.assign([], this.distributionTargetsSelected);
        TaskanaQueryParameters.page = 1;
        this.calculateNumberItemsList();
        this.getWorkbaskets();
      }
    );

    this.savingDistributionTargetsSubscription = this.savingWorkbaskets.triggeredDistributionTargetsSaving()
      .subscribe((savingInformation: SavingInformation) => {
        if (this.action === ACTION.COPY) {
          this.distributionTargetsSelectedResource._links.self.href = savingInformation.url;
          this.onSave();
        }
      });

    this.orientationSubscription = this.orientationService.getOrientation().subscribe((orientation: Orientation) => {
      this.calculateNumberItemsList();
      this.getWorkbaskets();
    });
  }

  private calculateNumberItemsList() {
    if (this.panelBody) {
      const cardHeight = 72;
      this.cards = this.orientationService.calculateNumberItemsList(this.panelBody.nativeElement.offsetHeight, cardHeight, 100, true) + 1;
    }
  }

  private fillDistributionTargets(side: Side, workbaskets: WorkbasketSummary[]) {
    this.distributionTargetsLeft = side === Side.LEFT ? workbaskets : this.distributionTargetsLeft;
    this.distributionTargetsRight = side === Side.RIGHT ? workbaskets : this.distributionTargetsRight;
  }

  private getNextPage(side: Side) {
    TaskanaQueryParameters.page += 1;
    this.getWorkbaskets(side);
  }

  private getWorkbaskets(side?: Side) {
    if (!this.distributionTargetsLeft) {
      this.distributionTargetsLeft = [];
    }
    if (!this.distributionTargetsRight) {
      this.distributionTargetsRight = [];
    }
    if (this.distributionTargetsSelected && !this.initialized) {
      this.initialized = true;
      TaskanaQueryParameters.pageSize = this.cards + this.distributionTargetsSelected.length;
    }

    this.workbasketSubscription = this.workbasketService.getWorkBasketsSummary(true)
      .subscribe(
        (distributionTargetsAvailable: WorkbasketSummaryResource) => {
          if (TaskanaQueryParameters.page === 1) {
            this.distributionTargetsLeft = [];
            this.page = distributionTargetsAvailable.page;
          }
          if (side === this.side.LEFT) {
            this.distributionTargetsLeft.push(...distributionTargetsAvailable.workbaskets);
          } else if (side === this.side.RIGHT) {
            this.distributionTargetsRight = Object.assign([], distributionTargetsAvailable.workbaskets);
          } else {
            this.distributionTargetsLeft.push(...distributionTargetsAvailable.workbaskets);
            this.distributionTargetsRight = Object.assign([], distributionTargetsAvailable.workbaskets);
            this.distributionTargetsClone = Object.assign([], distributionTargetsAvailable.workbaskets);
          }
          this.onRequest(true);
        }
      );
  }

  private setBadge() {
    if (this.action === ACTION.COPY) {
      this.badgeMessage = `Copying workbasket: ${this.workbasket.key}`;
    }
  }

  private getSelectedItems(originList: any): Array<any> {
    return originList.filter((item: any) => (item.selected === true));
  }

  private removeSelectedItems(originList: any, selectedItemList) {
    for (let index = originList.length - 1; index >= 0; index--) {
      if (selectedItemList.some(itemToRemove => (originList[index].workbasketId === itemToRemove.workbasketId))) {
        originList.splice(index, 1);
      }
    }
    return originList;
  }

  private onRequest(finished: boolean = false, side?: Side) {
    this.loadingItems = false;
    const inProgress = !finished;
    switch (side) {
      case Side.LEFT: this.requestInProgressLeft = inProgress;
        break;
      case Side.RIGHT: this.requestInProgressRight = inProgress;
        break;
      default:
        this.requestInProgressLeft = inProgress;
        this.requestInProgressRight = inProgress;
    }
  }

  private getSeletedIds(): Array<string> {
    const distributionTargetsSelelected: Array<string> = [];
    this.distributionTargetsSelected.forEach(item => {
      distributionTargetsSelelected.push(item.workbasketId);
    });
    return distributionTargetsSelelected;
  }

  private uncheckSelectAll(side: number) {
    if (side === Side.LEFT && this.selectAllLeft) { this.selectAllLeft = false; }
    if (side === Side.RIGHT && this.selectAllRight) { this.selectAllRight = false; }
  }
}

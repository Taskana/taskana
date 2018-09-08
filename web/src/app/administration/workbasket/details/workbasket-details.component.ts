import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { Workbasket } from 'app/models/workbasket';
import { ACTION } from 'app/models/action';
import { ErrorModel } from '../../../models/modal-error';

import { WorkbasketService } from 'app/services/workbasket/workbasket.service'
import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service'
import { DomainService } from 'app/services/domain/domain.service';
import { ErrorModalService } from '../../../services/errorModal/error-modal.service';


@Component({
	selector: 'taskana-workbasket-details',
	templateUrl: './workbasket-details.component.html'
})
export class WorkbasketDetailsComponent implements OnInit, OnDestroy {


	workbasket: Workbasket;
	workbasketCopy: Workbasket;
	selectedId: string = undefined;
	showDetail = false;
	requestInProgress = false;
	action: string;
	tabSelected = 'information';

	private workbasketSelectedSubscription: Subscription;
	private workbasketSubscription: Subscription;
	private routeSubscription: Subscription;
	private masterAndDetailSubscription: Subscription;
	private permissionSubscription: Subscription;
	private domainSubscription: Subscription;

	constructor(private service: WorkbasketService,
		private route: ActivatedRoute,
		private router: Router,
		private masterAndDetailService: MasterAndDetailService,
		private domainService: DomainService,
    private errorModalService: ErrorModalService) { }



	ngOnInit() {
		this.workbasketSelectedSubscription = this.service.getSelectedWorkBasket().subscribe(workbasketIdSelected => {
			this.workbasket = undefined;
			this.getWorkbasketInformation(workbasketIdSelected);
		});

		this.routeSubscription = this.route.params.subscribe(params => {
			let id = params['id'];
			this.action = undefined;
			if (id && id.indexOf('new-workbasket') !== -1) {
				this.tabSelected = 'information';
				this.action = ACTION.CREATE;
				id = undefined;
				this.getWorkbasketInformation(id);
			} else if (id && id.indexOf('copy-workbasket') !== -1) {
				if (!this.selectedId) {
					this.router.navigate(['./'], { relativeTo: this.route.parent });
					return;
				}
				this.action = ACTION.COPY;
				this.workbasket.key = undefined;
				this.workbasketCopy = this.workbasket;
				id = undefined;
				this.getWorkbasketInformation(id, this.selectedId);
			}

			if (id && id !== '') {
				this.selectWorkbasket(id);
			}
		});

		this.masterAndDetailSubscription = this.masterAndDetailService.getShowDetail().subscribe(showDetail => {
			this.showDetail = showDetail;
		});
	}

	backClicked(): void {
		this.service.selectWorkBasket(undefined);
		this.router.navigate(['./'], { relativeTo: this.route.parent });
	}

	selectTab(tab) {
		this.tabSelected = this.action === ACTION.CREATE ? 'information' : tab;
	}

	private selectWorkbasket(id: string) {
		this.selectedId = id;
		this.service.selectWorkBasket(id);
	}

	private getWorkbasketInformation(workbasketIdSelected: string, copyId: string = undefined) {
		this.requestInProgress = true;

		if (!workbasketIdSelected && this.action === ACTION.CREATE) { // CREATE
			this.workbasket = new Workbasket(undefined);
			this.domainSubscription = this.domainService.getSelectedDomain().subscribe(domain => {
				this.workbasket.domain = domain;
			});
			this.requestInProgress = false;
		} else if (!workbasketIdSelected && this.action === ACTION.COPY) { // COPY
			this.workbasket = { ...this.workbasketCopy };
			this.workbasket.workbasketId = undefined;
			this.requestInProgress = false;
		}
		if (workbasketIdSelected) {
			this.workbasketSubscription = this.service.getWorkBasket(workbasketIdSelected).subscribe(workbasket => {
				this.workbasket = workbasket;
				this.requestInProgress = false;
				this.checkDomainAndRedirect();
			}, err => {
        this.errorModalService.triggerError(
          new ErrorModel('An error occurred while fetching the workbasket', err));
      });
		}
	}

	private checkDomainAndRedirect() {
		this.domainSubscription = this.domainService.getSelectedDomain().subscribe(domain => {
			if (this.workbasket && this.workbasket.domain !== domain) {
				this.backClicked();
			}
		});
	}

	ngOnDestroy(): void {
		if (this.workbasketSelectedSubscription) { this.workbasketSelectedSubscription.unsubscribe(); }
		if (this.workbasketSubscription) { this.workbasketSubscription.unsubscribe(); }
		if (this.routeSubscription) { this.routeSubscription.unsubscribe(); }
		if (this.masterAndDetailSubscription) { this.masterAndDetailSubscription.unsubscribe(); }
		if (this.permissionSubscription) { this.permissionSubscription.unsubscribe(); }
		if (this.domainSubscription) { this.domainSubscription.unsubscribe(); }
	}
}

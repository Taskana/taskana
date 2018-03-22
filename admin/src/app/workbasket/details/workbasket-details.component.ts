import { Component, OnInit, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { Workbasket } from '../../model/workbasket';
import { WorkbasketService } from '../../services/workbasket.service'
import { MasterAndDetailService } from '../../services/master-and-detail.service'
import { ActivatedRoute, Params, Router, NavigationStart } from '@angular/router';
import { PermissionService } from '../../services/permission.service';
import { Subscription } from 'rxjs/Subscription';
import { WorkbasketSummary } from '../../model/workbasket-summary';
import { WorkbasketSummaryResource } from '../../model/workbasket-summary-resource';
import { ICONTYPES } from '../../model/type';
import { ErrorModel } from '../../model/modal-error';
import { ErrorModalService } from '../../services/error-modal.service';
import { ACTION } from '../../model/action';

@Component({
	selector: 'taskana-workbasket-details',
	templateUrl: './workbasket-details.component.html',
	styleUrls: ['./workbasket-details.component.scss']
})
export class WorkbasketDetailsComponent implements OnInit, OnDestroy {


	workbasket: Workbasket;
	workbasketCopy: Workbasket;
	selectedId: string = undefined;
	showDetail = false;
	hasPermission = true;
	requestInProgress = false;
	action: string;

	private workbasketSelectedSubscription: Subscription;
	private workbasketSubscription: Subscription;
	private routeSubscription: Subscription;
	private masterAndDetailSubscription: Subscription;
	private permissionSubscription: Subscription;

	constructor(private service: WorkbasketService,
		private route: ActivatedRoute,
		private router: Router,
		private masterAndDetailService: MasterAndDetailService,
		private permissionService: PermissionService,
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
				this.action = ACTION.CREATE;
				id = undefined;
				this.getWorkbasketInformation(id);
			} else if (id && id.indexOf('copy-workbasket') !== -1) {
				if (!this.selectedId) {
					this.router.navigate(['./'], { relativeTo: this.route.parent });
					return;
				}
				this.action = ACTION.COPY;
				this.workbasketCopy = this.workbasket;
				id = undefined
				this.getWorkbasketInformation(id, this.selectedId);
			}

			if (id && id !== '') {
				this.selectWorkbasket(id);
			}
		});

		this.masterAndDetailSubscription = this.masterAndDetailService.getShowDetail().subscribe(showDetail => {
			this.showDetail = showDetail;
		});

		this.permissionSubscription = this.permissionService.hasPermission().subscribe(permission => {
			this.hasPermission = permission;
			if (!this.hasPermission) {
				this.requestInProgress = false;
			}
		})

	}

	backClicked(): void {
		this.service.selectWorkBasket(undefined);
		this.router.navigate(['./'], { relativeTo: this.route.parent });
	}

	private selectWorkbasket(id: string) {
		this.selectedId = id;
		this.service.selectWorkBasket(id);
	}

	private getWorkbasketInformation(workbasketIdSelected: string, copyId: string = undefined) {
		this.requestInProgress = true;
		this.service.getWorkBasketsSummary(true).subscribe((workbasketSummary: WorkbasketSummaryResource) => {
			if (!workbasketIdSelected && this.action === ACTION.CREATE) { // CREATE
				this.workbasket = new Workbasket(undefined);
				this.workbasket._links.self = workbasketSummary._links.self;
				this.requestInProgress = false;
			} else if (!workbasketIdSelected && this.action === ACTION.COPY) { // COPY
				this.workbasket = { ...this.workbasketCopy };
				this.workbasket._links.self = workbasketSummary._links.self;
				this.workbasket.workbasketId = undefined;
				this.requestInProgress = false;
			}

			const workbasketSummarySelected = this.getWorkbasketSummaryById(workbasketSummary._embedded.workbaskets, workbasketIdSelected);
			if (workbasketSummarySelected && workbasketSummarySelected._links) {
				this.workbasketSubscription = this.service.getWorkBasket(workbasketSummarySelected._links.self.href).subscribe(workbasket => {
					this.workbasket = workbasket;
					this.requestInProgress = false;
				});
			}
		});
	}

	private getWorkbasketSummaryById(workbasketSummary: Array<WorkbasketSummary>, selectedId: string): WorkbasketSummary {
		return workbasketSummary.find((summary => summary.workbasketId === selectedId));
	}

	ngOnDestroy(): void {
		if (this.workbasketSelectedSubscription) { this.workbasketSelectedSubscription.unsubscribe(); }
		if (this.workbasketSubscription) { this.workbasketSubscription.unsubscribe(); }
		if (this.routeSubscription) { this.routeSubscription.unsubscribe(); }
		if (this.masterAndDetailSubscription) { this.masterAndDetailSubscription.unsubscribe(); }
		if (this.permissionSubscription) { this.permissionSubscription.unsubscribe(); }
	}
}

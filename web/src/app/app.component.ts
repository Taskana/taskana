import { Component, OnInit, HostListener, OnDestroy } from '@angular/core';
import { environment } from '../environments/environment';
import { Router, NavigationStart } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';

import { ErrorModel } from './models/modal-error';

import { ErrorModalService } from './services/errorModal/error-modal.service';
import { RequestInProgressService } from './services/requestInProgress/request-in-progress.service';
import { OrientationService } from './services/orientation/orientation.service';
import { SelectedRouteService } from './services/selected-route/selected-route';

@Component({
	selector: 'taskana-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
	title = 'Taskana administration';

	adminUrl: string = environment.taskanaAdminUrl;
	monitorUrl: string = environment.taskanaMonitorUrl;
	workplaceUrl: string = environment.taskanaWorkplaceUrl;
	workbasketsRoute = true;

	modalErrorMessage = '';
	modalTitle = '';
	selectedRoute = '';

	requestInProgress = false;

	errorModalSubscription: Subscription;
	requestInProgressSubscription: Subscription;
	selectedRouteSubscription: Subscription;
	routerSubscription: Subscription;

	@HostListener('window:resize', ['$event'])
	onResize(event) {
		this.orientationService.onResize();
	}

	constructor(
		private router: Router,
		private errorModalService: ErrorModalService,
		private requestInProgressService: RequestInProgressService,
		private orientationService: OrientationService,
		private selectedRouteService: SelectedRouteService) {
	}

	ngOnInit() {

		this.routerSubscription = this.router.events.subscribe(event => {
			if (event instanceof NavigationStart) {
				this.selectedRouteService.selectRoute(event);
			}
		});
		this.errorModalSubscription = this.errorModalService.getError().subscribe((error: ErrorModel) => {
			if (typeof error.message === 'string') {
				this.modalErrorMessage = error.message
			} else {
				this.modalErrorMessage = error.message.error ? (error.message.error.error + ' ' + error.message.error.message) : error.message.message;
			}
			this.modalTitle = error.title;
		})

		this.requestInProgressSubscription = this.requestInProgressService.getRequestInProgress().subscribe((value: boolean) => {
			this.requestInProgress = value;
		})

		this.selectedRouteSubscription = this.selectedRouteService.getSelectedRoute().subscribe((value: string) => {
			if (value.indexOf('classifications') !== -1) {
				this.workbasketsRoute = false;
			}
			this.selectedRoute = value;
		})
	}

	ngOnDestroy() {
		if (this.routerSubscription) { this.routerSubscription.unsubscribe(); }
		if (this.errorModalSubscription) { this.errorModalSubscription.unsubscribe(); }
		if (this.requestInProgressSubscription) { this.requestInProgressSubscription.unsubscribe(); }
		if (this.selectedRouteSubscription) { this.selectedRouteSubscription.unsubscribe(); }
	}
}

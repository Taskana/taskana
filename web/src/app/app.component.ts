import { Component, OnInit, HostListener } from '@angular/core';
import { environment } from '../environments/environment';
import { Router, NavigationStart } from '@angular/router';

import { ErrorModel } from './models/modal-error';

import { ErrorModalService } from './services/errorModal/error-modal.service';
import { RequestInProgressService } from './services/requestInProgress/request-in-progress.service';
import { OrientationService } from './services/orientation/orientation.service';

@Component({
	selector: 'taskana-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
	title = 'Taskana administration';

	adminUrl: string = environment.taskanaAdminUrl;
	monitorUrl: string = environment.taskanaMonitorUrl;
	workplaceUrl: string = environment.taskanaWorkplaceUrl;
	workbasketsRoute = true;

	modalErrorMessage = '';
	modalTitle = '';

	requestInProgress = false;

	@HostListener('window:resize', ['$event'])
	onResize(event) {
		this.orientationService.onResize();
	}

	constructor(
		private router: Router,
		private errorModalService: ErrorModalService,
		private requestInProgressService: RequestInProgressService,
		private orientationService: OrientationService) {
	}

	ngOnInit() {
		this.router.events.subscribe(event => {
			if (event instanceof NavigationStart) {
				if (event.url.indexOf('categories') !== -1) {
					this.workbasketsRoute = false;
				}
			}
		});

		this.errorModalService.getError().subscribe((error: ErrorModel) => {
			this.modalErrorMessage = error.message;
			this.modalTitle = error.title;
		})

		this.requestInProgressService.getRequestInProgress().subscribe((value: boolean) => {
			this.requestInProgress = value;
		})
	}
}

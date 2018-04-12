import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { Router, ActivatedRoute } from '@angular/router';

import { ImportType } from 'app/models/import-type';
import { Classification } from 'app/models/classification';
import { TreeNodeModel } from 'app/models/tree-node';

import { ClassificationsService } from 'app/services/classifications/classifications.service';

@Component({
	selector: 'taskana-classification-list',
	templateUrl: './classification-list.component.html',
	styleUrls: ['./classification-list.component.scss']
})
export class ClassificationListComponent implements OnInit, OnDestroy {


	selectedId: string;
	selectionToImport = ImportType.CLASSIFICATIONS;
	requestInProgress = false;
	initialized = false;

	classifications: Array<Classification> = [];
	classificationsTypes: Array<string> = [];
	classificationTypeSelected: string;
	classificationServiceSubscription: Subscription;
	classificationTypeServiceSubscription: Subscription;
	classificationSelectedSubscription: Subscription;
	classificationSavedSubscription: Subscription;
	selectedClassificationSubscription: Subscription;

	constructor(
		private classificationService: ClassificationsService,
		private router: Router,
		private route: ActivatedRoute, ) {
	}

	ngOnInit() {
		this.classificationSavedSubscription = this.classificationService
			.classificationSavedTriggered()
			.subscribe(value => {
				this.performRequest(true);
			})
		this.selectedClassificationSubscription = this.classificationService.getSelectedClassificationType().subscribe(value => {
			this.classificationTypeSelected = value;
			this.performRequest();
		})
	}

	selectClassificationType(classificationTypeSelected: string) {
		this.classifications = [];
		this.requestInProgress = true;
		this.classificationService.selectClassificationType(classificationTypeSelected);
		this.classificationService.getClassifications(true, classificationTypeSelected)
			.subscribe((classifications: Array<TreeNodeModel>) => {
				this.classifications = classifications;
				this.requestInProgress = false;
			});
	}

	selectClassification(id: string) {
		this.selectedId = id;
		if (!id) {
			this.router.navigate(['administration/classifications']);
			return;
		}
		this.router.navigate([{ outlets: { detail: [this.selectedId] } }], { relativeTo: this.route });
	}

	addClassification() {
		this.router.navigate([{ outlets: { detail: [`new-classification/${this.selectedId}`] } }], { relativeTo: this.route });
	}

	private performRequest(forceRequest = false) {
		if (this.initialized && !forceRequest) {
			return;
		}

		this.requestInProgress = true;
		this.classifications = [];
		this.classificationServiceSubscription = this.classificationService.getClassifications(true)
			.subscribe((classifications: Array<TreeNodeModel>) => {
				this.requestInProgress = false;
				if (!classifications.length) {
					return null;
				}
				this.classifications = classifications;
				this.classificationTypeServiceSubscription = this.classificationService.getClassificationTypes()
					.subscribe((classificationsTypes: Array<string>) => {
						this.classificationsTypes = classificationsTypes;
					});
			});
		this.classificationSelectedSubscription = this.classificationService.getSelectedClassification()
			.subscribe((classificationSelected: string) => {
				setTimeout(() => { this.selectedId = classificationSelected; }, 0);
			});

		this.initialized = true;

	}

	ngOnDestroy(): void {
		if (this.classificationServiceSubscription) { this.classificationServiceSubscription.unsubscribe(); }
		if (this.classificationTypeServiceSubscription) { this.classificationTypeServiceSubscription.unsubscribe(); }
		if (this.classificationSelectedSubscription) { this.classificationSelectedSubscription.unsubscribe(); }
		if (this.classificationSavedSubscription) { this.classificationSavedSubscription.unsubscribe(); }

	}
}
